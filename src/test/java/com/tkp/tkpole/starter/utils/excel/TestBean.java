package com.tkp.tkpole.starter.utils.excel;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.tkp.tkpole.starter.utils.JsonXmlUtil;
import com.tkp.tkpole.starter.utils.model.Msg;
import com.tkp.tkpole.starter.utils.parser.excel.rule.parse.ExcelParser;
import com.tkp.tkpole.starter.utils.parser.excel.ExcelParserFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static com.tkp.tkpole.starter.utils.model.Msg.msg;
import static com.tkp.tkpole.starter.utils.parser.rule.structure.StructureHandler.VALUE_RETURNED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Slf4j
public class TestBean {

    private static final Path PATH_TEST_FILE = Paths.get( URI.create( "file:///E:/测试数据.xls"));
    private static final Path PATH_TEST_FILE2 = Paths.get( URI.create( "file:///E:/大量数据.xls"));
    private static ExcelParser excelParser;

    private FileSystem fileSystem;

    public static void main(String[] args) {
        //如果该方法仅作为测试使用, 请在开发完成后删掉(测试请在Junit下完成)
        log.info(Collection.class.isAssignableFrom(List.class)+"");
    }

    @BeforeClass
    public static void beforeClass() {
        Msg<ExcelParser> abstractExcelParserMsg = ExcelParserFactory.createParser( PATH_TEST_FILE);
        assertFalse( abstractExcelParserMsg.isException());
        excelParser = abstractExcelParserMsg.getT();
    }

    @Before
    public void before() throws IOException {
        fileSystem = Jimfs.newFileSystem(
                "liunx",
                Configuration
                        .unix()
                        .toBuilder()
                        .setRoots("/")
                        .setMaxSize(1024L*1024L*40)
                        .build()
        );
    }

    @Test
    public void test() {
        assertTrue( ExcelParserFactory.createParser( null).isException());
        assertTrue( ExcelParserFactory.createParser( Paths.get( URI.create( "file:///C:/helloWorld.txt"))).isException());
        assertTrue( ExcelParserFactory.createParser( Paths.get( URI.create( "file:///E:/spring.rar"))).isException());
    }

    @Test
    public void test2() throws IOException {
        Msg<ExcelBean> excelBeanMsg = excelParser
                .addCustomerDataTypeTransformRule(
                        String.class, Cell.class,
                        (Cell cell) -> {
                            switch ( cell.getCellType()) {
                                case STRING: return "["+cell.getStringCellValue()+"]";
                                case NUMERIC: return Integer.valueOf( (int)cell.getNumericCellValue()).toString();
                                default: return null;
                            }
                        })
                .parse( ExcelBean.class);
        assertFalse(excelBeanMsg.isException());
        log.info( excelBeanMsg.getT().toString());
    }

    @Test
    public void test3() throws IOException {
        Path path = fileSystem.getPath("/test");
        if (!Files.exists( path)) { path = Files.createDirectory( path); }
        path = path.resolve("t.xls");
        path = Files.write(
                path,
                IOUtils.toByteArray(Files.newInputStream(PATH_TEST_FILE)));
        Msg<ExcelParser> abstractExcelParserMsg = ExcelParserFactory.createParser( path);
        assertFalse( abstractExcelParserMsg.isException());
        try (
                ExcelParser excelParser = abstractExcelParserMsg.getT()
        ) {
            Msg<ExcelBean> excelBeanMsg = excelParser.parse( ExcelBean.class);
            assertFalse( excelBeanMsg.isException());
            log.info( excelBeanMsg.getT().toString());
        }
    }

    @Test
    public void test4() throws IOException, InterruptedException {
            Msg<ExcelParser> abstractExcelParserMsg = ExcelParserFactory.createParser( PATH_TEST_FILE2);
            assertFalse( abstractExcelParserMsg.isException());
            ExcelParser abstractExcelParser = abstractExcelParserMsg.getT();
            Msg<?> piBeanMsg = abstractExcelParser.parse(PIBean.class);
            assertFalse(piBeanMsg.isException());
            log.info(JsonXmlUtil.javaBean2Json(piBeanMsg.getT()));
            ((PIBean)piBeanMsg.getT()).getRealPiBeanList().parallelStream().filter(realPiBean -> Integer.parseInt(realPiBean.getIdNo())/2==0).forEach(realPiBean -> log.info(realPiBean.toString()));

    }

//    @Test
//    public void test5() throws IOException, InterruptedException {
//        Msg<AbstractExcelParser> abstractExcelParserMsg = ExcelParserFactory.createParser( PATH_TEST_FILE2);
//        assertFalse( abstractExcelParserMsg.isException());
//        AbstractExcelParser abstractExcelParser = abstractExcelParserMsg.getT();
//        Msg<List<RealPiBean>> piBeanMsg = abstractExcelParser.parser(RealPiBean.class);
//        abstractExcelParser.close();
//        assertFalse(piBeanMsg.isException());
//        log.info(JsonXmlUtil.javaBean2Json(piBeanMsg.getT()));
//        piBeanMsg.getT().parallelStream().filter(realPiBean -> Integer.parseInt(realPiBean.getIdNo())/2==0).forEach(realPiBean -> log.info(realPiBean.toString()));
//    }

//    @Test
//    public void test6() throws IOException {
//        Msg<ExcelBean> excelBeanMsg = null;
//        abstractExcelParser
//                .addCustomRule(
//                        String.class,
//                        cell -> {
//                            switch ( cell.getCellType()) {
//                                case STRING: return "["+cell.getStringCellValue()+"]";
//                                case NUMERIC: return Integer.valueOf( (int)cell.getNumericCellValue()).toString();
//                                default: return null;
//                            }
//                        });
//        excelBeanMsg = ExcelAnnotationHandler.boot(ExcelBean.class, abstractExcelParser);
//        abstractExcelParser.close();
//        assertFalse(excelBeanMsg.isException());
//        log.info( excelBeanMsg.getT().toString());
//    }
}
