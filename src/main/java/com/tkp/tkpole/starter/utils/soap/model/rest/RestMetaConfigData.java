package com.tkp.tkpole.starter.utils.soap.model.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
public class RestMetaConfigData {

    /**
     * 资源名称 (默认值: 无; 允许值: 无; 是否可重载: 否; 是否必填: 是)
     * */
    @Builder.Default private String name = "";
    /**
     * 资源描述 (默认值: 无; 允许值: 无; 是否可重载: 否; 是否必填: 是)
     * */
    @Builder.Default private String desc = "";

    /*<开始>更改者: guojy24 更改时间: 2018/6/12 变更原因: 在这个级别应保证配置是在同一个域下, 下层配置不允许重载这些参数*/
    /**
     * 协议 (默认值: http; 允许值: http, https; 是否可重载: 否; 是否必填: 是)
     * */
    @Builder.Default private String scheme = "http";
    /**
     * 域名 (默认值: 127.0.0.1; 允许值: 域名或IPv4地址; 是否可重载: 否; 是否必填: 是)
     * */
    @Builder.Default private String host = null;
    /**
     * 端口 (默认值: 8080; 允许值: 1~65535; 是否可重载: 否; 是否必填: 是)
     * */
    @Builder.Default private Integer port = null;
    /*<结束>更改者: guojy24 更改时间: 2018/6/12 */

    /**
     * 请求方法 (默认值: get; 允许值: get, post, head; 是否可重载: 是; 是否必填: 否)
     * */
    @Builder.Default private String method = "get";
    /**
     * request header 设置 (默认值: 无; 允许值: 无; 是否可重载: 是; 是否必填: 否)
     * */
    @Builder.Default private Map<String, String> headers = new HashMap<>();
    /**
     * request parameter 设置 (默认值: 无; 允许值: 无; 是否可重载: 是; 是否必填: 否)
     * */
    @Builder.Default private Map<String, List<String>> parameters = new HashMap<>();
    /**
     * 子资源配置 (默认值: 无; 允许值: 无; 是否可重载: 是; 是否必填: 是)
     * */
    @Builder.Default private List<RestSubConfigData> detail = new ArrayList<>();
}
