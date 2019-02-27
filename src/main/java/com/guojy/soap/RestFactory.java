package com.guojy.soap;

import com.guojy.exception.TkpoleException;
import com.guojy.exception.TkpoleExceptionPredictable;
import com.guojy.soap.model.rest.RestConfigData;
import com.guojy.soap.model.rest.RestMetaConfigData;
import com.guojy.soap.model.rest.RestSubConfigData;
import com.tkp.tkpole.starter.utils.Assert;
import com.tkp.tkpole.starter.utils.exception.TkpoleException;
import com.tkp.tkpole.starter.utils.exception.TkpoleExceptionPredictable;
import com.tkp.tkpole.starter.utils.soap.model.rest.RestConfigData;
import com.tkp.tkpole.starter.utils.soap.model.rest.RestMetaConfigData;
import com.tkp.tkpole.starter.utils.soap.model.rest.RestSubConfigData;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.springframework.util.StringUtils;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tkp.tkpole.starter.utils.Assert.notNull;
import static java.lang.String.format;

/**
 * <p> Rest访问器工厂
 * <p> 创建时间：2018/1/19
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j
public final class RestFactory {
    /**
     * <p> 根据rest资源组名和子名获取服务
     *
     * @param group 组名
     * @param sub 子名
     * @return 根据group和sub找到的配置装配的RestService
     * */
    public final RestAccessible getRestAccessibleByName( String group, String sub) {
        return getRestServiceByName( group, sub);
    }

    public final boolean testExistenceRestServiceByName( String group, String sub) {
        try {
            findRestDetailConfigDataByName( findRestMetaConfigDataByName( restConfigData, group), sub);
            return true;
        } catch ( ConfigurationException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }

    /**
     * <p> 根据rest资源组名和子名获取服务
     *
     * @param group 组名
     * @param sub 子名
     * @return 根据group和sub找到的配置装配的RestService
     * */
    @Deprecated @SneakyThrows
    public final RestService getRestServiceByName( String group, String sub) {
        RestMetaConfigData restMetaConfigData = findRestMetaConfigDataByName( restConfigData, group);
        RestSubConfigData restSubConfigData = findRestDetailConfigDataByName(restMetaConfigData, sub);
        URI uri =  RestFactory.buildURI( restConfigData, restMetaConfigData, restSubConfigData);
        HttpRequestBase httpRequestBase = RestFactory.buildMethod(restMetaConfigData, restSubConfigData);
        httpRequestBase.setURI(uri);
        HttpClientConnectionManager httpClientConnectionManager = restConfigData.getUseConnPool() ? this.httpClientUtils.getDefaultPoolingHttpClientConnectionManager(): null;
        HttpHost proxy = restSubConfigData.getUseProxy() ? restSubConfigData.getProxy().getProxy() : null;
        return new RestService( httpRequestBase, httpClientConnectionManager, proxy, restMetaConfigData, restSubConfigData);
    }

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @return 描述返回值
     * */
    public final Map<String,List<String>> getAvailableRestResource() {
        Map<String,List<String>> result = new HashMap<>( 10);
        restConfigData.getRestList().forEach(
                rest -> {
                    List<String> sub = new LinkedList<>();
                    result.put(rest.getName(), sub);
                    rest.getDetail().forEach(detail -> sub.add( detail.getName()));
                });
        return result;
    }

    /**
     * <p> 服务通断测试
     *
     * @param group 描述此参数的作用
     * @return 描述返回值
     * */
    public final boolean attachRestResourceByName(String group) throws ConfigurationException {
        RestMetaConfigData restMetaConfigData = findRestMetaConfigDataByName(restConfigData, group);
        try ( Socket socket = new Socket(restMetaConfigData.getHost(), getPort(restMetaConfigData))) {
            return !socket.isClosed() && socket.isConnected();
        } catch (IOException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param restMetaConfigData 描述此参数的作用
     * @param sub 描述此参数的作用
     * @return 描述返回值
     * */
    private static RestSubConfigData findRestDetailConfigDataByName( RestMetaConfigData restMetaConfigData, final String sub) throws ConfigurationException {
        List<RestSubConfigData> restSubConfigDataList =
                restMetaConfigData
                        .getDetail()
                        .parallelStream()
                        .filter( restSubConfigData -> restSubConfigData.getName().equals( sub)).collect( Collectors.toList());
        if ( restSubConfigDataList.isEmpty()) {
            throw new ConfigurationException(format( "找不到配置项_rest.restList.{name=%s, detail.{name=%s}},", restMetaConfigData.getName(), sub));
        } else if ( restSubConfigDataList.size()>=CANT_REPEAT) {
            throw new ConfigurationException( format( "配置项_rest.restList.{name=%s, detail.{name=%s}}出现了%d", restMetaConfigData.getName(), sub, restSubConfigDataList.size()));
        } else {
            return restSubConfigDataList.get(0);
        }
    }

    private static final int CANT_REPEAT = 2;

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param restConfigData 描述此参数的作用
     * @param group 描述此参数的作用
     * @return 描述返回值
     * */
    public static RestMetaConfigData findRestMetaConfigDataByName(final RestConfigData restConfigData, final String group) throws ConfigurationException {
        List<RestMetaConfigData> restMetaConfigDataList =
                restConfigData
                        .getRestList()
                        .parallelStream()
                        .filter( restMetaConfigData -> restMetaConfigData.getName().equals( group))
                        .collect( Collectors.toList());
        if ( restMetaConfigDataList.isEmpty()) {
            throw new ConfigurationException( format( "找不到配置项_rest.restList.{name=%s}", group));
        } else if ( restMetaConfigDataList.size()>=2) {
            throw new ConfigurationException( format( "配置项_rest.restList.{name=%s}出现了%d次", group, restMetaConfigDataList.size()));
        } else {
            return restMetaConfigDataList.get( 0);
        }
    }

    public RestFactory(
           @NonNull RestConfigData restConfigData, HttpClientUtils httpClientUtils
    ) {
        this.restConfigData = restConfigData;
        if ( this.restConfigData.getUseConnPool()) {
            this.httpClientUtils = httpClientUtils;
        }
    }

    //==== 华丽的分割线 === 私有资源

    /**
     * 各个协议的默认端口
     * */
    private static final Map<String, Integer> PORT_DEFAULT = new HashMap<>();
    static {
        PORT_DEFAULT.put("HTTP", 80);
        PORT_DEFAULT.put("HTTPS", 443);
    }

    @Getter
    private RestConfigData restConfigData;
    private HttpClientUtils httpClientUtils;

    private static URI buildURI( RestConfigData restConfigData, RestMetaConfigData restMetaConfigData, RestSubConfigData restSubConfigData) throws URISyntaxException {
        URI uri;
        URIBuilder uriBuilder = new URIBuilder()
                .setCharset( Charset.forName( restSubConfigData.getUrlCharset()))
                .setScheme( restMetaConfigData.getScheme())
                .setHost( Assert.notNul( restMetaConfigData.getHost()) ? restMetaConfigData.getHost() : restConfigData.getHost())
                .setPort( notNull( restMetaConfigData.getPort()) ? getPort(restMetaConfigData) : restConfigData.getPort())
                .setPath( restSubConfigData.getPath());
        restMetaConfigData.getParameters().forEach( (parameterKey, parameterValues) ->  parameterValues.forEach( parameterValue -> uriBuilder.addParameter( parameterKey, parameterValue)));
        restSubConfigData.getParameters().forEach( (parameterKey, parameterValues) ->  parameterValues.forEach( parameterValue -> uriBuilder.addParameter( parameterKey, parameterValue)));
        uri = uriBuilder.build();
        log.debug( uri.toString());
        return uri;
    }

    private static HttpRequestBase buildMethod( RestMetaConfigData restMetaConfigData, RestSubConfigData restSubConfigData) {
        HttpRequestBase httpRequestBase;
        String method  = restSubConfigData.getMethod();
        method = StringUtils.isEmpty( method) ? restMetaConfigData.getMethod() : method;
        switch (method.toUpperCase()) {
            case HttpGet.METHOD_NAME:
                httpRequestBase = new HttpGet();
                break;
            case HttpPost.METHOD_NAME:
                httpRequestBase = new HttpPost();
                break;
            case HttpHead.METHOD_NAME:
                httpRequestBase = new HttpHead();
                break;
            default:
                throw TkpoleException.of( TkpoleExceptionPredictable.ERR_CONFIG, String.format("不支持的请求方法: %s", method));
        }
        restMetaConfigData.getHeaders().forEach( httpRequestBase::setHeader);
        restSubConfigData.getHeaders().forEach( httpRequestBase::setHeader);
        return httpRequestBase;
    }

    private static int getPort( RestMetaConfigData restMetaConfigData) {
        Integer port = restMetaConfigData.getPort();
        return notNull(port) ? port : PORT_DEFAULT.get(restMetaConfigData.getScheme().toUpperCase());
    }
}
