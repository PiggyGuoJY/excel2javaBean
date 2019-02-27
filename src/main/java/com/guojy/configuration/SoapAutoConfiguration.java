package com.guojy.configuration;

import com.tkp.tkpole.starter.utils.soap.SoapFactory;
import com.tkp.tkpole.starter.utils.soap.model.soap.SoapConfigData;
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
 * WebService功能Springboot自动配置类
 * 
 * <p> 创建时间：2018/9/7
 * 
 * @author guojy24
 * @version 1.0
 * */
@Configuration @EnableConfigurationProperties( SoapConfigData.class) @ConditionalOnProperty( name = {"_soap.autoConfig"})
@Slf4j @NoArgsConstructor @AllArgsConstructor( onConstructor_={@Autowired})
public class SoapAutoConfiguration {
    @Bean @Scope( "singleton")
    public SoapFactory getSoapFactory() {
        log.info( "自动配置 {}, 配置数据: {}", SoapFactory.class.getName(), soapConfigData.toString());
        if ( !soapConfigData.getAutoConfig()) {
            log.warn("_soap.autoConfig不能设置为false, 请设置为true或去掉本配置(_soap)");
            throw new IllegalArgumentException("_soap.autoConfig不能设置为false, 请设置为true或去掉本配置(_soap)");
        }
        return new SoapFactory( this.soapConfigData);
    }

    private SoapConfigData soapConfigData;
}
