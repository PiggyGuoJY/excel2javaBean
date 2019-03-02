package com.guojy.parser.rule.structure.annotation;

import com.guojy.ClassUtil;
import com.guojy.model.Msg;
import com.guojy.parser.rule.parse.AbstractParser;
import com.guojy.parser.rule.structure.OverrideRule;
import com.guojy.parser.rule.structure.StructureHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.guojy.Assert.*;

/**
 * 注解形式规则处理器基类
 * 
 * <p> 创建时间：2019/2/13
 * 
 * @author guojy
 * @version 1.0
 * */
@Slf4j
public abstract class AbstractAnnotationHandler<A extends Annotation, P extends AbstractParser> implements StructureHandler<P> {

    private static final Map<Class<? extends Annotation>, AbstractAnnotationHandler> MAP_ANNOTATION_HANDLER = new HashMap<>();
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    static { init( ClassUtil.getTopPackageName()); }

    /**
     * 用于TYPE类型的注解处理器
     *
     * @param <G> 目标泛型
     * @param gClass 目标类型
     * @param a 注解
     * @param p 解析器
     * @param args 附加参数
     * @return 消息实体(具体包含什么要看具体的使用场景)
     * */
    public abstract <G> Msg onType(Class<G> gClass, A a, P p, Object ... args);
    /**
     * 用于FIELD类型的注解处理器
     *
     * @param <G> 目标泛型
     * @param gClass 目标类型
     * @param a 注解
     * @param p 解析器
     * @param args 附加参数
     * @return 消息实体(具体包含什么要看具体的使用场景)
     * */
    public abstract <G> Msg onField(Class<G> gClass, A a, P p, Object ... args);

    /**
     * 注册一定区域内的注解处理器
     *
     * @param packagePath 包路径
     * */
    private static void init(String packagePath) {
        if (ATOMIC_INTEGER.addAndGet(1)<=2) {
            ClassUtil
                    .getClassesExtendClass(AbstractAnnotationHandler.class, packagePath, true)
                    .forEach(
                            selfClass-> {
                                String className = selfClass.getCanonicalName();
                                // 静态内部类需要特别处理(下面的处理不够强)
                                if (className.matches("^.+(\\.[A-Z_$][a-zA-Z_$]*){2}$")) {
                                    className = className.replaceAll("(^.+\\.[A-Z_$][a-zA-Z_$]*)\\.([A-Z_$][a-zA-Z_$]*$)","$1\\$$2");
                                }
                                try {
                                    Class.forName(className, true, Thread.currentThread().getContextClassLoader());
                                } catch ( ClassNotFoundException e) {
                                    log.error( e.getMessage(), e);
                                }
                            });
        } else { log.warn("达到最大更新次数, 禁止再次更新"); }
    }
    /**
     * 获取注解处理器
     *
     * @param annotationClass 注解类型
     * @return 对应的注解处理器或默认注解处理器(当找不到时)
     * */
    protected static AbstractAnnotationHandler getAnnotationHandler(Class<? extends Annotation> annotationClass) {
        return MAP_ANNOTATION_HANDLER.getOrDefault(annotationClass,DefaultAnnotationHandler.DEFAULT_ANNOTATION_HANDLER);
    }
    /**
     * 注册注解类型处理器
     *
     * @param <A> 注解泛型
     * @param <P> 解析器泛型
     * @param annotationClass 要处理的注解
     * @param abstractAnnotationHandler 对应的注解处理器
     * */
    protected static <A extends Annotation, P extends AbstractParser> void register(
            Class<A> annotationClass,
            AbstractAnnotationHandler<A,P> abstractAnnotationHandler) {
        AbstractAnnotationHandler previous = MAP_ANNOTATION_HANDLER.put(annotationClass, abstractAnnotationHandler);
        if ( notNull( previous)) { log.warn("注解 {} 的处理器已由 {} 替换为 {}", annotationClass.getCanonicalName(), previous.getClass().getCanonicalName(), abstractAnnotationHandler.getClass().getCanonicalName()); }
    }

