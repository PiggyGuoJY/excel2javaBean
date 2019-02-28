package com.guojy.soap.model.soap;

import com.guojy.HostInfoUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/8/9
 *
 * @author guojy24
 * @version 1.0
 * */
@ConfigurationProperties(prefix = "_soap")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SoapConfigData {
    @Builder.Default private Boolean autoConfig = false;
    @Builder.Default private String host = HostInfoUtil.Net.HOST_IP;
    @Builder.Default private Integer port = 80;
    @Builder.Default private List<SoapMetaConfigData> soapList = new ArrayList<>();
}
