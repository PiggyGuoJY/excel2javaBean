package com.tkp.tkpole.starter.utils.misc.sms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/12/26
 *
 * @author guojy24
 * @version 1.0
 * */
@ConfigurationProperties( prefix = "_tkSystem")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TkSystemConfigData implements Serializable {

    private String systemId;

    private String systemPassword;
}
