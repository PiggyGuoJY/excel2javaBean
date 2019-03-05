package com.guojy;

import com.google.common.collect.ImmutableSet;
import com.google.gson.reflect.TypeToken;
import com.guojy.gson.GsonBean;
import com.guojy.model.Msg;
import com.guojy.model.test.Marked;
import com.guojy.model.test.StudentRecordTable;
import com.guojy.parser.excel.rule.parse.ExcelParser;
import com.guojy.parser.excel.rule.structure.annotation.ExcelBean;
import com.guojy.parser.excel.rule.structure.annotation.handler.ExcelAnnotationHandler;
import com.guojy.parser.excel.rule.structure.annotation.handler.ExcelBeanHandler;
import com.guojy.parser.excel.rule.structure.annotation.handler.ExcelColumnHandler;
import com.guojy.parser.rule.parse.Parseable;
import com.guojy.parser.rule.structure.BiInheritableRule;
import com.guojy.parser.rule.structure.Inheritable;
import com.guojy.parser.rule.structure.OverrideRule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.xml.bind.annotation.XmlRootElement;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.guojy.ClassUtil.*;
import static org.junit.Assert.*;

@Slf4j
public class ClassUtilTest {

    @Test
    public void test() {
        assertEquals("com.guojy", getTopPackageName());
    }

    @Test
    public void test2() {
        Set<Class<? extends ExcelAnnotationHandler>> classes
                = getClassesExtendClass(
                        ExcelAnnotationHandler.class, "com.guojy.parser.excel", false);
        assertTrue(classes.isEmpty());
        classes = getClassesExtendClass(
                        ExcelAnnotationHandler.class, "com.guojy.parser.excel", true);
        assertTrue(classes.contains(ExcelBeanHandler.class));
        assertFalse(classes.contains(ExcelParser.class));
    }

    @Test
    public void test3() {
        Set<Class<? super ExcelAnnotationHandler>> classes
                = getClassesSuperClass(
                ExcelAnnotationHandler.class, "com.guojy.parser.rule", false);
        assertTrue(classes.isEmpty());
        classes = getClassesSuperClass(
                ExcelAnnotationHandler.class, "com.guojy.parser.rule", true);
        assertTrue(classes.contains(BiInheritableRule.class));
        assertFalse(classes.contains(Parseable.class));
    }

    @Test
    public void test4() {
        Set<Class<?>> classes = getClassesWithInterfaceImplemented(
                Inheritable.class, "com.guojy.parser", true);
        assertTrue(classes.contains(ExcelColumnHandler.class));
        assertFalse(classes.contains(ExcelParser.class));
    }

    @Test
    public void test5() {
        Set<Class<?>> classes = getClassesWithAnnotationMarked(
                GsonBean.class, "com.guojy", true);
        assertTrue(classes.contains(Msg.class));
        assertFalse(classes.contains(Assert.class));
    }

    @Test
    public void test6() {
        assertNull(getTheOnlyOneAnnotation(Msg.class, XmlRootElement.class, Slf4j.class));
        assertNotNull(getTheOnlyOneAnnotation(Msg.class, Deprecated.class, GsonBean.class));
        assertEquals(getTheOnlyOneAnnotation(Msg.class, Deprecated.class, GsonBean.class),GsonBean.class);
        assertNull(getTheOnlyOneAnnotation(
                Msg.class,
                ImmutableSet.<Class<? extends Annotation>>builder().add(XmlRootElement.class).add(Slf4j.class).build()));
        assertNotNull(getTheOnlyOneAnnotation(
                Msg.class,
                ImmutableSet.<Class<? extends Annotation>>builder().add(Deprecated.class).add(GsonBean.class).build()));
        assertEquals(getTheOnlyOneAnnotation(
                Msg.class,
                ImmutableSet.<Class<? extends Annotation>>builder().add(Deprecated.class).add(GsonBean.class).build()),GsonBean.class);
    }

    @Test @SneakyThrows
    public void test7() {

        assertNull(getGenericClass(
                InnerClassUtilTest.class.getDeclaredField("f"), 
                1,2));

        assertEquals(
                getGenericClass(InnerClassUtilTest.class.getDeclaredField("f2"),0),
                String.class);

        assertNull(getGenericClass(
                InnerClassUtilTest.class.getDeclaredField("f2"),
                0,0));

        assertNull(getGenericClass(
                InnerClassUtilTest.class.getDeclaredField("f2"),
                1));

        assertNull(getGenericClass(
                InnerClassUtilTest.class.getDeclaredField("f2"),
                1,0));

        assertEquals(
                getGenericClass(InnerClassUtilTest.class.getDeclaredField("f3"),0),
                List.class);

        assertEquals(
                getGenericClass(InnerClassUtilTest.class.getDeclaredField("f3"),0,0),
                Map.class);

        assertNull(getGenericClass(
                InnerClassUtilTest.class.getDeclaredField("f3"),
                0,1));

        assertEquals(
                getGenericClass(InnerClassUtilTest.class.getDeclaredField("f3"),0,0,0),
                String.class);

        assertEquals(
                getGenericClass(InnerClassUtilTest.class.getDeclaredField("f3"),0,0,1),
                Long.class);

        assertNull(getGenericClass(
                InnerClassUtilTest.class.getDeclaredField("f3"),0,0,2));

        assertNull(getGenericClass(
                InnerClassUtilTest.class.getDeclaredField("f3"),
                0,0,0,1));

        assertEquals(
                getGenericClass(InnerClassUtilTest.class.getDeclaredField("f3"),1,0,1,0),
                Integer.class);

        assertEquals(getGenericClass(
                new TypeToken<Map<List<Map<String,Long>>,Set<Map<Map<BigDecimal,Msg>,Set<Integer>>>>>(){},
                1,0,1,0),Integer.class);
    }

    @Test
    public void test8() {
        ExcelBean excelBean = StudentRecordTable.class.getDeclaredAnnotation(ExcelBean.class);
        log.info(excelBean.toString());
        assertEquals(OverrideRule.PARENT_FORCE,excelBean.overrideRule());
        ClassUtil.changeAnnotationFieldValue(excelBean,"overrideRule",OverrideRule.SON_FIRST);
        log.info(excelBean.toString());
        assertEquals(OverrideRule.SON_FIRST,excelBean.overrideRule());
    }

    @Test
    public void test9() {
        Marked marked = StudentRecordTable.class.getDeclaredAnnotation(Marked.class);
        log.info(marked.toString());
        assertArrayEquals(new int[]{1,2,3}, marked.ia());
        assertArrayEquals(new String[]{"a","b","c"}, marked.sa());
        ClassUtil.addValueToAnnotation(marked,"ia",4);
        ClassUtil.addValueToAnnotation(marked,"sa","d");
        log.info(marked.toString());
        assertArrayEquals(new int[]{1,2,3,4}, marked.ia());
        assertArrayEquals(new String[]{"a","b","c","d"}, marked.sa());
    }

    @Test
    public void test10() {
        //ClassUtil.set();
    }

    @Test
    public void test11() {
        //ClassUtil.instanceT();
    }

    private static class InnerClassUtilTest {
        private String f;
        private List<String> f2;
        private Map<
                    List< // 0
                        Map< // 0->0
                            String, // 0->0->0
                            Long //0->0->1
                        >
                    >,
                    Set< // 1
                        Map< // 1->0
                            Map< // 1->0->0
                                BigDecimal, // 1->0->0->0
                                Msg // 1->0->0->1
                            >,
                            Set< // 1->0->1
                                Integer // 1->0->1->0
                            >
                        >
                    >
                > f3;
        private int f4;
    }
}