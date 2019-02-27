package com.tkp.tkpole.starter.utils.misc.edm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/9/18
 *
 * @author guojy24
 * @version 1.0
 * */
@Component @ConfigurationProperties( prefix = "_edm")
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class EdmConfigData {
    private String code;
    private String password;
    private String defaultTitle;
    private String defaultContext;
}
