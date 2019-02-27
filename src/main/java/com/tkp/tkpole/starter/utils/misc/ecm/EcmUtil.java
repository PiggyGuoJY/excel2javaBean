package com.tkp.tkpole.starter.utils.misc.ecm;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.ibm.mq.*;
import com.tkp.tkpole.starter.utils.HostInfoUtil;
import com.tkp.tkpole.starter.utils.ResourceUtil;
import com.tkp.tkpole.starter.utils.misc.ecm.model.MimeType;
import com.tkp.tkpole.starter.utils.misc.ecm.model.Respone4Ecm;
import com.tkp.tkpole.starter.utils.model.Msg;
import com.tkp.tkpole.starter.utils.soap.RestFactory;
import com.tkp.tkpole.starter.utils.soap.RestRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.*;

import static com.tkp.tkpole.starter.utils.Assert.notNul;
import static com.tkp.tkpole.starter.utils.Assert.notNull;
import static com.tkp.tkpole.starter.utils.exception.TkpoleException.of;
import static com.tkp.tkpole.starter.utils.exception.TkpoleExceptionPredictable.ERR_PARAMS;
import static com.tkp.tkpole.starter.utils.exception.TkpoleExceptionPredictable.ERR_UNKOWN;
import static com.tkp.tkpole.starter.utils.model.Msg.msg;
import static java.lang.String.format;

