/* Copyright (c) 2019, Guo Jinyang. All rights reserved. */
package com.github.piggyguojy.parser.excel;

import com.github.piggyguojy.JsonUtil;
import com.github.piggyguojy.Msg;
import com.github.piggyguojy.model.test.StudentRecordTable;
import com.github.piggyguojy.parser.excel.rule.parse.ExcelParser;
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

}