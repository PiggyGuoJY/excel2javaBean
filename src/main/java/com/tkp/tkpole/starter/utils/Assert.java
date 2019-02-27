package com.tkp.tkpole.starter.utils;

import com.tkp.tkpole.starter.utils.exception.TkpoleException;
import com.tkp.tkpole.starter.utils.exception.TkpoleExceptionPredictable;
import com.tkp.tkpole.starter.utils.regex.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * <p> 断言类
 *     断言类目前主要提供非空测试(notNul和notNull)和正则测试(电子邮箱地址,移动电话号码, 身份证和IP地址)两类功能
 * <p> 创建时间：2018/1/9
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE)
public final class Assert{

    // 非空断言

    public static boolean notNull( Object object) {
        return object!=null;
    }
    public static boolean isNull( Object object) {
        return object==null;
    }
    public static boolean notNul( String string) {
        return !StringUtils.isEmpty( string);
    }
    public static boolean notNulLiterally( String string) { return notNul( string) && !"NULL".equalsIgnoreCase( string);}
    public static boolean notNul( Collection<?> collection) {
        return !CollectionUtils.isEmpty( collection);
    }
    public static boolean isNul( Collection<?> collection) {
        return CollectionUtils.isEmpty( collection);
    }
    public static boolean notNul( byte[] bytes) { return notNull( bytes)&&bytes.length!=0; }
    public static boolean isNul( byte[] bytes) { return notNull( bytes)&&bytes.length==0; }
    public static boolean isNul( int[] ints) { return notNull( ints)&&ints.length==0;}
    public static boolean notNul( short[] shorts) { return notNull( shorts)&&shorts.length!=0; }
    public static boolean notNul( int[] ints) { return notNull( ints)&&ints.length!=0; }
    public static boolean notNul( long[] longs) { return notNull( longs)&&longs.length!=0; }
    public static boolean notNul( float[] floats) { return notNull( floats)&&floats.length!=0; }
    public static boolean notNul( double[] doubles) { return notNull( doubles)&&doubles.length!=0; }
    public static boolean notNul( char[] chars) { return notNull( chars)&&chars.length!=0; }
    public static boolean notNul( boolean[] booleans) { return notNull( booleans)&&booleans.length!=0; }
    public static boolean notNul( Object[] objects) {
        return !ObjectUtils.isEmpty( objects);
    }
    public static boolean notNul( File file) {
        return file!=null &&
                file.exists() &&
                file.isFile() &&
                file.length()!=0L;
    }

    // 正则断言

    /**
     * <p> 判断是字符串否是符合正确的电子邮箱地址格式
     *
     * @param emailAddr 电子邮箱地址
     * @return 是否满足
     * */
    public static boolean isEmaillAddr( String emailAddr) {
        return  new EmailRegex().match( emailAddr);
    }
    /**
     * <p> 判断是字符串否是符合正确的手机号码格式
     *
     * @param phoneNumber 手机号码
     * @return 是否满足
     * */
    public static boolean isPhoneNumber( String phoneNumber) {
        return new PhoneNumberRegex().match( phoneNumber);
    }
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param phoneNumber 描述此参数的作用
     * @return 描述返回值
     * */
    public static String maskPhoneNumber( String phoneNumber) {
        return new PhoneNumberRegex().mask( phoneNumber);
    }
    /**
     * <p> 判断是字符串否是符合正确的身份证号码格式
     *
     * @param idNumber 身份证号
     * @return 是否满足
     * */
    public static boolean isIDNumber( String idNumber) {
        return new IdNumberRegex().match( idNumber);
    }
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param idNumber 描述此参数的作用
     * @return 描述返回值
     * */
    public static String maskIDNumber( String idNumber) {

        return new IdNumberRegex().mask( idNumber);
    }
    /**
     * <p> 判断是字符串否是符合正确的多IP格式
     *
     * @param ipAddrs 多IP地址
     * @return 是否满足
     * */
    public static boolean isIPAddrs(  String ipAddrs) {
        return new IpAddrsRegex().match( ipAddrs);
    }
    /**
     * <p> 判断是字符串否是符合正确的IP格式
     *
     * @param ipAddr IP地址
     * @return 是否满足
     * */
    public static boolean isIPAddr( String ipAddr) {
        return new IpAddrRegex().match( ipAddr);
    }

