/* Copyright (c) 2019, Guo Jinyang. All rights reserved. */
package com.github.piggyguojy;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.annotation.*;
import java.lang.reflect.Type;

/**
 * 类{@code JsonUtil}提供了一套基于Gson的json序列化/反序列化工具
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * @since JDK1.8
 *
 * @see GsonBuilderStrategy
 * @see GsonBean
 * @see Assert
 * @see ClassUtil
 * @see Msg
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtil {

    /**
     * java实体转json格式字符串(使用{@link GsonBuilderStrategy#NULLS_STRING_TO_EMPTY}策略)
     *
     * @param <T> 目标类型泛型
     * @param t 目标类型实例
     * @return 目标类型实例的json格式字符串
     * */
    public static <T> String javaBean2Json(T t) {
        return javaBean2Json(t, GsonBuilderStrategy.NULLS_STRING_TO_EMPTY);
    }
    /**
     * 使用特定策略将java实体转json格式字符串
     *
     * @param <T> 目标类型泛型
     * @param t 目标类型实例
     * @param gsonBuilderStrategy 序列化策略, 详见{@link GsonBuilderStrategy}
     * @return 目标类型实例的json格式字符串
     * */
    public static <T> String javaBean2Json(T t,  GsonBuilderStrategy gsonBuilderStrategy) {
        return gsonBuilderStrategy.getGson().toJson(t, t.getClass());
    }
    /**
     * json格式字符串转java实体(使用{@link GsonBuilderStrategy#NULLS_STRING_TO_EMPTY}策略)
     *
     * @param <T> 目标类型泛型
     * @param tClass 目标类型的类
     * @param json json格式字符串
     * @return 目标类型实例
     * */
    public static <T> T json2JavaBean(Class<T> tClass,  String json) {
        return json2JavaBean(tClass, json, GsonBuilderStrategy.NULLS_STRING_TO_EMPTY);
    }

    /**
     * 使用特定策略将json格式字符串转java实体
     *
     * @param <T> 目标类型泛型
     * @param tClass 目标类型的类
     * @param json json格式字符串
     * @param gsonBuilderStrategy 序列化策略, 详见{@link GsonBuilderStrategy}
     * @return 目标类型实例
     * */
    public static <T> T json2JavaBean(
            Class<T> tClass,
            String json,
            GsonBuilderStrategy gsonBuilderStrategy
    ) {
        return gsonBuilderStrategy.getGson().fromJson(json, tClass);
    }
    /**
     * <p> json格式字符串转java实体(使用{@link GsonBuilderStrategy#NULLS_STRING_TO_EMPTY}策略)
     *
     * @param <T> 目标类型泛型
     * @param type 类型, 对于复杂泛型可以使用 new {@code TypeToken}&lt;T&gt;(){}.getType()的方式
     * @param json json格式字符串
     * @return 目标类型实例
     * */
    public static <T> T json2JavaBean(Type type, String json) {
        return GsonBuilderStrategy.NULLS_STRING_TO_EMPTY.getGson().fromJson(json, type);
    }
    /**
     * <p> 使用特定策略将json格式字符串转java实体
     *
     * @param <T> 目标类型泛型
     * @param type 类型, 对于复杂泛型可以使用{@code new TypeToken&lt;T&gt;(){}.getType()}的方式
     * @param json json格式字符串
     * @param gsonBuilderStrategy 序列化策略, 详见{@link GsonBuilderStrategy}
     * @return 目标类型实例
     * */
    public static <T> T json2JavaBean(Type type, String json, GsonBuilderStrategy gsonBuilderStrategy) {
        return gsonBuilderStrategy.getGson().fromJson(json, type);
    }

    /**
     * 自定义Gson转换注解
     *
     * <p>当实体类被{@link GsonBean}标注时, 实体中被@{@code Expose}标注的字段可以序列(使用Expose注解且有serialize=true)/反序列化(使用Expose注解且有deserialize=true)
     *
     * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
     * @version 1.0
     * @since JDK1.8
     *
     * @see JsonUtil
     * @see GsonBuilderStrategy
     * */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface GsonBean { }

    /**
     * Gson序列化/反序列化策略
     *
     * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
     * @version 1.0
     * @since JDK1.8
     *
     * @see JsonUtil
     * @see GsonBean
     * */
    public enum GsonBuilderStrategy {

        /**
         * null对象属性在序列化时移除, 反序列化时置null
         * */
        REMOVE_NULLS(new GsonBuilder()
                //使用项目自定义的序列化和反序列化策略
                .addSerializationExclusionStrategy(new GsonBuilderStrategy.GsonDefaultSerializationExclusionStrategy())
                .addDeserializationExclusionStrategy(new GsonBuilderStrategy.GsonDefaultDeserializationExclusionStrategy())
                //禁用Html编码转义
                .disableHtmlEscaping()),
        /**
         * null对象属性在序列化时被置'null', 反序列化时置null
         * */
        NULLS_TO_NULL(new GsonBuilder()
                //使用项目自定义的序列化和反序列化策略
                .addSerializationExclusionStrategy(new GsonBuilderStrategy.GsonDefaultSerializationExclusionStrategy())
                .addDeserializationExclusionStrategy(new GsonBuilderStrategy.GsonDefaultDeserializationExclusionStrategy())
                //禁用Html编码转义
                .disableHtmlEscaping()
                //对空对象进行序列和反序列化
                .serializeNulls()),
        /**
         * null(String)对象在序列化时被置"", null和""反序列化时被置""; 其他类型null对象属性的序列化和反序列化策略同{@link GsonBuilderStrategy#REMOVE_NULLS}
         * */
        NULLS_STRING_TO_EMPTY(new GsonBuilder()
                //处理Null的String类型
                .registerTypeAdapter(
                        String.class,
                        new TypeAdapter<String>() {
                            @Override
                            public String read(JsonReader reader) throws IOException {
                                if (reader.peek()==JsonToken.NULL) {
                                    reader.nextNull();
                                    return "";
                                }
                                return reader.nextString();
                            }
                            @Override
                            public void write(JsonWriter writer, String value) throws IOException {
                                if (value==null) {
                                    writer.value("");
                                    return;
                                }
                                writer.value(value);
                            }})
                //使用项目自定义的序列化和反序列化策略
                .addSerializationExclusionStrategy(new GsonBuilderStrategy.GsonDefaultSerializationExclusionStrategy())
                .addDeserializationExclusionStrategy(new GsonBuilderStrategy.GsonDefaultDeserializationExclusionStrategy())
                //禁用Html编码转义
                .disableHtmlEscaping()
                //对空对象进行序列和反序列化
                .serializeNulls());

        GsonBuilderStrategy(GsonBuilder gsonBuilder) {
            this.gsonBuilder = gsonBuilder;
        }


        @Getter(AccessLevel.PUBLIC)
        private GsonBuilder gsonBuilder;

        /**
         * 完成最终的Gson创建, 从枚举获得的Builder可以继续增加自定义配置
         *
         * @return Gson实例
         * */
        public Gson getGson() {
            return gsonBuilder.create();
        }

        private static final GsonDefaultSerializationExclusionStrategy GSON_DEFAULT_SERIALIZATION_EXCLUSION_STRATEGY
                = new GsonDefaultSerializationExclusionStrategy();
        private static final GsonDefaultDeserializationExclusionStrategy GSON_DEFAULT_DESERIALIZATION_EXCLUSION_STRATEGY
                = new GsonDefaultDeserializationExclusionStrategy();

        /**
         * 自定义GSON序列化策略
         *
         * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
         * @version 1.0
         * @since JDK1.8
         * @see GsonBuilderStrategy
         * */
        private static final class GsonDefaultSerializationExclusionStrategy
                implements ExclusionStrategy {

            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                GsonBean gsonBean =  f.getDeclaringClass().getAnnotation(GsonBean.class);
                if (Assert.notNull(gsonBean)) {
                    Expose expose = f.getAnnotation(Expose.class);
                    return !(Assert.notNull(expose) && expose.serialize());
                } else {
                    return false;
                }
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }
        /**
         * 自定义GSON反序列化策略
         *
         * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
         * @version 1.0
         * @since JDK1.8
         * @see GsonBuilderStrategy
         * */
        private static final class GsonDefaultDeserializationExclusionStrategy
                implements ExclusionStrategy {

            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                GsonBean gsonBean =  fieldAttributes.getDeclaringClass().getAnnotation(GsonBean.class);
                if (Assert.notNull(gsonBean)) {
                    Expose expose = fieldAttributes.getAnnotation(Expose.class);
                    return !(Assert.notNull(expose) && expose.deserialize());
                } else {
                    return false;
                }
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }
    }

}