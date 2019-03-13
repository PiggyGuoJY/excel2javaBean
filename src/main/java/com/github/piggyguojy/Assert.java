package com.github.piggyguojy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Collection;

/**
 * 类{@code Assert}主要提供两类非空断言(notNul和notNull)和部分空断言(isNull和isNul)
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * @since JDK1.8
 *
 * @see ClassUtil
 * @see JsonUtil
 * @see Msg
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Assert{

    /**
     * 断言对象非空(主要用于取代 <pre>object!=null</pre> 的写法)
     *
     * @param object 测试实例
     * @return 是否非空
     */
    public static boolean notNull(Object object) { return object!=null; }
    /**
     * 断言对象为空(主要用于取代 <pre>object==null</pre> 的写法)
     *
     * @param object 测试实例
     * @return 是否为空
     */
    public static boolean isNull(Object object) { return object==null; }
    
    /**
     * 断言byte数组为空(null对象或长度等于0)
     *
     * @param bytes byte数组
     * @return byte数组是否为空
     */
    public static boolean isNul(byte[] bytes) { return isNull(bytes)||bytes.length==0; }
    /**
     * 断言byte数组非空(非null对象且长度不等于0)
     * 
     * @param bytes byte数组
     * @return byte数组是否非空
     */
    public static boolean notNul(byte[] bytes) { return notNull(bytes)&&bytes.length!=0; }
    /**
     * 断言short数组为空(null对象或长度等于0)
     *
     * @param shorts short数组
     * @return short数组是否为空
     */
    public static boolean isNul(short[] shorts) { return isNull(shorts)||shorts.length==0; }
    /**
     * 断言short数组非空(非null对象且长度不等于0)
     *
     * @param shorts short数组
     * @return short数组是否非空
     */
    public static boolean notNul(short[] shorts) { return notNull(shorts)&&shorts.length!=0; }
    /**
     * 断言int数组为空(null对象或长度等于0)
     * 
     * @param ints int数组
     * @return int数组是否为空
     */
    public static boolean isNul(int[] ints) { return isNull(ints)||ints.length==0;}
    /**
     * 断言int数组非空(非null对象且长度不等于0)
     *
     * @param ints int数组
     * @return int数组是否非空
     */
    public static boolean notNul(int[] ints) { return notNull(ints)&&ints.length!=0; }
    /**
     * 断言long数组为空(null对象或长度等于0)
     *
     * @param longs byte数组
     * @return long数组是否为空
     */
    public static boolean isNul(long[] longs) { return isNull(longs)||longs.length==0; }
    /**
     * 断言long数组非空(非null对象且长度不等于0)
     *
     * @param longs byte数组
     * @return long数组是否非空
     */
    public static boolean notNul(long[] longs) { return notNull(longs)&&longs.length!=0; }
    /**
     * 断言float数组为空(null对象或长度等于0)
     *
     * @param floats float数组
     * @return float数组是否为空
     */
    public static boolean isNul(float[] floats) { return isNull(floats)||floats.length==0; }
    /**
     * 断言float数组非空(非null对象且长度不等于0)
     *
     * @param floats float数组
     * @return float数组是否非空
     */
    public static boolean notNul(float[] floats) { return notNull(floats)&&floats.length!=0; }
    /**
     * 断言double数组为空(null对象或长度等于0)
     *
     * @param doubles double数组
     * @return double数组是否为空
     */
    public static boolean isNul(double[] doubles) { return isNull(doubles)||doubles.length==0; }
    /**
     * 断言double数组非空(非null对象且长度不等于0)
     *
     * @param doubles double数组
     * @return double数组是否非空
     */
    public static boolean notNul(double[] doubles) { return notNull(doubles)&&doubles.length!=0; }
    /**
     * 断言char数组为空(null对象或长度等于0)
     *
     * @param chars char数组
     * @return char数组是否为空
     */
    public static boolean isNul(char[] chars) { return isNull(chars)||chars.length==0; }
    /**
     * 断言char数组非空(非null对象且长度不等于0)
     *
     * @param chars char数组
     * @return char数组是否非空
     */
    public static boolean notNul(char[] chars) { return notNull(chars)&&chars.length!=0; }
    /**
     * 断言boolean数组为空(null对象或长度等于0)
     *
     * @param booleans boolean数组
     * @return boolean数组是否为空
     */
    public static boolean isNul(boolean[] booleans) { return isNull(booleans)||booleans.length==0; }
    /**
     * 断言boolean数组非空(非null对象且长度不等于0)
     *
     * @param booleans boolean数组
     * @return boolean数组是否非空
     */
    public static boolean notNul(boolean[] booleans) { return notNull(booleans)&&booleans.length!=0; }
    /**
     * 断言{@code Object}数组为空(null对象或长度等于0)
     *
     * @param objects {@code Object}数组
     * @return {@code Object}数组是否为空
     */
    public static boolean isNul(Object[] objects) { return isNull(objects)||objects.length==0; }
    /**
     * 断言{@code Object}数组非空(非null对象且长度不等于0)
     *
     * @param objects {@code Object}数组
     * @return {@code Object}数组是否非空
     */
    public static boolean notNul(Object[] objects) { return notNull(objects)&&objects.length!=0; }

    /**
     * 断言字符串为空(null对象或长度等于0)
     *
     * @param string 字符串
     * @return 字符串是否为空
     */
    public static boolean isNul(String string) { return StringUtils.isEmpty(string); }
    /**
     * 断言字符串非空(非null对象且长度不等于0)
     *
     * @param string 字符串
     * @return 字符串是否非空
     */
    public static boolean notNul(String string) { return !StringUtils.isEmpty(string); }

    /**
     * 断言集合非空(null对象或没有元素)
     *
     * @param collection 集合
     * @return 集合非空
     */
    public static boolean notNul(Collection<?> collection) { return !CollectionUtils.isEmpty(collection); }

    /**
     * 断言文件非空(不是null对象、文件类型正确且存在、长度不等于0)
     *
     * @param file 测试文件
     * @return 文件是否非空
     */
    public static boolean notNul(File file) {
        return file!=null && file.exists() && file.isFile() && file.length()!=0L;
    }
}
