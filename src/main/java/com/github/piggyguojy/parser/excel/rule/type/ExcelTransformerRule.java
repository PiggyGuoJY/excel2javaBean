
package com.github.piggyguojy.parser.excel.rule.type;


import com.github.piggyguojy.parser.rule.type.AbstractTransformerRule4SingleDataType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import static com.github.piggyguojy.Assert.isNull;

/**
 * 基于Excel单元格类型的转换器
 * <p>
 *     本类提供了对常用类型的转换规则(规则特定是对应才能转, 不支持公式计算)
 * </p>
 *    <table border="1">
 *        <caption>常用类型的转换规则(当cell为null时总是返回null)</caption>
 *        <tr>
 *            <th></th>
 *            <th>NUMERIC</th>
 *            <th>STRING</th>
 *            <th>FORMULA</th>
 *            <th>BLANK</th>
 *            <th>BOOLEAN</th>
 *            <th>ERROR</th>
 *            <th>default</th>
 *        </tr>
 *        <tr>
 *            <td>Boolean</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td style="background-color:lightgreen">单元格值</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>Byte</td>
 *            <td style="background-color:lightgreen">按byte强制类型转换后的单元格值</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>Short</td>
 *            <td style="background-color:lightgreen">按short强制类型转换后的单元格值</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>Character</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>Integer</td>
 *            <td style="background-color:lightgreen">按int强制类型转换后的单元格值</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>Long</td>
 *            <td style="background-color:lightgreen">按long强制类型转换后的单元格值</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>BigInteger</td>
 *            <td style="background-color:lightgreen">按long强制类型转换后的单元格值</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>Float</td>
 *            <td style="background-color:lightgreen">按float强制类型转换后的单元格值</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>Double</td>
 *            <td style="background-color:lightgreen">单元格值</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>BigDecimal</td>
 *            <td style="background-color:lightgreen">单元格值</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>String</td>
 *            <td>null</td>
 *            <td style="background-color:lightgreen">单元格值</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>Date</td>
 *            <td style="background-color:lightgreen">单元格值(转换异常时返回null)</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>LocalDate</td>
 *            <td style="background-color:lightgreen">单元格值(转换异常时返回null)</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>Class</td>
 *            <td>null</td>
 *            <td style="background-color:lightgreen">单元格值描述类类型, 获取失败时返回null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>Void</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>Object</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>Object[]</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *    </table>
 *
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 *
 *
 * @see AbstractTransformerRule4SingleDataType
 *
 * */
