/* Copyright (c) 2019, Guo Jinyang. All rights reserved. */
package com.github.piggyguojy.parser.rule.structure.annotation;

import com.github.piggyguojy.ClassUtil;
import com.github.piggyguojy.Msg;
import com.github.piggyguojy.parser.rule.parse.AbstractParser;
import com.github.piggyguojy.parser.rule.structure.OverrideRule;
import com.github.piggyguojy.parser.rule.structure.StructureHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.piggyguojy.Assert.isNull;
import static com.github.piggyguojy.Assert.notNull;

/**
 * 注解形式规则处理器基类
 * 
 * <p> 创建时间：2019/2/13
 * 
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
@Slf4j
public abstract class AbstractAnnotationHandler<A extends Annotation, P extends AbstractParser>
        implements StructureHandler<P> {

    /**
     * 注解处理器帮助类
     *
     * <p> 创建时间：2019/2/23
     *
     * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
     * @version 1.0
     * */
    @Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AbstractAnnotationHandlerHelper {

        @SuppressWarnings("unchecked")
        public static  <A extends Annotation, B extends Annotation> A decideAnnotationRule(
                A son,
                B parent,
                Map<String,Object> stringObjectMap,
                OverrideRule overrideRule
        ) {
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
        static  <G, A extends Annotation, B extends Annotation> A decideAnnotationRule(
                A son,
                B parent,
                String fieldName,
                G gDefaultValue,
                OverrideRule overrideRule
        ) {
            Field sonMemberValues;
            Field parentMemberValues;
            Map<String,Object> sonMap;
            Map<String,Object> parentMap;
            G sonGValue;
            G parentGValue;
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
     * 获取注解处理器
     *
     * @param annotationClass 注解类型
     * @return 对应的注解处理器或默认注解处理器(当找不到时)
     * */
    protected static AbstractAnnotationHandler getAnnotationHandlerRegistered(Class<? extends Annotation> annotationClass) {
        return MAP_ANNOTATION_HANDLER.getOrDefault(
                annotationClass,
                DefaultAnnotationHandler.DEFAULT_ANNOTATION_HANDLER);
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
            AbstractAnnotationHandler<A,P> abstractAnnotationHandler
    ) {
        AbstractAnnotationHandler previous = MAP_ANNOTATION_HANDLER.put(annotationClass, abstractAnnotationHandler);
        if ( notNull( previous)) {
            log.warn(
                    "注解 {} 的处理器已由 {} 替换为 {}",
                    annotationClass.getCanonicalName(),
                    previous.getClass().getCanonicalName(),
                    abstractAnnotationHandler.getClass().getCanonicalName());
        }
    }



    private static final Map<Class<? extends Annotation>, AbstractAnnotationHandler> MAP_ANNOTATION_HANDLER = new HashMap<>();
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    static { init( ClassUtil.getTopPackageName());}
    private static void init(String packagePath) {
        if (ATOMIC_INTEGER.addAndGet(1)<=2) {
            ClassUtil
                    .getClassesExtendClass(AbstractAnnotationHandler.class, packagePath, true)
                    .forEach(
                            selfClass-> {
                                String className = selfClass.getCanonicalName();
                                // todo ... 静态内部类需要特别处理(下面的处理不够强)
                                if (className.matches("^.+(\\.[A-Z_$][a-zA-Z_$]*){2}$")) {
                                    className = className.replaceAll("(^.+\\.[A-Z_$][a-zA-Z_$]*)\\.([A-Z_$][a-zA-Z_$]*$)","$1\\$$2");
                                }
                                try {
                                    Class.forName(className, true, Thread.currentThread().getContextClassLoader());
                                } catch ( ClassNotFoundException e) {
                                    log.error( e.getMessage(), e);
                                }
                            });
        } else { log.error("达到最大更新次数({}次), 禁止再次更新", 2); }
    }
}