    /**
     * 注解处理器帮助类
     *
     * <p> 创建时间：2019/2/23
     *
     * @author guojy
     * @version 1.0
     * */
    @Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
    protected static class AbstractAnnotationHandlerHelper {
        /**
         * 判断某个对象是否有且只有一个注解集合中的注解
         *
         * @param annotatedElement 判定对象
         * @param annotationClassesSet 注解集合
         * @return 当成功找到时, 返回唯一注解; 否则返回null
         * */
        public static Class<? extends Annotation> getTheOnlyOneAnnotation(AnnotatedElement annotatedElement, Set<Class< ? extends Annotation>> annotationClassesSet) {
            return ClassUtil.getTheOnlyOneAnnotation(annotatedElement, annotationClassesSet);
        }
        /**
         * 向对象中的属性注入值
         *
         * @param field 属性
         * @param target 目标对象
         * @param value 注入值
         * */
        public static void set(Field field, Object target, Object value) {
            try {
                if ( field.isAccessible()) {
                    field.set( target, value);
                } else {
                    field.setAccessible( true);
                    field.set( target, value);
                    field.setAccessible( false);
                }
            } catch ( IllegalAccessException e) {
                log.error( e.getMessage(), e);
            }
        }
        /**
         * 使用指定的类型初始化一个实例
         *
         * @param tClass 指定类型
         * @return 实例
         * */
        public static <T> T instanceT(Class<T> tClass) {
            try {  return tClass.newInstance(); } catch ( IllegalAccessException | InstantiationException e) {
                log.error( e.getMessage(), e);
                log.warn( "未能生产 {} 的实例", tClass.getCanonicalName());
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        public static  <A extends Annotation, B extends Annotation> A decideAnnotationRule(
                A son, B parent, Map<String,Object> stringObjectMap, OverrideRule overrideRule) {
            if ( isNull(parent)) {
                log.error("缺少要处理的参数 {}, 直接返回son", "parent");
                return son;
            }
            if ( !notNull(stringObjectMap)) {
                log.error("缺少要处理的参数 {}, 直接返回son", "stringObjectMap");
                return son;
            }
            if ( isNull(son)) {
                return (A)parent;
            }
            A a = son;
            for(Map.Entry entry : stringObjectMap.entrySet()) {
                a = decideAnnotationRule(a, parent, (String)entry.getKey(), entry.getValue(), overrideRule);
                if ( isNull(a)) { log.error("处理 {} 时出错, 中断执行", entry.getKey()); return son; }
            }
            return a;
        }

        @SuppressWarnings("unchecked")
        public static <G, A extends Annotation> A changeAnnotationFieldValue(A a, String fieldName, G gValue) {
            Field aMemberValues;
            Map<String,Object> aMap;
            try {
                InvocationHandler aInvocationHandler = Proxy.getInvocationHandler(a);
                aMemberValues = aInvocationHandler.getClass().getDeclaredField("memberValues");
                aMemberValues.setAccessible(true);
                aMap = (Map<String,Object>)aMemberValues.get(aInvocationHandler);
            } catch ( NoSuchFieldException | IllegalAccessException e) {
                log.error(e.getMessage(),e);
                return a;
            }
            aMap.put(fieldName, gValue);
            aMemberValues.setAccessible(false);
            return a;
        }

        @SuppressWarnings("unchecked")
        private static  <G, A extends Annotation, B extends Annotation> A decideAnnotationRule(
                A son,
                B parent,
                String fieldName,
                G gDefaultValue,
                OverrideRule overrideRule) {

            Field sonMemberValues, parentMemberValues;
            Map<String,Object> sonMap, parentMap;
            G sonGValue, parentGValue;
            try {
                InvocationHandler sonInvocationHandler = Proxy.getInvocationHandler(son);
                InvocationHandler parentInvocationHandler = Proxy.getInvocationHandler(parent);
                sonMemberValues = sonInvocationHandler.getClass().getDeclaredField("memberValues");
                parentMemberValues = parentInvocationHandler.getClass().getDeclaredField("memberValues");
                sonMemberValues.setAccessible(true);
                parentMemberValues.setAccessible(true);
                sonMap = (Map<String,Object>)sonMemberValues.get(sonInvocationHandler);
                parentMap = (Map<String,Object>)parentMemberValues.get(parentInvocationHandler);
                if (sonMap.get(fieldName).getClass().equals(gDefaultValue.getClass()) && parentMap.get(fieldName).getClass().equals(gDefaultValue.getClass())) {
                    sonGValue = (G)sonMap.get(fieldName);
                    parentGValue = (G)parentMap.get(fieldName);
                } else {
                    log.error("默认值类型与属性类型不匹配");
                    return null;
                }
            } catch ( NoSuchFieldException | IllegalAccessException e) {
                log.error(e.getMessage(),e);
                return null;
            }

            if ( isNull(parentGValue)) { return null; } else {
                if ( isNull(sonGValue)) { return null; } else {
                    switch (overrideRule) {
                        case PARENT_FIRST:
                            if (!parentGValue.equals(gDefaultValue)) { sonMap.put(fieldName, parentGValue); } break;
                        case PARENT_FORCE:
                            sonMap.put(fieldName, parentGValue); break;
                        case SON_FIRST:
                            if (sonGValue.equals(gDefaultValue)) { sonMap.put(fieldName, parentGValue); } break;
                        case SON_FORCE:
                            break;
                        default:
                            log.error("不能识别的 OverrideRule 类型");
                            return null;
                    }
                }
            }
            sonMemberValues.setAccessible(false); parentMemberValues.setAccessible(false);
            return son;
        }
    }
}
