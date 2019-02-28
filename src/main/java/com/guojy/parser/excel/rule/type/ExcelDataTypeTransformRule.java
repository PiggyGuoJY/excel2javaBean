package com.guojy.parser.excel.rule.type;


import com.guojy.parser.rule.type.AbstractSingleDataTypeTransformerRule;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 基于Excel单元格类型的类型转换类
 *
 * <p> 创建时间：2018/11/10
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor
public final class ExcelDataTypeTransformRule extends AbstractSingleDataTypeTransformerRule<Cell> {
    /**
     * 默认的时间转换格式
     * */
    private static final DateTimeFormatter DATE_TIME_FORMATTER_DEFAULT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    // 默认的类转换器
    {
        super.addDefaultDataTypeTransformRule( String.class, cell -> {
            switch ( cell.getCellType()) {
                case BLANK: return "";
                case STRING: return cell.getStringCellValue();
                case NUMERIC: return Double.toString( cell.getNumericCellValue());
                case BOOLEAN: return Boolean.toString( cell.getBooleanCellValue());
                case FORMULA: return cell.getCellFormula();
                case ERROR: return FormulaError.forInt( cell.getErrorCellValue()).getString();
                default: return null;
            }});
        super.addDefaultDataTypeTransformRule( Integer.class, cell -> {
            switch ( cell.getCellType()) {
                case BLANK: return 0;
                case STRING: try { return Integer.valueOf( cell.getStringCellValue());} catch ( NumberFormatException e) { log.error( e.getMessage(), e); return null;}
                case NUMERIC: log.warn("在转换中可能会有精度损失"); return (int)cell.getNumericCellValue();
                // 这个地方0表示false, 非0表示true(这里先用1来表示非0)
                case BOOLEAN: return cell.getBooleanCellValue() ? 1 : 0;
                case FORMULA: try { return (int)cell.getNumericCellValue(); } catch ( NumberFormatException e) { log.error( e.getMessage(), e); return null; }
                case ERROR: log.warn( FormulaError.forInt( cell.getErrorCellValue()).getString()); try { return (int)cell.getNumericCellValue(); } catch ( NumberFormatException e) { log.error( e.getMessage(), e); return null; }
                default: return null;
            }});
        super.addDefaultDataTypeTransformRule( Double.class, cell -> {
            switch ( cell.getCellType()) {
                case BLANK: return 0D;
                case STRING: try { return Double.valueOf( cell.getStringCellValue()); } catch ( NumberFormatException e) { log.error( e.getMessage(), e); return null;}
                case NUMERIC: return cell.getNumericCellValue();
                // 这个地方0表示false, 非0表示true(这里先用1来表示非0)
                case BOOLEAN: return cell.getBooleanCellValue() ? 1D : 0D;
                case FORMULA: try { return cell.getNumericCellValue(); } catch ( NumberFormatException e) { log.error( e.getMessage(), e); return null; }
                case ERROR: log.warn( FormulaError.forInt( cell.getErrorCellValue()).getString()); try { return cell.getNumericCellValue(); } catch ( NumberFormatException e) { log.error( e.getMessage(), e); return null; }
                default: return null;
            }});
        super.addDefaultDataTypeTransformRule( Boolean.class, cell -> {
            switch ( cell.getCellType()) {
                case STRING: return "true".equalsIgnoreCase( cell.getStringCellValue().trim());
                case NUMERIC: return cell.getNumericCellValue()!=0;
                case BOOLEAN:
                case FORMULA: return cell.getBooleanCellValue();
                case BLANK:
                case ERROR: return false;
                default: return null;
            }});
        super.addDefaultDataTypeTransformRule( LocalDate.class, cell -> {
            switch ( cell.getCellType()) {
                case STRING: return LocalDate.parse( cell.getStringCellValue(), DATE_TIME_FORMATTER_DEFAULT);
                case NUMERIC:
                case FORMULA: try{ return LocalDateTime.ofInstant( cell.getDateCellValue().toInstant(), ZoneId.systemDefault()).toLocalDate(); } catch ( NumberFormatException e) { log.error( e.getMessage(), e); return null;}
                case BOOLEAN:
                case BLANK:
                case ERROR:
                default: return null;
            }});
        super.addDefaultDataTypeTransformRule( Class.class, cell -> {
            switch ( cell.getCellType()) {
                case STRING:
                case FORMULA: try { return Class.forName( cell.getStringCellValue()); } catch ( ClassNotFoundException e) { log.error( e.getMessage(), e); return null;}
                case NUMERIC:
                case BOOLEAN:
                case BLANK:
                case ERROR:
                default: return null;
            }});
    }
}
