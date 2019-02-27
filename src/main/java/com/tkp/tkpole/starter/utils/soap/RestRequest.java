package com.tkp.tkpole.starter.utils.soap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpEntity;

import java.util.Map;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/8/9
 *
 * @author guojy24
 * @version 1.0
 * */
@Data @NoArgsConstructor @AllArgsConstructor
public class RestRequest {
    public RestRequest( HttpEntity httpEntity) {
        this.httpEntity = httpEntity;
    }

    /**
     * 路径参数
     * */
    private Map<String, String> pathValues;

    /**
     * 请求头参数
     * */
    private Map<String, String> headers;

    /**
     * 查询参数
     * */
    private Map<String, String> queryValues;

    /**
     * 请求实体
     * */
    private HttpEntity httpEntity;
}
