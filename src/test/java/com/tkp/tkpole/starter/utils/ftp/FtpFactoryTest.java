package com.tkp.tkpole.starter.utils.ftp;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.jimfs.Jimfs;
import com.tkp.tkpole.starter.utils.ftp.model.FtpConfigData;
import com.tkp.tkpole.starter.utils.ftp.model.FtpMetaConfigData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.function.Supplier;

@Slf4j
public class FtpFactoryTest {

    @Before
    public void setUp() throws Exception {
        FtpConfigData ftpConfigData = new FtpConfigData();
        ftpConfigData.setAutoConfig( true);
        ftpConfigData.setFtpList(
                Arrays.asList(
                        new FtpMetaConfigData(
                                "testUseNewUatBack",
                                "测试案例(使用传测试新后台)",
                                "10.137.126.21",
                                22,
                                "tomcat",
                                "TKtom8@015",
                                "sftp"),
                        new FtpMetaConfigData(
                                "testUsePreUatBack",
                                "测试案例(使用传测试新后台)",
                                "10.137.126.12",
                                21,
                                "wasadmin",
                                "wasadmin",
                                "ftp"),
                        new FtpMetaConfigData(
                                "testUseUatBack",
                                "测试案例(使用测试后台)",
                                "10.137.126.11",
                                21,
                                "wasadmin",
                                "wasadmin",
                                "ftp"
                        )));

        FtpFactory ftpFactory = new FtpFactory( ftpConfigData);
        ftpAccessibleUseSftp = ftpFactory.getFtpAccessibleByName( "testUseNewUatBack").getT();
        Assert.assertNotNull( ftpAccessibleUseSftp);
        ftpAccessibleUseFtp = ftpFactory.getFtpAccessibleByName( "testUsePreUatBack").getT();
        Assert.assertNotNull( ftpAccessibleUseFtp);
        fileSystem = Jimfs.newFileSystem("win");
        Path path = fileSystem.getPath("C:/test/");
        Files.createDirectory( path);
    }

