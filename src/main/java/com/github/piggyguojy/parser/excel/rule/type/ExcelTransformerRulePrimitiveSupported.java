package com.github.piggyguojy.parser.excel.rule.type;

import com.github.piggyguojy.parser.rule.type.AbstractTransformerRule4SingleDataType;
import org.apache.poi.ss.usermodel.Cell;

import static com.github.piggyguojy.Assert.isNull;

/**
 * 基于Excel单元格类型且支持Java原生类型的转换器
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
 *        <tr>
 *            <td>char</td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *        </tr>
 *        <tr>
 *            <td>int</td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *        </tr>
 *        <tr>
 *            <td>long</td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *        </tr>
 *        <tr>
 *            <td>float</td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *        </tr>
 *        <tr>
 *            <td>double</td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *        </tr>
 *        <tr>
 *            <td>boolean[]</td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *        </tr>
 *        <tr>
 *            <td>byte[]</td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *        </tr>
 *        <tr>
 *            <td>short[]</td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *        </tr>
 *        <tr>
 *            <td>char[]</td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *        </tr>
 *        <tr>
 *            <td>int[]</td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *        </tr>
 *        <tr>
 *            <td>long[]</td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *        </tr>
 *        <tr>
 *            <td>float[]</td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *        </tr>
 *        <tr>
 *            <td>double[]</td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *            <td></td>
 *        </tr>
 *    </table>
 *
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 *
 *
 * @see AbstractTransformerRule4SingleDataType
 * */
public class ExcelTransformerRulePrimitiveSupported
        extends ExcelTransformerRule {

    /**
     * 获取一个支持Java原生类型的转换器
     * @return 转换器
     */
    public static ExcelTransformerRulePrimitiveSupported of() {
        return DEFALUT_EXCEL_TRANSFORMER_RULE_PRIMITIVE_SUPPORTED;
    }



    // 对基本数据类型其数组类型的默认转换, 可以通过继承该类进行规则改写; 对数组类型, 默认不支持转换;

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
    /**
     * 生效父类配置的默认构造器
     */
    protected ExcelTransformerRulePrimitiveSupported() {
        super();
        this.initSuperDefaultRule();
    }



    /**
     * 单例化一个转换器
     */
    private static final ExcelTransformerRulePrimitiveSupported DEFALUT_EXCEL_TRANSFORMER_RULE_PRIMITIVE_SUPPORTED
            = new ExcelTransformerRulePrimitiveSupported();
    /**
     * 初始化默认规则
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
    }
}