@Slf4j
public class ExcelTransformerRule
        extends AbstractTransformerRule4SingleDataType<Cell> {

    /**
     * 获取一个支持常用类型的转换器
     * @return 转换器
     */
    public static ExcelTransformerRule of() {
        return DEFAULT_EXCEL_TRANSFORMER_RULE;
    }



    protected Boolean cell2Boolean(Cell cell) {
        if(isNull(cell)) { return null; }
        switch ( cell.getCellType()) {
            case BOOLEAN: return cell.getBooleanCellValue();
            default: return null;
        }
    }
    protected Byte cell2Byte(Cell cell) {
        if(isNull(cell)) { return null; }
        switch (cell.getCellType()) {
            case NUMERIC: return (byte) cell.getNumericCellValue();
            default: return null;
        }
    }
    protected Short cell2Short(Cell cell) {
        if(isNull(cell)) { return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return (short)cell.getNumericCellValue();
            default: return null;
        }
    }
    protected Character cell2Character(Cell cell) { return null;}
    protected Integer cell2Integer(Cell cell) {
        if(isNull(cell)) { return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return (int)cell.getNumericCellValue();
            default: return null;
        }
    }
    protected Long cell2Long(Cell cell) {
        if(isNull(cell)) { return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return (long)cell.getNumericCellValue();
            default: return null;
        }
    }
    protected BigInteger cell2BigInteger(Cell cell) {
        if(isNull(cell)) { return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return BigInteger.valueOf((long)cell.getNumericCellValue());
            default: return null;
        }
    }
    protected Float cell2Float(Cell cell) {
        if(isNull(cell)) { return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return (float)cell.getNumericCellValue();
            default: return null;
        }
    }
    protected Double cell2Double(Cell cell) {
        if(isNull(cell)) { return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return cell.getNumericCellValue();
            default: return null;
        }
    }
    protected BigDecimal cell2BigDecimal(Cell cell) {
        if(isNull(cell)) {return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return BigDecimal.valueOf(cell.getNumericCellValue());
            default: return null;
        }
    }

    protected String cell2String(Cell cell) {
        if(isNull(cell)) {return null;}
        switch ( cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            default: return null;
        }
    }
    protected Date cell2Date(Cell cell) {
        if(isNull(cell)) {return null;}
        switch ( cell.getCellType()) {
            case NUMERIC:
                try {
                    return cell.getDateCellValue();
                } catch (NumberFormatException e) {
                    log.error(e.getMessage(),e);
                    return null;
                }
            default: return null;
        }
    }
    protected LocalDate cell2LocalDate(Cell cell) {
        if(isNull(cell)) {return null;}
        switch ( cell.getCellType()) {
            case NUMERIC:
                try {
                    ZonedDateTime zonedDateTime = cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault());
                    return LocalDate.of(zonedDateTime.getYear(),zonedDateTime.getMonth(),zonedDateTime.getDayOfMonth());
                } catch (NumberFormatException|DateTimeParseException e) {
                    log.error(e.getMessage(),e);
                    return null;
                }
            default: return null;
        }}
    protected Class<?> cell2Class(Cell cell) {
        if(isNull(cell)) {return null;}
        switch ( cell.getCellType()) {
            case STRING:
                try {
                    return ClassUtils.getClass( cell.getStringCellValue());
                } catch ( ClassNotFoundException e) {
                    log.error( e.getMessage(), e);
                    return null;
                }
            default: return null;
        }
    }
    protected Void cell2Void(Cell cell) { return null;}
    protected Object cell2Object(Cell cell) {return null;}
    protected Object[] cell2Objects(Cell cell) {return new Object[]{};}
    /**
     * 生效父类配置的默认构造器
     */
    protected ExcelTransformerRule() {
        super();
        this.initSuperDefaultRule();
    }



    /**
     * 单例化一个转换器
     */
    private static final ExcelTransformerRule DEFAULT_EXCEL_TRANSFORMER_RULE = new ExcelTransformerRule();
    /**
     * 初始化默认规则
     */
    private void initSuperDefaultRule() {
        super.addDefaultRule4Transformer( void.class,       this::cell2Void);
        super.addDefaultRule4Transformer( Void.class,       this::cell2Void);
        super.addDefaultRule4Transformer( Object.class,     this::cell2Object);
        super.addDefaultRule4Transformer( Object[].class,   this::cell2Objects);

        super.addDefaultRule4Transformer( Boolean.class,    this::cell2Boolean);
        super.addDefaultRule4Transformer( Byte.class,       this::cell2Byte);
        super.addDefaultRule4Transformer( Short.class,      this::cell2Short);
        super.addDefaultRule4Transformer( Character.class,  this::cell2Character);
        super.addDefaultRule4Transformer( Integer.class,    this::cell2Integer);
        super.addDefaultRule4Transformer( Long.class,       this::cell2Long);
        super.addDefaultRule4Transformer( BigInteger.class, this::cell2BigInteger);
        super.addDefaultRule4Transformer( Float.class,      this::cell2Float);
        super.addDefaultRule4Transformer( Double.class,     this::cell2Double);
        super.addDefaultRule4Transformer( BigDecimal.class, this::cell2BigDecimal);

        super.addDefaultRule4Transformer( String.class,     this::cell2String);
        super.addDefaultRule4Transformer( Date.class,       this::cell2Date);
        super.addDefaultRule4Transformer( LocalDate.class,  this::cell2LocalDate);
        super.addDefaultRule4Transformer( Class.class,      this::cell2Class);
    }
}
