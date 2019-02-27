package com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.handler;

import com.google.common.collect.ImmutableMap;
import com.tkp.tkpole.starter.utils.model.Msg;
import com.tkp.tkpole.starter.utils.parser.excel.rule.parse.ExcelParser;
import com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelBean;
import com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelCell;
import com.tkp.tkpole.starter.utils.parser.rule.structure.BiInheritableRule;
import com.tkp.tkpole.starter.utils.parser.rule.structure.OverrideRule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.util.Map;

import static com.tkp.tkpole.starter.utils.Assert.isNull;
import static com.tkp.tkpole.starter.utils.Assert.notNul;
import static com.tkp.tkpole.starter.utils.Assert.notNull;
import static com.tkp.tkpole.starter.utils.model.Msg.msg;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/2/15
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelCellHandler extends ExcelAnnotationHandler<ExcelCell> {
    @Override
    public <G> Msg<?> onField(Class<G> gClass, ExcelCell excelCell, ExcelParser excelParser, Object ... args) {
        onFieldHandler(gClass,excelCell, excelParser,args);
        return msg();
    }
    @Override
    public <G> Msg<?> onType(Class<G> gClass, ExcelCell excelCell, ExcelParser excelParser, Object... args) {
        return onTypeHandler(gClass,excelCell, excelParser,args);
    }

    @Override
    protected Map<String, Object> getCustomerInheritableField() { return ExcelCellHandler.INHERITABLE_FIELD; }

    static { register(ExcelCell.class, new ExcelCellHandler());}

    private <G> void onFieldHandler(Class<G> gClass, ExcelCell excelCell, ExcelParser excelParser, Object ... args) {
        // 1.ExcelCell只有可能从属性所在类的ExcelBean上继承属性
        ExcelBean excelBeanParent = getAnnotationParent(ExcelBean.class,args);
        args[ANNOTATION_PARENT] = notNull(excelBeanParent) ? decideBiRule(excelCell, excelBeanParent, excelBeanParent.overideRule()) : excelCell;
        Msg<?> msg = onTypeHandler( gClass, gClass.getDeclaredAnnotation( ExcelCell.class), excelParser,args);
        if ( msg.isException()) { return;}
        AbstractAnnotationHandlerHelper.set( ( Field) args[FIELD_REF], args[GOAL_INST], msg.getT());
    }
    private <G> Msg<G> onTypeHandler(Class<G> gClass, ExcelCell excelCell, ExcelParser excelParser, Object ... args) {
        ExcelCell excelCellParent = getAnnotationParent(ExcelCell.class,args);
        excelCell = notNull(excelCellParent) ? decideRule(excelCell, excelCellParent, excelCellParent.overideRule()) : excelCell;
        Sheet sheet = ExcelParser.ExcelParserHelper.decideSheet(excelCell.sheet(), excelCell.sheetName(), excelParser.getWorkbook());
        if ( isNull( sheet)) { return msg(new IllegalStateException("无法找到Sheet"));}
        Cell cell = ExcelCellHandlerHelper.decideCell(excelCell, sheet);
        if ( isNull( cell)) { return msg(new IllegalStateException("无法找到Cell")); }
        return excelParser.transform(cell, gClass);
    }

    /**
     * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
     *
     * <p> 创建时间：2019/2/20
     *
     * @author guojy24
     * @version 1.0
     * */
    private static class ExcelCellHandlerHelper {
        private static Cell decideCell(ExcelCell excelCell, Sheet sheet) {
            Cell cell = decideCell( excelCell.columnName(), excelCell.column(), excelCell.row(), sheet);
            if ( isNull( cell) && notNul( excelCell.address())) {
                return decideCell( excelCell.address(), sheet);
            } else {
                return cell;
            }
        }
        private static Cell decideCell( String columnName, int columnNo, int rowNo, Sheet sheet) {
            Row row  = sheet.getRow( rowNo<1 ? 0 : rowNo-1);
            if ( isNull( row)) { return null; }
            return row.getCell( ExcelParser.ExcelParserHelper.decideColumnNo( columnName, columnNo)-1);
        }
        private static Cell decideCell( String address, Sheet sheet) {
            if ( address.matches("^\\$?[A-Z]+\\$?[0-9]+$")) {
                return decideCell(
                        address.replaceAll("^\\$?([A-Z]+)\\$?[0-9]+$","$1"),
                        -1,
                        Integer.parseInt(address.replaceAll("^\\$?[A-Z]+\\$?([0-9]+)$","$1")),
                        sheet);
            } else { return null; }
        }
    }
    private static final Map<String,Object> INHERITABLE_FIELD =
            ImmutableMap.<String,Object>builder()
                    .put("columnName", "")
                    .put("column", -1)
                    .put("row", -1)
                    .put("address", "")
                    .build();
}
