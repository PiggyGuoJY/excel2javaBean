package com.github.piggyguojy.parser.excel;

import com.github.piggyguojy.model.Msg;
import com.github.piggyguojy.parser.excel.rule.parse.ExcelParser;
import com.github.piggyguojy.parser.excel.rule.parse.XlsExcelParser;
import com.github.piggyguojy.parser.excel.rule.parse.XlsxExcelParser;
import com.github.piggyguojy.parser.excel.rule.structure.annotation.handler.ExcelAnnotationHandler;
import com.github.piggyguojy.parser.excel.rule.type.ExcelTransformerRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.piggyguojy.Assert.notNul;
import static com.github.piggyguojy.Assert.notNull;
import static com.github.piggyguojy.model.Msg.msg;
import static java.lang.String.format;

/**
 * Excel解析器的构造工厂
 *
 * <p> 创建时间：2018/10/30
 *
 * @author guojy
 * @version 1.0
 * */
@Slf4j
public class ExcelParserFactory {

    /**
     * <p> 程序员（guojy）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @return 描述返回值
     * */
    public static Msg<ExcelParser> createParser(Path srcPath) {
        String fileName;
        if ( !( notNull( srcPath) &&
                Files.exists( srcPath) &&
                Files.isRegularFile( srcPath) &&
                notNull( srcPath.getFileName()) &&
                notNul( fileName = srcPath.getFileName().toString()))) {
            return msg( new IllegalArgumentException(format( "入参为空, 路径不存在, 路径不是一个文件或不是xls(或xlsx)格式文件: [%s]", notNull(srcPath) ? srcPath.toString() : null)));
        }
        File srcFile = null;
        boolean getFileSuccessfully = true;
        try { srcFile = srcPath.toFile(); } catch ( Exception e) { log.error( e.getMessage(), e); getFileSuccessfully = false; }
        switch (FilenameUtils.getExtension(fileName).toLowerCase()) {
            case "xls":
                return new Msg<>( getFileSuccessfully ?
                        new XlsExcelParser( srcFile, new ExcelAnnotationHandler(), ExcelTransformerRule.of()) :
                        new XlsExcelParser( srcPath, new ExcelAnnotationHandler(), ExcelTransformerRule.of()));
            case "xlsx":
                return new Msg<>( getFileSuccessfully ?
                        new XlsxExcelParser( srcFile, new ExcelAnnotationHandler(), ExcelTransformerRule.of()) :
                        new XlsxExcelParser( srcPath, new ExcelAnnotationHandler(), ExcelTransformerRule.of()));
            default:
                return msg( new IllegalStateException(format(
                        "不受解析器 %s 支持的文件格式 %s",
                        ExcelParserFactory.class.getSimpleName(),
                        FilenameUtils.getExtension(fileName))));
        }
    }
}
