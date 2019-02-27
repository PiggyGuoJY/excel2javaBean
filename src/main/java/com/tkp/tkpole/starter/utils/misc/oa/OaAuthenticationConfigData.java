package com.tkp.tkpole.starter.utils.misc.oa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p> 程序员（itw_wantt）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 * <p> 创建时间：2017/12/21
 *
 * @author itw_wantt
 * @version 1.0
 * */
@Component @ConfigurationProperties(prefix = "_oa")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OaAuthenticationConfigData {
    private String url;
    private String basedn;
    private String username;
    private String password;
    private String filter;
    private String testUsername;
    private String testPassword;
}
