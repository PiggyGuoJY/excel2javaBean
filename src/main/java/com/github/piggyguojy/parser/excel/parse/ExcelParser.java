
package com.github.piggyguojy.parser.excel.parse;

import com.github.piggyguojy.parser.rule.parse.DefaultParser;
import com.github.piggyguojy.parser.rule.structure.StructureHandler;
import com.github.piggyguojy.parser.rule.type.TransformableAndRuleAddable;
import com.github.piggyguojy.util.Msg;
import com.google.common.math.IntMath;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static com.github.piggyguojy.util.Assert.*;


/**
 * Excel解析器
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExcelParser
        extends DefaultParser<ExcelParser>
        implements AutoCloseable {

    public static class ExcelParserHelper {
        public static int decideSheetNo(int parent, int son) {
            return son >= 0 ? son : ( parent>=0 ? parent : 0);
        }
        public static String decideSheetName( String parent,  String son) {
            return notNul(son) ? son : ( notNul(parent) ? parent : "");
        }
        public static Sheet decideSheet(int sheetNo, String sheetName, Workbook workbook) {
            if ( isNull(workbook)) { return null;}
            Sheet sheet;
            if ( sheetNo<0||sheetNo>workbook.getNumberOfSheets()) {
                return workbook.getSheet(sheetName);
            } else {
                return workbook.getSheetAt(sheetNo-1);
            }
        }
        public static int decideColumnNo( String columnNameOrNo) {
            if ( columnNameOrNo.matches("^\\d+$")) {
                return decideColumnNo( "", Integer.parseInt(columnNameOrNo));
            } else {
                return decideColumnNo( columnNameOrNo, -1);
            }
        }
        public static int decideColumnNo( String columnName, int columnNo) {
            int decimal = -1;
            return ( decimal = alphabet2decimal( columnName))<0 ? ( columnNo<1 ? 1 : columnNo) : decimal;
        }
        private static int alphabet2decimal( String str) {
            if ( notNul( str) && str.matches( "^[a-zA-Z]+$")) {
                int dec = 0;
                char[] chars = str.toUpperCase().toCharArray();
                for ( int i=1, length=chars.length; i<=length; i++) {
                    dec += ( chars[length-i] - 'A' + 1) * IntMath.pow(26, i-1);
                }
                return dec;
            }
            return -1;
        }
        public static Cell decideCell(int columnNo, int rowNo, Sheet sheet) {
            if ( isNull( sheet)) { return null; }
            Row row  = sheet.getRow( rowNo<1 ? 0 : rowNo-1);
            if ( isNull( row)) { return null; }
            return row.getCell( decideColumnNo( "", columnNo)-1);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        if ( notNull( workbook)) { workbook.close(); this.workbook = null; }
        if ( notNul( file)) { file = null;}
        if ( notNull( inputStream)) { inputStream.close(); this.inputStream = null; }
        if ( notNull( path)) { this.path = null; }
    }



    /**
     * {@inheritDoc}
     */
    @Override @SuppressWarnings("unchecked")
    protected <T> Msg<T> afterParse(Params params) {
        try {
            params.<ExcelParser>getParser().close();
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return (Msg<T>)params.getReturnMsg();
    }
    protected ExcelParser(
            final Path path,
            final StructureHandler<ExcelParser> structureHandler,
            final TransformableAndRuleAddable abstractDataTypeTransformerRule
    ) {
        super(structureHandler, abstractDataTypeTransformerRule);
        this.path = path;
        try {
            this.inputStream = Files.newInputStream( path, StandardOpenOption.READ);
            this.workbook = WorkbookFactory.create( this.inputStream, null);
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            this.workbook = null; try { this.close();} catch ( IOException e2) { log.error( e2.getMessage(), e2); this.inputStream = null;}
        }
    }
    protected ExcelParser(
            final File file,
            final StructureHandler<ExcelParser> structureHandler,
            final TransformableAndRuleAddable abstractDataTypeTransformerRule
    ) {
        super(structureHandler, abstractDataTypeTransformerRule);
        this.file = file;
        try {
            this.workbook = WorkbookFactory.create( this.file);
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            this.workbook = null; this.file = null;
        }
    }



    /**
     * 支持使用Path来初始化Workbook
     * */
    private Path path = null;
    private InputStream inputStream = null;
    /**
     * 全局持有的Workbook, 不要主动关闭, 使用close()进行关闭
     * */
    @Getter
    private Workbook workbook = null;
    /**
     * 支持使用文件对象初始化Workbook
     * */
    private File file = null;
}