    @Test
    public void test() {
        Assert.assertFalse( ftpAccessibleUseSftp.upload(
                Paths.get( URI.create( "file:///E:/ab2.exe")),
                Paths.get( URI.create( "file:///home/tomcat/test/"))).getT());
        Assert.assertTrue( ftpAccessibleUseSftp.upload(
                Paths.get( URI.create( "file:///E:/ab2.exe")),
                Paths.get( URI.create( "file:///home/tomcat/test/")),
                "--createFlagFile").getT());
        Assert.assertFalse( ftpAccessibleUseSftp.upload(
                Paths.get( URI.create( "file:///E:/ab2.exe")),
                Paths.get( URI.create( "file:///home/tomcat/test/")),
                "--createFlagFile", "com.tkp.tkpole.starter.utils.ftp.ftpAccessibleUseSftp").getT());
        Assert.assertTrue( ftpAccessibleUseSftp.upload(
                Paths.get( URI.create( "file:///E:/ab2.exe")),
                Paths.get( URI.create( "file:///home/tomcat/test/")),
                "--createFlagFile", "com.tkp.tkpole.starter.utils.ftp.SupplierTest").getT());
    }
    @Test
    public void test2() {
        Assert.assertFalse( ftpAccessibleUseSftp.upload(
                Paths.get( URI.create( "file:///E:/ab.exe")),
                Paths.get( URI.create( "file:///home/tomcat/test2/"))).getT());
        Assert.assertTrue( ftpAccessibleUseSftp.upload(
                Paths.get( URI.create( "file:///E:/ab.exe")),
                Paths.get( URI.create( "file:///home/tomcat/test2/")),
                "--createDirectory").getT());
    }
    @Test
    public void test3() {
        Assert.assertTrue( ftpAccessibleUseSftp.delete(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp.txt")),
                "--noneIsOk").getT());
        Assert.assertTrue( ftpAccessibleUseSftp.upload(
                Paths.get( URI.create( "file:///E:/test4ftp.txt")),
                Paths.get( URI.create( "file:///home/tomcat/test/")),
                "--createDirectory").getT());
        Assert.assertFalse( ftpAccessibleUseSftp.upload(
                Paths.get( URI.create( "file:///E:/test4ftp.txt")),
                Paths.get( URI.create( "file:///home/tomcat/test/"))).getT());
//        Assert.assertFalse( ftpAccessibleUseSftp.upload(
//                Paths.get( URI.create( "file:///E:/test4ftp.txt")),
//                Paths.get( URI.create( "file:///home/tomcat/test/")),
//                "--overwrite"));
        Assert.assertTrue( ftpAccessibleUseSftp.upload(
                Paths.get( URI.create( "file:///E:/test4ftp.txt")),
                Paths.get( URI.create( "file:///home/tomcat/test/")),
                "--overwrite", "append").getT());
        Assert.assertTrue( ftpAccessibleUseSftp.upload(
                Paths.get( URI.create( "file:///E:/test4ftp.txt")),
                Paths.get( URI.create( "file:///home/tomcat/test/")),
                "--overwrite", "overwrite").getT());
        Assert.assertTrue( ftpAccessibleUseSftp.delete(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp.txt"))).getT());
        Assert.assertFalse( ftpAccessibleUseSftp.delete(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp.txt"))).getT());
        Assert.assertTrue( ftpAccessibleUseSftp.delete(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp.txt")),
                "--noneIsOk").getT());
        Assert.assertTrue( ftpAccessibleUseSftp.delete(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp.txt")),
                "--isA", "file", "--noneIsOk").getT());
        Assert.assertTrue( ftpAccessibleUseSftp.delete(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp.txt")),
                "--isA", "directory", "--noneIsOk").getT());
    }
    @Test
    public void test4() {
        Assert.assertTrue( ftpAccessibleUseSftp.delete(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp.txt")),
                "--noneIsOk").getT());
        Assert.assertTrue( ftpAccessibleUseSftp.upload(
                Paths.get( URI.create( "file:///E:/test4ftp.txt")),
                Paths.get( URI.create( "file:///home/tomcat/test/")),
                "--createDirectory").getT());
        Assert.assertTrue( ftpAccessibleUseSftp.download(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp.txt"))).getT().isEmpty());
        Assert.assertTrue( ftpAccessibleUseSftp.download(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp2.txt")),
                "--path", "file:///E:/dump/", "file:///E:/dump2/", "file:///E:/dump3/", "file:///E:/dump4/").getT().isEmpty());
        Assert.assertTrue( ftpAccessibleUseSftp.download(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp.txt")),
                "--path", "file:///E:/dump/", "file:///E:/dump2/", "file:///E:/dump3/", "file:///E:/dump4/").getT().isEmpty());
        Set<Path> result =  ftpAccessibleUseSftp.download(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp.txt")),
                "--path", "file:///E:/dump/", "file:///E:/dump2/", "file:///E:/dump3/", "file:///E:/dump4/", "--createDirectory").getT();
        result.forEach( path -> log.info( path.toString()));
        Assert.assertFalse( result.isEmpty());
        Assert.assertTrue( ftpAccessibleUseSftp.download(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp.txt")),
                "--path", "file:///E:/dump/", "file:///E:/dump2/", "file:///E:/dump3/", "--createDirectory").getT().isEmpty());
        Assert.assertTrue( ftpAccessibleUseSftp.download(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp.txt")),
                "--path", "file:///E:/dump/", "file:///E:/dump2/", "file:///E:/dump3/", "file:///E:/dump4/", "--createDirectory", "--overwrite").getT().isEmpty());
        result =  ftpAccessibleUseSftp.download(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp.txt")),
                "--path", "file:///E:/dump/", "file:///E:/dump2/", "file:///E:/dump3/", "file:///E:/dump4/", "--createDirectory", "--overwrite", "overwrite").getT();
        result.forEach( path -> log.info( path.toString()));
        Assert.assertFalse( result.isEmpty());
        result =  ftpAccessibleUseSftp.download(
                Paths.get( URI.create( "file:///home/tomcat/test/test4ftp.txt")),
                "--path", "file:///E:/dump/", "file:///E:/dump2/", "file:///E:/dump3/", "file:///E:/dump4/", "--createDirectory", "--overwrite", "append").getT();
        result.forEach( path -> log.info( path.toString()));
        Assert.assertFalse( result.isEmpty());
    }
    @Test
    public void test5() {
        Assert.assertFalse( ftpAccessibleUseFtp.upload(
               "file:///E:/ab2.exe",
                "file:///home/wasadmin/test/").getT());
        Assert.assertFalse( ftpAccessibleUseFtp.upload(
                "file:///E:/ab2.exe",
                "file:///home/wasadmin/test/",
                "--createFlagFile").getT());
        Assert.assertFalse( ftpAccessibleUseFtp.upload(
                "file:///E:/ab2.exe",
                "file:///home/wasadmin/test/",
                "--createFlagFile", "com.tkp.tkpole.starter.utils.ftp.ftpAccessibleUseSftp").getT());
        Assert.assertTrue( ftpAccessibleUseFtp.upload(
                "file:///E:/ab2.exe",
                "file:///home/wasadmin/test/",
                "--createDirectory", "--createFlagFile", "com.tkp.tkpole.starter.utils.ftp.SupplierTest").getT());
    }
    @Test
    public void test6() {
        Assert.assertTrue( ftpAccessibleUseFtp.delete(
                "file:///home/wasadmin/test/test4ftp.txt",
                "--noneIsOk").getT());
        Assert.assertTrue( ftpAccessibleUseFtp.upload(
                "file:///E:/test4ftp.txt",
                "file:///home/wasadmin/test/",
                "--createDirectory").getT());
        Assert.assertFalse( ftpAccessibleUseFtp.upload(
                "file:///E:/test4ftp.txt",
                "file:///home/wasadmin/test/").getT());
        Assert.assertTrue( ftpAccessibleUseFtp.upload(
                "file:///E:/test4ftp.txt",
                "file:///home/wasadmin/test/",
                "--overwrite", "append").getT());
        Assert.assertTrue( ftpAccessibleUseFtp.upload(
                "file:///E:/test4ftp.txt",
                "file:///home/wasadmin/test/",
                "--overwrite", "overwrite").getT());
        Assert.assertTrue( ftpAccessibleUseFtp.delete(
                "file:///home/wasadmin/test/test4ftp.txt").getT());
        Assert.assertFalse( ftpAccessibleUseFtp.delete(
                "file:///home/wasadmin/test/test4ftp.txt").getT());
        Assert.assertTrue( ftpAccessibleUseFtp.delete(
                "file:///home/wasadmin/test/test4ftp.txt",
                "--noneIsOk").getT());
        Assert.assertTrue( ftpAccessibleUseFtp.delete(
                "file:///home/wasadmin/test/test4ftp.txt",
                "--isA", "file", "--noneIsOk").getT());
        Assert.assertTrue( ftpAccessibleUseFtp.delete(
                "file:///home/wasadmin/test/test4ftp.txt",
                "--isA", "directory", "--noneIsOk").getT());
    }
    @Test
    public void test7() throws IOException {
        Assert.assertTrue( ftpAccessibleUseFtp.delete(
                Paths.get( URI.create( "file:///home/wasadmin/test/test4ftp.txt")),
                "--noneIsOk").getT());
        Assert.assertTrue( ftpAccessibleUseFtp.upload(
                Paths.get( URI.create( "file:///E:/test4ftp.txt")),
                Paths.get( URI.create( "file:///home/wasadmin/test/")),
                "--createDirectory").getT());
        Assert.assertTrue( ftpAccessibleUseFtp.download(
                Paths.get( URI.create( "file:///home/wasadmin/test/test4ftp.txt"))).getT().isEmpty());
        Assert.assertTrue( ftpAccessibleUseFtp.download(
                Paths.get( URI.create( "file:///home/wasadmin/test/test4ftp2.txt")),
                "--path", "file:///E:/dump/", "file:///E:/dump2/", "file:///E:/dump3/", "file:///E:/dump4/").getT().isEmpty());
        Assert.assertTrue( ftpAccessibleUseFtp.download(
                Paths.get( URI.create( "file:///home/wasadmin/test/test4ftp.txt")),
                "--path", "file:///E:/dump/", "file:///E:/dump2/", "file:///E:/dump3/", "file:///E:/dump4/").getT().isEmpty());
        Set<Path> result = null;
//        Set<Path> result =  ftpAccessibleUseFtp.download(
//                Paths.get( URI.create( "file:///home/wasadmin/test/test4ftp.txt")),
//                "--path", "file:///E:/dump/", "file:///E:/dump2/", "file:///E:/dump3/", "file:///E:/dump4/", "--createDirectory");
//        result.forEach( path -> log.info( path.toString()));
//        Assert.assertFalse( result.isEmpty());
        Assert.assertTrue( ftpAccessibleUseFtp.download(
                Paths.get( URI.create( "file:///home/wasadmin/test/test4ftp.txt")),
                "--path", "file:///E:/dump/", "file:///E:/dump2/", "file:///E:/dump3/", "--createDirectory").getT().isEmpty());
        Assert.assertTrue( ftpAccessibleUseFtp.download(
                Paths.get( URI.create( "file:///home/wasadmin/test/test4ftp.txt")),
                "--path", "file:///E:/dump/", "file:///E:/dump2/", "file:///E:/dump3/", "file:///E:/dump4/", "--createDirectory", "--overwrite").getT().isEmpty());
        result =  ftpAccessibleUseFtp.download(
                Paths.get( URI.create( "file:///home/wasadmin/test/test4ftp.txt")),
                "--path", "file:///E:/dump/", "file:///E:/dump2/", "file:///E:/dump3/", "file:///E:/dump4/", "--createDirectory", "--overwrite", "overwrite").getT();
        result.forEach( path -> log.info( path.toString()));
        Assert.assertFalse( result.isEmpty());
        result =  ftpAccessibleUseFtp.download(
                Paths.get( URI.create( "file:///home/wasadmin/test/test4ftp.txt")),
                "--path", "file:///E:/dump/", "file:///E:/dump2/", "file:///E:/dump3/", "file:///E:/dump4/", "--createDirectory", "--overwrite", "append").getT();
        result.forEach( path -> log.info( path.toString()));
        Assert.assertFalse( result.isEmpty());


        log.info("-------------------------------------------");
        result =  ftpAccessibleUseFtp.download(
                fileSystem,
                Paths.get( URI.create( "file:///home/wasadmin/test/test4ftp.txt")),
                "--path", "C:/test/test2", "C:/test/test3", "--createDirectory").getT();
        result.forEach( path -> log.info( path.toString()));
        Assert.assertFalse( result.isEmpty());
        result =  ftpAccessibleUseFtp.download(
                fileSystem,
                Paths.get( URI.create( "file:///home/wasadmin/test/test4ftp.txt")),
                "--path", "C:/test/test2", "C:/test/test3", "--createDirectory", "--overwrite", "append").getT();
        result.forEach( path -> log.info( path.toString()));
        Assert.assertFalse( result.isEmpty());
        result =  ftpAccessibleUseFtp.download(
                fileSystem,
                Paths.get( URI.create( "file:///home/wasadmin/test/test4ftp.txt")),
                "--path", "C:/test/test2", "C:/test/test3", "--createDirectory", "--overwrite", "append").getT();
        result.forEach( path -> log.info( path.toString()));
        Assert.assertFalse( result.isEmpty());
        result.forEach( path -> {
            Path path2 = fileSystem.getPath( path.toString());
            try {
                Files.readAllLines( path2, Charsets.UTF_8).forEach(log::info);
            } catch ( IOException e) {
                log.error( e.getMessage(), e);
            }
        });
    }
    @Test
    public void test8() throws IOException {
        Path  path = fileSystem.getPath("C:/test/");
        Path path2 = path.resolve("test4jimfs.txt");
        Files.write( path2, ImmutableMultiset.of("测试:花动一山春色"), Charsets.UTF_8);

        Assert.assertTrue( ftpAccessibleUseFtp.upload(
                Paths.get( URI.create( "file:///C:/test/test4jimfs.txt")),
                fileSystem,
                Paths.get( URI.create( "file:///home/wasadmin/test/")),
                "--createDirectory").getT());
    }


    @After
    public void tearDown() throws Exception {
        ftpAccessibleUseSftp.close();
        ftpAccessibleUseFtp.close();
        fileSystem.close();
    }

    private FtpAccessible ftpAccessibleUseSftp;
    private FtpAccessible ftpAccessibleUseFtp;
    private FtpAccessible ftpAccessibleUseProxy;
    private FileSystem fileSystem;
}
class SupplierTest implements Supplier<String> {
    private static final FastDateFormat FAST_DATE_FORMAT_TEST_TEMP_FILE_NAME_PATTERN = FastDateFormat.getInstance("yyyyMMdd");
    @Override
    public String get() {
        return FAST_DATE_FORMAT_TEST_TEMP_FILE_NAME_PATTERN.format( new Date());
    }
}

