package com.tkp.tkpole.starter.utils.configuration.misc;

import com.tkp.tkpole.starter.utils.ResourceUtil;
import com.tkp.tkpole.starter.utils.misc.oa.OaAuthentication;
import com.tkp.tkpole.starter.utils.misc.oa.OaAuthenticationConfigData;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static com.tkp.tkpole.starter.utils.Assert.notNull;

/**
 * 集团OA认证系统自动配置
 * 
 * <p> 创建时间：2019/1/11
 * 
 * @author guojy24
 * @version 1.0
 * */
@Configuration @ConditionalOnProperty( name = { "_util.enableOaAuth"}) @EnableConfigurationProperties(OaAuthenticationConfigData.class)
@Slf4j @NoArgsConstructor
public class OaAuthenticationAutoConfiguration {
    @Bean @Scope( "singleton")
    public OaAuthentication getOaAuthentication() {
        if ( !enable) {
            log.warn("_util.enableOaAuth不能设置为false, 请设置为true或去掉本配置");
            throw new IllegalArgumentException("_util.enableOaAuth不能设置为false, 请设置为true或去掉本配置");
        }
        log.info("自动配置 {} (默认使用默认数据, 当子项目中存在足够的配置时, 使用子项目的配置)", OaAuthentication.class.getName());
        try {
            OaAuthenticationConfigData realOaAuthenticationConfigData = ResourceUtil.getBean( OaAuthenticationConfigData.class);
            if (!(notNull(realOaAuthenticationConfigData)&&notNull(realOaAuthenticationConfigData.getUrl()))) {
                throw new BeanCreationException("没有找到特定bean");
            }
            log.info( "使用子项目配置配置 {}", OaAuthentication.class.getName());
            return new OaAuthentication(realOaAuthenticationConfigData );
        } catch ( BeansException e) {
            log.trace(e.getMessage(), e);
            log.warn( "未能成从子项目中获取到足够的配置信息, 使用默认配置配置 {}", OaAuthentication.class.getName());
            return new OaAuthentication( oaAuthenticationConfigData);
        }
    }

    @Value("${_util.enableOaAuth}")
    private boolean enable;
    private OaAuthenticationConfigData oaAuthenticationConfigData = new OaAuthenticationConfigData();
    {
        if ( ResourceUtil.EnvironmentType.PRO.equals( ResourceUtil.getEnvironmentType())) {
            oaAuthenticationConfigData = OaAuthenticationConfigData.builder()
                    //这里不能使用源地址, 走代理
//                    .url("ldap://amldap.uiam.group.taikang.com:389")
                    .url("10.130.103.180:1389")
                    .basedn("cn=users,DC=TAIKANGLIFE")
                    .username("uid=tkpcsuser,cn=appusers,DC=TAIKANGLIFE")
                    .password("tkpcs&20180410")
                    .filter("(&(objectclass=inetOrgPerson)(uid=${uid}))")
                    .testUsername("guojy24")
                    .testPassword("lvt12465292_")
                    .build();
        } else {
            oaAuthenticationConfigData = OaAuthenticationConfigData.builder()
                    //这里不能使用源地址, 走代理
//                    .url("ldap://10.137.138.11:389")
                    .url("10.130.216.146:1389")
                    .basedn("cn=users,DC=TAIKANGLIFE")
                    .username("uid=ldapuser,cn=appusers,DC=TAIKANGLIFE")
                    .password("pass1234")
                    .filter("(&(objectclass=inetOrgPerson)(uid=${uid}))")
                    .testUsername("guanxin")
                    .testPassword("pass1234")
                    .build();
        }
    }
}
