package com.guojy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.guojy.Assert.notNul;

/**
 * 1.文件工具类(这里主要是一些杂七杂八的工具)
 * 2.静态获取bean实例
 * <p> 创建时间：2018/2/11
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE)
public final class ResourceUtil {

    /**
     * <p> 获取文件的拓展名
     *
     * @param fileName 文件名
     * @return 拓展名,当文件没有拓展名时返回""
     * */
    public static String getExtensionName( String fileName) {
        int pos;
        return notNul( fileName) ? ( ( pos = fileName.lastIndexOf( '.'))==-1 ? "" : fileName.substring( pos+1)) : "";
    }
}
