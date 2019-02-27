package com.guojy.ftp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 细节数据配置类
 * 
 * <p> 创建时间：2018/9/19
 * 
 * @author guojy24
 * @version 1.0
 * */
@Data @AllArgsConstructor @NoArgsConstructor
public class FtpMetaConfigData {

    private String name;
    private String desc;

    private String host;
    private Integer port = 22;
    private String username;
    private String password;

    private String type = "sftp";
}