/**
 * ECM文件上传下载原生实现
 *
 * <p> 创建时间：2018/5/29
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j
public class EcmUtil {

    /**
     * <p> 向ECM系统上传文件
     *
     * @param cardType 类型编码(8位数字)
     * @param cardName 类型名称(40位长)
     * @param file 要上传的文件的文件对象
     * @param args 选项( compangNumber)
     * @return 标准消息
     * */
    Msg<Respone4Ecm.Upload> upload(String cardType, String cardName, File file, String ... args) {
        if ( !( notNull( file) && notNul( file))) {
            return msg( of( ERR_PARAMS));
        }
        try {
            return upload( cardType, cardName, file.getName(), FileUtils.readFileToByteArray( file), args);
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            return msg( e);
        }
    }
    /**
     * <p> 向ECM系统上传文件
     *
     * @param cardType 类型编码(8位数字)
     * @param cardName 类型名称(40位长)
     * @param path 要上传的文件的路径对象
     * @param args 选项( compangNumber)
     * @return 标准消息
     * */
    Msg<Respone4Ecm.Upload> upload(String cardType, String cardName, Path path, String ... args) {
        if ( !( notNull( path) && Files.exists( path) && Files.isRegularFile( path) && notNull( path.getFileName()))) {
            return msg( of( ERR_PARAMS));
        }
        try {
            return upload( cardType, cardName, path.getFileName().toString(), Files.readAllBytes( path), args);
        } catch ( IOException e) {
            log.error( e.getMessage(), e);
            return msg( e);
        }
    }


    private final String ERR = "错误的args参数: [%s]";

    private static final String X_HCP_CONTENT_LENGTH = "X-HCP-ContentLength";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    /**
     * <p> 从ECM下载文件
     *
     * @param cardType 类型编码(8位数字)
     * @param kv 文件kv值(40位数字)
     * @param args 选项( compangNumber, path, fileName)
     * @return 标准消息
     * */
    Msg<Respone4Ecm.Download> download(String cardType, String kv, String ... args) {
        CommandLine commandLine = EcmUtil.CliHelper.parse( EcmUtil.CliHelper.getOPTIONS_GENERAL(), args);
        if ( !notNull( commandLine)) {
            return msg( new IllegalArgumentException(format( ERR, Arrays.toString( args))));
        }
        val queryValue = ImmutableMap.<String,String>builder()
                .put( KEYVALUE, kv).put( CARDTYPE, cardType)
                .put( COMPNO, commandLine.hasOption( CliHelper.COMMAND_COMPANY_NO) ? commandLine.getOptionValue( CliHelper.COMMAND_COMPANY_NO) : CliHelper.COMMAND_COMPANY_NO_DEFAULT_VALUE)
                .build();
        return restFactory.getRestAccessibleByName( "ECM", "download").execute(
                Collections.singletonList( new RestRequest(null, null, queryValue, null)),
                response -> {
                    String fileName = commandLine.hasOption( CliHelper.COMMAND_FILENAME) ? commandLine.getOptionValue( CliHelper.COMMAND_FILENAME) : null;
                    if ( notNull( response.getFirstHeader( X_HCP_CONTENT_LENGTH))) {
                        byte[] buffer = IOUtils.readFully( response.getEntity().getContent(), Integer.valueOf( response.getFirstHeader( X_HCP_CONTENT_LENGTH).getValue()));
                        fileName = notNul( fileName) ? fileName : ( kv + "." + ( notNull( response.getFirstHeader( CONTENT_DISPOSITION)) ? ResourceUtil.getExtensionName( response.getFirstHeader( CONTENT_DISPOSITION).toString().replaceAll( CONTENT_DISPOSITION_PATTERN,"$1")) : MimeType.mimeTypeName2extensionName( response.getFirstHeader( HttpHeaders.CONTENT_TYPE).getValue())));
                        Path filePath;
                        // 尝试向本地写入文件
                        if ( commandLine.hasOption( CliHelper.COMMAND_PATH)) {
                            Path localPath = Paths.get( URI.create( commandLine.getOptionValue( CliHelper.COMMAND_PATH)));
                            if ( !localPath.toFile().exists() && !localPath.toFile().mkdirs()) {
                                return Msg.<Respone4Ecm.Download>msg( new IOException( "无法创建目录" + localPath.toString()));
                            }
                            filePath = localPath.resolve( fileName);
                            Files.write( filePath, buffer);
                            return msg( new Respone4Ecm.Download( filePath, fileName));
                            // 往内存里存一份
                        } else {
                            filePath = FileSystemOnMemory.getRootPath().resolve( fileName);
                            return notNull(FileSystemOnMemory.write( filePath, buffer)) ?
                                    msg( new Respone4Ecm.Download( filePath, fileName)) :
                                    Msg.<Respone4Ecm.Download>msg( new IllegalStateException("向内存中缓存文件失败"));
                        }
                    } else {
                        return Msg.<Respone4Ecm.Download>msg( new IllegalArgumentException( format( "没有获得正确的响应: %s%n遗失参数:%s", Joiner.on( "\n").join( response.getAllHeaders()), X_HCP_CONTENT_LENGTH)));
                    }
                },
                new Msg<Respone4Ecm.Download>( of( ERR_UNKOWN))
        ).get(0);
    }
    private static final String TRANSFER_ENCODING_CHUNKED = "chunked";
    /**
     * <p> 文件存在性测试
     *
     * @param cardType 类型编码(8位数字)
     * @param kv 文件kv值(40位数字)
     * @return Msg中的实体在文件存在时返回TRUE否则返回FALSE, 在发生异常时实体返回异常
     * */
    public Msg<Boolean> testExistence(String cardType, String kv, final String ... args) {
        CommandLine commandLine = EcmUtil.CliHelper.parse( EcmUtil.CliHelper.getOPTIONS_GENERAL(), args);
        if ( !notNull(commandLine)) {
            return new Msg<>( of( ERR_PARAMS, String.format( ERR, Arrays.toString( args))));
        }
        Map<String,String> queryValue = new HashMap<>( 3);
        queryValue.put( CARDTYPE, cardType);
        queryValue.put( KEYVALUE, kv);
        queryValue.put( COMPNO, commandLine.hasOption( CliHelper.COMMAND_COMPANY_NO) ? commandLine.getOptionValue( CliHelper.COMMAND_COMPANY_NO) : CliHelper.COMMAND_COMPANY_NO_DEFAULT_VALUE);
        return restFactory.getRestAccessibleByName( "ECM", "testExistence").execute(
                Collections.singletonList( new RestRequest( null, null, queryValue, null)),
                // 判决条件值得商榷(目前这里是通过在Postman上的测试总结出的经验结果)
                response -> new Msg<>( HttpStatus.SC_FORBIDDEN==response.getStatusLine().getStatusCode() && TRANSFER_ENCODING_CHUNKED.equals( response.getFirstHeader( HttpHeaders.TRANSFER_ENCODING).getValue().trim())),
                new Msg<Boolean>( of( ERR_UNKOWN))
        ).get(0);
    }

    public EcmUtil(
            EcmConfigData ecmConfigData,
            RestFactory restFactory
    ) {
        this.ecmConfigData = ecmConfigData;
        this.restFactory = restFactory;
    }

    //==== 华丽的分割线 ==== 私有资源

    private EcmConfigData ecmConfigData;
    private RestFactory restFactory;

    private static final String CARDTYPE = "cardtype";
    private static final String KEYVALUE = "keyvalue";
    private static final String COMPNO = "compno";
    private static final String CONTENT_DISPOSITION_PATTERN = "^.*filename=\"([0-9a-zA-Z]+\\.[a-zA-Z]+)\".*?$";
    private static final FastDateFormat FAST_DATE_FORMAT_FULL = FastDateFormat.getInstance("yyyyMMddmmssSSS");
    private static final FastDateFormat FAST_DATE_FORMAT_DATE = FastDateFormat.getInstance("yyyyMMdd");
    private static final FastDateFormat FAST_DATE_FORMAT_TIME = FastDateFormat.getInstance("HHmmss");

    private static final long UPLOAD_MAX_FILE_SIZE = 1024*1024*4L;
    private Msg<Respone4Ecm.Upload> upload(String cardType, String cardName, String fileName, byte[] fileContent, String ... args) {
        CommandLine commandLine;
        if ( !notNull( commandLine = EcmUtil.CliHelper.parse( EcmUtil.CliHelper.getOPTIONS_GENERAL(), args))) {
            return msg( of( ERR_PARAMS, format( "错误的args参数: [%s]", Arrays.toString( args))));
        }
        if ( fileContent.length>UPLOAD_MAX_FILE_SIZE) {
            return msg( of( ERR_PARAMS, "文件大小不能超出4M"));
        }
        initEnv();
        MQQueueManager mqQueueManager = null;
        MQQueue mqQueue = null;
        try(
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream( byteArrayOutputStream)
        ) {
            mqQueueManager = new MQQueueManager( ecmConfigData.getQmName());
            String kv = generateKv(cardType);
            String compNo = commandLine.hasOption(CliHelper.COMMAND_COMPANY_NO) ? commandLine.getOptionValue(CliHelper.COMMAND_COMPANY_NO) : CliHelper.COMMAND_COMPANY_NO_DEFAULT_VALUE;
            String[] configData = generateConfigData(cardType, cardName, kv, MimeType.valueOfFileName(fileName), compNo);
            objectOutputStream.writeObject(configData);
            objectOutputStream.writeObject(fileContent);
            mqQueue = mqQueueManager.accessQueue(ecmConfigData.getQueueName(), MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING, null, null, null);
            MQMessage mqMessage = new MQMessage();
            mqMessage.format = MQC.MQFMT_STRING;
            mqMessage.messageId = generateMessageId();
            mqMessage.write(byteArrayOutputStream.toByteArray());
            mqQueue.put(mqMessage, new MQPutMessageOptions());
            return msg(new Respone4Ecm.Upload(cardType, kv, format("%s&cardtype=%s&keyvalue=%s&compno=%s", ecmConfigData.getHostDownload(), cardType, kv, compNo)));
        } catch( MQException | IOException e){
            log.error( e.getMessage(), e);
            return msg( e);
        } finally {
            if ( mqQueue!=null) { try { mqQueue.close(); } catch ( MQException e) { log.error(e.getMessage(), e); } }
            if ( mqQueueManager!=null) {
                if ( mqQueueManager.isOpen()) { try { mqQueueManager.close(); } catch ( MQException e) { log.error( e.getMessage(), e); } }
                if ( mqQueueManager.isConnected()) { try { mqQueueManager.disconnect(); } catch ( MQException e) { log.error( e.getMessage(), e); } }
            }
        }
    }

    /**
     * <p> 初始化环境
     *
     * */
    @SuppressWarnings( "unchecked")
    private void initEnv() {
        MQEnvironment.hostname = this.ecmConfigData.getHost();
        MQEnvironment.port = this.ecmConfigData.getPort();
        MQEnvironment.channel = this.ecmConfigData.getChannel();
        MQEnvironment.CCSID = this.ecmConfigData.getCcsid();
        MQEnvironment.properties.put( MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES);
    }
    /**
     * <p> 获取随机消息号
     *
     * @return 随机消息号
     * */
    private byte[] generateMessageId() {
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
            secureRandom.setSeed( UUID.randomUUID().toString().getBytes( Charsets.UTF_8));
            return String.format( "%s%s%s", FAST_DATE_FORMAT_FULL.format( new Date()), UUID.randomUUID().toString(), secureRandom.nextLong()).getBytes( Charsets.UTF_8);
        } catch ( NoSuchAlgorithmException | NoSuchProviderException e) {
            log.error( e.getMessage(), e);
            return String.format( "%s%s%s", FAST_DATE_FORMAT_FULL.format( new Date()), UUID.randomUUID().toString(), System.currentTimeMillis()).getBytes( Charsets.UTF_8);
        }
    }
    /**
     * <p> 生成关键码(不能保证强不一致, 大并发下可能一致)
     *
     * @param cardType 描述此参数的作用
     * @return 关键码
     * */
    private String generateKv( String cardType) {
        return String.format(
                "%s%s%015d",
                cardType,
                FAST_DATE_FORMAT_FULL.format( new Date()),
                System.currentTimeMillis());
    }
    /**
     * <p> 构造请求头
     *
     * @param cardType 类型编码(最长8位)
     * @param cardName 类型名称(最长40位)
     * @param key 文件kv值(最长40位)
     * @param mimeType 文件的MimeType
     * @return 请求头
     * */
    private String[] generateConfigData(
            String cardType,
            String cardName,
            String key,
            MimeType mimeType,
            String ... options
    ) {
        Date now = new Date();
        return new String[] {
                //公司代码(同个险代码, 长度1位)
                notNul( options) && notNul( options[0]) ? options[0] : "1",
                //类型代码(长度8位)
                cardType,
                //类型名称(最长40位)
                cardName,
                //关键码(最长40位)
                key,
                //机构名称(最长12位)
                HostInfoUtil.Project.SYSTEM_NAME,
                //操作员名称(最长12位)
                HostInfoUtil.Project.DEFAULT_OPERATOR,
                //mimeType类型
                mimeType.getCode(),
                //意义不明(先传8位随机数),
                String.format( "%08d", EcmUtil.nextLong()%10000000L),
                //图像页数(最长2位)
                "1",
                //重传标志(长度1位)
                "0",
                //上传日期
                FAST_DATE_FORMAT_DATE.format( now),
                //上传时间
                FAST_DATE_FORMAT_TIME.format( now),
                //档案类型(个银险 01)
                "01",
                //20位唯一字符串(这个字段让ECM自己重新生成)
                "1234567890",
        };
    }
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static long nextLong() {
        return EcmUtil.SECURE_RANDOM.nextLong();
    }

    @Slf4j @NoArgsConstructor( access = AccessLevel.PRIVATE)
    private static class CliHelper {
        public static CommandLine parse(Options options, String ... args) {
            try {
                return new DefaultParser().parse( options, args);
            } catch ( ParseException e) {
                log.error( "[入参异常]: options 解析失败");
                log.error( e.getMessage(), e);
                return null;
            }
        }

        private static final String COMMAND_COMPANY_NO = "companyNumber";
        private static final String COMMAND_COMPANY_NO_DEFAULT_VALUE = "1";
        private static final String COMMAND_PATH = "path";
        private static final String COMMAND_FILENAME = "fileName";
        @Getter
        private static final Options OPTIONS_GENERAL = new Options();
        static {
            OPTIONS_GENERAL.addOption( Option.builder().longOpt( COMMAND_COMPANY_NO).hasArg().numberOfArgs( 1).desc("用来指定公司编号(不指定时默认为1)").build());
            OPTIONS_GENERAL.addOption( Option.builder().longOpt( COMMAND_PATH).hasArg().numberOfArgs( 1).optionalArg( true).desc("指定下载目录").build());
            OPTIONS_GENERAL.addOption( Option.builder().longOpt( COMMAND_FILENAME).hasArg().numberOfArgs( 1).optionalArg( true).desc("指定文件名称").build());
        }
    }

    /**
     * 内存文件系统
     * */
    @Slf4j
    private static class FileSystemOnMemory {
        private static final long DISK_SIZE =  1024L*1024*20;
        private static final Configuration CONFIGURATION = Configuration.unix().toBuilder().setRoots("/").setMaxSize(DISK_SIZE).build();
        private static FileSystem fileSystem;
        @Getter
        private static Path rootPath;
        static { init(); }
        static @Synchronized Path write(Path path, byte[] bytes, OpenOption... options) {
            for ( int time = 1, chance=3; time<=chance; time++) {
                try {
                    return Files.write( path, bytes, options);
                } catch ( IOException e) {
                    log.error( e.getMessage(), e);
                    // 尝试重新初始化环境
                    init();
                }
            }
            return null;
        }
        private static void init() {
            if ( notNull(fileSystem)) {
                try { fileSystem.close(); fileSystem = null; } catch ( IOException e) { log.error( e.getMessage(), e); fileSystem = null;}
            }
            fileSystem = Jimfs.newFileSystem("liunx", CONFIGURATION);
            rootPath = fileSystem.getPath("/");
        }
    }
}