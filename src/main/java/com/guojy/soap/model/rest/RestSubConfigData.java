package com.guojy.soap.model.rest;

import com.guojy.soap.model.ProxyConfig;
import com.tkp.tkpole.starter.utils.soap.model.ProxyConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/8/9
 *
 * @author guojy24
 * @version 1.0
 * */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RestSubConfigData {

    /**
     * 资源名称 (默认值: 无; 允许值: 无; 是否可重载: 否; 是否必填: 是)
     * */
    @Builder.Default private String name = "default";
    /**
     * 资源描述 (默认值: 无; 允许值: 无; 是否可重载: 否; 是否必填: 是)
     * */
    @Builder.Default private String desc = "默认地址";

    /**
     * 路径 (默认值: /; 允许值: 无; 是否可重载: 否; 是否必填: 否)
     * */
    @Builder.Default private String path = "/";

    /**
     * 方法 (默认值: 空(这种情况使用上层配置); 允许值: get, post, head; 是否可重载: 否; 是否必填: 否)
     * */
    @Builder.Default private String method  = null;

    /**
     * request header 设置 (默认值: 空(这种情况使用上层配置); 允许值: 无; 是否可重载: 否; 是否必填: 否)
     * */
    @Builder.Default private Map<String, String> headers = new HashMap<>();

    /**
     * url字符编码设置 (默认值: UTF-8; 允许值: java允许的字符集; 是否可重载: 否; 是否必填: 否)
     * */
    @Builder.Default private String urlCharset = "UTF-8";

    /**
     * request parameter 设置 (默认值: 空(这种情况使用上层配置); 允许值: 无; 是否可重载: 否; 是否必填: 否)
     * */
    @Builder.Default private Map<String, List<String>> parameters = new HashMap<>();

    /**
     * 代理使能 (默认值: false; 允许值: false, true; 是否可重载: 否; 是否必填: 否)
     * */
    @Builder.Default private Boolean useProxy = false;
    /**
     * 代理设置 (默认值: 无; 允许值: 无; 是否可重载: 否; 是否必填: 否)
     * */
    @Builder.Default private ProxyConfig proxy = new ProxyConfig();
}
