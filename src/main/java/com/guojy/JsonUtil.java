package com.guojy;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.guojy.gson.GsonBuilderStrategy;
import com.guojy.model.Msg;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.guojy.Assert.notNul;
import static com.guojy.Assert.notNull;

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