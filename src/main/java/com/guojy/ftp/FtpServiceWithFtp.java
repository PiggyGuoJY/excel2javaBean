package com.guojy.ftp;

import com.google.common.base.Charsets;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 *  文件服务的ftp协议实现
 *
 * <p> 创建时间：2018/9/18
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE) @AllArgsConstructor
public final class FtpServiceWithFtp extends AbstractFtpService {

    @Override
    protected boolean isRemoteFileExist( Path remoteFilePath) {
        try {
            return Stream.of( ftpClient.listFiles( changeFileSeparator( remoteFilePath.getParent()))).anyMatch( ftpFile -> ftpFile.getName().equals( remoteFilePath.toFile().getName()) && ftpFile.isFile());
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }

    @Override
    protected boolean isRemoteDirectoryExist( Path remoteDirectoryPath) {
        try {
            String originalPath = ftpClient.doCommandAsStrings( "pwd", null)[0].replaceAll("^.*?\"(.+)\"$","$1");
            boolean result = ftpClient.changeWorkingDirectory( changeFileSeparator( remoteDirectoryPath));
            ftpClient.changeWorkingDirectory( originalPath);
            return result;
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }
    @Override
    protected boolean createRemoteDirectory( Path remoteDirectoryPath) {
        try {
            return ftpClient.makeDirectory( changeFileSeparator( remoteDirectoryPath));
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }
    @Override
    protected boolean uploadFile( Reader reader, Path remotePath, String overwriteType) {
        if ( !CliHelper.getMAP_OVERWRITE().keySet().contains( overwriteType)) {
            log.warn( "不能识别的文件覆盖方式 [{}]", overwriteType);
            return false;
        }
        try {
            switch ( overwriteType) {
                case COMMAND_OVERWRITE__OVERWRITE:
                    IOUtils.copy( reader, ftpClient.storeFileStream(changeFileSeparator( remotePath)), Charsets.UTF_8);
                    return true;
                case COMMAND_OVERWRITE__APPEND:
                    IOUtils.copy( reader, ftpClient.appendFileStream( changeFileSeparator( remotePath)), Charsets.UTF_8);
                    return true;
                default:
                    log.warn( "不能识别的文件覆盖方式 [{}]", overwriteType);
                    return false;
            }
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }
    @Override
    protected InputStream downloadFile( Path remoteFilePath) {
        try {
            return ftpClient.retrieveFileStream( changeFileSeparator( remoteFilePath));
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            return null;
        }
    }
    @Override
    protected void afterDownloadFile() {
        try {
            ftpClient.completePendingCommand();
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
        }
    }
    @Override
    protected boolean deleteRemoteFile( Path remoteFilePath) {
        try {
            return ftpClient.deleteFile( changeFileSeparator( remoteFilePath));
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }
    @Override
    protected boolean deleteRemoteDirectory( Path remoteDirectoryPath) {
        try {
            return ftpClient.removeDirectory( changeFileSeparator( remoteDirectoryPath));
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }
    @Override
    protected void closeConnection() {
        if ( ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch ( IOException e) {
                log.error( e.getMessage(), e);
            }
        }
    }

    private FTPClient ftpClient;
}
