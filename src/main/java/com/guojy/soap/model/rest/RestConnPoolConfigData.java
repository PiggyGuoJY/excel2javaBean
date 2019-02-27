package com.guojy.soap.model.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/8/9
 *
 * @author guojy24
 * @version 1.0
 * */
@Data @NoArgsConstructor @AllArgsConstructor
public class RestConnPoolConfigData {

    /**
     * 连接池最大并发量
     * */
    private Integer maxTotal = 200;
    /**
     * 每个路由的默认最大并发量
     * */
    private Integer defaultMaxPerRoute = 20;
    /**
     * 每个路由的配置最大并发量
     * */
    private Map<String, Integer> maxPerRoute = new HashMap<>();
}
