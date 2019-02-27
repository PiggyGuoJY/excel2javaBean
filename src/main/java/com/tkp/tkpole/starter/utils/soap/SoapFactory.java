package com.tkp.tkpole.starter.utils.soap;

import com.tkp.tkpole.starter.utils.Assert;
import com.tkp.tkpole.starter.utils.exception.TkpoleException;
import com.tkp.tkpole.starter.utils.exception.TkpoleExceptionPredictable;
import com.tkp.tkpole.starter.utils.soap.model.soap.SoapConfigData;
import com.tkp.tkpole.starter.utils.soap.model.soap.SoapMetaConfigData;
import com.tkp.tkpole.starter.utils.soap.model.soap.SoapSubConfigData;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.http.client.utils.URIBuilder;

import javax.naming.ConfigurationException;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/7/25
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j
public final class SoapFactory {

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param group 描述此参数的作用
     * @param sub 描述此参数的作用
     * @return 描述返回值
     * */
    public final SoapAccessible getSoapAccessibleByName( String group, String sub) {
        return getSoapServiceByName( group, sub);
    }

    public final boolean testExistenceSoapServiceByName( String group, String sub) {
        try {
            findSoapSubConfigDataByName( findSoapMetaConfigDataByName( soapConfigData, group), sub);
            return true;
        } catch ( ConfigurationException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param group 描述此参数的作用
     * @param sub 描述此参数的作用
     * @return 描述返回值
     * */
    @Deprecated @SneakyThrows
    public final SoapService getSoapServiceByName( String group, String sub) {
        SoapMetaConfigData soapMetaConfigData = findSoapMetaConfigDataByName( soapConfigData, group);
        SoapSubConfigData soapSubConfigData = findSoapSubConfigDataByName( soapMetaConfigData, sub);
        Service service = new Service();
        Call call;
        try {
            call = ( Call)service.createCall();
        } catch ( ServiceException e) {
            log.error( e.getMessage(), e);
            throw TkpoleException.of( TkpoleExceptionPredictable.ERR_INIT, "不能初始化Call");
        }
        //根据配置数据配置Call
        decorateCall( call, soapMetaConfigData, soapSubConfigData);
        return new SoapService( call);
    }
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @return 描述返回值
     * */
    public Map<String,List<String>> getAvailableSoapResource() {

        Map<String,List<String>> result = new HashMap<>( 10);
        soapConfigData.getSoapList().forEach(
                rest -> {
                    List<String> sub = new LinkedList<>();
                    result.put(rest.getName(), sub);
                    rest.getDetail().forEach(detail -> sub.add( detail.getName()));
                });
        return result;
    }
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param group 描述此参数的作用
     * @return 描述返回值
     * */
    public boolean attachSoapResourceByName( String group) throws ConfigurationException {
        SoapMetaConfigData soapMetaConfigData = findSoapMetaConfigDataByName( soapConfigData, group);
        try (
                Socket socket = new Socket(soapMetaConfigData.getHost(), getPort(soapMetaConfigData))
        ) {
            return !socket.isClosed() && socket.isConnected();
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param soapConfigData 描述此参数的作用
     * @param group 描述此参数的作用
     * @return 描述返回值
     * */
    public static SoapMetaConfigData findSoapMetaConfigDataByName( SoapConfigData soapConfigData, final String group) throws ConfigurationException {
        List<SoapMetaConfigData> soapMetaConfigDataList =
                soapConfigData
                        .getSoapList()
                        .parallelStream()
                        .filter( soapMetaConfigData -> soapMetaConfigData.getName().equals( group))
                        .collect( Collectors.toList());
        if ( soapMetaConfigDataList.isEmpty()) {
            throw new ConfigurationException( format( "找不到配置项_soap.soapList.{name=%s}", group));
        } else if ( soapMetaConfigDataList.size()>=2) {
            throw new ConfigurationException( format( "配置项_soap.soapList.{name=%s}出现了%d次", group, soapMetaConfigDataList.size()));
        } else {
            return soapMetaConfigDataList.get( 0);
        }
    }
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param soapMetaConfigData 描述此参数的作用
     * @param sub 描述此参数的作用
     * @return 描述返回值
     * */
    public static SoapSubConfigData findSoapSubConfigDataByName( SoapMetaConfigData soapMetaConfigData, final String sub) throws ConfigurationException {
        List<SoapSubConfigData> soapSubConfigDataList =
                soapMetaConfigData
                        .getDetail()
                        .parallelStream()
                        .filter( soapSubConfigData -> soapSubConfigData.getName().equals( sub))
                        .collect(Collectors.toList());
        if ( soapSubConfigDataList.isEmpty()) {
            throw new ConfigurationException(format( "找不到配置项_soap.soapList.{name=%s, detail.{name=%s}}", soapMetaConfigData.getName(), sub));
        } else if ( soapSubConfigDataList.size()>=2) {
            throw new ConfigurationException(format( "配置项_soap.soapList.{name=%s, detail.{name=%s}}出现了%d", soapMetaConfigData.getName(), sub, soapSubConfigDataList.size()));
        } else {
            return soapSubConfigDataList.get( 0);
        }
    }

    public SoapFactory(
            SoapConfigData soapConfigData
    ) {
        this.soapConfigData = soapConfigData;
    }

    //==== 华丽的分割线 === 私有资源

    @Getter
    private SoapConfigData soapConfigData;
    /**
     * 各个协议的默认端口
     * */
    private static final Map<String, Integer> PORT_DEFAULT = new HashMap<>();

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param call 描述此参数的作用
     * @param soapMetaConfigData 描述此参数的作用
     * @param soapSubConfigData 描述此参数的作用
     * */
    private void decorateCall( Call call, SoapMetaConfigData soapMetaConfigData, SoapSubConfigData soapSubConfigData) {
        //确定访问路径
        call.setTargetEndpointAddress( makeURL( soapMetaConfigData, soapSubConfigData));
        if ( Assert.notNul( soapSubConfigData.getSOperationName())) {
            call.setOperationName( soapSubConfigData.getSOperationName());
        } else if ( Assert.notNul( soapSubConfigData.getSOperationName_QName())) {
            call.setOperationName( soapSubConfigData.makeOperationName());
        } else {
            throw TkpoleException.of( TkpoleExceptionPredictable.ERR_CONFIG, "没有配置OperationName");
        }
        if ( Assert.notNul( soapSubConfigData.getSParameter())) {
            soapSubConfigData.getSParameter().forEach( ( k, v) -> {
                try {
                    call.addParameter(
                            k,
                            ( QName) Class.forName("org.apache.axis.encoding.XMLType").getField( v.getQName()).get(null),
                            ( ParameterMode) Class.forName("javax.xml.rpc.ParameterMode").getField( v.getParameterMode()).get(null));
                } catch ( ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
                    log.error(e.getMessage(),e);
                }
            });
        }
        if ( Assert.notNull( soapSubConfigData.getSReturnType())) {
            try {
                call.setReturnType( ( QName)Class.forName( "org.apache.axis.encoding.XMLType").getField( soapSubConfigData.getSReturnType()).get( null));
            } catch ( ClassNotFoundException | NoSuchFieldException |IllegalAccessException e) {
                log.error( e.getMessage(), e);
            }

        }

        call.setTimeout( Assert.notNull( soapSubConfigData.getSTimeout()) ?  soapSubConfigData.getSTimeout() : soapMetaConfigData.getSTimeout());
    }
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param soapMetaConfigData 描述此参数的作用
     * @param soapSubConfigData 描述此参数的作用
     * @return 描述返回值
     * */
    private URL makeURL( SoapMetaConfigData soapMetaConfigData, SoapSubConfigData soapSubConfigData) {

        URL url;
        try {
            url = new URIBuilder()
                    .setScheme( soapMetaConfigData.getScheme())
                    .setHost( Assert.notNul( soapMetaConfigData.getHost()) ? soapMetaConfigData.getHost() : soapConfigData.getHost())
                    .setPort( Assert.notNull( soapMetaConfigData.getPort()) ? soapMetaConfigData.getPort() : soapConfigData.getPort())
                    .setPath( soapSubConfigData.getPath())
                    .build()
                    .toURL();
        } catch ( URISyntaxException | MalformedURLException e) {
            log.error( e.getMessage(), e);
            throw TkpoleException.of( TkpoleExceptionPredictable.ERR_INIT, "不能初始化URL");
        }
        return url;
    }
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param soapMetaConfigData 描述此参数的作用
     * @return 描述返回值
     * */
    private int getPort( SoapMetaConfigData soapMetaConfigData) {

        Integer port = soapMetaConfigData.getPort();
        return Assert.notNull(port) ? port : PORT_DEFAULT.get( soapMetaConfigData.getScheme().toUpperCase());
    }

    static {
        PORT_DEFAULT.put("HTTP", 80);
        PORT_DEFAULT.put("HTTPS", 443);
    }
}