    // 环境断言

    public static boolean isUatEnv() {
        return ResourceUtil.EnvironmentType.UAT.equals( ResourceUtil.getEnvironmentType());
    }
    public static boolean isProEnv() {
        return ResourceUtil.EnvironmentType.PRO.equals( ResourceUtil.getEnvironmentType());
    }
    public static boolean isSpringJar() {
        return ResourceUtil.isRunWithSpringJar();
    }

    @Deprecated
    public static void notNull( Object object, String msg) {
        if ( !Assert.notNull( object)) {
            Assert.pop( msg);
        }
    }
    @Deprecated
    public static void notNull( Object object, TkpoleExceptionPredictable tkpoleExceptionPredictable, String msg) {
        if ( !Assert.notNull( object)) {
            Assert.pop( tkpoleExceptionPredictable, msg);
        }
    }
    @Deprecated
    public static void notNul( String string, String msg) {
        if ( !Assert.notNul( string)) {
            Assert.pop( msg);
        }
    }
    @Deprecated
    public static void notNul( String string, TkpoleExceptionPredictable tkpoleExceptionPredictable, String msg) {
        if ( !Assert.notNul( string)) {
            Assert.pop( tkpoleExceptionPredictable, msg);
        }
    }
    @Deprecated
    public static void notNul( Collection<?> collection, String msg) {
        if ( !Assert.notNul( collection)) {
            Assert.pop( msg);
        }
    }
    @Deprecated
    public static void notNul( Collection<?> collection, TkpoleExceptionPredictable tkpoleExceptionPredictable, String msg) {
        if ( !Assert.notNul( collection)) {
            Assert.pop( tkpoleExceptionPredictable, msg);
        }
    }
    public static boolean notNul( Map<?,?> map) {
        return !CollectionUtils.isEmpty( map);
    }
    @Deprecated
    public static void notNul( Map<?,?> map, String msg) {
        if ( !Assert.notNul( map)) {
            Assert.pop( msg);
        }
    }
    @Deprecated
    public static void notNul( Map<?,?> map, TkpoleExceptionPredictable tkpoleExceptionPredictable, String msg) {
        if ( !Assert.notNul( map)) {
            Assert.pop( tkpoleExceptionPredictable, msg);
        }
    }
    @Deprecated
    public static void notNul( Object[] objects, String msg) {
        if ( !Assert.notNul( objects)) {
            Assert.pop( msg);
        }
    }
    @Deprecated
    public static void notNul( Object[] objects, TkpoleExceptionPredictable tkpoleExceptionPredictable, String msg) {
        if ( !Assert.notNul( objects)) {
            Assert.pop( tkpoleExceptionPredictable, msg);
        }
    }
    @Deprecated
    public static void notNul( File file, String msg) {
        if ( !Assert.notNul( file)) {
            Assert.pop( msg);
        }
    }
    @Deprecated
    public static void notNul( File file, TkpoleExceptionPredictable tkpoleExceptionPredictable, String msg) {
        if ( !Assert.notNul( file)) {
            Assert.pop( tkpoleExceptionPredictable, msg);
        }
    }

    //==== 华丽的分割线 ==== 私有资源

    private static void pop(  String msg) {
        Assert.pop( TkpoleExceptionPredictable.ERR_PARAMS, msg);
    }
    private static void pop( TkpoleExceptionPredictable tkpoleExceptionPredictable,  String msg) {
        throw TkpoleException.of( tkpoleExceptionPredictable, msg);
    }
}
