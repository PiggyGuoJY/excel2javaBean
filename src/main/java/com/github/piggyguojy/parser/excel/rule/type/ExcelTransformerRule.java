
package com.github.piggyguojy.parser.excel.rule.type;


import com.github.piggyguojy.parser.rule.type.AbstractTransformerRule4SingleDataType;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static com.github.piggyguojy.Assert.isNull;

/**
 * 基于Excel单元格类型的类型转换类
 *    <table border="1">
 *        <caption>常用类型的转换规则</caption>
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
 *            <td>boolean</td>
 *            <td>false</td>
 *            <td style="background-color:lightgreen">当单元格值大写格式和TRUE相同时返回ture,其他情况返回false</td>
 *            <td style="background-color:lightgreen">false</td>
 *            <td>false</td>
 *            <td style="background-color:lightgreen">单元格值</td>
 *            <td>false</td>
 *            <td>false</td>
 *        </tr>
 *        <tr>
 *            <td>byte</td>
 *            <td style="background-color:lightgreen">按byte强制类型转换后的单元格值</td>
 *            <td style="background-color:lightgreen">单元格值转换为整数后强制转换为byte,转换失败返回0</td>
 *            <td style="background-color:lightgreen">0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr>
 *            <td>short</td>
 *            <td style="background-color:lightgreen">按short强制类型转换后的单元格值</td>
 *            <td style="background-color:lightgreen">单元格值转换为整数后强制转换为short,转换失败返回0</td>
 *            <td style="background-color:lightgreen">0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr><td>char</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>int</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>long</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>float</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>double</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr>
 *            <td>Boolean</td>
 *            <td>null</td>
 *            <td style="background-color:lightgreen">当单元格值大写格式和TRUE相同时返回ture,其他情况返回false</td>
 *            <td style="background-color:lightgreen">false</td>
 *            <td>null</td>
 *            <td style="background-color:lightgreen">单元格值</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>Byte</td>
 *            <td style="background-color:lightgreen">按byte强制类型转换后的单元格值</td>
 *            <td style="background-color:lightgreen">单元格值转换为整数后强制转换为byte,转换失败返回0</td>
 *            <td style="background-color:lightgreen">0</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *            <td>null</td>
 *        </tr>
 *        <tr>
 *            <td>Short</td>
 *            <td style="background-color:lightgreen">按short强制类型转换后的单元格值</td>
 *            <td style="background-color:lightgreen">单元格值转换为整数后强制转换为short,转换失败返回0</td>
 *            <td style="background-color:lightgreen">0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr><td>Character</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>Integer</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>Long</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>BigInteger</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>Float</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>Double</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>BigDecimal</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>Void</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>Object</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>String</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>Date</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>LocalDate</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>Class</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>boolean[]</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>byte[]</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>short[]</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>char[]</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>int[]</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>long[]</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>float[]</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>double[]</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *        <tr><td>Object[]</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
 *    </table>
 *
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * @since JDK1.8
 *
 * @see AbstractTransformerRule4SingleDataType
 *
 * */
