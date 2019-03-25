package com.github.piggyguojy.parser.excel.type;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;

/**
 * 基于Excel单元格类型的高级转换器
 * <p>
 *     本类提供了对常用类型的拓展转换规则(支持公式计算)
 * </p>
 *    <table border="1" cellspacing="0">
 *        <caption>常用类型的转换规则(当cell为null时总是返回null)</caption>
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
 *            <td style="background-color:lightgreen">错误码,含义见{@link FormulaError}</td>
 *        </tr>
 *        <tr>
 *            <td>{@link Short}</td>
 *            <td style="background-color:lightgreen">按{@code short}强制类型转换后的单元格值</td>
 *            <td style="background-color:lightgreen">{@link Short#decode(String)}</td>
 *            <td style="background-color:khaki">{@link Short#MIN_VALUE}</td>
 *            <td style="background-color:khaki">{@link Short#MIN_VALUE}({@code true})或0({@code false})</td>
 *            <td style="background-color:lightgreen">错误码,含义见{@link FormulaError}</td>
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
 *            <td style="background-color:lightgreen">按int强制类型转换后的单元格值</td>
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
 *        </tr>
 *        <tr>
 *            <td>BigInteger</td>
 *            <td style="background-color:lightgreen">按long强制类型转换后的单元格值</td>
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
 *        </tr>
 *        <tr>
 *            <td>Double</td>
 *            <td style="background-color:lightgreen">单元格值</td>
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
 *        </tr>
 *        <tr>
 *            <td>String</td>
 *            <td>null</td>
 *            <td style="background-color:lightgreen">单元格值</td>
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
 *        <tr>
 *            <td>boolean</td>
 *            <td>false</td>
 *            <td>false</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>false</td>
 *            <td style="background-color:lightgreen">单元格值</td>
 *            <td>false</td>
 *            <td>false</td>
 *        </tr>
 *        <tr>
 *            <td>byte</td>
 *            <td style="background-color:lightgreen">按byte强制类型转换后的单元格值</td>
 *            <td>0</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr>
 *            <td>short</td>
 *            <td style="background-color:lightgreen">按short强制类型转换后的单元格值</td>
 *            <td>0</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr>
 *            <td>char</td>
 *            <td>\u0000</td>
 *            <td>\u0000</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>\u0000</td>
 *            <td>\u0000</td>
 *            <td>\u0000</td>
 *            <td>\u0000</td>
 *        </tr>
 *        <tr>
 *            <td>int</td>
 *            <td style="background-color:lightgreen">按int强制类型转换后的单元格值</td>
 *            <td>0</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr>
 *            <td>long</td>
 *            <td style="background-color:lightgreen">按long强制类型转换后的单元格值</td>
 *            <td>0</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr>
 *            <td>float</td>
 *            <td style="background-color:lightgreen">按float强制类型转换后的单元格值</td>
 *            <td>0</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr>
 *            <td>double</td>
 *            <td style="background-color:lightgreen">单元格值</td>
 *            <td>0</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr>
 *            <td>boolean[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *        </tr>
 *        <tr>
 *            <td>byte[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *        </tr>
 *        <tr>
 *            <td>short[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *        </tr>
 *        <tr>
 *            <td>char[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *        </tr>
 *        <tr>
 *            <td>int[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *        </tr>
 *        <tr>
 *            <td>long[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *        </tr>
 *        <tr>
 *            <td>float[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *        </tr>
 *        <tr>
 *            <td>double[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td style="background-color:lightgreen">计算后再走一遍本逻辑</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
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
        switch (cell.getCellType()) {
            case NUMERIC: return cell.getNumericCellValue()!=0D;
            case STRING: return "true".equalsIgnoreCase(cell.getStringCellValue());
            case FORMULA: return cell2Boolean(cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator().evaluateInCell(cell));
            case BLANK: return false;
            case BOOLEAN: return cell.getBooleanCellValue();
            case ERROR: return false;
            default: return null;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Byte cell2Byte(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC: return (byte)cell.getNumericCellValue();
            case STRING: return Byte.decode(cell.getStringCellValue());
            case FORMULA: return cell2Byte(cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator().evaluateInCell(cell));
            case BLANK: return Byte.MIN_VALUE;
            case BOOLEAN: return cell.getBooleanCellValue() ? Byte.MIN_VALUE : 0;
            case ERROR: return cell.getErrorCellValue();
            default: return null;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Short cell2Short(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC: return (short)cell.getNumericCellValue();
            case STRING: return Short.decode(cell.getStringCellValue());
            case FORMULA: return cell2Short(cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator().evaluateInCell(cell));
            case BLANK: return Short.MIN_VALUE;
            case BOOLEAN: return cell.getBooleanCellValue() ? Short.MIN_VALUE : 0;
            case ERROR: return (short)cell.getErrorCellValue();
            default: return null;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Character cell2Character(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC: return null;
            case STRING: return cell.getStringCellValue().length()==0 ? Character.MIN_VALUE : cell.getStringCellValue().charAt(0);
            case FORMULA: return cell2Character(cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator().evaluateInCell(cell));
            case BLANK: return Character.MIN_VALUE;
            case BOOLEAN: return cell.getBooleanCellValue() ? 'T' : 'F';
            case ERROR: return Character.MIN_VALUE;
            default: return null;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer cell2Integer(Cell cell) {
        return super.cell2Integer(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Long cell2Long(Cell cell) {
        return super.cell2Long(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected BigInteger cell2BigInteger(Cell cell) {
        return super.cell2BigInteger(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Float cell2Float(Cell cell) {
        return super.cell2Float(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Double cell2Double(Cell cell) {
        return super.cell2Double(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected BigDecimal cell2BigDecimal(Cell cell) {
        return super.cell2BigDecimal(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected String cell2String(Cell cell) {
        return super.cell2String(cell);
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
     * {@inheritDoc}
     */
    @Override
    protected boolean cell2boolean(Cell cell) {
        return super.cell2boolean(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean[] cell2booleans(Cell cell) {
        return super.cell2booleans(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected byte cell2byte(Cell cell) {
        return super.cell2byte(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected byte[] cell2bytes(Cell cell) {
        return super.cell2bytes(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected short cell2short(Cell cell) {
        return super.cell2short(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected short[] cell2shorts(Cell cell) {
        return super.cell2shorts(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected char cell2char(Cell cell) {
        return super.cell2char(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected char[] cell2chars(Cell cell) {
        return super.cell2chars(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected int cell2int(Cell cell) {
        return super.cell2int(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected int[] cell2ints(Cell cell) {
        return super.cell2ints(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected long cell2long(Cell cell) {
        return super.cell2long(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected long[] cell2longs(Cell cell) {
        return super.cell2longs(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected float cell2float(Cell cell) {
        return super.cell2float(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected float[] cell2floats(Cell cell) {
        return super.cell2floats(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected double cell2double(Cell cell) {
        return super.cell2double(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected double[] cell2doubles(Cell cell) {
        return super.cell2doubles(cell);
    }
    /**
     * 生效父类配置的默认构造器
     */
    protected ExcelTransformerRuleTypeAdvanced() { super(); }


    private static final ExcelTransformerRuleTypeAdvanced DEFAULT_EXCEL_TRANSFORMER_RULE_TYPE_ADVANCED
            = new ExcelTransformerRuleTypeAdvanced();
}
