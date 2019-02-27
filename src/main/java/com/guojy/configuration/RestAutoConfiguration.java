package com.guojy.configuration;

import com.guojy.soap.HttpClientUtils;
import com.tkp.tkpole.starter.utils.soap.HttpClientUtils;
import com.tkp.tkpole.starter.utils.soap.RestFactory;
import com.tkp.tkpole.starter.utils.soap.model.rest.RestConfigData;
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
 * Rest功能SpringBoot自动配置类
 *
 * <p> 创建时间：2018/9/7
 *
 * @author guojy24
 * @version 1.0
 * */
@Configuration @EnableConfigurationProperties( RestConfigData.class) @ConditionalOnProperty( name = {"_rest.autoConfig"})
@Slf4j @NoArgsConstructor @AllArgsConstructor( onConstructor_={@Autowired})
public class RestAutoConfiguration {
    @Bean @Scope( "singleton")
    public RestFactory getRestFactory() {
        log.info( "自动配置 {}, 配置数据: {}", RestFactory.class.getName(), restConfigData.toString());
        if ( !restConfigData.getAutoConfig()) {
            log.warn("_rest.autoConfig不能设置为false, 请设置为true或去掉本配置(_rest)");
            throw new IllegalArgumentException("_rest.autoConfig不能设置为false, 请设置为true或去掉本配置(_rest)");
        }
        return new RestFactory( this.restConfigData, new HttpClientUtils( this.restConfigData));
    }

    private RestConfigData restConfigData;
}
