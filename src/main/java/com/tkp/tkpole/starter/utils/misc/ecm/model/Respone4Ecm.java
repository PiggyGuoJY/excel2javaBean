package com.tkp.tkpole.starter.utils.misc.ecm.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

/**
 * ECM接口返回实体
 *
 * <p> 创建时间：2018/10/24
 *
 * @author guojy24
 * @version 1.0
 * */
@Data @NoArgsConstructor( access = AccessLevel.PRIVATE)
public class Respone4Ecm {

    /**
     * ECM上传接口返回实体
     *
     * <p> 创建时间：2018/10/24
     *
     * @author guojy24
     * @version 1.0
     * */
    @Data @AllArgsConstructor @NoArgsConstructor
    public static class Upload {
        /**
         * 类型代码
         * */
        private String cardType;
        /**
         * 关键码
         * */
        private String keyvalue;
        /**
         * 下载链接
         * */
        private String link;
    }

    /**
     * ECM下载接口返回实体
     *
     * <p> 创建时间：2018/10/24
     *
     * @author guojy24
     * @version 1.0
     * */
    @Data @AllArgsConstructor @NoArgsConstructor
    public static class Download {
        /**
         * 文件路径
         * */
        private Path filePath;
        /**
         * 文件名称
         * */
        private String fileName;
    }
}
