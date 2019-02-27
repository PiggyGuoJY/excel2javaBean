package com.guojy;

import com.google.common.base.Charsets;
import com.guojy.gson.GsonBuilderStrategy;
import com.tkp.tkpole.starter.utils.gson.GsonBuilderStrategy;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 序列化/反序列化工具
 *
 * <p> 创建时间：2018/8/9
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE)
public final class JsonXmlUtil {

    /**
     * <p> java实体转xml格式字符串
     *
     *  当期对Map类型存在bug
     *
     * @param <T> 实体类型
     * @param t 实体实例
     * @return xml格式字符串
     * */
    public static <T> String javaBean2Xml(@NonNull T t) {
        log.debug( "<<=" + t.toString());
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            Marshaller marshaller = JAXBContext.newInstance( t.getClass()).createMarshaller();
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty( Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.setProperty( "com.sun.xml.internal.bind.xmlHeaders", "<?xml version='1.0' encoding='UTF-8'?>");
            marshaller.marshal(t, byteArrayOutputStream);
            String result = new String( byteArrayOutputStream.toByteArray(), Charsets.UTF_8);
            log.debug( "=>>" + result);
            return result;
        } catch ( IOException | JAXBException e) {
            log.error( e.getMessage(), e);
            return null;
        }
    }

    /**
     * <p> xml格式字符串转java实体
     *
     * @param <T> 实体类型
     * @param tClass 实体类型的类型包装
     * @param xml xml格式字符串
     * @return 实体实例
     * */
    @SuppressWarnings("unchecked")
    public static <T> T xml2JavaBean(@NonNull Class<T> tClass, @NonNull String xml) {
        log.debug( "<<=" + tClass.getName() + "," + xml);
        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance( tClass).createUnmarshaller();
            T t = ( T)unmarshaller.unmarshal( new StringReader( xml));
            log.debug( "=>>" + t.toString());
            return t;
        } catch ( JAXBException e) {
            log.error( e.getMessage(), e);
            return null;
        }
    }

    /**
     * <p> java实体转json格式字符串(使用NULLS_STRING_TO_EMPTY策略)
     *
     * @param <T> 实体类型
     * @param t 实体实例
     * @return json格式字符串
     * */
    public static <T> String javaBean2Json(@NonNull T t) {
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
    public static <T> String javaBean2Json(@NonNull T t, @NonNull GsonBuilderStrategy gsonBuilderStrategy) {
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
    public static <T> T json2JavaBean(@NonNull Class<T> tClass, @NonNull String json) {
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
    public static <T> T json2JavaBean(@NonNull Class<T> tClass, @NonNull String json,@NonNull GsonBuilderStrategy gsonBuilderStrategy) {
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
    public static <T> T json2JavaBean(@NonNull Type type,@NonNull String json) {
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
    public static <T> T json2JavaBean(@NonNull Type type,@NonNull String json,@NonNull GsonBuilderStrategy gsonBuilderStrategy) {
        return gsonBuilderStrategy.getGson().fromJson( json, type);
    }

    /**
     * 用于xml序列/反序列化过程中Map类型的数据
     *
     * <p> 创建时间：2018/10/12
     *
     * @author guojy24
     * @version 1.0
     * */
    public static class TkpoleMapAdapter extends XmlAdapter<MapEntity[],Map<?,?>> {
        @Override
        public MapEntity[] marshal( Map<?,?> map) {
            List<MapEntity> mapEntities = new ArrayList<>();
            map.forEach( ( k, v) -> mapEntities.add( new MapEntity(k,v)));
            return mapEntities.toArray( new MapEntity[]{});
        }
        @Override
        public Map unmarshal( MapEntity[] mapEntities) {
            Map map = new HashMap(10);
            for ( MapEntity mapEntity : mapEntities) {
                map.put( mapEntity.getKey(), mapEntity.getValue());
            }
            return map;
        }
    }

    /**
     * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
     *
     * <p> 创建时间：2018/10/12
     *
     * @author guojy24
     * @version 1.0
     * */
    @Data @AllArgsConstructor @NoArgsConstructor
    @XmlRootElement @XmlAccessorType( XmlAccessType.FIELD)
    public static class MapEntity {
        private Object key;
        private Object value;
    }
}