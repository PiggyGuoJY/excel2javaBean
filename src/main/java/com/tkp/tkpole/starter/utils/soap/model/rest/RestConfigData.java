package com.tkp.tkpole.starter.utils.soap.model.rest;

import com.tkp.tkpole.starter.utils.HostInfoUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;


/**
 * <p> 程序员（itw_wantt）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 * <p> 创建时间：2017/12/21
 *
 * @author itw_wantt
 * @version 1.0
 * */
@ConfigurationProperties(prefix = "_rest")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RestConfigData {

    /**
     * 自动配置开关(默认不使用)
     * */
    @Builder.Default private Boolean autoConfig = false;

    @Builder.Default private String host = HostInfoUtil.Net.HOST_IP;

    @Builder.Default private Integer port = 8080;

    /**
     * 资源配置
     * */
    @Builder.Default private List<RestMetaConfigData> restList = new ArrayList<>();
    /**
     * 是否使用连接池(默认不使用, 使用PoolingHttpClientConnectionManager)
     * */
    @Builder.Default private Boolean useConnPool = false;
    /**
     * 连接池设置
     * */
    @Builder.Default private RestConnPoolConfigData connPool = new RestConnPoolConfigData();
}
