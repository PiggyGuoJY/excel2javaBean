package com.tkp.tkpole.starter.utils.configuration.misc;

import com.google.common.collect.ImmutableMap;
import com.tkp.tkpole.starter.utils.ResourceUtil;
import com.tkp.tkpole.starter.utils.misc.sms.MsgSender;
import com.tkp.tkpole.starter.utils.misc.sms.TkSms;
import com.tkp.tkpole.starter.utils.misc.sms.TkSystemConfigData;
import com.tkp.tkpole.starter.utils.misc.sms.TkpMsg;
import com.tkp.tkpole.starter.utils.soap.HttpClientUtils;
import com.tkp.tkpole.starter.utils.soap.RestFactory;
import com.tkp.tkpole.starter.utils.soap.model.rest.RestConfigData;
import com.tkp.tkpole.starter.utils.soap.model.rest.RestMetaConfigData;
import com.tkp.tkpole.starter.utils.soap.model.rest.RestSubConfigData;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.tkp.tkpole.starter.utils.Assert.notNull;

/**
 * 短信系统自动配置
 *
 * <p> 创建时间：2019/1/11
 *
 * @author guojy24
 * @version 1.0
 * */
@Configuration @ConditionalOnProperty( name = {"_util.enableSms"}) @EnableConfigurationProperties({TkSystemConfigData.class,RestConfigData.class})
@Slf4j @NoArgsConstructor
public class SmsAutoConfiguration {
    @Bean( "MsgSender4TkpMsg") @Scope( "singleton")
    public MsgSender getRestAccessible4TkpMsg() {
        if ( !enable) {
            log.warn("_util.enableSms不能设置为false, 请设置为true或去掉本配置");
            throw new IllegalArgumentException("_util.enableSms不能设置为false, 请设置为true或去掉本配置");
        }
        log.info( "自动配置 {} (默认使用默认数据, 当子项目中存在足够的配置时, 使用子项目的配置)", TkpMsg.class.getName());
        try {
            TkSystemConfigData realTkSystemConfigData = ResourceUtil.getBean( TkSystemConfigData.class);
            RestConfigData realRestConfigData = ResourceUtil.getBean( RestConfigData.class);
            if ( !(notNull( realRestConfigData)&&notNull(realRestConfigData.getAutoConfig())&&notNull(realTkSystemConfigData)&&notNull(realTkSystemConfigData.getSystemId()))) {
                throw new BeanCreationException("没有找到特定bean");
            }
            RestFactory realRestFactory = new RestFactory( realRestConfigData, new HttpClientUtils( realRestConfigData));
            if ( !(realRestFactory.testExistenceRestServiceByName( "TkpMsg", "smsMsgSend"))) {
                throw new BeanCreationException("没有找到特定配置");
            }
            log.info( "使用子项目配置配置 {}", TkpMsg.class.getName());
            return new TkpMsg( realTkSystemConfigData, realRestFactory);
        } catch ( BeansException e) {
            log.trace( e.getMessage(), e);
            log.warn( "未能成从子项目中获取到足够的配置信息, 使用默认配置配置 {}", TkpMsg.class.getName());
            return new TkpMsg( tkSystemConfigData, restFactory);
        }
    }
    @Bean( "MsgSender4TkSms") @Scope( "singleton")
    public MsgSender getRestAccessible4TkSms() {
        if ( !enable) {
            log.warn("_util.enableSms不能设置为false, 请设置为true或去掉本配置");
            throw new IllegalArgumentException("_util.enableSms不能设置为false, 请设置为true或去掉本配置");
        }
        log.info( "自动配置 {} (默认使用默认数据, 当子项目中存在足够的配置时, 使用子项目的配置)", TkSms.class.getName());
        try {
            RestConfigData realRestConfigData = ResourceUtil.getBean( RestConfigData.class);
            if ( !(notNull(realRestConfigData)&&notNull(realRestConfigData.getAutoConfig()))) {
                throw new BeanCreationException("没有找到特定bean");
            }
            RestFactory realRestFactory = new RestFactory( realRestConfigData, new HttpClientUtils( realRestConfigData));
            if ( !realRestFactory.testExistenceRestServiceByName( "TkSms", "sendSmsMsg")) {
                throw new BeanCreationException("没有找到特定配置");
            }
            log.info( "使用子项目配置配置 {}", TkSms.class.getName());
            return new TkSms( realRestFactory);
        } catch ( BeansException e) {
            log.trace( e.getMessage(), e);
            log.warn( "未能成从子项目中获取到足够的配置信息, 使用默认配置配置 {}", TkSms.class.getName());
            return  new TkSms( restFactory);
        }
    }

    @Value("${_util.enableSms}")
    private boolean enable;
    private RestConfigData restConfigData = new RestConfigData();
    private TkSystemConfigData tkSystemConfigData = new TkSystemConfigData();
    {
        boolean isPro = ResourceUtil.EnvironmentType.PRO.equals(ResourceUtil.getEnvironmentType());

        val parameters = ImmutableMap.<String,List<String>>builder()
                .put("operatorCode", Collections.singletonList("SYSTI"))
                .put("operatorPwd", Collections.singletonList( isPro ? "Tksmsti" : "123456"))
                .put("msgtype", Collections.singletonList("1"))
                .build();
        val headers = ImmutableMap.<String,String>builder().put("Content-Type","application/json;charset=utf-8").build();
        restConfigData = RestConfigData.builder()
                .autoConfig(true)
                .useConnPool(false)
                .host( isPro ? "10.130.103.180" : "10.130.216.146")
                .port(8888)
                .restList(Arrays.asList(
                        RestMetaConfigData.builder()
                                .name("TkSms").desc("TkSms")
                                .scheme("http").host(null).port(null).method("get")
                                .headers(Collections.emptyMap())
                                .parameters(Collections.emptyMap())
                                .detail(Collections.singletonList(RestSubConfigData.builder()
                                        .name("sendSmsMsg").desc("sendSmsMsg")
                                        .path("/tk_msg/submitMessage").method(null).urlCharset("GBK")
                                        .headers(Collections.emptyMap()).parameters(parameters)
                                        .useProxy(false).proxy(null)
                                        .build()))
                                .build(),
                        RestMetaConfigData.builder()
                                .name("TkpMsg").desc("TkpMsg")
                                .scheme("http").host(null).port(null).method("post")
                                .headers(headers)
                                .parameters(Collections.emptyMap())
                                .detail(Collections.singletonList(RestSubConfigData.builder()
                                        .name("smsMsgSend").desc("smsMsgSend").urlCharset("UTF-8")
                                        .path("/tkp_msg/tkmsg/service/SmsMsgSend").method(null)
                                        .headers(Collections.emptyMap())
                                        .parameters(Collections.emptyMap())
                                        .useProxy(false).proxy(null)
                                        .build()))
                                .build()))
                .build();
        tkSystemConfigData = TkSystemConfigData.builder()
                .systemId("tkponline")
                .systemPassword("tkponlinePassword")
                .build();
    }
    private RestFactory restFactory = new RestFactory( restConfigData, null);
}
