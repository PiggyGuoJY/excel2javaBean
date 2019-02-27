package com.guojy;

import com.google.common.base.Charsets;
import com.guojy.ftp.AbstractFtpService;
import com.guojy.model.Msg;
import com.tkp.tkpole.starter.utils.ftp.AbstractFtpService;
import com.tkp.tkpole.starter.utils.model.Msg;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.stream.Stream;

import static com.tkp.tkpole.starter.utils.Assert.*;

/**
 * 1.文件工具类(这里主要是一些杂七杂八的工具)
 * 2.静态获取bean实例
 * <p> 创建时间：2018/2/11
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE)
public final class ResourceUtil {

    /**
     * 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param path 描述此参数的作用
     * @param args 描述此参数的作用
     * @return 描述返回值
     * */
    public static String changeFileSeparatorToString(Path path, boolean ... args) {
        return AbstractFtpService.changeFileSeparator( path, args);
    }

    /**
     * 获取当前项目所在的根路径
     *
     * @return 路径
     * */
    public static String getRuntimePath() {
        URL url = ClassUtils.getDefaultClassLoader().getResource( NUL);
        String path;
        if ( notNull( url)) { path = url.toString(); } else {
            log.warn("未能获取资源位置, 可能影响后续功能使用");
            return null;
        }
        String result = isRunWithSpringJar() ?
                path.replaceAll( REGEX_PATH_JAR, "$1//$2") :
                path.replaceAll( REGEX_PATH_FILE,"$1//$2");
        log.debug( "成功获取项目所在目录路径: {}", result);
        return result;
    }


    /**
     * 转换Path到File(如果Path不能直接转化为File, 在运行路径下新建temp文件, 并将文件保存在这里)
     *
     * @param srcPath 源文件Path
     * @return 当转换成功时, 返回文件; 否则返回NULL
     * */
    public static File path2File( Path srcPath) {
        return path2File( srcPath, getRuntimePath() + "/temp");
    }
    /**
     * 转换Path到File
     *
     * @param srcPath 源文件Path(任意实现都可以)
     * @param position 保存目录(当srcPath可以正常转化为File时, 此参数不生效)
     * @return 当转换成功时, 返回文件; 否则返回NULL
     * */
    public static File path2File( Path srcPath, String position) {
        try {
            return srcPath.toFile();
        } catch ( Exception e) {
            log.warn( "该Path对象不不支持toFile方法(该Path对象可能由第三方FileSystem实现), 将在本地创建临时目录");
            log.warn( e.getMessage(), e);
        }
        if ( notNul( position) && notNull( srcPath.getFileName())) {
            try {
                Path tempPath = Paths.get( URI.create( addSeparator(
                        position,
                        srcPath.getFileName().toString())));
                return Files.copy( srcPath, tempPath , StandardCopyOption.REPLACE_EXISTING).toFile();
            } catch ( IOException e) {
                log.error( e.getMessage(), e);
                return null;
            }
        } else {
            log.warn( "不进行本地创建, position {} 为空 或 不能取得文件名", position);
            return null;
        }
    }

    private static String addSeparator( String ... args) {
        String defaultSeparator = "/";
        for ( String s : args) {
            if ( s.contains("/")) { defaultSeparator = "/"; break;
            } else if ( s.contains("\\")) { defaultSeparator = "\\"; break; }
        }
        final String separator = defaultSeparator;
        final StringBuilder stringBuilder = new StringBuilder();
        Stream.of(args).forEach(s -> stringBuilder.append(s.endsWith(separator)?s:(s+separator)));
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }

    /**
     * <p> 获取文件的拓展名
     *
     * @param fileName 文件名
     * @return 拓展名,当文件没有拓展名时返回""
     * */
    public static String getExtensionName( String fileName) {
        int pos;
        return notNul( fileName) ? ( ( pos = fileName.lastIndexOf( '.'))==-1 ? "" : fileName.substring( pos+1)) : "";
    }

    /**
     * <p> 获取特定properties文件中的属性
     *
     * @param path classpath下的文件目录
     * @param key key值
     * @return 对应的value
     * */
    public static Msg<String> getProperty(@NonNull String path, @NonNull String key) {

        Properties properties = new Properties();
        try (
                FileInputStream fileInputStream = new FileInputStream( ResourceUtils.getFile( ResourceUtils.CLASSPATH_URL_PREFIX + path));
                InputStreamReader inputStreamReader = new InputStreamReader( fileInputStream, Charsets.UTF_8)) {
            properties.load( inputStreamReader);
            return new Msg<>( properties.getProperty( key, null));
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            return new Msg<>( e);
        }
    }

    /**
     * 得到当前系统日期 author: itw_yusw
     * @return 当前日期的格式字符串,日期格式为"yyyy-MM-dd"
     */
    public static String getCurrentDate() {
        return FAST_DATE_FORMAT_DATE.format( System.currentTimeMillis());
    }

    /**
     * 得到当前系统时间 author: itw_yusw
     * @return 当前时间的格式字符串，时间格式为"HH:mm:ss"
     */
    public static String getCurrentTime() {
        return FAST_DATE_FORMAT_TIME.format( System.currentTimeMillis());
    }


    // 这里的工具用于静态的获取当前环境里的Bean对象

    /**
     * <p> 注入Spring环境容器
     *
     * @param applicationContext 注入进来的Spring环境容器
     * */
    public static void setApplicationContext( @NonNull ApplicationContext applicationContext ) {
        log.info(BANNER);
        log.info("尝试复制 Spring应用上下文");
        if ( isNull( getApplicationContextCopy())) {
            ResourceUtil.setApplicationContextCopy( applicationContext);
            log.info( "成功复制 Spring应用上下文");
        } else {
            log.error( "复制 Spring应用上下文 失败, 这将导致程序不能正常运行. 请检查程序并重新启动应用");
            throw new IllegalStateException("复制 Spring应用上下文 失败, 这将导致程序不能正常运行. 请检查程序并重新启动应用");
        }
    }
    /**
     * <p> 使用名称获取Bean
     *
     * @param name beanNane
     * @return bean
     * */
    public static Object getBean( @NonNull String name){
        return getApplicationContextCopy().getBean( name);
    }
    /**
     * <p> 使用类型获取Bean
     *
     * @param <T> T
     * @param clazz beanType
     * @return bean
     * */
    public static <T> T getBean( @NonNull Class<T> clazz){
        return getApplicationContextCopy().getBean( clazz);
    }
    /**
     * <p> 根据类型和名称获取Bean
     *
     * @param <T> T
     * @param name beanName
     * @param clazz beanType
     * @return bean
     * */
    public static <T> T getBean( @NonNull String name, @NonNull Class<T> clazz){
        return getApplicationContextCopy().getBean( name, clazz);
    }

    /**
     * 区分项目的运行环境(目前主要区分在IDE下还是java -jar下运行), 后期考虑增加对Tomcat下的支持
     *
     * @return 描述返回值
     * */
    public static boolean isRunWithSpringJar() {
        return CLASS_LOADER_SPRING.equals( CLASS_LOADER_BOOT);
    }

    @Deprecated
    public static boolean isRunWithSpringWarUnderTomcat() {
        return false;
    }


    /**
     * 环境枚举
     *   这个和子项目使用的配置文件命名有关.
     *   在application.yaml中指定spring.profiles.active=uat, 则代表{@code EnvironmentType.UAT}.
     *   如果对环境的命名不在以下枚举之列, 则统一按{@code EnvironmentType.UNL}处理
     *
     * <p> 创建时间：2018/12/18
     *
     * @author guojy24
     * @version 1.0
     * */
    @Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public enum EnvironmentType {
        /**
         * 未知环境(出错情况下的返回值)
         * */
        NUL,
        /**
         * 本地环境(适应于开发人员的本地开发)
         * */
        LOC,
        /**
         * 开发环境
         * */
        DEV,
        /**
         * 测试环境
         * */
        UAT,
        /**
         * 演示环境(预生产环境)
         * */
        PRE,
        /**
         * 生产环境
         * */
        PRO;

        /**
         * 更据名称返回环境枚举
         *
         * @param name 描述此参数的作用
         * @return 描述返回值
         * */
        private static EnvironmentType forName( @NonNull String name) {
            if ( !notNul( name) || !name.trim().matches("^[a-zA-Z]+$")) {
                log.warn("环境名称 {} 为空或不符合命名规范", name);
                return EnvironmentType.NUL;
            }
            for ( EnvironmentType environmentType : EnvironmentType.values()) {
                if ( environmentType.name().equalsIgnoreCase( name.trim())) { return environmentType; }
            }
            log.warn( "配置文件的命名不符合规范: 尝试使用application-xxx.yaml的格式命名配置文件, 其中xxx代表环境, xxx应属于EnvironmentType定义中的一种.");
            return EnvironmentType.NUL;
        }
    }
    public static EnvironmentType getEnvironmentType () {
        if ( isNull( applicationContextCopy)) {
            log.warn("未能获取到Spring应用上下文, 按未知环境返回");
            return EnvironmentType.NUL;
        }
        return EnvironmentType.forName(
                StringUtils.arrayToCommaDelimitedString(
                        applicationContextCopy.getEnvironment().getActiveProfiles()));
    }

    //==== 华丽的分割线 ==== 私有资源

    private static final String BANNER =
            " =>> Poewred By Taikang Pension Online [Util] <<= \n"+
            "                                                  \n"+
            ".---..   ..--.  .--. .    .---.   .   . .       . \n"+
            "  |  |  / |   ):    :|    |       |   |_|_   o  | \n"+
            "  |  |-'  |--' |    ||    |---    |   | |    .  | \n"+
            "  |  |  \\ |    :    ;|    |       :   ; |    |  | \n"+
            "  '  '   `'     `--' '---''---'____`-'  `-'-' `-`-\n"+
            "                                                  \n"+
            " =>> ---------------------------------------- <<= \n";
    private static final Class<? extends ClassLoader> CLASS_LOADER_SPRING = LaunchedURLClassLoader.class;
    private static final Class<? extends ClassLoader> CLASS_LOADER_BOOT = ResourceUtil.class.getClassLoader().getClass();
    private static final String NUL = "";
    private static final String REGEX_PATH_FILE = "^(file:)(.+?)/classes/$";
    private static final String REGEX_PATH_JAR = "^jar:(file:)(.+)/([0-9a-zA-Z_-]+)\\.jar!(.+)$";

    private static final FastDateFormat FAST_DATE_FORMAT_DATE = FastDateFormat.getInstance("yyyy-MM-dd");
    private static final FastDateFormat FAST_DATE_FORMAT_TIME = FastDateFormat.getInstance("HH:mm:ss");
    @Getter( AccessLevel.PRIVATE) @Setter( AccessLevel.PRIVATE)
    private static ApplicationContext applicationContextCopy = null;
}
