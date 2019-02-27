package com.guojy.gson;

import java.lang.annotation.*;


/**
 * 项目自有Gson转换标记
 * <p>当实体类没有被@TkpoleGsonBean标注时, 实体可以通过HttpMessageConverter自动序列/反序列化;</p>
 * <p>当实体类被@TkpoleGsonBean标注时, 实体中被@Expose标注的字段可以通过HttpMessageConverter序列(使用Expose注解且有serialize=true)/反序列化(使用Expose注解且有deserialize=true);</p>
 *
 * <p> 创建时间：2018/8/2
 *
 * @author guojy24
 * @version 1.0
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TkpoleGsonBean { }
