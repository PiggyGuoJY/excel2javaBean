package com.github.piggyguojy.parser.excel.structure.annotation.handler;

import static com.github.piggyguojy.util.Assert.isNull;
import static com.github.piggyguojy.util.Assert.notNul;
import static com.github.piggyguojy.util.Assert.notNull;
import static com.github.piggyguojy.util.Msg.msg;
import static java.lang.String.format;

import com.github.piggyguojy.parser.excel.parse.ExcelParser;
import com.github.piggyguojy.parser.excel.structure.annotation.ExcelBean;
import com.github.piggyguojy.parser.excel.structure.annotation.ExcelCell;
import com.github.piggyguojy.parser.rule.structure.StructureHandler;
import com.github.piggyguojy.util.ClassUtil;
import com.github.piggyguojy.util.Msg;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * {@link ExcelCell}注解处理器
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelCellHandler
    extends ExcelAnnotationHandler<ExcelCell> {

  private static final Map<String, Object> INHERITABLE_FIELD =
      ImmutableMap.<String, Object>builder()
          .put("sheetName", "")
          .put("sheet", -1)
          .put("columnName", "")
          .put("column", -1)
          .put("row", -1)
          .put("address", "")
          .build();

  static {
    register(ExcelCell.class, new ExcelCellHandler());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <G> Msg<?> onField(
      Class<G> gClass,
      ExcelCell excelCell,
      ExcelParser excelParser,
      Object... args
  ) {
    return onFieldHandler(gClass, excelCell, excelParser, args);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <G> Msg<?> onType(
      Class<G> gClass,
      ExcelCell excelCell,
      ExcelParser excelParser,
      Object... args
  ) {
    return onTypeHandler(gClass, excelCell, excelParser, args);
  }

  @Override
  protected Map<String, Object> getCustomerInheritableField() {
    return ExcelCellHandler.INHERITABLE_FIELD;
  }

  private <G> Msg<?> onFieldHandler(
      Class<G> gClass,
      ExcelCell excelCell,
      ExcelParser excelParser,
      Object... args
  ) {
    // 1.ExcelCell只有可能从属性所在类的ExcelBean上继承属性
    ExcelBean excelBeanParent = getAnnotationParent(ExcelBean.class, args);
    args[ANNOTATION_PARENT] = notNull(excelBeanParent) ? decideBiRule(excelCell, excelBeanParent,
        excelBeanParent.overrideRule()) : excelCell;
    Msg<?> msg = onTypeHandler(
        gClass,
        gClass.getDeclaredAnnotation(ExcelCell.class),
        excelParser,
        args);
    if (msg.isException()) {
      return msg;
    }
    ClassUtil.set(
        (Field) args[StructureHandler.FIELD_REF],
        args[StructureHandler.GOAL_INST],
        msg.getT());
    return msg;
  }

  private <G> Msg<G> onTypeHandler(
      Class<G> gClass,
      ExcelCell excelCell,
      ExcelParser excelParser,
      Object... args
  ) {
    ExcelCell excelCellParent = getAnnotationParent(ExcelCell.class, args);
    ExcelCell finalExcelCell = notNull(excelCellParent) ? decideRule(excelCell, excelCellParent,
        excelCellParent.overrideRule()) : excelCell;
    // todo finalExcelCell也有可能为null
    Sheet sheet = ExcelParser.ExcelParserHelper.decideSheet(
        finalExcelCell.sheet(),
        finalExcelCell.sheetName(),
        excelParser.getWorkbook());
    if (isNull(sheet)) {
      return msg(new IllegalStateException(format(
          "根据[sheet:%d, sheetName:%s]无法在Workbook中找到对应Sheet",
          finalExcelCell.sheet(),
          finalExcelCell.sheetName())));
    }
    Cell cell = ExcelCellHandlerHelper.decideCell(finalExcelCell, sheet);
    if (isNull(cell)) {
      return msg(new IllegalStateException(format(
          "根据[%s]无法在Sheet中找到对应Cell",
          finalExcelCell.toString())));
    }
    return excelParser.transform(cell, gClass);
  }

  /**
   * 程序员（guojy）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
   *
   * <p> 创建时间：2019/2/20
   *
   * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
   * @version 1.0
   */
  private static class ExcelCellHandlerHelper {

    private static Cell decideCell(ExcelCell excelCell, Sheet sheet) {
      Cell cell = decideCell(excelCell.columnName(), excelCell.column(), excelCell.row(), sheet);
      if (isNull(cell) && notNul(excelCell.address())) {
        return decideCell(excelCell.address(), sheet);
      } else {
        return cell;
      }
    }

    private static Cell decideCell(String columnName, int columnNo, int rowNo, Sheet sheet) {
      if (rowNo < 1) {
        return null;
      }
      Row row = sheet.getRow(rowNo - 1);
      if (isNull(row)) {
        return null;
      }
      return row.getCell(ExcelParser.ExcelParserHelper.decideColumnNo(columnName, columnNo) - 1);
    }

    private static Cell decideCell(String address, Sheet sheet) {
      if (address.matches("^\\$?[A-Z]+\\$?[0-9]+$")) {
        return decideCell(
            address.replaceAll("^\\$?([A-Z]+)\\$?[0-9]+$", "$1"),
            -1,
            Integer.parseInt(address.replaceAll("^\\$?[A-Z]+\\$?([0-9]+)$", "$1")),
            sheet);
      } else {
        return null;
      }
    }
  }
}
