package com.tkp.tkpole.starter.utils.ftp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * FTP配置数据结构
 *
 * <p> 创建时间：2018/10/11
 *
 * @author guojy24
 * @version 1.0
 * */
@Component @ConfigurationProperties("_ftp")
@Data @AllArgsConstructor @NoArgsConstructor
public class FtpConfigData {
    private Boolean autoConfig = true;
    private List<FtpMetaConfigData> ftpList = new ArrayList<>();
}
