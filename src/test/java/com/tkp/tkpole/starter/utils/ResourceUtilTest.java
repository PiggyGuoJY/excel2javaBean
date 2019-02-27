package com.tkp.tkpole.starter.utils;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.jimfs.Jimfs;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
public class ResourceUtilTest {

    private static Path PATH_TEST;
    private static Path PATH_TEST2;

    @BeforeClass
    public static void beforeClass() {
        PATH_TEST = Paths.get(URI.create("file:///E:/test/helloWorld.txt"));
        PATH_TEST2 = Paths.get(URI.create("file:///home/tomcat/helloWorld.txt"));

    }

    private FileSystem fileSystem;

    @Before
    public void before() throws IOException {
        fileSystem = Jimfs.newFileSystem("win");
    }

    @Test
    public void testChangeFileSeparatorToString() {
        assertEquals("E:\\test\\helloWorld.txt", ResourceUtil.changeFileSeparatorToString(PATH_TEST));
        assertEquals("/E:/test/helloWorld.txt", ResourceUtil.changeFileSeparatorToString(PATH_TEST, true));
        assertEquals("/home/tomcat/helloWorld.txt", ResourceUtil.changeFileSeparatorToString(PATH_TEST2));
        assertEquals("/home/tomcat/helloWorld.txt", ResourceUtil.changeFileSeparatorToString(PATH_TEST2, true));
    }

    @Test
    public void testGetExtensionName() {
        assertEquals("txt", ResourceUtil.getExtensionName(PATH_TEST.getFileName().toString()));
        assertEquals("", ResourceUtil.getExtensionName("test-txt"));
        assertEquals("", ResourceUtil.getExtensionName(null));
    }

    @Test
    public void testPath2File () throws IOException {
        Path  path = fileSystem.getPath("C:/test/");
        Files.createDirectory( path);
        Path path2 = path.resolve("test4jimfs.txt");
        Files.write( path2, ImmutableMultiset.of("测试:花动一山春色\naha"), Charsets.UTF_8);
        Files.readAllLines( path2, Charsets.UTF_8).forEach( s -> log.info(s));
        assertTrue( Assert.notNul( ResourceUtil.path2File( path2, "file:///E:/")));
    }

    @After
    public void after() throws IOException {
        fileSystem.close();
    }
}