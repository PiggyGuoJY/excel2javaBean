package com.tkp.tkpole.starter.utils.soap;

import org.apache.http.client.methods.CloseableHttpResponse;

import java.util.List;

/**
 * 可访问的Rest
 *
 * <p> 创建时间：2018/9/18
 *
 * @author guojy24
 * @version 1.0
 * */
public interface RestAccessible {
   /**
    * 可执行方法
    *
    * @param restRequests 请求实体
    * @param tkpoleFunction 响应处理器
    * @param defultT 默认返回
    * @return 返回实体列表
    * */
   <T> List<T>  execute( List<RestRequest> restRequests, final TkpoleFunction<CloseableHttpResponse,T> tkpoleFunction, final T defultT);
}
