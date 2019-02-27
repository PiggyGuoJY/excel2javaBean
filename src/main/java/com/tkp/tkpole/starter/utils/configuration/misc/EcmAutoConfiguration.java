package com.tkp.tkpole.starter.utils.configuration.misc;

import com.google.common.collect.ImmutableMap;
import com.tkp.tkpole.starter.utils.ResourceUtil;
import com.tkp.tkpole.starter.utils.misc.ecm.Ecm;
import com.tkp.tkpole.starter.utils.misc.ecm.EcmConfigData;
import com.tkp.tkpole.starter.utils.misc.ecm.EcmUtil;
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
 * 集团影像件系统自动配置
 *
 * <p> 创建时间：2019/1/11
 * 
 * @author guojy24
 * @version 1.0
 * */
@Configuration
@ConditionalOnProperty( name = {"_util.enableEcm"})
@EnableConfigurationProperties({EcmConfigData.class, RestConfigData.class})
@Slf4j
@NoArgsConstructor
public class EcmAutoConfiguration {

    @Bean
    @Scope( "singleton")
    public Ecm getEcm() {
        if (!enable) {
            log.warn( "_util.enableEcm不能设置为false, 请设置为true或去掉本配置");
            throw new IllegalArgumentException( "_util.enableEcm不能设置为false, 请设置为true或去掉本配置");
        }
        log.info( "自动配置 {} (默认使用默认数据, 当子项目中存在足够的配置时, 使用子项目的配置)", Ecm.class.getName());
        try {
            EcmConfigData realEcmConfigData = ResourceUtil.getBean( EcmConfigData.class);
            RestConfigData realRestConfigData = ResourceUtil.getBean( RestConfigData.class);
            if (!(notNull( realEcmConfigData) &&
                notNull( realEcmConfigData.getHost()) &&
                notNull( realRestConfigData) &&
                notNull( realRestConfigData.getAutoConfig()))) {
                throw new BeanCreationException( "没有找到特定bean");
            }
            RestFactory realRestFactory = new RestFactory( realRestConfigData, new HttpClientUtils( realRestConfigData));
            if (!(restFactory.testExistenceRestServiceByName( "ECM", "download") &&
                restFactory.testExistenceRestServiceByName( "ECM", "testExistence"))) {
                throw new BeanCreationException("没有找到特定配置");
            }
            log.warn( "使用子项目配置配置 {}", Ecm.class.getName());
            return new Ecm( new EcmUtil( realEcmConfigData, realRestFactory));
        } catch ( BeansException e) {
            log.trace( e.getMessage(), e);
            log.warn( "未能成从子项目中获取到足够的配置信息, 使用默认配置配置(默认配置需要在代码中修改) {}", Ecm.class.getName());
            return new Ecm( new EcmUtil( ecmConfigData, restFactory));
        }
    }


    @Value("${_util.enableEcm}")
    private boolean enable;
    private EcmConfigData ecmConfigData;
    private RestConfigData restConfigData;
    {
        boolean isPro = ResourceUtil.EnvironmentType.PRO.equals( ResourceUtil.getEnvironmentType());
        ecmConfigData = EcmConfigData
                .builder()
                .host(isPro ? "10.130.103.180" : "10.130.216.146").port(1414)
                .channel("ECM.IMAGE.CHANNEL")
                .ccsid(1381)
                .qmName("ECM.IMAGE.QM").queueName(isPro ? "ECM.IMAGE.LQ.CEPH.YLJKF" : "ECM.IMAGE.LQ.CEPH.YLJDZBD")
                .hostDownload(isPro ? "http://ecm.taikang.com/ImageQuery/ImageQueryServlet?IP=10.1" : "http://10.137.133.1:9080/ImageQuery/ImageQueryServlet?IP=10.1&flag=1")
                .build();
        val parametersUat = ImmutableMap.<String,List<String>>builder()
                .put("compno", Collections.singletonList( "1"))
                .put("IP", Collections.singletonList( "10.1"))
                .build();
        val parametersPro = ImmutableMap.<String,List<String>>builder()
                .put("IP", Collections.singletonList( "10.1"))
                .build();
        restConfigData = RestConfigData.builder()
                .autoConfig( true)
                .useConnPool( false)
                .host(isPro ? "10.130.103.180" : "10.130.216.146")
                .port(8888)
                .restList( Collections.singletonList(
                        RestMetaConfigData.builder()
                                .name("ECM").desc("ECM")
                                .scheme("http").host(null).port(null).method("get")
                                .headers(Collections.emptyMap())
                                .parameters(Collections.emptyMap())
                                .detail(Arrays.asList(
                                        RestSubConfigData.builder()
                                                .name("download").desc("download")
                                                .path("/tk_ecm/ImageQuery/ImageQueryServlet").method(null)
                                                .headers(Collections.emptyMap())
                                                .parameters(isPro ? parametersPro : parametersUat)
                                                .urlCharset("UTF-8")
                                                .useProxy(false).proxy(null)
                                                .build(),
                                        RestSubConfigData.builder()
                                                .name("testExistence").desc("testExistence")
                                                .path("/tk_ecm/ImageQuery/ImageQueryServlet").method("head")
                                                .headers(Collections.emptyMap())
                                                .parameters(isPro ? parametersPro : parametersUat)
                                                .urlCharset("UTF-8")
                                                .useProxy(false).proxy(null)
                                                .build()))
                                .build()))
                .build();
    }
    private RestFactory restFactory = new RestFactory( restConfigData, null);
}