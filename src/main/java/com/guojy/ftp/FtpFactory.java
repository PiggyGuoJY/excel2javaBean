package com.guojy.ftp;

import com.google.common.base.Charsets;
import com.guojy.ftp.model.FtpConfigData;
import com.guojy.ftp.model.FtpMetaConfigData;
import com.guojy.model.Msg;
import com.guojy.soap.TkpoleFunction;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import javax.naming.ConfigurationException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.guojy.model.Msg.msg;
import static java.lang.String.format;

/**
 * Ftp服务工厂
 *
 * <p> 创建时间：2018/9/18
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j
public class FtpFactory {

    public final Msg<FtpAccessible> getFtpAccessibleByName(String name) {
        try {
            FtpMetaConfigData ftpMetaConfigData = findFtpMetaConfigDataByName( this.ftpConfigData, name);
            FtpType ftpType = FtpType.forName( ftpMetaConfigData.getType());
            return msg( ftpType.tkpoleFunction.apply( ftpMetaConfigData));
        } catch ( Exception e) {
            log.error( e.getMessage(), e);
            return msg( e);
        }
    }


    public FtpFactory(
            FtpConfigData ftpConfigData
    ) {
        this.ftpConfigData = ftpConfigData;
    }

    //==== 华丽的分割线 === 私有资源

    private FtpConfigData ftpConfigData;
    private static final int CANT_REPEAT = 2;

    public boolean testExistenceFtpMetaConfigDataByName( @NonNull String name ) {
        try {
            findFtpMetaConfigDataByName( this.ftpConfigData, name);
            return true;
        } catch ( ConfigurationException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }

    private FtpMetaConfigData findFtpMetaConfigDataByName( @NonNull final FtpConfigData ftpConfigData, @NonNull String name) throws ConfigurationException {
        List<FtpMetaConfigData> ftpMetaConfigDataList =
                ftpConfigData
                        .getFtpList()
                        .parallelStream()
                        .filter( ftpMetaConfigData -> ftpMetaConfigData.getName().equals( name))
                        .collect( Collectors.toList());
        if ( ftpMetaConfigDataList.isEmpty()) {
            throw new ConfigurationException( format( "找不到配置项_ftp.ftpList.{name=%s}", name));
        } else if ( ftpMetaConfigDataList.size()>=CANT_REPEAT) {
            throw new ConfigurationException( format( "配置项_ftp.ftpList.{name=%s}出现了%d次", name, ftpMetaConfigDataList.size()));
        } else {
            return ftpMetaConfigDataList.get( 0);
        }
    }

    /**
     * 文件传输协议枚举
     *
     * <p> 创建时间：2018/9/19
     *
     * @author guojy24
     * @version 1.0
     * */
    @Slf4j
    private enum FtpType {
        /**
         * FTP协议实现
         * */
        FTP( "ftp", ftpMetaConfigData -> {
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect( ftpMetaConfigData.getHost(), ftpMetaConfigData.getPort());
            ftpClient.setCharset( Charsets.UTF_8);
            ftpClient.setControlEncoding( Charsets.UTF_8.name());
            ftpClient.setFileType( FTPClient.BINARY_FILE_TYPE);
            ftpClient.setBufferSize( 1024*4);
            ftpClient.login( ftpMetaConfigData.getUsername(), ftpMetaConfigData.getPassword());
            ftpClient.enterLocalPassiveMode();
            return new FtpServiceWithFtp( ftpClient);
        }),
        /**
         * SFTP协议实现
         * */
        SFTP( "sftp", ftpMetaConfigData -> {
            JSch jSch = new JSch();
            Session session = jSch.getSession( ftpMetaConfigData.getUsername(), ftpMetaConfigData.getHost(), ftpMetaConfigData.getPort());
            session.setPassword( ftpMetaConfigData.getPassword());
            Properties config = new Properties();
            config.put( "StrictHostKeyChecking", "no");
            session.setConfig( config);
            session.connect();
            Channel channel = session.openChannel( "sftp");
            channel.connect();
            return new FtpServiceWithSftp( session, channel);
        });

        public static FtpType forName( @NonNull String name) throws ConfigurationException {
            List<FtpType> ftpTypeList = Stream.of( FtpType.values()).parallel().filter( ftpType -> ftpType.name.equals( name)).collect( Collectors.toList());
            if ( ftpTypeList.isEmpty()) {
                throw new ConfigurationException( format( "在枚举类[%s]中没有找到name=[%s]的常量", FtpType.class.getCanonicalName(), name));
            } else if ( ftpTypeList.size()>=CANT_REPEAT) {
                throw new ConfigurationException( format( "在枚举类[%s]中找到name=[%s]的常量[%s]次", FtpType.class.getCanonicalName(), name, ftpTypeList.size()));
            } else {
                return ftpTypeList.get( 0);
            }
        }


        FtpType(
                String name,
                TkpoleFunction<FtpMetaConfigData,FtpAccessible> tkpoleFunction
        ) {
            this.name = name;
            this.tkpoleFunction = tkpoleFunction;
        }

        private String name;
        private TkpoleFunction<FtpMetaConfigData,FtpAccessible> tkpoleFunction;
    }
}
