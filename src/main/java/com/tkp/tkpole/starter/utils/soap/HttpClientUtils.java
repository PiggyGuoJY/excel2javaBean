package com.tkp.tkpole.starter.utils.soap;


import com.tkp.tkpole.starter.utils.soap.model.rest.RestConfigData;
import com.tkp.tkpole.starter.utils.soap.model.rest.RestConnPoolConfigData;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/8/9
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j
public class HttpClientUtils {
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @return 描述返回值
     * */
    PoolingHttpClientConnectionManager getDefaultPoolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        //本连接池的最大并发量
        poolingHttpClientConnectionManager.setMaxTotal(this.restConnPoolConfigData.getMaxTotal());
        //单路由默认最大并发量
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(this.restConnPoolConfigData.getDefaultMaxPerRoute());
        //为特定路由单独设置最大并发量
        if ( this.restConnPoolConfigData.getMaxPerRoute().size()!=0) {
            this.restConnPoolConfigData.getMaxPerRoute().forEach( ( k, v) -> poolingHttpClientConnectionManager.setMaxPerRoute( new HttpRoute( new HttpHost( k)), v));
        }
        return poolingHttpClientConnectionManager;
    }

    public HttpClientUtils(
            RestConfigData restConfigData
    ) {
        this.restConnPoolConfigData = restConfigData.getConnPool();
    }

    private RestConnPoolConfigData restConnPoolConfigData;
}
