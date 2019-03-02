package com.guojy;

import com.google.common.collect.Sets;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.guojy.Assert.*;


/**
 * 类工具
 *
 * <p> 创建时间：2018/8/10
 *
 * @author guojy
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE)
public final class ClassUtil {

    public static String getTopPackageName() { return ClassUtil.class.getPackage().getName(); }

    public static <T> Set<Class<? extends T>> getClassesExtendClass( @NonNull Class<T> tClass, @NonNull String packagePath, boolean recursive) {
        return true ?
                getClassesExtendClassUseReflections( tClass, packagePath, recursive) :
                getClassesExtendClassUseRaw( tClass, packagePath, recursive);
    }
    public static <T> Set<Class<? super T>> getClassesSuperClass( @NonNull Class<T> tClass, @NonNull String packagePath, boolean recursive) {
        return true ?
                getClassesSuperClassUseReflections( tClass, packagePath, recursive) :
                getClassesSuperClassUseRaw( tClass, packagePath, recursive);
    }
    public static <T extends Annotation> Set<Class<?>> getClassesWithAnnotationMarked( @NonNull Class<T> annotationPara, @NonNull String packagePath, boolean recursive) {
        return getClassesWithAnnotation(annotationPara, packagePath, recursive);
    }
    public static Set<Class<?>> getClassesWithInterfaceImplemented( @NonNull Class interfaceType, @NonNull String packagePath, boolean recursive) {
        return getClassesWithInterface( interfaceType, packagePath, recursive);
    }
    /**
     * 查找使用特定注解标注的类
     *
     * @param annotationPara 目标注解类型
     * @param packagePath 起始包路径
     * @param recursive 是否迭代查询
     * @return 类集合
     * */
    public static Set<Class<?>> getClassesWithAnnotation( @NonNull Class<? extends Annotation> annotationPara, @NonNull String packagePath, boolean recursive) {
        return true ?
                getClassesWithAnnotationUseReflections( annotationPara, packagePath, recursive) :
                getClassesWithAnnotationUseRaw( annotationPara, packagePath, recursive);
    }
    /**
     * 查找实现特定接口的类
     *
     * @param interfaceType 目标接口类型
     * @param packagePath 起始包路径
     * @param recursive 是否迭代查询
     * @return 类集合
     * */
    public static Set<Class<?>> getClassesWithInterface( @NonNull Class interfaceType, @NonNull String packagePath, boolean recursive) {
        return true ?
                getClassesWithInterfaceUseReflections( interfaceType, packagePath, recursive) :
                getClassesWithInterfaceUseRaw( interfaceType, packagePath, recursive);
    }
    /**
     * 判断某个对象是否有且只有一个注解集合中的注解
     *
     * @param annotatedElement 判定对象
     * @param annotations 注解集合
     * @return 当成功找到时, 返回唯一注解; 否则返回null
     * */
    @SafeVarargs
    public static Class<? extends Annotation> getTheOnlyOneAnnotation(AnnotatedElement annotatedElement, Class< ? extends Annotation> ... annotations) {
        Set<Class<? extends Annotation>> annotationsSetOfField = Stream.of( annotatedElement.getDeclaredAnnotations()).map( Annotation::annotationType).collect( Collectors.toSet());
        Set<Class<? extends Annotation>> annotationsSetOfParam = Stream.of( annotations).collect( Collectors.toSet());
        @SuppressWarnings("unchecked")
        Class<? extends Annotation> annotationClass =
                ( notNul( annotationsSetOfField) &&
                        notNul( annotationsSetOfParam) &&
                        annotationsSetOfParam.retainAll( annotationsSetOfField)&&annotationsSetOfParam.size()==1) ?
                        ( Class<? extends Annotation>)annotationsSetOfParam.toArray()[0] :
                        null;
        return annotationClass;
    }
    /**
     * 判断某个对象是否有且只有一个注解集合中的注解
     *
     * @param annotatedElement 判定对象
     * @param annotationClassesSet 注解集合
     * @return 当成功找到时, 返回唯一注解; 否则返回null
     * */
    public static Class<? extends Annotation> getTheOnlyOneAnnotation(AnnotatedElement annotatedElement, Set<Class< ? extends Annotation>> annotationClassesSet) {
        Set<Class<? extends Annotation>> annotationsSetOfField = Stream.of( annotatedElement.getDeclaredAnnotations()).map( Annotation::annotationType).collect( Collectors.toSet());
        Set<Class<? extends Annotation>> annotationsSetOfParam = Sets.newHashSet(annotationClassesSet);
        @SuppressWarnings("unchecked")
        Class<? extends Annotation> annotationClass =
                ( notNul( annotationsSetOfField) &&
                        notNul( annotationsSetOfParam) &&
                        annotationsSetOfParam.retainAll( annotationsSetOfField)&&annotationsSetOfParam.size()==1) ?
                        ( Class<? extends Annotation>)annotationsSetOfParam.toArray()[0] :
                        null;
        return annotationClass;
    }

    private static final String EXTENSION_CLASS = ".class";
    private static final String REGEX_CLASS = "\\.class$";
    private static final String NUL = "";
    private static final String DOT = ".";
    private static final String FILE = "file";
    private static final String JAR = "jar";
    private static final List<ClassLoader> CLASS_LOADER_LIST = new LinkedList<>();
    static {
        CLASS_LOADER_LIST.add( ClasspathHelper.contextClassLoader());
        CLASS_LOADER_LIST.add( ClasspathHelper.staticClassLoader());
    }

    @SuppressWarnings("unchecked")
    private static <T> Set<Class<? extends T>> getClassesExtendClassUseReflections( final Class<T> tClass, String packagePath, boolean recursive) {
        return getClassesUseReflections( packagePath, recursive)
                .stream()
                .filter( selfClass -> !tClass.equals(selfClass) && tClass.isAssignableFrom(selfClass))
                .map( selfClass -> (Class<? extends T>) selfClass)
                .collect( Collectors.toSet());
    }
    @SuppressWarnings("unchecked")
    private static <T> Set<Class<? extends T>> getClassesExtendClassUseRaw( final Class<T> tClass, String packagePath, boolean recursive) {
        return getClassesUseRaw( packagePath, recursive)
                .stream()
                .filter( selfClass -> !tClass.equals(selfClass) && tClass.isAssignableFrom(selfClass))
                .map( selfClass -> (Class<? extends T>)selfClass)
                .collect( Collectors.toSet());
    }
    @SuppressWarnings("unchecked")
    private static <T> Set<Class<? super T>> getClassesSuperClassUseReflections( final Class<T> tClass, String packagePath, boolean recursive) {
        return getClassesUseReflections( packagePath, recursive)
                .stream()
                .filter( selfClass -> !tClass.equals(selfClass) && selfClass.isAssignableFrom(tClass))
                .map( selfClass -> (Class<? super T>)selfClass)
                .collect( Collectors.toSet());
    }
    @SuppressWarnings("unchecked")
    private static <T> Set<Class<? super T>> getClassesSuperClassUseRaw( final Class<T> tClass, String packagePath, boolean recursive) {
        return getClassesUseRaw( packagePath, recursive)
                .stream()
                .filter( selfClass -> !tClass.equals(selfClass) && selfClass.isAssignableFrom(tClass))
                .map( selfClass -> (Class<? super T>)selfClass)
                .collect( Collectors.toSet());
    }
    /**
     * 查找使用特定注解标注的类(使用原生方法查找, 主要用于使用原生classloader的情况)
     *
     * @param annotationPara 目标注解类型
     * @param packagePath 起始包路径
     * @param recursive 是否迭代查询
     * @return 类集合
     * */
    private static Set<Class<?>> getClassesWithAnnotationUseRaw( final Class<? extends Annotation> annotationPara, String packagePath, boolean recursive) {
        return getClassesUseRaw( packagePath, recursive)
                .stream()
                .filter( selfClass -> !( selfClass.isInterface() || selfClass.isAnnotation() || selfClass.isEnum()) && selfClass.getDeclaredAnnotation( annotationPara)!=null)
                .collect( Collectors.toSet());
    }
    /**
     * 查找实现特定接口的类
     *
     * @param interfaceType 目标接口类型
     * @param packagePath 起始包路径
     * @param recursive 是否迭代查询
     * @return 描述返回值
     * */
    private static Set<Class<?>> getClassesWithInterfaceUseRaw( final Class interfaceType, String packagePath, boolean recursive) {
        return getClassesUseRaw( packagePath, recursive)
                .stream()
                .filter( selfClass -> !selfClass.isInterface() && Stream.of( selfClass.getInterfaces()).anyMatch( selfInterface -> selfInterface.equals( interfaceType)))
                .collect( Collectors.toSet());
    }
    /**
     * 查找使用特定注解标注的类(使用反射方法查找, 主要用于使用自定义classloader的情况)
     *
     * @param annotationPara 目标注解类型
     * @param packagePath 起始包路径
     * @param recursive 是否迭代查询
     * @return 类集合
     * */
    private static Set<Class<?>> getClassesWithAnnotationUseReflections( final Class<? extends Annotation> annotationPara, String packagePath, boolean recursive) {
        return new Reflections( CONFIGURATION_BUILDER.filterInputsBy( new FilterBuilder().include( recursive ? FilterBuilder.prefix( packagePath) : packagePath.replace(".","\\.")+"\\.\\w+.class"))).getTypesAnnotatedWith( annotationPara);
    }
    /**
     * 查找实现特定接口的类(使用反射方法查找, 主要用于使用自定义classloader的情况)
     *
     * @param interfaceType 目标接口类型
     * @param packagePath 起始包路径
     * @param recursive 是否迭代查询
     * @return 类集合
     * */
    private static Set<Class<?>> getClassesWithInterfaceUseReflections( final Class interfaceType, String packagePath, boolean recursive) {
        return new Reflections( CONFIGURATION_BUILDER.filterInputsBy( new FilterBuilder().include(recursive ? FilterBuilder.prefix( packagePath) : packagePath.replace(".","\\.")+"\\.\\w+.class"))).getSubTypesOf( interfaceType);
    }


    private static final ConfigurationBuilder CONFIGURATION_BUILDER = new ConfigurationBuilder().setScanners( new SubTypesScanner(false ), new ResourcesScanner(), new TypeAnnotationsScanner()).setUrls( ClasspathHelper.forClassLoader( CLASS_LOADER_LIST.toArray( new ClassLoader[0])));
    /**
     * <p> 使用反射方法获取特定包路径下的所有类
     *
     * @param packagePath 包路径
     * @param recursive 是否迭代
     * @return 类集合
     * */
    private static Set<Class<?>> getClassesUseReflections( String packagePath, boolean recursive) {
        return new Reflections( CONFIGURATION_BUILDER.filterInputsBy( new FilterBuilder().include( recursive ? FilterBuilder.prefix( packagePath) : packagePath.replace(".","\\.")+"\\.\\w+.class"))).getSubTypesOf(Object.class);
    }
    /**
     * <p> 使用原生方法获取特定包路径下的所有类
     *
     * @param packagePath 包路径
     * @param recursive 是否迭代
     * @return 类集合
     * */
    private static Set<Class<?>> getClassesUseRaw( String packagePath, boolean recursive) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        String packageDirPath = packagePath.replace('.', '/');
        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources( packageDirPath);
            while ( dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                switch ( url.getProtocol()) {
                    /*<开始>更改者: guojy 更改时间: 2018/12/21 变更原因: 代码味道*/
                    case FILE : findAndAddClassesInPackageByFile( packagePath, URLDecoder.decode( url.getFile(), "UTF-8"), recursive, classes); break;
                    /*<结束>更改者: guojy 更改时间: 2018/12/21 */
                    case JAR: findAndAddClassesInPackageByJar( packagePath, packageDirPath, url, recursive, classes); break;
                    default: log.warn( " 不支持的协议 : {}", url.getProtocol()); break;
                }
            }
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
        }
        return classes;
    }
    /**
     * 以jar的形式来获取包下的所有Class
     *
     * @param packageName 包名
     * @param packagePath 包文件路径
     * @param url 文件统一地址
     * @param recursive 是否迭代
     * @param classes 类容器
     * */
    private static void findAndAddClassesInPackageByJar( String packageName, String packagePath, URL url, final boolean recursive, Set<Class<?>> classes) {
        JarFile jar;
        try {
            jar = ( ( JarURLConnection) url.openConnection()).getJarFile();
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            return;
        }
        Enumeration<JarEntry> entries = jar.entries();
        while ( entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if ( name.charAt( 0)=='/') { name = name.substring( 1); }
            if ( name.startsWith( packagePath)) {
                int idx = name.lastIndexOf('/');
                if ( idx!=-1) {
                    packageName = name.substring( 0, idx).replace('/', '.');
                }
                if ( (idx!=-1 || recursive) && (name.endsWith( EXTENSION_CLASS) && !entry.isDirectory())) {
                    try {
                        classes.add( Thread.currentThread().getContextClassLoader().loadClass( packageName + DOT + name.replaceAll( REGEX_CLASS, NUL)));
                    } catch ( ClassNotFoundException e) {
                        log.error( e.getMessage(), e);
                    }
                }
            }
        }
    }
    /*<开始>更改者: guojy 更改时间: 2018/12/21 变更原因: 代码味道*/
    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName 包名
     * @param recursive 是否迭代
     * @param classes 类容器
     */
    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName 包名
     * @param packagePath 包文件路径
     * @param recursive 是否迭代
     * @param classes 类容器
     */
    private static void findAndAddClassesInPackageByFile( String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        File dir = new File( packagePath);
        if ( !dir.exists() || !dir.isDirectory()) { return; }
        File[] files = dir.listFiles( file ->  ( recursive && file.isDirectory()) || ( file.getName().endsWith( EXTENSION_CLASS)));
        if ( notNul( files)) {
            for (File file : files) {
                if ( file.isDirectory()) {
                    findAndAddClassesInPackageByFile( packageName + DOT + file.getName(), file.getAbsolutePath(), recursive, classes);
                } else {
                    try {
                        classes.add( Thread.currentThread().getContextClassLoader().loadClass( packageName + DOT + file.getName().replaceAll( REGEX_CLASS, NUL)));
                    } catch ( ClassNotFoundException e) {
                        log.error( e.getMessage(), e);
                    }
                }
            }
        } else { }
    }
    /*<结束>更改者: guojy 更改时间: 2018/12/21 */

    private static final Class PARAMETERIZED_TYPE_IMPL_CLASS = $Gson$Types.canonicalize(new TypeToken<Iterable<String>>(){}.getType()).getClass();

    public static boolean isType(@NonNull Field field, @NonNull Type type) {
        return type.equals(field.getType());
    }
    public static boolean isGenericType(@NonNull Field field, @NonNull Type type) {
        if ( !(field.getGenericType() instanceof ParameterizedType)) { return false;}
        ParameterizedType parameterizedType = ((ParameterizedType)field.getGenericType());
        return type.equals(parameterizedType);
    }

    public static Class<?> getGenericClass(@NonNull ParameterizedType parameterizedType, int ... pos) {
        Type type = getGenericType( parameterizedType, pos);
        try {
            return Class.forName(type.getTypeName().replaceAll("^([^<]+?)<.*$","$1"));
        } catch ( ClassNotFoundException e) { log.error(e.getMessage(),e); return null;}
    }
    public static Class<?> getGenericClass(@NonNull Field field, int ... pos) {
        if ( !(field.getGenericType() instanceof ParameterizedType)) { log.warn("{} 没有泛型参数", field.getGenericType()); return null;}
        return getGenericClass(((ParameterizedType)field.getGenericType()), pos);
    }
    public static <O> Class<?> getGenericClass(@NonNull TypeToken<O> oTypeToken, int ... pos) {
        if ( !(oTypeToken.getType() instanceof ParameterizedType)) { log.warn("{} 没有泛型参数", oTypeToken.getType()); return null;}
        return getGenericClass(((ParameterizedType)oTypeToken.getType()), pos);
    }

    public static Type getGenericType(@NonNull ParameterizedType parameterizedType, int ... pos) {
        if ( isNull(parameterizedType) || isNul(pos)) {
            return null;
        } else {
            ParameterizedType parameterizedTypeTemp = parameterizedType;
            for ( int i=0; i<pos.length; i++) {
                Type type = getTypeWithPos(parameterizedTypeTemp, pos[i]);
                if (type.getClass().isAssignableFrom(PARAMETERIZED_TYPE_IMPL_CLASS)) {
                    parameterizedTypeTemp = (ParameterizedType)type;
                    continue;
                } else {
                    if(i+1==pos.length) {
                        return type;
                    } else {
                        log.warn("解析未结束, 但 {} 没有泛型, 无法继续解析", type.getTypeName());
                        return null;
                    }
                }
            }
            return parameterizedTypeTemp;
        }
    }
    public static Type getGenericType(@NonNull Field field, int ... pos) {
        if ( !(field.getGenericType() instanceof ParameterizedType)) { log.warn("{} 没有泛型参数", field.getGenericType()); return null;}
        return getGenericType(((ParameterizedType)field.getGenericType()), pos);
    }
    public static <O> Type getGenericType(@NonNull TypeToken<O> oTypeToken, int ... pos) {
        if ( !(oTypeToken.getType() instanceof ParameterizedType)) { log.warn("{} 没有泛型参数", oTypeToken.getType()); return null;}
        return getGenericType(((ParameterizedType)oTypeToken.getType()), pos);
    }
    private static Type getTypeWithPos(ParameterizedType parameterizedType, int pos) {
        if ( isNull(parameterizedType) || pos < 0) {return null;}
        try {
            Type[] types = parameterizedType.getActualTypeArguments();
            if ( types.length < pos) {
                return null;
            } else {
                return types[pos];
            }
        } catch (TypeNotPresentException|MalformedParameterizedTypeException e) {
            return null;
        }
    }
}

