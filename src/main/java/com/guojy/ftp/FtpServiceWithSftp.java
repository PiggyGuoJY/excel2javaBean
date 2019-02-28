package com.guojy.ftp;

import com.google.common.base.Charsets;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Vector;

import static com.guojy.Assert.notNull;

/**
 * 文件服务的sftp协议实现
 *
 * <p> 创建时间：2018/9/25
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE) @AllArgsConstructor
public final class FtpServiceWithSftp extends AbstractFtpService {

    @Override
    protected boolean isRemoteFileExist( Path remoteFilePath) {
        ChannelSftp channelSftp = (ChannelSftp)channel;
        try {
            Path remoteFilePathParent;
            if ( !notNull( remoteFilePathParent = remoteFilePath.getParent())) { return false;}
            @SuppressWarnings("unchecked")
            Vector<ChannelSftp.LsEntry> lsEntries = channelSftp.ls( changeFileSeparator( remoteFilePathParent));
            return lsEntries.parallelStream().anyMatch( lsEntry -> lsEntry.getFilename().equals( remoteFilePath.toFile().getName())&&lsEntry.getAttrs().isReg());
        } catch ( SftpException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }
    @Override
    protected boolean isRemoteDirectoryExist( Path remoteDirectoryPath) {
        ChannelSftp channelSftp = ( ChannelSftp)channel;
        try {
            String originalPath = channelSftp.pwd();
            channelSftp.cd( changeFileSeparator( remoteDirectoryPath));
            channelSftp.cd( originalPath);
            return true;
        } catch ( SftpException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }
    @Override
    protected boolean createRemoteDirectory( Path remoteDirectoryPath) {
        ChannelSftp channelSftp = ( ChannelSftp)channel;
        try {
            channelSftp.mkdir( changeFileSeparator( remoteDirectoryPath));
            return true;
        } catch ( SftpException e) {
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
        ChannelSftp channelSftp = ( ChannelSftp)channel;
        try {
            IOUtils.copy(
                    reader,
                    channelSftp.put( changeFileSeparator( remotePath), CliHelper.getMAP_OVERWRITE().getOrDefault( overwriteType, ChannelSftp.OVERWRITE)),
                    Charsets.UTF_8);
            return true;
        } catch ( SftpException | IOException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }
    @Override
    protected InputStream downloadFile(Path remoteFilePath) {
        ChannelSftp channelSftp = ( ChannelSftp)channel;
        try  {
            return channelSftp.get( changeFileSeparator( remoteFilePath));
        } catch ( SftpException e) {
            log.error( e.getMessage(), e);
            return null;
        }
    }
    @Override
    protected void afterDownloadFile() { }
    @Override
    protected boolean deleteRemoteFile( Path remoteFilePath) {
        ChannelSftp channelSftp = ( ChannelSftp)channel;
        try  {
            channelSftp.rm( changeFileSeparator( remoteFilePath));
            return true;
        } catch ( SftpException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }
    @Override
    protected boolean deleteRemoteDirectory( Path remoteDirectoryPath) {
        ChannelSftp channelSftp = ( ChannelSftp)channel;
        try {
            channelSftp.rmdir( changeFileSeparator( remoteDirectoryPath));
            return true;
        } catch ( SftpException e) {
            log.error( e.getMessage(), e);
            return false;
        }
    }
    @Override
    protected void closeConnection() {
        if ( notNull( channel)&&channel.isConnected()) { channel.disconnect(); }
        if ( notNull( session)&&session.isConnected()) { session.disconnect(); }
    }

    private Session session;
    private Channel channel;
}
