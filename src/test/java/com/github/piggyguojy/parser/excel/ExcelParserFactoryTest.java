
package com.github.piggyguojy.parser.excel;

import com.github.piggyguojy.model.test.BasicTypeClass;
import com.github.piggyguojy.model.test.CensusMetaData;
import com.github.piggyguojy.model.test.StudentRecordTable;
import com.github.piggyguojy.model.test.StudentRecordTable2;
import com.github.piggyguojy.parser.excel.parse.ExcelParser;
import com.github.piggyguojy.util.JsonUtil;
import com.github.piggyguojy.util.Msg;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;

@Slf4j
public class ExcelParserFactoryTest {

    private static Path TEST_FILE;

    @BeforeClass
    @SneakyThrows
    public static void beforeOnce() {
        TEST_FILE = Paths.get(ExcelParserFactoryTest.class.getResource("/simple/TestExcelFile.xlsx").toURI());
    }

    @Test
    public void test() {
        Msg<ExcelParser> excelParserMsg = ExcelParserFactory.createParser(TEST_FILE);
        assertFalse(excelParserMsg.isException());
        ExcelParser excelParser = excelParserMsg.getT();
        Msg<StudentRecordTable> studentRecordTableMsg = excelParser.parse(StudentRecordTable.class);
        assertFalse(studentRecordTableMsg.isException());
        log.info(JsonUtil.javaBean2Json(studentRecordTableMsg.getT()));
    }

    @Test
    public void test2() {
        Msg<ExcelParser> excelParserMsg = ExcelParserFactory.createParser(TEST_FILE);
        assertFalse(excelParserMsg.isException());
        ExcelParser excelParser = excelParserMsg.getT();
        Msg<StudentRecordTable2> studentRecordTable2Msg = excelParser.parse(StudentRecordTable2.class);
        assertFalse(studentRecordTable2Msg.isException());
        log.info(JsonUtil.javaBean2Json(studentRecordTable2Msg.getT()));
    }

    @Test
    public void test3() {
        Msg<ExcelParser> excelParserMsg = ExcelParserFactory.createParser(TEST_FILE);
        assertFalse(excelParserMsg.isException());
        ExcelParser excelParser = excelParserMsg.getT();
        Msg<CensusMetaData> censusMetaDataMsg = excelParser.parse(CensusMetaData.class);
        assertFalse(censusMetaDataMsg.isException());
        log.info(JsonUtil.javaBean2Json(censusMetaDataMsg.getT()));
    }

    @Test
    public void test4() {
        Msg<ExcelParser> excelParserMsg = ExcelParserFactory.createParser(TEST_FILE);
        assertFalse(excelParserMsg.isException());
        ExcelParser excelParser = excelParserMsg.getT();
        Msg<BasicTypeClass> basicTypeClassMsg = excelParser.parse(BasicTypeClass.class);
        assertFalse(basicTypeClassMsg.isException());
        log.info(JsonUtil.javaBean2Json(basicTypeClassMsg.getT()));
    }
}