package com.github.piggyguojy;

import com.google.common.primitives.*;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.piggyguojy.Assert.*;


/**
 * 类工具
 *
 * <p> 创建时间：2018/8/10
 *
 * @author guojy
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassUtil {

    public static String getTopPackageName() {
        return ClassUtil.class.getPackage().getName();
    }
    public static <T> Set<Class<? extends T>> getClassesExtendClass(
            final Class<T> tUpperBoundClass,
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return getClassesExtendClassUseReflections(tUpperBoundClass, packagePath4Seek, isRecursive);
    }
    public static <T> Set<Class<? super T>> getClassesSuperClass(
            final Class<T> tLowerBoundClass,
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return getClassesSuperClassUseReflections(tLowerBoundClass, packagePath4Seek, isRecursive);
    }
    public static <T extends Annotation> Set<Class<?>> getClassesWithAnnotationMarked(
            final Class<T> annotationClass,
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return getClassesWithAnnotationUseReflections(annotationClass, packagePath4Seek, isRecursive);
    }
    public static Set<Class<?>> getClassesWithInterfaceImplemented(
            final Class interfaceClass,
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return getClassesWithInterfaceUseReflections(interfaceClass, packagePath4Seek, isRecursive);
    }

    @SafeVarargs
    public static Class<? extends Annotation> getTheOnlyOneAnnotation(
            AnnotatedElement annotatedElement, 
            Class< ? extends Annotation> ... annotations
    ) {

        Set<Class<? extends Annotation>> annotationsSetOfField
                = Stream.of(annotatedElement.getDeclaredAnnotations())
                    .parallel()
                    .map(Annotation::annotationType)
                    .collect(Collectors.toSet());
        Set<Class<? extends Annotation>> annotationsSetOfParam 
                = Stream.of(annotations)
                .parallel()
                .filter(RUNTIME_ANNOTATION_CHECKER)
                .collect(Collectors.toSet());
        @SuppressWarnings("unchecked")
        Class<? extends Annotation> annotationClass =
                (Assert.notNul(annotationsSetOfField) &&
                Assert.notNul(annotationsSetOfParam) &&
                annotationsSetOfParam.retainAll(annotationsSetOfField)&&
                annotationsSetOfParam.size()==1) ?
                        (Class<? extends Annotation>)annotationsSetOfParam.toArray()[0] :
                        null;
        return annotationClass;
    }
    public static Class<? extends Annotation> getTheOnlyOneAnnotation(
            AnnotatedElement annotatedElement,
            Set<Class< ? extends Annotation>> annotationClassesSet
    ) {
        Set<Class<? extends Annotation>> annotationsSetOfField
                = Stream.of(annotatedElement.getDeclaredAnnotations())
                    .parallel()
                    .map(Annotation::annotationType)
                    .collect(Collectors.toSet());
        Set<Class<? extends Annotation>> annotationsSetOfParam
                = annotationClassesSet
                .parallelStream()
                .filter(RUNTIME_ANNOTATION_CHECKER)
                .collect(Collectors.toSet());
        @SuppressWarnings("unchecked")
        Class<? extends Annotation> annotationClass =
                (Assert.notNul(annotationsSetOfField) &&
                Assert.notNul(annotationsSetOfParam) &&
                annotationsSetOfParam.retainAll(annotationsSetOfField)&&
                annotationsSetOfParam.size()==1) ?
                        (Class<? extends Annotation>)annotationsSetOfParam.toArray()[0] :
                        null;
        return annotationClass;
    }

    public static Class<?> getGenericClass(
            final Field field,
            final int ... pos
    ) {
        if (!(field.getGenericType() instanceof ParameterizedType)) {
            log.warn("{} 没有泛型参数", field.getGenericType());
            return null;
        }
        return getGenericClass(((ParameterizedType)field.getGenericType()), pos);
    }
    public static <O> Class<?> getGenericClass(
            final TypeToken<O> oTypeToken,
            final int ... pos) {
        if (!(oTypeToken.getType() instanceof ParameterizedType)) { log.warn("类型 {} 没有泛型参数", oTypeToken.getType()); return null;}
        return getGenericClass(((ParameterizedType)oTypeToken.getType()), pos);
    }

    @SuppressWarnings("unchecked")
    public static <G, A extends Annotation> A changeAnnotationFieldValue(
            A annotation,
            String fieldName,
            G gValue
    ) {
        Field aMemberValues;
        Map<String,Object> aMap;
        try {
            InvocationHandler aInvocationHandler = Proxy.getInvocationHandler(annotation);
            aMemberValues = aInvocationHandler.getClass().getDeclaredField("memberValues");
            aMemberValues.setAccessible(true);
            aMap = (Map<String,Object>)aMemberValues.get(aInvocationHandler);
        } catch ( NoSuchFieldException | IllegalAccessException e) {
            log.error(e.getMessage(),e);
            log.error(
                    "未能修改注解 {} 参数 {} 的值到 {}, 直接返回原注解",
                    annotation.getClass().getCanonicalName(),
                    fieldName,
                    gValue);
            return annotation;
        }
        aMap.put(fieldName, gValue);
        aMemberValues.setAccessible(false);
        return annotation;
    }
    @SuppressWarnings("unchecked")
    public static <G,A extends Annotation> A addValueToAnnotation(
            A annotation,
            String fieldName,
            G gValue
    ) {
        Field aMemberValues;
        Map<String,Object> aMap;
        try {
            InvocationHandler aInvocationHandler = Proxy.getInvocationHandler(annotation);
            aMemberValues = aInvocationHandler.getClass().getDeclaredField("memberValues");
            aMemberValues.setAccessible(true);
            aMap = (Map<String,Object>)aMemberValues.get(aInvocationHandler);
        } catch ( NoSuchFieldException | IllegalAccessException e) {
            log.error(e.getMessage(),e);
            log.error(
                    "未能为注解 {} 参数 {} 添加值 {}, 直接返回原注解",
                    annotation.getClass().getCanonicalName(),
                    fieldName,
                    gValue);
            return annotation;
        }
        Object value = aMap.get(fieldName);
        if ( !value.getClass().isArray()) {
            log.error(
                    "注解 {} 的参数 {} 类型不是数组， 不能进行值的添加. 直接返回原注解",
                    annotation.getClass().getCanonicalName(),
                    fieldName);
            return annotation;
        }
        try {
            Object finalValue;
            if(value instanceof int[]){
                finalValue = Ints.concat((int[])value, (new int[]{(Integer) gValue}));
            } else if(value instanceof float[]){
                finalValue = Floats.concat((float[])value, (new float[]{(Float) gValue}));
            } else if(value instanceof boolean[]){
                finalValue = Booleans.concat((boolean[])value, (new boolean[]{(Boolean) gValue}));
            } else if(value instanceof byte[]){
                finalValue = Bytes.concat((byte[])value, (new byte[]{(Byte) gValue}));
            } else if(value instanceof double[]){
                finalValue = Doubles.concat((double[])value, (new double[]{(Double) gValue}));
            } else if(value instanceof char[]){
                finalValue = Chars.concat((char[])value, (new char[]{(Character) gValue}));
            } else if(value instanceof long[]){
                finalValue = Longs.concat((long[])value, (new long[]{(Long) gValue}));
            } else if(value instanceof short[]){
                finalValue = Shorts.concat((short[])value, (new short[]{(Short) gValue}));
            } else if(value instanceof String[]){
                finalValue = new ArrayList(Arrays.asList((String[])value));
                ((List<String>)finalValue).add((String) gValue);
                finalValue =((List<String>)finalValue).toArray(new String[]{});
            } else if(value instanceof Class<?>[]){
                finalValue = new ArrayList(Arrays.asList((Class<?>[])value));
                ((List<Class<?>>)finalValue).add((Class<?>)gValue);
                finalValue =((List<Class<?>>)finalValue).toArray(new Class<?>[]{});
            } else if(value instanceof Enum<?>[]){
                finalValue = new ArrayList(Arrays.asList((Enum<?>[])value));
                ((List<Enum<?>>)finalValue).add((Enum<?>)gValue);
                finalValue =((List<Enum<?>>)finalValue).toArray(new Enum<?>[]{});
            } else if(value instanceof Annotation[]){
                finalValue = new ArrayList(Arrays.asList((Annotation[])value));
                ((List<Annotation>)finalValue).add((Annotation) gValue);
                finalValue =((List<Annotation>)finalValue).toArray(new Annotation[]{});
            } else {
                aMemberValues.setAccessible(false);
                return annotation;
            }
            aMap.put(fieldName,finalValue);
        } catch (ClassCastException e){
            log.error(e.getMessage(),e);
            log.error("值 {} 发生类型转换异常, 直接返回原注解", gValue);
        }
        aMemberValues.setAccessible(false);
        return annotation;
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



    private static final Predicate<Class<? extends Annotation>> RUNTIME_ANNOTATION_CHECKER
            = annotation-> {
                if(Assert.notNull(annotation.getDeclaredAnnotation(Retention.class))&&
                   RetentionPolicy.RUNTIME.equals(annotation.getDeclaredAnnotation(Retention.class).value())
                ) {
                    return true;
                } else {
                    log.warn("注解 {} 无法在运行时获取到, 取消对其的检查", annotation.getCanonicalName());
                    return false;
                }};
    private static final List<ClassLoader> CLASS_LOADER_LIST = new LinkedList<>();
    static {
        CLASS_LOADER_LIST.add(ClasspathHelper.contextClassLoader());
        CLASS_LOADER_LIST.add(ClasspathHelper.staticClassLoader());
    }
    private static final ConfigurationBuilder CONFIGURATION_BUILDER
            = new ConfigurationBuilder()
            .setScanners(
                    new TypeAnnotationsScanner(),
                    new SubTypesScanner(false),
                    new ResourcesScanner())
            .setUrls(
                    ClasspathHelper.forClassLoader(
                            CLASS_LOADER_LIST.toArray(new ClassLoader[0])));
    private static Set<Class<?>> getClassesUseReflections(
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return new Reflections(CONFIGURATION_BUILDER
                .filterInputsBy(new FilterBuilder()
                        .include(filter(
                                packagePath4Seek,
                                isRecursive))))
                .getResources(resourceName -> true)
                .stream()
                .map(resourceName -> resourceName.replaceAll("/",".").replaceAll("^(.*)\\.class$","$1"))
                .map(className -> {
                    try {
                        return ClassUtils.getClass(className);
                    } catch (ClassNotFoundException e) { return null; }
                })
                .collect(Collectors.toSet());
    }
    @SuppressWarnings("unchecked")
    private static <T> Set<Class<? extends T>> getClassesExtendClassUseReflections(
            final Class<T> tUpperBoundClass,
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return getClassesUseReflections(packagePath4Seek, isRecursive)
                .stream()
                .filter(selfClass -> Assert.notNull(selfClass) && !tUpperBoundClass.equals(selfClass) && ClassUtils.isAssignable(selfClass,tUpperBoundClass))
                .map(selfClass -> (Class<? extends T>) selfClass)
                .collect(Collectors.toSet());
    }
    @SuppressWarnings("unchecked")
    private static <T> Set<Class<? super T>> getClassesSuperClassUseReflections(
            final Class<T> tLowerBoundClass,
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return getClassesUseReflections(packagePath4Seek, isRecursive)
                .stream()
                .filter(selfClass -> Assert.notNull(selfClass) && !tLowerBoundClass.equals(selfClass) && ClassUtils.isAssignable(tLowerBoundClass,selfClass))
                .map(selfClass -> (Class<? super T>)selfClass)
                .collect(Collectors.toSet());
    }
    private static Set<Class<?>> getClassesWithAnnotationUseReflections(
            final Class<? extends Annotation> annotationClass,
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return new Reflections(CONFIGURATION_BUILDER
                .filterInputsBy(new FilterBuilder()
                        .include(filter(
                                packagePath4Seek,
                                isRecursive))))
                .getTypesAnnotatedWith(annotationClass);
    }
    @SuppressWarnings("unchecked")
    private static Set<Class<?>> getClassesWithInterfaceUseReflections(
            final Class interfaceClass,
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return new Reflections(CONFIGURATION_BUILDER
                .filterInputsBy(new FilterBuilder()
                        .include(filter(
                                packagePath4Seek,
                                isRecursive))))
                .getSubTypesOf(interfaceClass);
    }
    private static String filter(
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return isRecursive ? FilterBuilder.prefix(packagePath4Seek) :  packagePath4Seek.replace(".","\\.")+"\\.[a-zA-Z0-9$_]+\\.class";
    }

    private static Class<?> getGenericClass(
            final ParameterizedType parameterizedType,
            final int ... pos
    ) {
        Type type = getGenericType(parameterizedType, pos);
        if (type==null) { return null;}
        try {
            return Class.forName(type.getTypeName().replaceAll("^([^<]+?)<.*$","$1"));
        } catch (ClassNotFoundException e) { log.error(e.getMessage(),e); return null;}
    }
    private static Type getGenericType(
            final ParameterizedType parameterizedType,
            final int ... pos
    ) {
        if (Assert.isNull(parameterizedType) || Assert.isNul(pos)) {
            return null;
        }
        ParameterizedType parameterizedTypeTemp = parameterizedType;
        for (int level=0; level<pos.length; level++) {
            Type type = getTypeWithPos(parameterizedTypeTemp, pos[level]);
            if (type==null) { return null; }
            //PARAMETERIZED_TYPE_IMPL_CLASS.isAssignableFrom(type.getClass())
            if (type instanceof ParameterizedType) {
                parameterizedTypeTemp = (ParameterizedType)type;
            } else {
                return level+1==pos.length ? type : null;
            }

        }
        return parameterizedTypeTemp;
    }
    private static Type getTypeWithPos(
            final ParameterizedType parameterizedType,
            final int pos
    ) {
        if (Assert.isNull(parameterizedType) || pos < 0) {return null;}
        try {
            Type[] types = parameterizedType.getActualTypeArguments();
            if (types.length <= pos) {
                return null;
            } else {
                return types[pos];
            }
        } catch (TypeNotPresentException|MalformedParameterizedTypeException e) {
            return null;
        }
    }
}

