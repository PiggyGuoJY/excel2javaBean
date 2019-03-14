
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

import static com.github.piggyguojy.Assert.notNul;


/**
 *      类{@code ClassUtil}主要提供了获取特定类集合、动态修改注解参数、注解分析和泛型动态分析等工具.
 * <p>
 *      关于{@link ClassUtil#getGenericClass(Field, int...)}和
 * {@link ClassUtil#getGenericClass(TypeToken, int...)}中参数pos的解释:
 * <p>
 *      考虑如下泛型定义:
 * <blockquote><pre>
 * Map&lt;Set&lt;Map&lt;List&lt;Map&lt;String,Integer&gt;&gt;,BiFunction&lt;Long,Map&lt;String,Function&lt;String,Long&gt;&gt;,Boolean&gt;&gt;&gt;,BiFunction&lt;Class&lt;Byte&gt;,Map&lt;List&lt;Boolean&gt;,Long&gt;,String&gt;&gt;
 * </pre></blockquote>
 * <p>
 *      或者展开来看:
 *  <ul>
 *	<li>Map
 *		<ol start="0" style="border-left:gray dashed thin;">
 *			<li>Set <span style="position:absolute;left:400px">-&gt;[0]</span>
 *				<ol start="0" style="border-left:gray dashed thin;">
 *					<li>Map <span style="position:absolute;left:400px">-&gt;[0,0]</span>
 *						<ol start="0" style="border-left:gray dashed thin;">
 *							<li>List <span style="position:absolute;left:400px">-&gt;[0,0,0]</span>
 *								<ol start="0" style="border-left:gray dashed thin;">
 *									<li>Map <span style="position:absolute;left:400px">-&gt;[0,0,0,0]</span>
 *										<ol start="0" style="border-left:gray dashed thin;">
 *											<li>String <span style="position:absolute;left:400px">-&gt;[0,0,0,0,0]</span></li>
 *											<li>Integer <span style="position:absolute;left:400px">-&gt;[0,0,0,0,1]</span></li>
 *										</ol>
 *									</li>
 *								</ol>
 *							</li>
 *							<li>BiFunction <span style="position:absolute;left:400px">-&gt;[0,0,1]</span>
 *								<ol start="0" style="border-left:gray dashed thin;">
 *									<li>String <span style="position:absolute;left:400px">-&gt;[0,0,1,0]</span></li>
 *									<li>Function <span style="position:absolute;left:400px">-&gt;[0,0,1,1]</span>
 *										<ol start="0" style="border-left:gray dashed thin;">
 *											<li>String <span style="position:absolute;left:400px">-&gt;[0,0,1,1,0]</span></li>
 *											<li>Long <span style="position:absolute;left:400px">-&gt;[0,0,1,1,1]</span></li>
 *										</ol>
 *									</li>
 *									<li>Boolean <span style="position:absolute;left:400px">-&gt;[0,0,1,2]</span></li>
 *								</ol>
 *							</li>
 *						</ol>
 *					</li>
 *				</ol>
 *			</li>
 *			<li>BiFunction <span style="position:absolute;left:400px">-&gt;[1]</span>
 *				<ol start="0" style="border-left:gray dashed thin;">
 *					<li>Class <span style="position:absolute;left:400px">-&gt;[1,0]</span>
 *						<ol start="0" style="border-left:gray dashed thin;">
 *							<li>Byte <span style="position:absolute;left:400px">-&gt;[1,0,0]</span></li>
 *						</ol>
 *					</li>
 *					<li>Map <span style="position:absolute;left:400px">-&gt;[1,1]</span>
 *						<ol start="0" style="border-left:gray dashed thin;">
 *							<li>List <span style="position:absolute;left:400px">-&gt;[1,1,0]</span>
 *								<ol start="0" style="border-left:gray dashed thin;">
 *									<li>Boolean <span style="position:absolute;left:400px">-&gt;[1,1,1]</span></li>
 *								</ol>
 *							</li>
 *						</ol>
 *					</li>
 *					<li>String <span style="position:absolute;left:400px">-&gt;[1,2]</span></li>
 *				</ol>
 *			</li>
 *		</ol>
 *	</li>
 *  </ul>
 * <p>
 *      则称上图右边的数列集合中数列是可以找到泛型子元素的, 举例来说:
 * <blockquote><pre>
 * TypeToken&lt;Map&lt;Set&lt;Map&lt;List&lt;Map&lt;String,Integer&gt;&gt;,BiFunction&lt;Long,Map&lt;String,Function&lt;String,Long&gt;&gt;,Boolean&gt;&gt;&gt;,BiFunction&lt;Class&lt;Byte&gt;,Map&lt;List&lt;Boolean&gt;,Long&gt;,String&gt;&gt;&gt; typeToken
 *     = new TypeToken&lt;Map&lt;Set&lt;Map&lt;List&lt;Map&lt;String,Integer&gt;&gt;,BiFunction&lt;Long,Map&lt;String,Function&lt;String,Long&gt;&gt;,Boolean&gt;&gt;&gt;,BiFunction&lt;Class&lt;Byte&gt;,Map&lt;List&lt;Boolean&gt;,Long&gt;,String&gt;&gt;&gt;(){}.getType();
 * assertNotNull(ClassUtil.getGenericClass(typeToken,1,1,1));//通过
 * assertNotNull(ClassUtil.getGenericClass(typeToken,1,1,2));//失败
 * assertEquals(ClassUtil.getGenericClass(typeToken,1,1,1),Boolean.class);//通过
 * assertEquals(ClassUtil.getGenericClass(typeToken,1),BiFunction.class);//通过
 * assertNotEquals(ClassUtil.getGenericClass(typeToken,1,1,1),BiFunction.class);//失败
 * </pre></blockquote>
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 *
 * @see Assert
 * @see JsonUtil
 * @see Msg
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassUtil {

    /**
     * 返回项目的顶级包名
     *
     * @return 包名
     */
    public static String getTopPackageName() {
        return ClassUtil.class.getPackage().getName();
    }

    /**
     * 获取指定类在特定位置下的所有子类类型
     *
     * @param tUpperBoundClass 父类类型
     * @param packagePath4Seek 包位置
     * @param isRecursive 是否迭代查找
     * @param <T> 父类泛型
     * @return 子类类型集合(不存在时返回空集)
     */
    public static <T> Set<Class<? extends T>> getClassesExtendClass(
            final Class<T> tUpperBoundClass,
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return getClassesExtendClassUseReflections(
                tUpperBoundClass,
                packagePath4Seek,
                isRecursive);
    }
    /**
     * 获取指定类在特定位置下的所有父类类型
     *
     * @param tLowerBoundClass 子类类型
     * @param packagePath4Seek 包位置
     * @param isRecursive 是否迭代查找
     * @param <T> 子类泛型
     * @return 父类类型集合(不存在时返回空集)
     */
    public static <T> Set<Class<? super T>> getClassesSuperClass(
            final Class<T> tLowerBoundClass,
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return getClassesSuperClassUseReflections(
                tLowerBoundClass,
                packagePath4Seek,
                isRecursive);
    }
    /**
     * 在特定位置下查找所有标注特定注解的类类型
     *
     * @param annotationClass 注解类型
     * @param packagePath4Seek 包位置
     * @param isRecursive 是否迭代查找
     * @param <T> 注解泛型
     * @return 注解类型集合(不存在时返回空集)
     */
    public static <T extends Annotation> Set<Class<?>> getClassesWithAnnotationMarked(
            final Class<T> annotationClass,
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return getClassesWithAnnotationUseReflections(
                annotationClass,
                packagePath4Seek,
                isRecursive);
    }
    /**
     * 在特定位置下查找所有实现特定注接口的类类型
     *
     * @param interfaceClass 接口类型
     * @param packagePath4Seek 包位置
     * @param isRecursive 是否迭代查找
     * @return 实现类集合(不存在时返回空集)
     */
    public static Set<Class<?>> getClassesWithInterfaceImplemented(
            final Class interfaceClass,
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return getClassesWithInterfaceUseReflections(
                interfaceClass,
                packagePath4Seek,
                isRecursive);
    }

    /**
     * 判断可注解元素上是否有且只有一个注解和测试集合元素重合
     *
     * @param annotatedElement 可注解元素
     * @param annotations 测试注解集合
     * @return 重合的注解或null(如果不存在)
     */
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
                (notNul(annotationsSetOfField) &&
                notNul(annotationsSetOfParam) &&
                annotationsSetOfParam.retainAll(annotationsSetOfField)&&
                annotationsSetOfParam.size()==1) ?
                        (Class<? extends Annotation>)annotationsSetOfParam.toArray()[0] :
                        null;
        return annotationClass;
    }
    /**
     * 判断可注解元素上是否有且只有一个注解和测试集合元素重合
     *
     * @param annotatedElement 可注解元素
     * @param annotationClassesSet 测试注解集合
     * @return 重合的注解或null(如果不存在)
     */
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
                (notNul(annotationsSetOfField) &&
                notNul(annotationsSetOfParam) &&
                annotationsSetOfParam.retainAll(annotationsSetOfField)&&
                annotationsSetOfParam.size()==1) ?
                        (Class<? extends Annotation>)annotationsSetOfParam.toArray()[0] :
                        null;
        return annotationClass;
    }

    /**
     * 从类属性上按指定顺序查找泛型类型
     *
     * @param field 类属性
     * @param pos 泛型位置(关于泛型位置, 详见{@link ClassUtil}关于泛型位置的注释)
     * @return 泛型类型或null(如果无法找到)
     */
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

    /**
     * 从{@code TypeToken}上按指定顺序查找泛型类型
     *
     * @param oTypeToken 类型Token
     * @param pos 泛型位置(关于泛型位置, 详见{@link ClassUtil}关于泛型位置的注释)
     * @param <O> 原类型泛型
     * @return 泛型类型或null(如果无法找到)
     */
    public static <O> Class<?> getGenericClass(
            final TypeToken<O> oTypeToken,
            final int ... pos) {
        if (!(oTypeToken.getType() instanceof ParameterizedType)) {
            log.warn("类型 {} 没有泛型参数", oTypeToken.getType());
            return null;
        }
        return getGenericClass(((ParameterizedType)oTypeToken.getType()), pos);
    }

    /**
     * 修改注解属性的值
     *
     * @param annotation 注解实例
     * @param fieldName 注解属性名
     * @param gValue 新属性值
     * @param <G> 注解属性的类型(8种基本数据类型、String、Class、枚举、注解和以上类型的数组)
     * @param <A> 注解泛型
     * @return 修改过的注解实例
     */
    @SuppressWarnings("unchecked")
    public static <G, A extends Annotation> A changeAnnotationFieldValue(
            A annotation,
            String fieldName,
            G gValue
    ) {
        Field memberValues;
        Map<String,Object> map;
        try {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            memberValues = invocationHandler.getClass().getDeclaredField("memberValues");
            memberValues.setAccessible(true);
            map = (Map<String,Object>)memberValues.get(invocationHandler);
        } catch ( NoSuchFieldException | IllegalAccessException e) {
            log.error(e.getMessage(),e);
            log.error(
                    "未能修改注解 {} 参数 {} 的值到 {}, 直接返回原注解",
                    annotation.getClass().getCanonicalName(),
                    fieldName,
                    gValue);
            return annotation;
        }
        map.put(fieldName, gValue);
        memberValues.setAccessible(false);
        return annotation;
    }
    /**
     * 增加单个值到注解的数组类型属性中
     *
     * @param annotation 注解实例
     * @param fieldName 注解属性名
     * @param gValue 新增属性值
     * @param <G> 注解属性的类型(8种基本数据类型、String、Class、枚举、注解和以上类型的数组)
     * @param <A> 注解泛型
     * @return 修改过的注解实例
     */
    @SuppressWarnings("unchecked")
    public static <G,A extends Annotation> A addValueToAnnotationArrayField(
            A annotation,
            String fieldName,
            G gValue
    ) {
        Field memberValues;
        Map<String,Object> map;
        try {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            memberValues = invocationHandler.getClass().getDeclaredField("memberValues");
            memberValues.setAccessible(true);
            map = (Map<String,Object>)memberValues.get(invocationHandler);
        } catch ( NoSuchFieldException | IllegalAccessException e) {
            log.error(e.getMessage(),e);
            log.error(
                    "未能为注解 {} 参数 {} 添加值 {}, 直接返回原注解",
                    annotation.getClass().getCanonicalName(),
                    fieldName,
                    gValue);
            return annotation;
        }
        Object value = map.get(fieldName);
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
                memberValues.setAccessible(false);
                return annotation;
            }
            map.put(fieldName,finalValue);
        } catch (ClassCastException e){
            log.error(e.getMessage(),e);
            log.error("值 {} 发生类型转换异常, 直接返回原注解", gValue);
        }
        memberValues.setAccessible(false);
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
     * @param <T> 目标泛型
     * @param tClass 目标类型类
     * @return 目标类型实例
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
                        .include(filter(packagePath4Seek, isRecursive))))
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
                        .include(filter(packagePath4Seek, isRecursive))))
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
                        .include(filter(packagePath4Seek, isRecursive))))
                .getSubTypesOf(interfaceClass);
    }
    private static String filter(
            final String packagePath4Seek,
            final boolean isRecursive
    ) {
        return isRecursive ? FilterBuilder.prefix(packagePath4Seek) : packagePath4Seek.replace(".","\\.")+"\\.[a-zA-Z0-9$_]+\\.class";
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