@Slf4j
public class ExcelTransformerRule
        extends AbstractTransformerRule4SingleDataType<Cell> {

    public static ExcelTransformerRule of() {
        return new ExcelTransformerRule(DEFAULT_EXCEL_TRANSFORMER_RULE.getDefaultTransformerRule());
    }

    // 对基本数据类型,void和Object及其数组类型的默认转换, 可以通过继承该类进行规则改写; 对数组类型, 默认不支持转换;

    protected boolean cell2boolean(Cell cell) {
        Boolean b = this.cell2Boolean(cell);
        return isNull(b) ? false : b;
    }
    protected boolean[] cell2booleans(Cell cell) { return new boolean[]{};}
    protected byte cell2byte(Cell cell) {
        Byte b = this.cell2Byte(cell);
        return isNull(b) ? 0 : b;
    }
    protected byte[] cell2bytes(Cell cell) { return new byte[]{};}
    protected short cell2short(Cell cell) {
        Short s = this.cell2Short(cell);
        return isNull(s) ? 0 : s;
    }
    protected short[] cell2shorts(Cell cell) { return new short[]{};}
    protected char cell2char(Cell cell)  {
        Character c = this.cell2Character(cell);
        return isNull(c) ? '\u0000' : c;
    }
    protected char[] cell2chars(Cell cell) { return new char[]{};}
    protected int cell2int(Cell cell) {
        Integer i = this.cell2Integer(cell);
        return isNull(i) ? 0 : i;
    }
    protected int[] cell2ints(Cell cell) { return new int[]{};}
    protected long cell2long(Cell cell) {
        Long l = this.cell2Long(cell);
        return isNull(l) ? 0 : l;
    }
    protected long[] cell2longs(Cell cell) { return new long[]{};}
    protected float cell2float(Cell cell) {
        Float f = this.cell2Float(cell);
        return isNull(f) ? 0 : f;
    }
    protected float[] cell2floats(Cell cell) { return new float[]{};}
    protected double cell2double(Cell cell) {
        Double d = this.cell2Double(cell);
        return isNull(d) ? 0 : d;
    }
    protected double[] cell2doubles(Cell cell) {return new double[]{};}

    protected Void cell2Void(Cell cell) { return null;}
    protected Object cell2Object(Cell cell) {return null;}
    protected Object[] cell2Objects(Cell cell) {return new Object[]{};}

    protected Boolean cell2Boolean(Cell cell) {
        if(isNull(cell)) { return null; }
        switch ( cell.getCellType()) {
            case NUMERIC: return null;
            case STRING: return "true".equalsIgnoreCase( cell.getStringCellValue().trim());
            case FORMULA:
            case BLANK: return null;
            case BOOLEAN: return cell.getBooleanCellValue();
            case ERROR:
            default: return null;
        }
    }
    protected Byte cell2Byte(Cell cell) {
        if(isNull(cell)) { return null; }
        switch (cell.getCellType()) {
            case NUMERIC: return (byte) cell.getNumericCellValue();
            case STRING:
            case FORMULA:
            case BLANK:
            case BOOLEAN:
            case ERROR:
            default: return null;
        }
    }
    protected Short cell2Short(Cell cell) {
        if(isNull(cell)) { return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return (short)cell.getNumericCellValue();
            case STRING:
            case FORMULA:
            case BLANK:
            case BOOLEAN:
            case ERROR:
            default: return null;
        }
    }
    protected Character cell2Character(Cell cell) { return null;}
    protected Integer cell2Integer(Cell cell) {
        if(isNull(cell)) { return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return (int)cell.getNumericCellValue();
            case STRING: return Ints.tryParse(cell.getStringCellValue());
            case FORMULA:
            case BLANK:
            case BOOLEAN:
            case ERROR:
            default: return null;
        }
    }
    protected Long cell2Long(Cell cell) {
        if(isNull(cell)) { return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return (long)cell.getNumericCellValue();
            case STRING: return Longs.tryParse(cell.getStringCellValue());
            case FORMULA:
            case BLANK:
            case BOOLEAN:
            case ERROR:
            default: return null;
        }
    }
    protected BigInteger cell2BigInteger(Cell cell) {
        if(isNull(cell)) { return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return BigInteger.valueOf((long)cell.getNumericCellValue());
            case STRING: return new BigInteger(cell.getStringCellValue());
            case FORMULA:
            case BLANK:
            case BOOLEAN:
            case ERROR:
            default: return null;
        }
    }
    protected Float cell2Float(Cell cell) {
        if(isNull(cell)) { return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return (float)cell.getNumericCellValue();
            case STRING: return Floats.tryParse(cell.getStringCellValue());
            case FORMULA:
            case BLANK:
            case BOOLEAN:
            case ERROR:
            default: return null;
        }
    }
    protected Double cell2Double(Cell cell) {
        if(isNull(cell)) { return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return cell.getNumericCellValue();
            case STRING: return Doubles.tryParse(cell.getStringCellValue());
            case FORMULA:
            case BLANK:
            case BOOLEAN:
            case ERROR:
            default: return null;
        }
    }
    protected BigDecimal cell2BigDecimal(Cell cell) {
        if(isNull(cell)) {return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING: return new BigDecimal(cell.getStringCellValue());
            case FORMULA:
            case BLANK:
            case BOOLEAN:
            case ERROR:
            default: return null;
        }
    }

    protected String cell2String(Cell cell) {
        if(isNull(cell)) {return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return Double.toString( cell.getNumericCellValue());
            case STRING: return cell.getStringCellValue();
            case FORMULA: return cell.getCellFormula();
            case BLANK: return null;
            case BOOLEAN: return Boolean.toString( cell.getBooleanCellValue());
            case ERROR: return FormulaError.forInt( cell.getErrorCellValue()).getString();
            default: return null;
        }
    }
    protected Date cell2Date(Cell cell) {
        if(isNull(cell)) {return null;}
        switch ( cell.getCellType()) {
            case NUMERIC:
            case STRING:
            case FORMULA:
            case BLANK:
            case BOOLEAN:
            case ERROR:
            default: return null;
        }
    }
    protected LocalDate cell2LocalDate(Cell cell) {
        if(isNull(cell)) {return null;}
        switch ( cell.getCellType()) {
            case NUMERIC:return null;
            case STRING: return LocalDate.parse( cell.getStringCellValue(), DATE_TIME_FORMATTER_DEFAULT);
            case FORMULA:
            case BLANK:
            case BOOLEAN:
            case ERROR:
            default: return null;
        }}
    protected Class<?> cell2Class(Cell cell) {
        if(isNull(cell)) {return null;}
        switch ( cell.getCellType()) {
            case NUMERIC: return null;
            case STRING: try { return ClassUtils.getClass( cell.getStringCellValue()); } catch ( ClassNotFoundException e) { log.error( e.getMessage(), e); return null;}
            case FORMULA:
            case BLANK:
            case BOOLEAN:
            case ERROR:
            default: return null;
        }
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER_DEFAULT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final ExcelTransformerRule DEFAULT_EXCEL_TRANSFORMER_RULE = new ExcelTransformerRule();
    private ExcelTransformerRule() { initSuperDefaultRule(); }
    private ExcelTransformerRule(Map<Class<?>,Function<Cell,?>> defaultTransformerRule) {
        super.setDefaultTransformerRule(defaultTransformerRule);
    }

    /**
     * 初始化对常用数据类型的转化规则
     */
    private void initSuperDefaultRule() {

        super.addDefaultRule4Transformer( boolean.class,    this::cell2boolean);
        super.addDefaultRule4Transformer( boolean[].class,  this::cell2booleans);
        super.addDefaultRule4Transformer( byte.class,       this::cell2byte);
        super.addDefaultRule4Transformer( byte[].class,     this::cell2bytes);
        super.addDefaultRule4Transformer( short.class,      this::cell2short);
        super.addDefaultRule4Transformer( short[].class,    this::cell2shorts);
        super.addDefaultRule4Transformer( char.class,       this::cell2char);
        super.addDefaultRule4Transformer( char[].class,     this::cell2chars);
        super.addDefaultRule4Transformer( int.class,        this::cell2int);
        super.addDefaultRule4Transformer( int[].class,      this::cell2ints);
        super.addDefaultRule4Transformer( long.class,       this::cell2long);
        super.addDefaultRule4Transformer( long[].class,     this::cell2longs);
        super.addDefaultRule4Transformer( float.class,      this::cell2float);
        super.addDefaultRule4Transformer( float[].class,    this::cell2floats);
        super.addDefaultRule4Transformer( double.class,     this::cell2double);
        super.addDefaultRule4Transformer( double[].class,   this::cell2doubles);

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
