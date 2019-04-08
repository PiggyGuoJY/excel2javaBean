package com.github.piggyguojy.parser.excel.type;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.function.Function;

import static com.github.piggyguojy.util.Assert.isNull;

/**
 * 基于Excel单元格类型的高级转换器
 * <p>
 *     本类提供了对常用类型的拓展转换规则(支持公式计算)
 * </p>
 *    <table border="1" cellspacing="0">
 *        <caption>常用类型的转换规则(当cell为{@code null}时总是返回{@code null})</caption>
 *        <tr>
 *            <th></th>
 *            <th>{@link org.apache.poi.ss.usermodel.CellType#NUMERIC}</th>
 *            <th>{@link org.apache.poi.ss.usermodel.CellType#STRING}</th>
 *            <th>{@link org.apache.poi.ss.usermodel.CellType#FORMULA}</th>
 *            <th>{@link org.apache.poi.ss.usermodel.CellType#BLANK}</th>
 *            <th>{@link org.apache.poi.ss.usermodel.CellType#BOOLEAN}</th>
 *            <th>{@link org.apache.poi.ss.usermodel.CellType#ERROR}</th>
 *            <th>default</th>
 *        </tr>
 *        <tr>
 *            <td>{@link Boolean}</td>
 *            <td style="background-color:lightgreen">非0时为{@code true}</td>
 *            <td style="background-color:lightgreen">在忽略大小写的情况下与“true”相同时为{@code true}</td>
 *            <td style="background-color:lightgreen" rowspan="17">计算后再走一遍本逻辑</td>
 *            <td style="background-color:khaki">{@link Boolean#FALSE}</td>
 *            <td style="background-color:lightgreen">单元格值</td>
 *            <td style="background-color:khaki">{@link Boolean#FALSE}</td>
 *            <td style="background-color:lightpink" rowspan="17">{@code null}</td>
 *        </tr>
 *        <tr>
 *            <td>{@link Byte}</td>
 *            <td style="background-color:lightgreen">按{@code byte}强制类型转换后的单元格值</td>
 *            <td style="background-color:lightgreen">{@link Byte#decode(String)}</td>
 *            <td style="background-color:khaki">{@link Byte#MIN_VALUE}</td>
 *            <td style="background-color:khaki">{@link Byte#MIN_VALUE}({@code true})或0({@code false})</td>
 *            <td style="background-color:khaki" rowspan="2">错误码,含义见{@link FormulaError}</td>
 *        </tr>
 *        <tr>
 *            <td>{@link Short}</td>
 *            <td style="background-color:lightgreen">按{@code short}强制类型转换后的单元格值</td>
 *            <td style="background-color:lightgreen">{@link Short#decode(String)}</td>
 *            <td style="background-color:khaki">{@link Short#MIN_VALUE}</td>
 *            <td style="background-color:khaki">{@link Short#MIN_VALUE}({@code true})或0({@code false})</td>
 *        </tr>
 *        <tr>
 *            <td>{@link Character}</td>
 *            <td style="background-color:lightpink">{@code null}</td>
 *            <td style="background-color:lightgreen">{@link Character#MIN_VALUE}(字符串长度为0时)或字符串的第一个字符</td>
 *            <td style="background-color:khaki">{@link Character#MIN_VALUE}</td>
 *            <td style="background-color:lightgreen">'T'({@code true})或'F'({@code false})</td>
 *            <td style="background-color:khaki">{@link Character#MIN_VALUE}</td>
 *        </tr>
 *        <tr>
 *            <td>Integer</td>
 *            <td style="background-color:lightgreen">按{@code int}强制类型转换后的单元格值</td>
 *            <td style="background-color:lightgreen">{@link Integer#decode(String)}</td>
 *            <td style="background-color:khaki">{@link Integer#MIN_VALUE}</td>
 *            <td style="background-color:khaki">{@link Integer#MIN_VALUE}({@code true})或0({@code false})</td>
 *            <td style="background-color:khaki" rowspan="6">错误码,含义见{@link FormulaError}</td>
 *        </tr>
 *        <tr>
 *            <td>Long</td>
 *            <td style="background-color:lightgreen">按{@code long}强制类型转换后的单元格值</td>
 *            <td style="background-color:lightgreen">{@link Long#decode(String)}</td>
 *            <td style="background-color:khaki">{@link Long#MIN_VALUE}</td>
 *            <td style="background-color:khaki">{@link Long#MIN_VALUE}({@code true})或0({@code false})</td>
 *        </tr>
 *        <tr>
 *            <td>BigInteger</td>
 *            <td style="background-color:lightgreen">按{@code long}强制类型转换后的单元格值</td>
 *            <td style="background-color:lightgreen">{@code BigInteger::new}</td>
 *            <td style="background-color:khaki">{@link BigInteger#ONE}</td>
 *            <td style="background-color:khaki">{@link BigInteger#ONE}({@code true})或{@link BigInteger#ZERO}({@code false})</td>
 *        </tr>
 *        <tr>
 *            <td>Float</td>
 *            <td style="background-color:lightgreen">按float强制类型转换后的单元格值</td>
 *            <td style="background-color:lightgreen">{@link Floats#tryParse(String)}</td>
 *            <td style="background-color:khaki">{@link Float#MIN_VALUE}</td>
 *            <td style="background-color:khaki">{@link Float#MIN_VALUE}({@code true})或0({@code false})</td>
 *        </tr>
 *        <tr>
 *            <td>Double</td>
 *            <td style="background-color:lightgreen">单元格值</td>
 *            <td style="background-color:lightgreen">{@link Doubles#tryParse(String)}</td>
 *            <td style="background-color:khaki">{@link Double#MIN_VALUE}</td>
 *            <td style="background-color:khaki">{@link Double#MIN_VALUE}({@code true})或0({@code false})</td>
 *        </tr>
 *        <tr>
 *            <td>BigDecimal</td>
 *            <td style="background-color:lightgreen">单元格值</td>
 *            <td style="background-color:lightgreen">{@code BigDecimal::new}</td>
 *            <td style="background-color:khaki">{@link BigDecimal#ONE}</td>
 *            <td style="background-color:khaki">{@link BigDecimal#ONE}({@code true})或0({@code false})</td>
 *        </tr>
 *        <tr>
 *            <td>String</td>
 *            <td style="background-color:lightgreen">单元格值的字符串形式</td>
 *            <td style="background-color:lightgreen">单元格值</td>
 *            <td style="background-color:lightgreen">""</td>
 *            <td style="background-color:lightgreen">{@link Boolean#toString(boolean)}</td>
 *            <td style="background-color:lightgreen">{@link FormulaError#name()}</td>
 *        </tr>
 *        <tr>
 *            <td>Date</td>
 *            <td style="background-color:lightgreen">单元格值(转换异常时返回null)</td>
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
 *        </tr>
 *        <tr>
 *            <td>Class</td>
 *            <td>null</td>
 *            <td style="background-color:lightgreen">单元格值描述类类型, 获取失败时返回null</td>
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
 *        </tr>
 *        <tr>
 *            <td>Object</td>
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
 *        </tr>
 *    </table>
 *
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 *
 * */
