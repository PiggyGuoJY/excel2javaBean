package com.tkp.tkpole.starter.utils;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class JimfsTest {

    private FileSystem fileSystem;

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
    public void test() throws IOException, InterruptedException {
       Path path = fileSystem.getPath("/test");
       Files.createDirectory(path);

    }

    @After
    public void after() throws IOException {
        fileSystem.close();
    }
}
