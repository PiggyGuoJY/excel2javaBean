package com.guojy.parser.excel;

import com.guojy.model.Msg;
import com.guojy.parser.excel.rule.parse.ExcelParser;
import com.guojy.parser.excel.rule.parse.XlsExcelParser;
import com.guojy.parser.excel.rule.parse.XlsxExcelParser;
import com.guojy.parser.excel.rule.structure.annotation.handler.ExcelAnnotationHandler;
import com.guojy.parser.excel.rule.type.ExcelDataTypeTransformRule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.guojy.Assert.notNul;
import static com.guojy.Assert.notNull;
import static com.guojy.model.Msg.msg;
import static java.lang.String.format;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/10/30
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j
public class ExcelParserFactory {

    private static final String REGEX_EXCEL_EXT = "^.+\\.xlsx?$";
    private static final String FORMAT_XLS = "xls";
    private static final String FORMAT_XLSX = "xlsx";

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @return 描述返回值
     * */
    @SneakyThrows
    public static Msg<ExcelParser> createParser(Path srcPath) {
        String fileName;
        if ( !( notNull( srcPath) &&
                Files.exists( srcPath) &&
                Files.isRegularFile( srcPath) &&
                notNull( srcPath.getFileName()) &&
                notNul( fileName = srcPath.getFileName().toString()) &&
                fileName.matches( REGEX_EXCEL_EXT))) {
            return msg( new IllegalArgumentException(format( "入参为空, 路径不存在, 路径不是一个文件或不是xls(或xlsx)格式文件: [%s]", notNull(srcPath) ? srcPath.toString() : null)));
        }
        File srcFile = null;
        boolean getFileSuccessfully = true;
        try { srcFile = srcPath.toFile(); } catch ( Exception e) { log.error( e.getMessage(), e); getFileSuccessfully = false; }
        switch (FilenameUtils.getExtension(fileName)) {
            case FORMAT_XLS: return new Msg<>( getFileSuccessfully ?
                    new XlsExcelParser( srcFile, new ExcelAnnotationHandler(), new ExcelDataTypeTransformRule()) : new XlsExcelParser( srcPath, new ExcelAnnotationHandler(), new ExcelDataTypeTransformRule()));
            case FORMAT_XLSX: return new Msg<>( getFileSuccessfully ?
                    new XlsxExcelParser( srcFile, new ExcelAnnotationHandler(), new ExcelDataTypeTransformRule()) : new XlsxExcelParser( srcPath, new ExcelAnnotationHandler(), new ExcelDataTypeTransformRule()));
            default: return msg( new IllegalStateException(format( "不能识别的格式: [%s]", FilenameUtils.getExtension( fileName))));
        }
    }
}
