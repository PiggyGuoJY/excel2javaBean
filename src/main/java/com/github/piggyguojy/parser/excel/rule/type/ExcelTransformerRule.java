/* Copyright (c) 2019, Guo Jinyang. All rights reserved. */
package com.github.piggyguojy.parser.excel.rule.type;


import com.github.piggyguojy.Assert;
import com.github.piggyguojy.parser.rule.type.AbstractTransformerRule4SingleDataType;
import com.sun.corba.se.impl.ior.OldJIDLObjectKeyTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static com.github.piggyguojy.Assert.isNull;
import static com.github.piggyguojy.Assert.notNull;

/**
 * 基于Excel单元格类型的类型转换类
 *
 * todo ... 还得完善基本数据类型和其数组类型; 公式类型得支持计算
 *
 * <p> 创建时间：2018/11/10
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
@Slf4j
public class ExcelTransformerRule
        extends AbstractTransformerRule4SingleDataType<Cell> {

    public static ExcelTransformerRule of() {
        return new ExcelTransformerRule(DEFAULT_EXCEL_TRANSFORMER_RULE.getDefaultTransformerRule());
    }

    // 对基本数据类型,void和Object及其数组类型的默认转换, 可以通过继承该类进行规则改写

    protected boolean cell2boolean(Cell cell) {
        return this.cell2Boolean(cell);
    }
    protected boolean[] cell2booleans(Cell cell) { return new boolean[]{};}
    protected byte cell2byte(Cell cell) { return (byte) this.cell2Byte(cell);}
    protected byte[] cell2bytes(Cell cell) { return new byte[]{};}
    protected short cell2short(Cell cell) { return (short)this.cell2Short(cell);}
    protected short[] cell2shorts(Cell cell) { return new short[]{};}
    protected char cell2char(Cell cell)  { return this.cell2Character(cell);}
    //todo ... 这里之后优化
    protected char[] cell2chars(Cell cell) { return new char[]{};}
    protected int cell2int(Cell cell) { return (int)this.cell2Integer(cell);}
    protected int[] cell2ints(Cell cell) { return new int[]{};}
    protected long cell2long(Cell cell) { return (long)this.cell2Long(cell);}
    protected long[] cell2longs(Cell cell) { return new long[]{};}
    protected float cell2float(Cell cell) { return this.cell2Float(cell);}
    protected float[] cell2floats(Cell cell) { return new float[]{};}
    protected double cell2double(Cell cell) { return (double) this.cell2Double(cell);}
    protected double[] cell2doubles(Cell cell) {return new double[]{};}

    //todo ... 这个地方就是用于什么都不产生的,可以做一些注重过程的事情; 下面的还没想好
    protected Void cell2Void(Cell cell) { return null;}
    protected Object cell2Object(Cell cell) {return null;}
    protected Object[] cell2Objects(Cell cell) {return null;}
    // 对常用数据类型的默认转换, 可以通过继承该类进行规则改写

    protected Boolean cell2Boolean(Cell cell) {
        if(isNull(cell)) {
            return false;
        }
        switch ( cell.getCellType()) {
            // 这里参考C语言的规定
            case NUMERIC: return cell.getNumericCellValue()!=0;
            case STRING: return "true".equalsIgnoreCase( cell.getStringCellValue().trim());
            // todo ... 之后加上对公式的支持
            case FORMULA:
            case BLANK: return false;
            case BOOLEAN: return cell.getBooleanCellValue();
            case ERROR:
            default: return false;
        }
    }
    protected Byte cell2Byte(Cell cell) {
        if(isNull(cell)) {
            return (byte)0;
        }
        switch (cell.getCellType()) {
            case NUMERIC:break;
            case STRING:break;
            case FORMULA:break;
            case BLANK:break;
            case BOOLEAN:break;
            case ERROR:break;
            default:break;
        }
        return null;
    }
    protected Short cell2Short(Cell cell) { return 0;}
    protected Character cell2Character(Cell cell) { return '\u0000';}
    protected Integer cell2Integer(Cell cell) {
        if(isNull(cell)) {return 0;}
        switch ( cell.getCellType()) {
            case BLANK: return 0;
            case STRING: try { return Integer.valueOf( cell.getStringCellValue());} catch ( NumberFormatException e) { log.error( e.getMessage(), e); return null;}
            case NUMERIC: log.warn("在转换中可能会有精度损失"); return (int)cell.getNumericCellValue();
            // 这个地方0表示false, 非0表示true(这里先用1来表示非0)
            case BOOLEAN: return cell.getBooleanCellValue() ? 1 : 0;
            case FORMULA: try { return (int)cell.getNumericCellValue(); } catch ( NumberFormatException e) { log.error( e.getMessage(), e); return null; }
            case ERROR: log.warn( FormulaError.forInt( cell.getErrorCellValue()).getString()); try { return (int)cell.getNumericCellValue(); } catch ( NumberFormatException e) { log.error( e.getMessage(), e); return null; }
            default: return null;
        }
    }
    protected Long cell2Long(Cell cell) { return 0L;}
    protected BigInteger cell2BigInteger(Cell cell) {
        return null;
    }
    protected Float cell2Float(Cell cell) {
        return 0F;
    }
    protected Double cell2Double(Cell cell) {
        if(isNull(cell)) {return 0D;}
        switch ( cell.getCellType()) {
            case BLANK: return 0D;
            case STRING: try { return Double.valueOf( cell.getStringCellValue()); } catch ( NumberFormatException e) { log.error( e.getMessage(), e); return null;}
            case NUMERIC: return cell.getNumericCellValue();
            // 这个地方0表示false, 非0表示true(这里先用1来表示非0)
            case BOOLEAN: return cell.getBooleanCellValue() ? 1D : 0D;
            case FORMULA: try { return cell.getNumericCellValue(); } catch ( NumberFormatException e) { log.error( e.getMessage(), e); return null; }
            case ERROR: log.warn( FormulaError.forInt( cell.getErrorCellValue()).getString()); try { return cell.getNumericCellValue(); } catch ( NumberFormatException e) { log.error( e.getMessage(), e); return null; }
            default: return null;
        }
    }
    protected BigDecimal cell2BigDecimal(Cell cell) {
        return null;
    }

    protected String cell2String(Cell cell) {
        if(isNull(cell)) {return DEFAULT_STRING;}
        switch ( cell.getCellType()) {
            case BLANK: return DEFAULT_STRING;
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return Double.toString( cell.getNumericCellValue());
            case BOOLEAN: return Boolean.toString( cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            case ERROR: return FormulaError.forInt( cell.getErrorCellValue()).getString();
            default: return DEFAULT_STRING;
        }
    }
    protected Date cell2Date(Cell cell) { return null;}
    protected LocalDate cell2LocalDate(Cell cell) {
        if(isNull(cell)) {return null;}
        switch ( cell.getCellType()) {
            case STRING: return LocalDate.parse( cell.getStringCellValue(), DATE_TIME_FORMATTER_DEFAULT);
            case NUMERIC:
            case FORMULA: try{ return LocalDateTime.ofInstant( cell.getDateCellValue().toInstant(), ZoneId.systemDefault()).toLocalDate(); } catch ( NumberFormatException e) { log.error( e.getMessage(), e); return null;}
            case BOOLEAN:
            case BLANK:
            case ERROR:
            default: return null;
        }}
    protected Cell cell2cell(Cell cell) { return cell;}
    protected Enum<?> cell2Enum(Cell cell) {
        return null;
    }
    protected Class<?> cell2Class(Cell cell) {
        if(isNull(cell)) {return null;}
        switch ( cell.getCellType()) {
            case STRING:
            case FORMULA: try { return ClassUtils.getClass( cell.getStringCellValue()); } catch ( ClassNotFoundException e) { log.error( e.getMessage(), e); return null;}
            case NUMERIC:
            case BOOLEAN:
            case BLANK:
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
    private static final String DEFAULT_STRING = null;

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
        super.addDefaultRule4Transformer( Enum.class,       this::cell2Enum);
        super.addDefaultRule4Transformer( Class.class,      this::cell2Class);
    }
}
