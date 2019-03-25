package com.github.piggyguojy.parser.excel.type;

import org.apache.poi.ss.usermodel.Cell;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;

/**
 * 基于Excel单元格类型的高级转换器
 * <p>
 *     本类提供了对常用类型的拓展转换规则(支持公式计算)
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
 *        <tr>
 *            <td>boolean</td>
 *            <td>false</td>
 *            <td>false</td>
 *            <td>false</td>
 *            <td>false</td>
 *            <td style="background-color:lightgreen">单元格值</td>
 *            <td>false</td>
 *            <td>false</td>
 *        </tr>
 *        <tr>
 *            <td>byte</td>
 *            <td style="background-color:lightgreen">按byte强制类型转换后的单元格值</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr>
 *            <td>short</td>
 *            <td style="background-color:lightgreen">按short强制类型转换后的单元格值</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr>
 *            <td>char</td>
 *            <td>\u0000</td>
 *            <td>\u0000</td>
 *            <td>\u0000</td>
 *            <td>\u0000</td>
 *            <td>\u0000</td>
 *            <td>\u0000</td>
 *            <td>\u0000</td>
 *        </tr>
 *        <tr>
 *            <td>int</td>
 *            <td style="background-color:lightgreen">按int强制类型转换后的单元格值</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr>
 *            <td>long</td>
 *            <td style="background-color:lightgreen">按long强制类型转换后的单元格值</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr>
 *            <td>float</td>
 *            <td style="background-color:lightgreen">按float强制类型转换后的单元格值</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr>
 *            <td>double</td>
 *            <td style="background-color:lightgreen">单元格值</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *            <td>0</td>
 *        </tr>
 *        <tr>
 *            <td>boolean[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *        </tr>
 *        <tr>
 *            <td>byte[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *        </tr>
 *        <tr>
 *            <td>short[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *        </tr>
 *        <tr>
 *            <td>char[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *        </tr>
 *        <tr>
 *            <td>int[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *        </tr>
 *        <tr>
 *            <td>long[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *        </tr>
 *        <tr>
 *            <td>float[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *        </tr>
 *        <tr>
 *            <td>double[]</td>
 *            <td>{}</td>
 *            <td>{}</td>
 *            <td>{}</td>
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
        return super.cell2Boolean(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Byte cell2Byte(Cell cell) {
        return super.cell2Byte(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Short cell2Short(Cell cell) {
        return super.cell2Short(cell);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Character cell2Character(Cell cell) {
        return super.cell2Character(cell);
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
