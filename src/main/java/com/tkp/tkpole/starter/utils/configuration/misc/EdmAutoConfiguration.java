package com.tkp.tkpole.starter.utils.configuration.misc;

import com.google.common.collect.ImmutableMap;
import com.tkp.tkpole.starter.utils.ResourceUtil;
import com.tkp.tkpole.starter.utils.misc.edm.Edm;
import com.tkp.tkpole.starter.utils.misc.edm.EdmConfigData;
import com.tkp.tkpole.starter.utils.soap.SoapFactory;
import com.tkp.tkpole.starter.utils.soap.model.soap.SoapConfigData;
import com.tkp.tkpole.starter.utils.soap.model.soap.SoapMetaConfigData;
import com.tkp.tkpole.starter.utils.soap.model.soap.SoapSubConfigData;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Collections;

import static com.tkp.tkpole.starter.utils.Assert.notNull;

/**
 * 集团邮件系统自动配置
 *
 * <p> 创建时间：2019/1/11
 *
 * @author guojy24
 * @version 1.0
 * */
@Configuration @ConditionalOnProperty( name = {"_util.enableEdm"}) @EnableConfigurationProperties({EdmConfigData.class,SoapConfigData.class})
@Slf4j @NoArgsConstructor
public class EdmAutoConfiguration {
    @Bean @Scope( "singleton")
    public Edm getEdm() {
        if ( !enable) {
            log.warn( "_util.enableEdm不能设置为false, 请设置为true或去掉本配置");
            throw new IllegalArgumentException("_util.enableEdm不能设置为false, 请设置为true或去掉本配置");
        }
        log.info("自动配置 {} (默认使用默认数据, 当子项目中存在足够的配置时, 使用子项目的配置)", Edm.class.getName());
        try {
            EdmConfigData realEdmConfigData = ResourceUtil.getBean( EdmConfigData.class);
            SoapConfigData realSoapConfigData = ResourceUtil.getBean( SoapConfigData.class);
            if ( !( notNull(realEdmConfigData) && notNull(realEdmConfigData.getCode()) &&notNull(realSoapConfigData) && notNull(realSoapConfigData.getAutoConfig()))) {
                throw new BeanCreationException("没有找到特定bean");
            }
            SoapFactory realSoapFactory = new SoapFactory( realSoapConfigData);
            if ( !(realSoapFactory.testExistenceSoapServiceByName("EDM", "sendEmail"))) {
                throw new BeanCreationException("没有找到特定配置");
            }
            log.info( "使用子项目配置配置 {}", Edm.class.getName());
            return new Edm( realSoapFactory, realEdmConfigData);
        } catch ( BeansException e) {
            log.trace( e.getMessage(), e);
            log.warn( "未能成从子项目中获取到足够的配置信息, 使用默认配置配置 {}", Edm.class.getName());
            return new Edm( soapFactory, edmConfigData);
        }
    }

    @Value("${_util.enableEdm}")
    private boolean enable;
    private SoapConfigData soapConfigData = new SoapConfigData();
    private EdmConfigData edmConfigData = new EdmConfigData();
    {
        boolean isPro = ResourceUtil.EnvironmentType.PRO.equals( ResourceUtil.getEnvironmentType());
        final SoapSubConfigData.ParamType paramType = SoapSubConfigData.ParamType.builder().qName("XSD_STRING").parameterMode("IN").build();
        val sParameter = ImmutableMap.<String, SoapSubConfigData.ParamType>builder()
                .put("triggerCode", paramType)
                .put("password",    paramType)
                .put("subject",     paramType)
                .put("content",     paramType)
                .put("receivers",   paramType)
                .put("datahandler", SoapSubConfigData.ParamType.builder().qName("MIME_DATA_HANDLER").parameterMode("IN").build())
                .put("attachName",  paramType)
                .put("sendTime",    paramType)
                .build();
        soapConfigData = SoapConfigData.builder()
                .autoConfig( true)
                .host(isPro ? "10.130.103.180" : "10.130.216.146" ).port(8888)
                .soapList(Collections.singletonList( SoapMetaConfigData.builder()
                        .name("EDM").desc("EDM")
                        .scheme("http").host(null).port(null).sTimeout(100000)
                        .detail(Collections.singletonList( SoapSubConfigData.builder()
                                .name("sendEmail").desc("sendEmail")
                                .path("/tk_edm/services/SendEmail").sTimeout(100000)
                                .sOperationName(null).sOperationName_QName("http://Entry.Service.com,SendEmail")
                                .sParameter(sParameter).sReturnType("XSD_STRING")
                                .build()))
                        .build()))
                .build();
        edmConfigData = EdmConfigData.builder()
                .code(isPro ? "NDA5" : "Mzg5")
                .password(isPro ? "Tkpole2018" : "Tk2018ylj")
                .defaultTitle("默认标题")
                .defaultContext("默认内容")
                .build();
    }
    private SoapFactory soapFactory = new SoapFactory( soapConfigData);
}