public class ExcelTransformerRuleTypeAdvanced
        extends ExcelTransformerRulePrimitiveSupported {

    /**
     * 获取一个支持Java富原生类型的转换器
     * @return 转换器
     */
    public static ExcelTransformerRuleTypeAdvanced of() {
        return DEFAULT_EXCEL_TRANSFORMER_RULE_TYPE_ADVANCED;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected Boolean cell2Boolean(Cell cell) {
        return template(
                cell,
                c -> c.getNumericCellValue()!=0D,
                c -> "true".equalsIgnoreCase(c.getStringCellValue()),
                this::cell2Boolean,
                c -> false,
                Cell::getBooleanCellValue,
                c -> false);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Byte cell2Byte(Cell cell) {
        return template(
                cell,
                c -> (byte)c.getNumericCellValue(),
                c -> Byte.decode(c.getStringCellValue()),
                this::cell2Byte,
                c -> Byte.MIN_VALUE,
                c -> c.getBooleanCellValue() ? Byte.MIN_VALUE : 0,
                Cell::getErrorCellValue);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Short cell2Short(Cell cell) {
        return template(
                cell,
                c -> (short)c.getNumericCellValue(),
                c -> Short.decode(c.getStringCellValue()),
                this::cell2Short,
                c -> Short.MIN_VALUE,
                c -> c.getBooleanCellValue() ? Short.MIN_VALUE : 0,
                c -> (short)c.getErrorCellValue());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Character cell2Character(Cell cell) {
        return template(
                cell,
                c -> null,
                c -> c.getStringCellValue().length()==0 ? Character.MIN_VALUE : c.getStringCellValue().charAt(0),
                this::cell2Character,
                c -> Character.MIN_VALUE,
                c -> c.getBooleanCellValue() ? 'T' : 'F',
                c -> Character.MIN_VALUE);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer cell2Integer(Cell cell) {
        return template(
                cell,
                c -> (int)c.getNumericCellValue(),
                c ->Integer.decode(c.getStringCellValue()),
                this::cell2Integer,
                c -> Integer.MIN_VALUE,
                c -> c.getBooleanCellValue() ? Integer.MIN_VALUE : 0,
                c -> (int)c.getErrorCellValue());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Long cell2Long(Cell cell) {
        return template(
                cell,
                c -> (long)c.getNumericCellValue(),
                c -> Long.decode(c.getStringCellValue()),
                this::cell2Long,
                c -> Long.MIN_VALUE,
                c -> c.getBooleanCellValue() ? Long.MIN_VALUE: 0,
                c -> (long)c.getErrorCellValue());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected BigInteger cell2BigInteger(Cell cell) {
        return template(
                cell,
                c -> BigInteger.valueOf((long)c.getNumericCellValue()),
                c -> new BigInteger(c.getStringCellValue()),
                this::cell2BigInteger,
                c -> BigInteger.ONE,
                c -> c.getBooleanCellValue() ? BigInteger.ONE : BigInteger.ZERO,
                c -> BigInteger.valueOf(c.getErrorCellValue()));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Float cell2Float(Cell cell) {
        return template(
                cell,
                c -> (float)c.getNumericCellValue(),
                c -> Floats.tryParse(c.getStringCellValue()),
                this::cell2Float,
                c -> Float.MIN_VALUE,
                c -> c.getBooleanCellValue() ? Float.MIN_VALUE : 0,
                c -> (float)c.getErrorCellValue());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Double cell2Double(Cell cell) {
        return template(
                cell,
                Cell::getNumericCellValue,
                c -> Doubles.tryParse(cell.getStringCellValue()),
                this::cell2Double,
                c -> Double.MIN_VALUE,
                c -> c.getBooleanCellValue() ? Double.MIN_VALUE : 0,
                c -> (double)c.getErrorCellValue());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected BigDecimal cell2BigDecimal(Cell cell) {
        return template(
                cell,
                c -> BigDecimal.valueOf(c.getNumericCellValue()),
                c -> new BigDecimal(c.getStringCellValue()),
                this::cell2BigDecimal,
                c -> BigDecimal.ONE,
                c -> c.getBooleanCellValue() ? BigDecimal.ONE : BigDecimal.ZERO,
                c -> BigDecimal.valueOf(c.getErrorCellValue()));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected String cell2String(Cell cell) {
        return template(
                cell,
                c -> Double.toString(c.getNumericCellValue()),
                Cell::getStringCellValue,
                this::cell2String,
                c -> "",
                c -> Boolean.toString(c.getBooleanCellValue()),
                c -> FormulaError.forInt(c.getErrorCellValue()).name());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Date cell2Date(Cell cell) {
        return super.cell2Date(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected LocalDate cell2LocalDate(Cell cell) {
        return super.cell2LocalDate(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> cell2Class(Cell cell) {
        return super.cell2Class(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Void cell2Void(Cell cell) {
        return super.cell2Void(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object cell2Object(Cell cell) {
        return super.cell2Object(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object[] cell2Objects(Cell cell) {
        return super.cell2Objects(cell);
    }

    /**
     * 生效父类配置的默认构造器
     */
    protected ExcelTransformerRuleTypeAdvanced() { super(); }

    private static final ExcelTransformerRuleTypeAdvanced DEFAULT_EXCEL_TRANSFORMER_RULE_TYPE_ADVANCED
            = new ExcelTransformerRuleTypeAdvanced();
    private static <T> T template(
            Cell cell,
            Function<Cell,T> numeric,
            Function<Cell,T> string,
            Function<Cell,T> formula,
            Function<Cell,T> blank,
            Function<Cell,T> bool,
            Function<Cell,T> error
    ) {
        if (isNull(cell)) { return null;}
        switch (cell.getCellType()) {
            case NUMERIC: return numeric.apply(cell);
            case STRING: return string.apply(cell);
            case FORMULA: return formula.apply(cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator().evaluateInCell(cell));
            case BLANK: return blank.apply(cell);
            case BOOLEAN: return bool.apply(cell);
            case ERROR: return error.apply(cell);
            default: return null;
        }
    }
}
