package com.tkp.tkpole.starter.utils.soap;

import org.apache.axis.message.RPCElement;

import javax.xml.namespace.QName;

/**
 * 可访问的web service
 *
 * <p> 创建时间：2018/9/18
 *
 * @author guojy24
 * @version 1.0
 * */
public interface SoapAccessible {
    /**
     * <p> 可执行方法
     *
     * @param <T> 响应实体类型
     * @param <R> 目标实体类型
     * @param params 描述此参数的作用
     * @param tkpoleFunction 描述此参数的作用
     * @param defaultR 默认目标实体
     * @return 目标实体
     * */
    <T, R> R execute( Object[] params, TkpoleFunction<T, R> tkpoleFunction, R defaultR);
    /**
     * <p> 可执行方法
     *
     * @param <T> 响应实体类型
     * @param <R> 目标实体类型
     * @param body 描述此参数的作用
     * @param tkpoleFunction 描述此参数的作用
     * @param defaultR 默认目标实体
     * @return 目标实体
     * */
    <T, R> R execute(RPCElement body, TkpoleFunction<T, R> tkpoleFunction, R defaultR);
    /**
     * <p> 可执行方法
     *
     * @param <T> 响应实体类型
     * @param <R> 目标实体类型
     * @param method 描述此参数的作用
     * @param args 描述此参数的作用
     * @param tkpoleFunction 描述此参数的作用
     * @param defaultR 默认目标实体
     * @return 目标实体
     * */
    <T, R> R execute( String method, Object[] args, TkpoleFunction<T, R> tkpoleFunction, R defaultR);
    /**
     * <p> 可执行方法
     *
     * @param <T> 响应实体类型
     * @param <R> 目标实体类型
     * @param operationName 描述此参数的作用
     * @param params 描述此参数的作用
     * @param tkpoleFunction 描述此参数的作用
     * @param defaultR 默认目标实体
     * @return 目标实体
     * */
    <T, R> R execute(QName operationName, Object[] params, TkpoleFunction<T, R> tkpoleFunction, R defaultR);
    /**
     * <p> 可执行方法
     *
     * @param <T> 响应实体类型
     * @param <R> 目标实体类型
     * @param namespace 描述此参数的作用
     * @param method 描述此参数的作用
     * @param args 描述此参数的作用
     * @param tkpoleFunction 描述此参数的作用
     * @param defaultR 默认目标实体
     * @return 目标实体
     * */
    <T, R> R execute( String namespace, String method, Object[] args, TkpoleFunction<T,R> tkpoleFunction, R defaultR);
}
