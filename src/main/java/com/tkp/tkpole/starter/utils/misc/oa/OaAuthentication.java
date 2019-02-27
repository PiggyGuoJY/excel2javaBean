package com.tkp.tkpole.starter.utils.misc.oa;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/8/9
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j
public class OaAuthentication {

    public boolean test() {
        return check(this.testUsername, this.testPassword);
    }

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param uid 描述此参数的作用
     * @param password 描述此参数的作用
     * @return 描述返回值
     * */
    public boolean check( String uid, String password) {

        InitialDirContext loginCtx = null;
        try {
            if ( !openLdap()) { return false; }
            /* 获取登录用户的DN */
            String userDn = this.getUserDnByUid( adminCtx, uid);
            if ( "".equals(userDn.trim())) { return false; }
            /* 登录用户连接Ldap */
            loginCtx = this.getDirContext( userDn, password);
            if ( loginCtx==null) {
                setMessage( "LDAP: " + getMessage());
                log.info( getMessage());
                return false;
            }
        } catch ( Exception e) {
            log.error( e.getMessage(), e);
            return false;
        } finally {
            if ( loginCtx!=null) { try { loginCtx.close(); } catch ( NamingException e) { log.error( e.getMessage(), e); } }
            if ( !this.closeLdap()) {
               log.error( "未正常关闭连接");
            }
        }
        return true;
    }

    public OaAuthentication(
            String ldapBaseDn,
            String ldapUrl,
            String bindUser,
            String bindPasswd) {
        super();
        this.ldapUrl = ldapUrl;
        this.ldapBaseDn = ldapBaseDn;
        this.bindUser = bindUser;
        this.bindPasswd = bindPasswd;
    }

    public OaAuthentication(
            OaAuthenticationConfigData oaAuthenticationConfigData
    ) {
        this.setConfigData( oaAuthenticationConfigData);
    }

    /**
     * Ldap连接地址
     * */
    private String ldapUrl;
    /**
     * Ldap用户节点
     * */
    private String ldapBaseDn;
    /**
     * Ldap查询用户名
     * */
    private String bindUser;
    /**
     * Ldap查询用户密码
     * */
    private String bindPasswd;
    /**
     * Ldap查询条件
     * */
    private String ldapFilter;

    private String testUsername;
    private String testPassword;
    /**
     * 程序员（guojy24）很懒，关于这个属性，ta什么也没写╮(╯▽╰)╭
     * */
    private InitialDirContext adminCtx = null;
    /**
     * 提示信息
     * */
    @Setter( value = AccessLevel.PRIVATE) @Getter
    private String message = "";

    private static final String TRUE = "TRUE";

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param ldapUser 描述此参数的作用
     * @param ldapPasswd 描述此参数的作用
     * @return 描述返回值
     * */
    private InitialDirContext getDirContext( String ldapUser, String ldapPasswd) {

        InitialDirContext ldapCtx = null;
        try {
            Hashtable<String, String> env = new Hashtable<>(10);
            env.put( "java.naming.ldap.version", "3");
            env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put( Context.SECURITY_AUTHENTICATION, "simple");
            env.put( Context.PROVIDER_URL, ldapUrl);
            env.put( Context.SECURITY_PRINCIPAL, ldapUser);
            env.put( Context.SECURITY_CREDENTIALS, ldapPasswd);
            env.put( Context.REFERRAL, "follow");
            env.put( "com.sun.jndi.ldap.connect.pool", "true");
            env.put( "com.sun.jndi.ldap.connect.timeout", "20000");
            ldapCtx = new InitialDirContext( env);
        } catch ( AuthenticationException e) {
            log.error( e.getMessage(), e);
            this.setMessage( "认证失败!");
        } catch ( NamingException e) {
            log.error( e.getMessage(), e);
            this.setMessage( "连接失败!");
        }
        return ldapCtx;
    }

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param context 描述此参数的作用
     * @param uid 描述此参数的作用
     * @return 描述返回值
     * */
    private String getUserDnByUid( InitialDirContext context, String uid) {

        // 定义变量放置登录用户DN
        String userDn = "";
        try {
            SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope( SearchControls.SUBTREE_SCOPE);
            ctrl.setReturningAttributes( new String[] { "uid", "tkssovalid" });
            String filter = ldapFilter.replaceAll("\\$\\{uid}", uid);
            NamingEnumeration<SearchResult> en = context.search( ldapBaseDn, filter, ctrl);
            if ( en.hasMore()) {
                SearchResult result = en.next();
                Attributes attrs = result.getAttributes();
                Attribute status = attrs.get("tkssovalid");
                /* tkssovalid TRUE:启用、 FALSE:禁用 */
                if ( status!=null&& OaAuthentication.TRUE.equalsIgnoreCase( status.get().toString())) {
                    userDn = result.getNameInNamespace();
                } else {
                    setMessage( "登录用户[" + uid + "]状态无效!");
                }
            } else {
                setMessage( "登录用户[" + uid + "]不存在!");
            }
        } catch ( Exception e) {
            log.error( e.getMessage(), e);
            setMessage( "Ldap获取登录用户DN时失败!");
        }
        return userDn;
    }

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @return 描述返回值
     * */
    private boolean openLdap() {

        if ( adminCtx!=null) {
            return true;
        } else {
            try {
                adminCtx = this.getDirContext( bindUser, bindPasswd);
            } catch ( Exception e) {
                log.error( e.getMessage(), e);
                return false;
            }
            return true;
        }
    }

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @return 描述返回值
     * */
    private boolean closeLdap() {

        if ( adminCtx != null) {
            try {
                adminCtx.close();
                adminCtx = null;
            } catch ( Exception e) {
                log.error( e.getMessage(), e);
                return false;
            }
        }
        return true;
    }

    private void setConfigData( OaAuthenticationConfigData oaAuthenticationConfigData) {
        this.ldapUrl = oaAuthenticationConfigData.getUrl();
        this.ldapBaseDn = oaAuthenticationConfigData.getBasedn();
        this.bindUser = oaAuthenticationConfigData.getUsername();
        this.bindPasswd = oaAuthenticationConfigData.getPassword();
        this.ldapFilter = oaAuthenticationConfigData.getFilter();
        this.testUsername = oaAuthenticationConfigData.getTestUsername();
        this.testPassword = oaAuthenticationConfigData.getTestPassword();
    }
}

