package com.tkp.tkpole.starter.utils.ftp;

import com.google.common.base.Charsets;
import com.jcraft.jsch.ChannelSftp;
import com.tkp.tkpole.starter.utils.Assert;
import com.tkp.tkpole.starter.utils.model.Msg;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tkp.tkpole.starter.utils.Assert.notNul;
import static com.tkp.tkpole.starter.utils.Assert.notNull;
import static com.tkp.tkpole.starter.utils.model.Msg.msg;
import static java.lang.String.format;

/**
 * ftp服务基类
 *
 * <p> 创建时间：2018/9/21
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor( access = AccessLevel.PROTECTED)
public abstract class AbstractFtpService implements FtpAccessible{

    public static String changeFileSeparator( Path path, boolean ... args) {
        String originalPath = path.toString();
        log.debug( "变更前:[{}]", originalPath);
        String changedPath = originalPath.contains(":") ?
                ( notNul(args)&&args.length==1&&args[0] ?
                        originalPath.replaceAll("\\\\","/").replaceAll("^(.+)$", "/$1") :
                        originalPath.replaceAll("/","\\\\")) :
                originalPath.replaceAll("\\\\","/");
        log.debug( "变更后:[{}]", changedPath);
        return changedPath;
    }

    private static final String TIP1 = "未能正确解析可选参数";
    private static final String TIP2 = "本地路径[%s]不存在且 没有createFlagFile标志[%s]或createFlagFile标志的参数[%s]不合法";
    private static final String TIP3 = "远程路径[%s]不存在且 没有createDirectory标志[%s]或有createDirectory标志但目录创建失败";
    private static final String TIP4 = "远程文件[%s]存在且没有覆盖选项[%s]";
    @Override
    public Msg<Boolean> upload(Path localFilePath, FileSystem fileSystem, Path remoteDirectoryPath, String... args) {
        CommandLine commandLine;
        if ( !notNull(commandLine = CliHelper.parse( CliHelper.getOPTIONS_UPLOAD(), args))) { return msg( false, TIP1); }
        final boolean useExternalFileSystem = Assert.notNull( fileSystem) && fileSystem.isOpen();
        //1. 判断本地文件是否存在, 如果存在, 或虽然不存在但有标志 createFlagFile:[namePatternClass](默认使用时间戳)的话继续, 否则失败
        boolean flag;
        Path tempPath = null;
        boolean isLocalPathLegal = notNull( localFilePath) && ( useExternalFileSystem ? Files.exists( fileSystem.getPath( localFilePath.toString())) && Files.isRegularFile( fileSystem.getPath( localFilePath.toString())) : notNul( localFilePath.toFile()));
        flag = !isLocalPathLegal && (!commandLine.hasOption( COMMAND_CREATE_FLAG_FILE) || !notNull( tempPath = createFlagFile( commandLine.getOptionValue( COMMAND_CREATE_FLAG_FILE))));
        if ( flag) { return msg( false, format( TIP2, localFilePath.toString(), commandLine.hasOption( COMMAND_CREATE_FLAG_FILE), commandLine.getOptionValue( COMMAND_CREATE_FLAG_FILE))); }
        //2. 判断远程路径是否存在, 如果不存在且有有标志 createDirectory的话, 创建目录; 否则不创建目录并返回失败
        flag = !isRemoteDirectoryExist( remoteDirectoryPath) && (!commandLine.hasOption( COMMAND_CREATE_DIRECTORY) || !createRemoteDirectory( remoteDirectoryPath));
        if ( flag) { return msg( false, format( TIP3, remoteDirectoryPath.toString(), commandLine.hasOption( COMMAND_CREATE_DIRECTORY))); }
        //3. 判断远程路径下是否存在和本次上传文件同名的文件, 如果没有, 或虽然有但同时有标志overwrite的话, 上传; 否则返回失败
        Path remoteFilePath = Paths.get( URI.create( format( "file://%s/%s", changeFileSeparator( remoteDirectoryPath), ( isLocalPathLegal ? localFilePath.toFile().getName() : tempPath.toFile().getName()))));
        boolean isRemoteFileExist = isRemoteFileExist( remoteFilePath);
        if ( isRemoteFileExist && !commandLine.hasOption( COMMAND_OVERWRITE)) { return msg( false, format( TIP4, remoteFilePath.toString(), commandLine.hasOption( COMMAND_OVERWRITE))); }
        //4. 上传文件并返回结果
        return upload( ( isLocalPathLegal ? ( useExternalFileSystem ? fileSystem.getPath( localFilePath.toString()) : localFilePath) : tempPath), remoteFilePath, isRemoteFileExist ? commandLine.getOptionValue( COMMAND_OVERWRITE) : COMMAND_OVERWRITE__OVERWRITE);
    }

    private static final String TIP5 = "远程文件[%s]不存在";
    private static final String TIP6 = "未能创建本地路径[%s]";
    private static final String TIP7 = "选项[%s]中的指定的路径不都存在且选项[%s]不存在";
    private static final Set<Path> EMPTY_PATH_SET = new HashSet<>(1);
    @Override
    public Msg<Set<Path>> download( FileSystem fileSystem, Path remoteFilePath,  String... args) {

        CommandLine commandLine;
        if ( !notNull( commandLine = CliHelper.parse( CliHelper.getOPTIONS_DOWNLOAD(), args))) { return msg( EMPTY_PATH_SET, TIP1); }
        // 1.判断远程文件是否存在, 如果不存在返回失败
        if ( !isRemoteFileExist( remoteFilePath)) { return msg( EMPTY_PATH_SET, format( TIP5, remoteFilePath.toString())); }
        // 2 判断path指定的路径是否存在
        // 2.1 不存在, 如果有命令-createDirectory, 创建这些路径; 否则返回失败
        // 2.2. 存在, 继续执行
        // 外源文件系统标志( 当fileSystem不存在时, 使用本地环境否则使用外源环境)
        final boolean useExternalFileSystem = Assert.notNull( fileSystem) && fileSystem.isOpen();
        Set<Path> pathNotExistSet =
                Stream.of( commandLine.getOptionValues( COMMAND_PATH)).parallel()
                        .map( stringPath ->  useExternalFileSystem ? fileSystem.getPath( stringPath) : Paths.get( URI.create( stringPath)))
                        .filter( path -> !Files.exists( path))
                        .collect( Collectors.toSet());
        if ( commandLine.hasOption( COMMAND_CREATE_DIRECTORY)) {
            for ( Path path : pathNotExistSet) {
                try { Files.createDirectory( path); } catch ( IOException e ) {
                    pathNotExistSet.forEach( path2 -> {
                        try { Files.deleteIfExists( path2); } catch ( IOException e2) {
                            log.error( e2.getMessage(), e2);
                            log.error( "未能删除本地路径[{}]", path2.toString());
                        }
                    });
                    log.error( e.getMessage(), e);
                    return msg( EMPTY_PATH_SET, format( TIP6 , path.toString()));
                }
            }
        } else { if ( !pathNotExistSet.isEmpty()) { return msg( EMPTY_PATH_SET, format( TIP7, COMMAND_PATH, COMMAND_CREATE_DIRECTORY)); } }
        // 3. 下载文件
        // 4. 存放文件
        Set<Path> result = new HashSet<>();
        for ( Path path : Stream.of( commandLine.getOptionValues( COMMAND_PATH)).parallel().map( stringPath ->  useExternalFileSystem ? fileSystem.getPath( stringPath) : Paths.get( URI.create( stringPath))).collect( Collectors.toSet())) {
            Path localFilePath =
                    useExternalFileSystem ?
                    fileSystem.getPath( format( "%s/%s", changeFileSeparator( path), remoteFilePath.toFile().getName())) :
                    Paths.get( URI.create(  format( "file://%s/%s", changeFileSeparator( path, true), remoteFilePath.toFile().getName())));
            if ( !Files.exists( localFilePath)) {
                // 4.1 没有同名文件, 继续(复制失败的情况直接跳过)
                if ( !download( localFilePath, false, fileSystem, remoteFilePath, path, result)) { break; }
            } else {
                // 4.2 如果目录下已有同名文件且没有overwrite命令, 跳过; 否则按指令覆盖
                if ( !commandLine.hasOption( COMMAND_OVERWRITE)) { log.warn( "在本地路径[{}]下存在同名文件且不存在[{}]选项, 跳过下载", path.toString(), COMMAND_OVERWRITE); }
                else {
                    if ( !CliHelper.getMAP_OVERWRITE().keySet().contains( commandLine.getOptionValue( COMMAND_OVERWRITE))) { log.warn( "不能识别的文件覆盖方式 [{}], 跳过下载", commandLine.getOptionValue( COMMAND_OVERWRITE)); continue; }
                    if ( !download( localFilePath, COMMAND_OVERWRITE__APPEND.equals( commandLine.getOptionValue( COMMAND_OVERWRITE)), fileSystem, remoteFilePath, path, result)) { break; }
                }
            }
        }
        // 5. 返回本地存放路径
        return msg( result);
    }

    private static final String TIP8 = "针对参数[%s]不能识别的参数[%s]";
    @Override
    public Msg<Boolean> delete(Path remoteFileOrDirectoryPath, String... args) {
        CommandLine commandLine;
        if ( !notNull(commandLine = CliHelper.parse( CliHelper.getOPTIONS_DELETE(), args))) { return msg( false, TIP1); }
        boolean isFile;
        boolean isRemoteFileExist = false;
        boolean isRemoteDirectoryExist = false;
        // 1. 判断文件或路径是否存在, 默认以路径中最后一项中是否存在"."来判断; 可以使用isA来指明
        if ( commandLine.hasOption( COMMAND_IS_A)) {
            switch ( commandLine.getOptionValue( COMMAND_IS_A)) {
                case COMMAND_IS_A__FILE: isFile = true; isRemoteFileExist = isRemoteFileExist( remoteFileOrDirectoryPath); break;
                case COMMAND_IS_A__DIRECTORY: isFile = false; isRemoteDirectoryExist = isRemoteDirectoryExist( remoteFileOrDirectoryPath); break;
                default: return msg( false, format( TIP8, COMMAND_IS_A, commandLine.getOptionValue( COMMAND_IS_A)));
            }
        } else {
            final String dot = ".";
            if( remoteFileOrDirectoryPath.toFile().getName().contains( dot)) {
                isFile = true; isRemoteFileExist = isRemoteFileExist( remoteFileOrDirectoryPath);
            } else {
                isFile = false; isRemoteDirectoryExist = isRemoteDirectoryExist( remoteFileOrDirectoryPath);
            }
        }
        // 1.1 不存在, 如果有指令"-noneIsOk", 返回成功; 否则返回失败
        if ( !isRemoteFileExist && !isRemoteDirectoryExist) {
            log.warn( "远程文件或路径不存在");
            return commandLine.hasOption( COMMAND_NONE_IS_OK) ? msg( true) : msg( false, "远程文件或路径不存在且没有指定noneIsOk选项");
        }
        // 1.2 存在
        return isFile ? msg( deleteRemoteFile( remoteFileOrDirectoryPath)) : msg( deleteRemoteDirectory( remoteFileOrDirectoryPath));
    }

    @Override
    public void close() throws IOException {
        closeConnection();
    }

    @Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE)
    static class CliHelper {

        private static CommandLine parse( Options options, String ... args) {
            try {
                return new DefaultParser().parse( options, args);
            } catch ( ParseException e) {
                log.error( "[入参异常]: 解析参数 {} 失败", Arrays.toString(args));
                log.error( e.getMessage(), e);
                return null;
            }
        }

        @Getter
        private static final Options OPTIONS_UPLOAD = new Options();
        @Getter
        private static final Map<String, Integer> MAP_OVERWRITE = new HashMap<>(2);
        @Getter
        private static final Options OPTIONS_DOWNLOAD = new Options();
        @Getter
        private static final Options OPTIONS_DELETE = new Options();
        static {

            final String TIP = "如果不存在目录则创建";

            OPTIONS_UPLOAD.addOption( Option.builder().longOpt( COMMAND_CREATE_FLAG_FILE).hasArg().numberOfArgs( 1).optionalArg( true).desc( TIP).build());
            OPTIONS_UPLOAD.addOption( Option.builder().longOpt( COMMAND_CREATE_DIRECTORY).desc( TIP).build());
            OPTIONS_UPLOAD.addOption( Option.builder().longOpt( COMMAND_OVERWRITE).hasArg().numberOfArgs( 1).desc( TIP).build());
            MAP_OVERWRITE.put( COMMAND_OVERWRITE__OVERWRITE, ChannelSftp.OVERWRITE);
            MAP_OVERWRITE.put( COMMAND_OVERWRITE__APPEND, ChannelSftp.APPEND);

            OPTIONS_DOWNLOAD.addOption( Option.builder().longOpt( COMMAND_PATH).hasArgs().optionalArg( false).required().desc("下载路径").build());
            OPTIONS_DOWNLOAD.addOption( Option.builder().longOpt( COMMAND_CREATE_DIRECTORY).desc( TIP).build());
            OPTIONS_DOWNLOAD.addOption( Option.builder().longOpt( COMMAND_OVERWRITE).hasArg().numberOfArgs( 1).desc( TIP).build());

            OPTIONS_DELETE.addOption( Option.builder().longOpt( COMMAND_IS_A).hasArg().numberOfArgs(1).desc("标识路径代表一个文件或一个目录").required(false).build());
            OPTIONS_DELETE.addOption( Option.builder().longOpt( COMMAND_NONE_IS_OK).desc("标识文件或目录不存在即可").build());
        }
    }

    /**
     * 远程文件是否存在
     *
     * @param remoteFilePath 远程文件位置
     * @return boolean
     * */
    protected abstract boolean isRemoteFileExist( Path remoteFilePath);
    /**
     * 远程目录是否存在
     *
     * @param remoteDirectoryPath 远程目录位置
     * @return boolean
     * */
    protected abstract boolean isRemoteDirectoryExist( Path remoteDirectoryPath);
    /**
     * 创建远程目录
     *
     * @param remoteDirectoryPath 远程目录位置
     * @return boolean
     * */
    protected abstract boolean createRemoteDirectory( Path remoteDirectoryPath);
    /**
     * 上传文件
     *
     * @param reader 本地文件输入
     * @param remoteDirectoryPath 远程目录位置
     * @param overwriteType 覆盖类型
     * @return boolean
     * */
    protected abstract boolean uploadFile( Reader reader, Path remoteDirectoryPath, String overwriteType);
    /**
     * 下载文件
     *
     * @param remoteFilePath 远程文件位置
     * @return 文件输入流
     * */
    protected abstract InputStream downloadFile( Path remoteFilePath);
    /**
     * 下载完毕后执行的事
     *
     * */
    protected abstract void afterDownloadFile();
    /**
     * 删除文件
     *
     * @param remoteFilePath 远程文件位置
     * @return boolean
     * */
    protected abstract boolean deleteRemoteFile( Path remoteFilePath);
    /**
     * 删除远程目录
     *
     * @param remoteDirectoryPath 远程目录位置
     * @return boolean
     * */
    protected abstract boolean deleteRemoteDirectory( Path remoteDirectoryPath);
    /**
     * 关闭连接
     *
     * */
    protected abstract void closeConnection();


    private static final FastDateFormat FAST_DATE_FORMAT_DEFAULT_TEMP_FILE_NAME_PATTERN = FastDateFormat.getInstance("yyyyMMdd_hhmmssSSS");
    private Msg<Boolean> upload(  Path localFilePath, Path remoteDirectoryPath, String overwriteType) {
        try (
                BufferedReader bufferedReader = Files.newBufferedReader( localFilePath, Charsets.UTF_8)
        ) {
            return msg( uploadFile( bufferedReader, remoteDirectoryPath, overwriteType));
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            return msg( e);
        }
    }
    private boolean download( Path localFilePath, boolean append , FileSystem fileSystem, Path remoteFilePath, Path path, Set<Path> result) {
        InputStream inputStream;
        final boolean useExternalFileSystem = Assert.notNull( fileSystem) && fileSystem.isOpen();
        try (
                BufferedWriter bufferedWriter = Files.newBufferedWriter(
                        ( useExternalFileSystem ? fileSystem.getPath( localFilePath.toString()) : localFilePath),
                        Charsets.UTF_8,
                        append ? StandardOpenOption.APPEND : ( !Files.exists( useExternalFileSystem ? fileSystem.getPath( localFilePath.toString()) : localFilePath) ? StandardOpenOption.CREATE_NEW : StandardOpenOption.TRUNCATE_EXISTING))
        ){
            inputStream =  downloadFile( remoteFilePath);
            if ( notNull( inputStream)) {
                IOUtils.copy( inputStream, bufferedWriter, Charsets.UTF_8);
                bufferedWriter.flush();
                result.add( localFilePath);
                return true;
            } else {
                log.warn( "文件[{}]下载到本地[{}]失败(获取文件流失败), 跳过余下的下载过程", remoteFilePath.toString(), path.toString());
                return false;
            }
        } catch ( IOException e) {
            log.warn( "文件[{}]下载到本地[{}]失败, 跳过下载", remoteFilePath.toString(), path.toString());
            log.error( e.getMessage(), e);
            return false;
        } finally { afterDownloadFile(); }
    }
    private Path createFlagFile( String pattern) {
        try {
            if ( notNull( pattern)) {
                @SuppressWarnings("unchecked")
                Class<Supplier<String>> supplierClass= ( Class<Supplier<String>>)Class.forName( pattern);
                return Files.createTempFile( supplierClass.newInstance().get() + "_", ".tmp");
            } else {
                return Files.createTempFile( AbstractFtpService.FAST_DATE_FORMAT_DEFAULT_TEMP_FILE_NAME_PATTERN.format( new Date()) + "_", ".tmp");
            }
        } catch ( ClassNotFoundException | IllegalAccessException | InstantiationException | IOException e) {
            log.error( e.getMessage(), e);
            return null;
        }
    }
}
