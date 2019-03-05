package com.guojy;

import com.guojy.gson.GsonBuilderStrategy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * 序列化/反序列化工具
 *
 * <p> 创建时间：2018/8/9
 *
 * @author guojy
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE)
public final class JsonUtil {

    /**
     * <p> java实体转json格式字符串(使用NULLS_STRING_TO_EMPTY策略)
     *
     * @param <T> 实体类型
     * @param t 实体实例
     * @return json格式字符串
     * */
    public static <T> String javaBean2Json( T t) {
        return javaBean2Json( t, GsonBuilderStrategy.NULLS_STRING_TO_EMPTY);
    }

    /**
     * <p> java实体转json格式字符串
     *
     * @param <T> 实体类型
     * @param t 实体实例
     * @param gsonBuilderStrategy 序列化策略
     * @return json格式字符串
     * */
    public static <T> String javaBean2Json( T t,  GsonBuilderStrategy gsonBuilderStrategy) {
        return gsonBuilderStrategy.getGson().toJson( t, t.getClass());
    }

    /**
     * <p> json转java实体(使用NULLS_STRING_TO_EMPTY策略)
     *
     * @param <T> 实体类型
     * @param tClass 实体类型的类型包装
     * @param json json格式字符串
     * @return 实体实例
     * */
    public static <T> T json2JavaBean( Class<T> tClass,  String json) {
        return json2JavaBean( tClass, json, GsonBuilderStrategy.NULLS_STRING_TO_EMPTY);
    }

    /**
     * <p> json转java实体
     *
     * @param <T> 实体类型
     * @param tClass 实体类型的类型包装
     * @param json json格式字符串
     * @param gsonBuilderStrategy 序列化策略
     * @return 实体实例
     * */
    public static <T> T json2JavaBean( Class<T> tClass,  String json, GsonBuilderStrategy gsonBuilderStrategy) {
        return gsonBuilderStrategy.getGson().fromJson( json, tClass);
    }

    /**
     * <p> json转java实体
     *
     * @param <T> 实体类型
     * @param type 类型
     * @param json json格式字符串
     * @return 实体实例
     * */
    public static <T> T json2JavaBean( Type type, String json) {
        return GsonBuilderStrategy.NULLS_STRING_TO_EMPTY.getGson().fromJson( json, type);
    }

    /**
     * <p> json转java实体
     *
     * @param <T> 实体类型
     * @param type 类型
     * @param json json格式字符串
     * @param gsonBuilderStrategy 序列化策略
     * @return 实体实例
     * */
    public static <T> T json2JavaBean( Type type, String json, GsonBuilderStrategy gsonBuilderStrategy) {
        return gsonBuilderStrategy.getGson().fromJson( json, type);
    }

}