package com.github.piggyguojy.parser.excel.rule.type;

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
 *    </table>
 *
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 *
 * */
public class ExcelTransformerRuleTypeAdvanced
        extends ExcelTransformerRulePrimitiveSupported {

    @Override
    protected Boolean cell2Boolean(Cell cell) {
        return super.cell2Boolean(cell);
    }

    @Override
    protected Byte cell2Byte(Cell cell) {
        return super.cell2Byte(cell);
    }

    @Override
    protected Short cell2Short(Cell cell) {
        return super.cell2Short(cell);
    }

    @Override
    protected Character cell2Character(Cell cell) {
        return super.cell2Character(cell);
    }

    @Override
    protected Integer cell2Integer(Cell cell) {
        return super.cell2Integer(cell);
    }

    @Override
    protected Long cell2Long(Cell cell) {
        return super.cell2Long(cell);
    }

    @Override
    protected BigInteger cell2BigInteger(Cell cell) {
        return super.cell2BigInteger(cell);
    }

    @Override
    protected Float cell2Float(Cell cell) {
        return super.cell2Float(cell);
    }

    @Override
    protected Double cell2Double(Cell cell) {
        return super.cell2Double(cell);
    }

    @Override
    protected BigDecimal cell2BigDecimal(Cell cell) {
        return super.cell2BigDecimal(cell);
    }

    @Override
    protected String cell2String(Cell cell) {
        return super.cell2String(cell);
    }

    @Override
    protected Date cell2Date(Cell cell) {
        return super.cell2Date(cell);
    }

    @Override
    protected LocalDate cell2LocalDate(Cell cell) {
        return super.cell2LocalDate(cell);
    }

    @Override
    protected Class<?> cell2Class(Cell cell) {
        return super.cell2Class(cell);
    }

    @Override
    protected Void cell2Void(Cell cell) {
        return super.cell2Void(cell);
    }

    @Override
    protected Object cell2Object(Cell cell) {
        return super.cell2Object(cell);
    }

    @Override
    protected Object[] cell2Objects(Cell cell) {
        return super.cell2Objects(cell);
    }
}
