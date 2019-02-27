package com.tkp.tkpole.starter.utils.ftp;

import com.tkp.tkpole.starter.utils.model.Msg;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 可通过Ftp或Sftp议访问的
 *
 * <p> 创建时间：2018/9/18
 * <p> 修改时间：2018/10/23 guojy24()
 *
 * @author guojy24
 * @version 1.0
 * */
public interface FtpAccessible extends AutoCloseable {

    String COMMAND_CREATE_DIRECTORY = "createDirectory";
    String COMMAND_OVERWRITE = "overwrite";
    String COMMAND_CREATE_FLAG_FILE = "createFlagFile";
    String COMMAND_PATH = "path";
    String COMMAND_IS_A = "isA";
    String COMMAND_NONE_IS_OK = "noneIsOk";

    String COMMAND_OVERWRITE__OVERWRITE = "overwrite";
    String COMMAND_OVERWRITE__APPEND = "append";
    String COMMAND_IS_A__FILE = "file";
    String COMMAND_IS_A__DIRECTORY = "directory";

    /**
     * <p> 文件上传
     *
     * @param localFilePath 本地路径
     * @param fileSystem 本地文件系统(用于自定义的第三方文件系统)
     * @param remoteDirectoryPath 远程路径
     * @param args 可选操作
     * @return 是否上传成功
     * */
    Msg<Boolean> upload( Path localFilePath, FileSystem fileSystem, Path remoteDirectoryPath, String ... args);
    /**
     * <p> 文件上传
     *
     * @param localFilePath 本地路径
     * @param fileSystem 本地文件系统(用于自定义的第三方文件系统)
     * @param remoteDirectoryPath 远程路径
     * @param args 可选操作
     * @return 是否上传成功
     * */
    default Msg<Boolean> upload( String localFilePath, FileSystem fileSystem, String remoteDirectoryPath, String ... args) {
        return upload( Paths.get( URI.create( localFilePath)), fileSystem, Paths.get( URI.create( remoteDirectoryPath)), args);
    }
    /**
     * <p> 文件上传(使用本地文件系统)
     *
     * @param localFilePath 本地路径
     * @param remoteDirectoryPath 远程路径
     * @param args 可选操作
     * @return 是否上传成功
     * */
    default Msg<Boolean> upload( Path localFilePath, Path remoteDirectoryPath, String ... args) {
        return upload( localFilePath, null, remoteDirectoryPath, args);
    }
    /**
     * <p> 文件上传(使用本地文件系统)
     *
     * @param localFilePath 本地路径
     * @param remoteDirectoryPath 远程路径
     * @param args 可选操作
     * @return 是否上传成功
     * */
    default Msg<Boolean> upload( String localFilePath, String remoteDirectoryPath, String ... args) {
        return upload( Paths.get( URI.create( localFilePath)), Paths.get( URI.create( remoteDirectoryPath)), args);
    }

    /**
     * 文件下载
     *
     * @param fileSystem 目标文件系统
     * @param remoteFilePath 远程文件位置
     * @param args 可选参数
     * @return 下载位置路径集合
     * */
    Msg<Set<Path>> download( FileSystem fileSystem, Path remoteFilePath, String ... args);
    /**
     * 文件下载
     *
     * @param fileSystem 目标文件系统
     * @param remoteFilePath 远程文件位置
     * @param args 可选参数
     * @return 下载位置路径集合
     * */
    default Msg<Set<String>> download( FileSystem fileSystem, String remoteFilePath, String ... args) {
        Msg<Set<Path>> msg = download( fileSystem, Paths.get( URI.create( remoteFilePath)), args);
        return msg.isException() ? new Msg<>( msg.getCode(), msg.getMsg(), msg.getDetail(), null) : new Msg<>( msg.getT().parallelStream().map( Path::toString).collect( Collectors.toSet()));
    }
    /**
     * 文件下载
     *
     * @param remoteFilePath 远程地址
     * @param args 可选操作
     * @return 下载位置路径集合
     * */
    default Msg<Set<Path>> download( Path remoteFilePath, String ... args) {
        return download( null, remoteFilePath, args);
    }
    /**
     * 文件下载
     *
     * @param remoteFilePath 远程地址
     * @param args 可选操作
     * @return 下载位置路径集合
     * */
    default Msg<Set<String>> download( String remoteFilePath, String ... args) {
        Msg<Set<Path>> msg = download( Paths.get( URI.create( remoteFilePath)), args);
        return msg.isException() ? new Msg<>( msg.getCode(), msg.getMsg(), msg.getDetail(), null) : new Msg<>( msg.getT().parallelStream().map( Path::toString).collect( Collectors.toSet()));
    }

    /**
     * 文件删除
     *
     * @param remoteFileOrDirectoryPath 远程路径
     * @param args 可选参数
     * @return 是否删除成功
     * */
     Msg<Boolean> delete( Path remoteFileOrDirectoryPath, String ... args);
    /**
     * 文件删除
     *
     * @param remoteFileOrDirectoryPath 远程路径
     * @param args 可选操作
     * @return 是否删除成功
     * */
    default Msg<Boolean> delete(String remoteFileOrDirectoryPath, String ... args) {
        return delete( Paths.get( URI.create( remoteFileOrDirectoryPath)), args);
    }
}