package com.tkp.tkpole.starter.utils;

import com.google.gson.reflect.TypeToken;
import com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelBean;
import com.tkp.tkpole.starter.utils.parser.rule.structure.annotation.AbstractAnnotationHandler;
import com.tkp.tkpole.starter.utils.parser.excel.rule.parse.XlsExcelParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ClassUtilTest {
    @Test
    public void getRuntimePath() throws Exception {
        log.info( ResourceUtil.getRuntimePath());

        log.info("file:/E:/gitProject/tkpole_schedule/target/classes/".replaceAll("^(.+?)/classes/$","$1/"));
        log.info("jar:file:/E:/gitProject/tkpole_schedule/target/tkpole_schedule.jar!/BOOT-INF/classes!/".replaceAll("^jar:(.+)/([0-9a-zA-Z_]+)\\.jar!(.+)$", "$1/"));

        Path path = Paths.get(URI.create("file:///E:/gitProject/tkpole_schedule/target/tmp/email"));
        log.info( path.toString());
    }


    @Test
    public void test() {
        Set<Class<? extends AbstractAnnotationHandler>> classSet = ClassUtil.getClassesExtendClass(AbstractAnnotationHandler.class, "com.tkp.tkpole.starter.utils", true);
        classSet.forEach( c -> log.info(c.getName()));
    }

    @Test
    public void test2() {
        Set<Class<? super XlsExcelParser>> classSet = ClassUtil.getClassesSuperClass(XlsExcelParser.class, "com.tkp.tkpole.starter.utils", true);
        classSet.forEach( c -> log.info(c.getName()));
    }


    @Test
    public void test3() {
        Set<Class<?>> classSet = ClassUtil.getClassesWithAnnotation(ExcelBean.class, "com.tkp.tkpole.starter.utils", true);
        classSet.forEach( c -> log.info(c.getName()));
    }

    @Test
    public void test4() {
        Set<Class<?>> classSet = ClassUtil.getClassesWithInterface(AutoCloseable.class, "com.tkp.tkpole.starter.utils", true);
        classSet.forEach( c -> log.info(c.getName()));
    }

    @Test
    public void test5() throws Exception {
        log.info(ClassUtil.getGenericType(AAA.class.getDeclaredField("map"),1).getClass().getName());
    }

    @Test
    public void test6() {
        log.info(ClassUtil.getGenericType(new TypeToken<List<String>>(){},0).getTypeName());
    }
}

class AAA {
    public Map<Map<List<String>, Set<Integer>>, Long> map;
}