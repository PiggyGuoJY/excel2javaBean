package com.guojy.soap.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.http.HttpHost;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/8/9
 *
 * @author guojy24
 * @version 1.0
 * */
@Data @NoArgsConstructor @Builder
public class ProxyConfig {

    /**
     * 代理服务器主机地址 ( 默认值: 无; 允许值: 无; 是否可重载: 否; 是否必填: 是)
     * */
    private String host;
    /**
     * 代理服务器端口 ( 默认值: 无; 允许值: 无; 是否可重载: 否; 是否必填: 是)
     * */
    private Integer port;

    public ProxyConfig( @NonNull String host, @NonNull Integer port) {
        this.host = host;
        this.port = port;
    }

    public HttpHost getProxy() {
        return new HttpHost( this.host, this.port);
    }

}
