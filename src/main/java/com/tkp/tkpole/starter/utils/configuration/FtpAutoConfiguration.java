package com.tkp.tkpole.starter.utils.configuration;

import com.tkp.tkpole.starter.utils.ftp.FtpFactory;
import com.tkp.tkpole.starter.utils.ftp.model.FtpConfigData;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Ftp功能SpringBoot自动配置类
 *
 * <p> 创建时间：2018/9/18
 *
 * @author guojy24
 * @version 1.0
 * */
@Configuration @EnableConfigurationProperties( FtpConfigData.class) @ConditionalOnProperty( name = {"_ftp.autoConfig"})
@Slf4j @NoArgsConstructor @AllArgsConstructor( onConstructor_={@Autowired})
public class FtpAutoConfiguration {
    @Bean @Scope( "singleton")
    public FtpFactory getFtpFactory() {
        log.info( "自动配置 {}, 配置数据: {}", FtpFactory.class.getName(), ftpConfigData.toString());
        if ( !ftpConfigData.getAutoConfig()) {
            log.warn("_ftp.autoConfig不能设置为false, 请设置为true或去掉本配置(_ftp)");
            throw new IllegalArgumentException("_ftp.autoConfig不能设置为false, 请设置为true或去掉本配置(_ftp)");
        }
        return new FtpFactory( ftpConfigData);
    }

    private FtpConfigData ftpConfigData;
}
