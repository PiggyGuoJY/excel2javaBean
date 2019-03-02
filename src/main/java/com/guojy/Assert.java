package com.guojy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

/**
 * <p> 断言类
 *     断言类目前主要提供非空测试(notNul和notNull)和正则测试(电子邮箱地址,移动电话号码, 身份证和IP地址)两类功能
 * <p> 创建时间：2018/1/9
 *
 * @author guojy
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
    public static boolean notNul( Collection<?> collection) {
        return !CollectionUtils.isEmpty( collection);
    }
    public static boolean notNul( byte[] bytes) { return notNull( bytes)&&bytes.length!=0; }
    public static boolean notNul( short[] shorts) { return notNull( shorts)&&shorts.length!=0; }
    public static boolean isNul( int[] ints) { return notNull( ints)&&ints.length==0;}
    public static boolean notNul( int[] ints) { return notNull( ints)&&ints.length!=0; }
    public static boolean notNul( long[] longs) { return notNull( longs)&&longs.length!=0; }
    public static boolean notNul( float[] floats) { return notNull( floats)&&floats.length!=0; }
    public static boolean notNul( double[] doubles) { return notNull( doubles)&&doubles.length!=0; }
    public static boolean notNul( char[] chars) { return notNull( chars)&&chars.length!=0; }
    public static boolean notNul( boolean[] booleans) { return notNull( booleans)&&booleans.length!=0; }
    public static boolean notNul( Object[] objects) {
        return (objects == null || objects.length == 0);
    }
    public static boolean notNul( File file) {
        return file!=null &&
                file.exists() &&
                file.isFile() &&
                file.length()!=0L;
    }

    public static boolean isPaht(Path path) {

        return false;
    }
}
