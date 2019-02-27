package com.tkp.tkpole.starter.utils.misc.ecm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/8/9
 *
 * @author guojy24
 * @version 1.0
 * */
@Component @ConfigurationProperties( prefix = "_ecm")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EcmConfigData {
    private String host;
    private Integer port;
    private Integer ccsid;
    private String qmName;
    private String channel;
    private String queueName;

    private String hostDownload;
}
