package com.guojy.soap;

import com.guojy.JsonXmlUtil;
import com.guojy.model.Msg;
import com.guojy.soap.model.rest.RestMetaConfigData;
import com.guojy.soap.model.rest.RestSubConfigData;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.guojy.Assert.notNul;
import static com.guojy.model.Msg.msg;
import static java.lang.String.format;

/**
 * <p> 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 * <p> 创建时间：2018/1/4
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j
@ToString( exclude = {"restMetaConfigData", "restSubConfigData", "closeableHttpClient"})
public final class RestService implements RestAccessible {
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param <T> 指定实体
     * @param restRequests 描述此参数的作用
     * @param tkpoleFunction 描述此参数的作用
     * @param defultT 描述此参数的作用
     * @return 描述返回值
     * */
    @Override
    public <T> List<T> execute( List<RestRequest> restRequests, final TkpoleFunction<CloseableHttpResponse,T> tkpoleFunction, final T defultT) {
        List<T> result = new ArrayList<>();
        List<Callable<T>> tasks = new ArrayList<>();
        restRequests.forEach( request -> {
            try {
                tasks.add( new RestTask<>( this.closeableHttpClient, this.decorateHttpRequestBase( this.httpRequestBase, request), tkpoleFunction, defultT));
            } catch ( CloneNotSupportedException | URISyntaxException e) { log.error( e.getMessage(), e); }
        });
        ExecutorService executorService = Executors.newFixedThreadPool( tasks.size());
        try {
            List<Future<T>> futureList = executorService.invokeAll( tasks, 10, TimeUnit.SECONDS);
            futureList.forEach( future -> {try {
                result.add( future.get());
            } catch ( ExecutionException | InterruptedException e) {
                log.error( e.getMessage(), e);
                result.add( defultT instanceof Msg ? (T)msg( e) : defultT);
            } catch ( Exception e) {
                log.error( e.getMessage(), e);
                result.add( defultT instanceof Msg ? (T)msg( e) : defultT);
            }});
        } catch ( InterruptedException e) { log.error( e.getMessage(), e); }
        executorService.shutdown();
        return result;
    }
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param <T> T
     * @param ts 描述此参数的作用
     * @return 描述返回值
     * */
    public static <T> List<RestRequest> javaBeans2Restquests (T ... ts) {
        return ts.length == 1 ? Collections.singletonList( new RestRequest( javaBean2HttpEntity( ts[0]))) : Stream.of( ts).map( t -> new RestRequest( javaBean2HttpEntity( t))).collect(Collectors.toList());
    }
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param <T> T
     * @param closeableHttpResponse 描述此参数的作用
     * @param tClass 描述此参数的作用
     * @return 描述返回值
     * */
    public static <T> T response2JavaBean (CloseableHttpResponse closeableHttpResponse, Class<T> tClass) {
        try {
            return httpEntity2JavaBean( closeableHttpResponse.getEntity(), tClass);
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            try {
                return tClass.newInstance();
            } catch ( InstantiationException | IllegalAccessException e2) {
                log.error( e2.getMessage(), e2);
                return null;
            }
        }
    }
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param <T> T
     * @param t 描述此参数的作用
     * @return 描述返回值
     * */
    public static <T> HttpEntity javaBean2HttpEntity( T t) { return new StringEntity( JsonXmlUtil.javaBean2Json( t), ContentType.APPLICATION_JSON); }
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param <T> T
     * @param httpEntity 描述此参数的作用
     * @param tClass 描述此参数的作用
     * @return 描述返回值
     * @throws IOException IOException
     * */
    public static <T> T httpEntity2JavaBean(HttpEntity httpEntity, Class<T> tClass) throws IOException { return JsonXmlUtil.json2JavaBean( tClass, EntityUtils.toString(httpEntity)); }

    public RestService(
            HttpRequestBase httpRequestBase,
            HttpClientConnectionManager httpClientConnectionManager,
            HttpHost proxy,
            RestMetaConfigData restMetaConfigData,
            RestSubConfigData restSubConfigData
    ){
        this.httpRequestBase = httpRequestBase;
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if ( httpClientConnectionManager!=null) {
            httpClientBuilder.setConnectionManager( httpClientConnectionManager);
        }
        if( proxy!=null) {
            httpClientBuilder.setDefaultRequestConfig( RequestConfig.custom().setProxy( proxy).build());
        }
        this.closeableHttpClient = httpClientBuilder.build();
        this.restMetaConfigData = restMetaConfigData;
        this.restSubConfigData = restSubConfigData;
    }

    //==== 华丽的分割线 === 私有资源

    private RestMetaConfigData restMetaConfigData;
    private RestSubConfigData restSubConfigData;
    private CloseableHttpClient closeableHttpClient ;
    private HttpRequestBase httpRequestBase;

    @Slf4j
    @AllArgsConstructor
    private static class RestTask<T> implements Callable<T> {
        @Override
        public T call() throws IOException {
            RestTask.debugHttpRequestBase( httpRequestBase);
            long begin = System.currentTimeMillis();
            try (
                    CloseableHttpResponse closeableHttpResponse = httpClient.execute( httpRequestBase)
            ) {
                log.debug( String.format( "REST调用执行时长: %d ms", System.currentTimeMillis()-begin));
                RestTask.debugHttpResponse( closeableHttpResponse);
                return tkpoleFunction.apply( closeableHttpResponse);
            } catch ( Exception e) {
                log.error( e.getMessage(), e);
                return t;
            }
        }

        //==== 华丽的分割线 === 私有资源
        private final CloseableHttpClient httpClient;
        private final HttpRequestBase httpRequestBase;
        private final TkpoleFunction<CloseableHttpResponse, T> tkpoleFunction;
        private final T t;

        /**
         * 打印请求头内容
         *
         * @param httpRequestBase  http请求
         * */
        private static void debugHttpRequestBase( HttpRequestBase httpRequestBase) {
            final StringBuilder debug = new StringBuilder( format(
                    "%n-- rest request --%n请求头:%n" +
                            "    请求地址: %s%n" +
                            "    请求方法: %s%n" +
                            "    请求版本: %s%n",
                    httpRequestBase.getURI(),
                    httpRequestBase.getMethod(),
                    httpRequestBase.getProtocolVersion()));
            Stream.of( httpRequestBase.getAllHeaders()).forEach( header -> debug.append( format( "    %s: %s%n", header.getName(), header.getValue())));
            log.debug( debug.toString());
        }
        /**
         * 打印响应头内容
         *
         * @param httpResponse http响应
         * */
        private static void debugHttpResponse( HttpResponse httpResponse) {
            final StringBuilder debug = new StringBuilder( format(
                    "%n-- rest response--%n响应头:%n" +
                            "    状态码  : %d%n" +
                            "    状态描述: %s%n" +
                            "    协议版本: %s%n",
                    httpResponse.getStatusLine().getStatusCode(),
                    httpResponse.getStatusLine().getReasonPhrase(),
                    httpResponse.getProtocolVersion().toString()));
            Stream.of( httpResponse.getAllHeaders()).forEach( header -> debug.append( format( "    %s: %s%n", header.getName(), header.getValue())));
            log.debug( debug.toString());
        }
    }
    /**
     * <p> 修饰请求
     *
     * @param httpRequestBase 原始请求体(根据定义生成)
     * @param restRequest 请求参数
     * @return 修饰后的请求
     * @throws CloneNotSupportedException 异常声明
     * @throws URISyntaxException 异常声明
     * */
    private HttpRequestBase decorateHttpRequestBase( final HttpRequestBase httpRequestBase, RestRequest restRequest) throws CloneNotSupportedException, URISyntaxException {
        HttpRequestBase result = ( HttpRequestBase) httpRequestBase.clone();
        if ( notNul( restRequest.getPathValues())) {
            String path = httpRequestBase.getURI().getPath();
            Iterator<String> pathKeyIterator = restRequest.getPathValues().keySet().iterator();
            while ( pathKeyIterator.hasNext()) {
                String pathKey = pathKeyIterator.next();
                path = path.replaceAll( "\\{" + pathKey + "}", restRequest.getPathValues().get( pathKey));
            }
            result.setURI( new URIBuilder( httpRequestBase.getURI()).setPath( path).build());
        }
        if ( notNul( restRequest.getQueryValues())) {
            URIBuilder uriBuilder = new URIBuilder( result.getURI());
            uriBuilder.setCharset( Charset.forName( restSubConfigData.getUrlCharset()));
            restRequest.getQueryValues().forEach( uriBuilder::addParameter);
            result.setURI( uriBuilder.build());
        }
        if ( notNul( restRequest.getHeaders())) {
            restRequest.getHeaders().forEach( result::setHeader);
        }
        switch ( httpRequestBase.getMethod()) {
            case HttpPost.METHOD_NAME:
                if ( restRequest.getHttpEntity()!=null) { ( ( HttpPost)result).setEntity( restRequest.getHttpEntity()); } break;
            case HttpGet.METHOD_NAME:
            case HttpHead.METHOD_NAME:
                if ( restRequest.getHttpEntity()!=null) { log.warn( "[{}]方法不能设置请求实体", httpRequestBase.getMethod()); } break;
            default:
                log.warn( "未定义的Http方法[{}]", httpRequestBase.getMethod()); break;
        }
        return result;
    }
}
