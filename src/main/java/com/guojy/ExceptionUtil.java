package com.guojy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.tkp.tkpole.starter.utils.Assert.notNull;
import static java.lang.String.format;

/**
 * 异常工具类
 * <p> 提供一些关于异常的便捷方法
 * <p> 创建时间：2018/2/24
 *
 * @author guojy24
 * @version 1.1
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionUtil {

    /**
     * 简化堆栈时, 只留下以此字符串常亮开头的堆栈信息
     * */
    private static final String PROJECT_PACKAGE_PREFIX = HostInfoUtil.Project.BASE_PATH;

    /**
     * <p> 简化的错误堆栈信息
     *
     * @param t  异常
     * @return 精简后的异常信息
     * */
    public static String getSimpleStackTrace(@NonNull Throwable t) {
        StringBuffer stringBuffer = new StringBuffer();
        ExceptionUtil.printSimpleStackTrace( t, stringBuffer);
        return stringBuffer.toString();
    }

    /**
     * <p> 使用标准输出打印简化后的异常堆栈信息
     *
     * @param t 异常
     * */
    public static void printSimpleStackTrace( @NonNull Throwable t) {
        ExceptionUtil.printSimpleStackTrace( t, System.err::println);
    }
    /**
     * <p> 使用指定的日志记录器以error级别打印简化后的异常堆栈信息
     *
     * @param t 异常
     * @param logger  日志记录器(如果传入null则使用类自带的logger)
     * */
    public static void printSimpleStackTrace( @NonNull Throwable t, @NonNull Logger logger) {
        ExceptionUtil.printSimpleStackTrace( t, notNull( logger) ? logger::error : log::error);
    }

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param t 异常
     * @param stringBuffer  缓存区
     * */
    public static void printSimpleStackTrace(@NonNull Throwable t, @NonNull StringBuffer stringBuffer) {
        ExceptionUtil.printSimpleStackTrace( t, simpleStackTrace -> stringBuffer.append( format( "%s", simpleStackTrace)));
    }

    public static void printSimpleStackTrace(@NonNull Throwable t, @NonNull StringBuilder stringBuilder) {
        ExceptionUtil.printSimpleStackTrace( t, simpleStackTrace -> stringBuilder.append( format( "%s", simpleStackTrace)));
    }

    //==== 华丽的分割线 === 私有资源

    /**
     * <p> 通用的优化后异常堆栈打印方法
     *
     * @param t 异常
     * @param consumer 打印方法实现
     * */
    private static void printSimpleStackTrace( Throwable t, Consumer<String> consumer) {
        consumer.accept( format( ">>>  %s: %s", t.getClass().getName(), t.getMessage()));
        try ( Stream<StackTraceElement> stream = Arrays.stream( t.getStackTrace()).filter( stackTraceElement -> stackTraceElement.getClassName().startsWith( ExceptionUtil.PROJECT_PACKAGE_PREFIX))) {
            stream.forEach( stackTraceElement -> consumer.accept( format( "  at %s.%s(Line:%s)", stackTraceElement.getClassName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber())));
        } catch ( Exception e) {
            log.error( e.getMessage(), e);
        }
    }
}