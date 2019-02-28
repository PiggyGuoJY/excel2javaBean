package com.guojy.soap;

import com.guojy.Assert;
import com.guojy.exception.TkpoleException;
import com.guojy.exception.TkpoleExceptionPredictable;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.apache.axis.message.RPCElement;

import javax.xml.namespace.QName;
import java.rmi.RemoteException;
import java.util.Arrays;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 * 
 * <p> 创建时间：2018/8/8
 * 
 * @author guojy24
 * @version 1.0
 * */
@Slf4j
public class SoapService implements SoapAccessible {
    @Override
    public <T, R> R execute( Object[] params, TkpoleFunction<T, R> tkpoleFunction, R defaultR) {
        return rawExecute( "params", params, tkpoleFunction, defaultR);
    }
    @Override
    public <T, R> R execute( RPCElement body, TkpoleFunction<T, R> tkpoleFunction, R defaultR) {
        return rawExecute( "body", new Object[]{ body}, tkpoleFunction, defaultR);
    }
    @Override
    public <T, R> R execute( String method, Object[] args, TkpoleFunction<T, R> tkpoleFunction, R defaultR) {
        return rawExecute( "method_args", new Object[]{ method, args}, tkpoleFunction, defaultR);
    }
    @Override
    public <T, R> R execute( QName operationName, Object[] params, TkpoleFunction<T, R> tkpoleFunction, R defaultR) {
        return rawExecute( "operationName_params", new Object[]{ operationName, params}, tkpoleFunction, defaultR);
    }
    @Override
    public <T, R> R execute( String namespace, String method, Object[] args, TkpoleFunction<T,R> tkpoleFunction, R defaultR) {
        return rawExecute( "namespace_method_args", new Object[]{ namespace, method, args}, tkpoleFunction, defaultR);
    }

    SoapService ( Call call){
        this.call = call;
    }

    //==== 华丽的分割线 === 私有资源

    private Call call;

    @SuppressWarnings("unchecked")
    private  <T, R> R rawExecute( String callType, Object[] params, TkpoleFunction<T, R> tkpoleFunction, R defaultR) {
        log.debug( "\n-- web service request--\n callType: {},\nparams: {}", callType, Arrays.toString( params));
        R r = null;
        try {
            Object object = null;
            switch ( callType) {
                case "params":
                    object = call.invoke( params);
                    break;
                case "body":
                    object = call.invoke( ( RPCElement)params[0]);
                    break;
                case "method_args":
                    object = call.invoke( ( String)params[0], ( Object[])params[1]);
                    break;
                case "operationName_params":
                    object = call.invoke( ( QName)params[0], ( Object[])params[1]);
                    break;
                case "namespace_method_args":
                    object = call.invoke( ( String)params[0], ( String)params[1], ( Object[])params[2]);
                    break;
                default:
                    throw TkpoleException.of( TkpoleExceptionPredictable.ERR_PARAMS, String.format( "不能识别的调用方式: %s", callType));
            }
            log.debug( "\n-- web service response--\n response:{}", object.toString());
            if ( Assert.notNull( object)) {
                try {
                    long begin = System.currentTimeMillis();
                    r = tkpoleFunction.apply( ( T)object);
                    log.debug( "\n--web service time--\nSOAP调用执行时长: %d ms", System.currentTimeMillis()-begin);
                } catch ( Exception e) {
                    log.error( e.getMessage(), e);
                    r = defaultR;
                }
            } else {
                log.debug( "SOAP调用返回空");
                r = defaultR;
            }
        } catch ( RemoteException e) {
            log.error( e.getMessage(), e);
            r = defaultR;
        }
        return r;
    }
}