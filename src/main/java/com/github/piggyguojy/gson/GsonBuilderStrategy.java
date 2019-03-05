package com.github.piggyguojy.gson;

import com.github.piggyguojy.Assert;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.Getter;

import java.io.IOException;

import static com.github.piggyguojy.Assert.notNull;

/**
 * Gson序列化策略
 *
 * <p> 创建时间：2018/8/9
 *
 * @author guojy
 * @version 1.0
 *
 * Copyright 2019 Guo JinYang. All Rights Reserved.
 * */
public enum GsonBuilderStrategy {

    /**
     * 空对象属性在序列化时移除
     * */
    REMOVE_NULLS( new GsonBuilder()
            //使用项目自定义的序列化和反序列化策略
            .addSerializationExclusionStrategy( new GsonDefaultSerializationExclusionStrategy())
            .addDeserializationExclusionStrategy( new GsonDefaultDeserializationExclusionStrategy())
            //禁用Html编码转义
            .disableHtmlEscaping()),
    /**
     * 空对象属性在序列化时被置null
     * */
    NULLS_TO_NULL( new GsonBuilder()
            //使用项目自定义的序列化和反序列化策略
            .addSerializationExclusionStrategy( new GsonDefaultSerializationExclusionStrategy())
            .addDeserializationExclusionStrategy( new GsonDefaultDeserializationExclusionStrategy())
            //禁用Html编码转义
            .disableHtmlEscaping()
            //对空对象进行序列和反序列化
            .serializeNulls()),
    /**
     * 空对象String在序列化时被置"", null和""反序列化时也被置"", 其他类型空对象属性在序列化时被置null
     * */
    NULLS_STRING_TO_EMPTY( new GsonBuilder()
            //处理Null的String类型
            .registerTypeAdapter(
                    String.class,
                    new TypeAdapter<String>() {
                        @Override
                        public String read( JsonReader reader) throws IOException {
                            if ( reader.peek()==JsonToken.NULL) {
                                reader.nextNull();
                                return "";
                            }
                            return reader.nextString();
                        }
                        @Override
                        public void write( JsonWriter writer, String value) throws IOException {
                            if ( value==null) {
                                writer.value("");
                                return;
                            }
                            writer.value(value);
                        }})
            //使用项目自定义的序列化和反序列化策略
            .addSerializationExclusionStrategy( new GsonDefaultSerializationExclusionStrategy())
            .addDeserializationExclusionStrategy( new GsonDefaultDeserializationExclusionStrategy())
            //禁用Html编码转义
            .disableHtmlEscaping()
            //对空对象进行序列和反序列化
            .serializeNulls());

    GsonBuilderStrategy( GsonBuilder gsonBuilder) {
        this.gsonBuilder = gsonBuilder;
    }


    @Getter
    private GsonBuilder gsonBuilder;

    /**
     * 完成最终的Gson创建, 从枚举获得的Builder可以继续增加自定义配置
     *
     * @return Gson实例
     * */
    public Gson getGson() {
        return gsonBuilder.create();
    }

    /**
     * 客服平台默认GSON序列化策略
     *
     * <p> 创建时间：2018/8/1
     * <p> 最近修改: 2019/2/15
     *
     * @author guojy
     * @version 1.0
     * */
    private static final class GsonDefaultSerializationExclusionStrategy implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField( FieldAttributes f) {
            GsonBean gsonBean =  f.getDeclaringClass().getAnnotation( GsonBean.class);
            if ( Assert.notNull(gsonBean)) {
                Expose expose = f.getAnnotation( Expose.class);
                return !( Assert.notNull( expose) && expose.serialize());
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
     * 客服平台默认GSON反序列化策略
     *
     * <p> 创建时间：2018/8/1
     * <p> 最近修改: 2019/2/15
     *
     * @author guojy
     * @version 1.0
     * */
    private static final class GsonDefaultDeserializationExclusionStrategy implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField( FieldAttributes fieldAttributes) {
            GsonBean gsonBean =  fieldAttributes.getDeclaringClass().getAnnotation( GsonBean.class);
            if ( Assert.notNull(gsonBean)) {
                Expose expose = fieldAttributes.getAnnotation( Expose.class);
                return !( Assert.notNull( expose) && expose.deserialize());
            } else {
                return false;
            }
        }

        @Override
        public boolean shouldSkipClass( Class<?> clazz) {
            return false;
        }
    }

}