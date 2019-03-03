package com.guojy;

import com.google.common.collect.ImmutableSet;
import com.google.gson.reflect.TypeToken;
import com.guojy.gson.GsonBean;
import com.guojy.model.Msg;
import com.guojy.parser.excel.rule.parse.ExcelParser;
import com.guojy.parser.excel.rule.structure.annotation.handler.*;
import com.guojy.parser.rule.parse.Parseable;
import com.guojy.parser.rule.structure.BiInheritableRule;
import com.guojy.parser.rule.structure.Inheritable;
import com.guojy.parser.rule.structure.StructureHandler;
import com.guojy.parser.rule.structure.annotation.AbstractAnnotationHandler;
import lombok.SneakyThrows;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ClassUtils;
import org.junit.Test;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.guojy.Assert.isNull;
import static org.junit.Assert.*;

@Slf4j
public class ClassUtilTest {

    @Test
    public void test() {
        assertTrue(true);
    }

    @Test
    public void test2() {
        assertEquals("com.guojy", ClassUtil.getTopPackageName());
    }

    @Test
    public void test3() {
        Set<Class<? extends ExcelAnnotationHandler>> classes
                = ClassUtil.getClassesExtendClass(
                        ExcelAnnotationHandler.class, "com.guojy.parser.excel", false);
        assertTrue(classes.isEmpty());
        classes = ClassUtil.getClassesExtendClass(
                        ExcelAnnotationHandler.class, "com.guojy.parser.excel", true);
        assertTrue(classes.contains(ExcelBeanHandler.class));
        assertFalse(classes.contains(ExcelParser.class));
    }

    @Test
    public void test4() {
        Set<Class<? super ExcelAnnotationHandler>> classes
                = ClassUtil.getClassesSuperClass(
                ExcelAnnotationHandler.class, "com.guojy.parser.rule", false);
        assertTrue(classes.isEmpty());
        classes = ClassUtil.getClassesSuperClass(
                ExcelAnnotationHandler.class, "com.guojy.parser.rule", true);
        assertTrue(classes.contains(BiInheritableRule.class));
        assertFalse(classes.contains(Parseable.class));
    }

    @Test
    public void test5() {
        Set<Class<?>> classes = ClassUtil.getClassesWithInterfaceImplemented(Inheritable.class, "com.guojy.parser", true);
        assertTrue(classes.contains(ExcelColumnHandler.class));
        assertFalse(classes.contains(ExcelParser.class));
    }

    @Test
    public void test6() {
        Set<Class<?>> classes = ClassUtil.getClassesWithAnnotationMarked(GsonBean.class, "com.guojy", true);
        assertTrue(classes.contains(Msg.class));
        assertFalse(classes.contains(Assert.class));
    }

    @Test
    public void test7() {
        assertNull(ClassUtil.getTheOnlyOneAnnotation(Msg.class, XmlRootElement.class, GsonBean.class));
        assertNotNull(ClassUtil.getTheOnlyOneAnnotation(Msg.class, Deprecated.class, GsonBean.class));
        assertEquals(ClassUtil.getTheOnlyOneAnnotation(Msg.class, Deprecated.class, GsonBean.class),GsonBean.class);
        assertNull(ClassUtil.getTheOnlyOneAnnotation(
                Msg.class,
                ImmutableSet.<Class<? extends Annotation>>builder().add(XmlRootElement.class).add(GsonBean.class).build()));
        assertNotNull(ClassUtil.getTheOnlyOneAnnotation(
                Msg.class,
                ImmutableSet.<Class<? extends Annotation>>builder().add(Deprecated.class).add(GsonBean.class).build()));
        assertEquals(ClassUtil.getTheOnlyOneAnnotation(
                Msg.class,
                ImmutableSet.<Class<? extends Annotation>>builder().add(Deprecated.class).add(GsonBean.class).build()),GsonBean.class);
    }


    @Test @SneakyThrows
    public void test8() {
        assertNull(ClassUtil.getGenericClass(InnerClassUtilTest.class.getDeclaredField("f"), 1,2));
        assertEquals(ClassUtil.getGenericClass(InnerClassUtilTest.class.getDeclaredField("f2"),0),String.class);
        assertNull(ClassUtil.getGenericClass(InnerClassUtilTest.class.getDeclaredField("f2"),0,0));
        assertNull(ClassUtil.getGenericClass(InnerClassUtilTest.class.getDeclaredField("f2"),1));
        assertNull(ClassUtil.getGenericClass(InnerClassUtilTest.class.getDeclaredField("f2"),1,0));
        assertEquals(ClassUtil.getGenericClass(InnerClassUtilTest.class.getDeclaredField("f3"),0),List.class);
        assertEquals(ClassUtil.getGenericClass(InnerClassUtilTest.class.getDeclaredField("f3"),0,0),Map.class);
        assertNull(ClassUtil.getGenericClass(InnerClassUtilTest.class.getDeclaredField("f3"),0,1));
        assertEquals(ClassUtil.getGenericClass(InnerClassUtilTest.class.getDeclaredField("f3"),0,0,0),String.class);
        assertEquals(ClassUtil.getGenericClass(InnerClassUtilTest.class.getDeclaredField("f3"),0,0,1),Long.class);
        assertNull(ClassUtil.getGenericClass(InnerClassUtilTest.class.getDeclaredField("f3"),0,0,2));
        assertNull(ClassUtil.getGenericClass(InnerClassUtilTest.class.getDeclaredField("f3"),0,0,0,1));
        assertEquals(ClassUtil.getGenericClass(InnerClassUtilTest.class.getDeclaredField("f3"),1,0,1,0),Integer.class);

        assertEquals(ClassUtil.getGenericClass(
                new TypeToken<Map<List<Map<String,Long>>,Set<Map<Map<BigDecimal,Msg>,Set<Integer>>>>>(){},
                1,0,1,0),Integer.class);
    }

    public static class InnerClassUtilTest {
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
    }
}