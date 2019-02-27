package com.tkp.tkpole.starter.utils.misc.ecm.model;

import com.tkp.tkpole.starter.utils.ResourceUtil;
import com.tkp.tkpole.starter.utils.exception.TkpoleException;
import com.tkp.tkpole.starter.utils.exception.TkpoleExceptionPredictable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.tkp.tkpole.starter.utils.Assert.notNul;
import static com.tkp.tkpole.starter.utils.Assert.notNull;

/**
 * 针对ECM系统的MimeType枚举类
 *
 * 这里的数据参考了http://wiki.taikang.com/display/DSD2/ECM++mimecode, 没有全部收纳, 以后按需增加吧
 *
 * <p> 创建时间：2018/5/29
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @AllArgsConstructor( access = AccessLevel.PRIVATE) @ToString
public enum MimeType {
    //todo ... 这个以后可以根据外部接口规范进行修改(2018-05-29)
    //todo ... 这里的数据可以考虑放到数据库里(2018-10-24)
    GIF(    "02", "image/gif",      "gif"),
    IEF(    "03", "image/ief",      "ief"),
    JPG(    "04", "image/jpeg",     "jpe"),
    JPEG(   "05", "image/jpeg",     "jpeg"),
    PNG(    "07", "image/png",      "png"),
    TIFF(   "08", "image/tiff",     "tiff"),
    HTML(   "09", "text/html",      "html"),
    TXT(    "10", "text/plain",     "txt"),
    XML(    "11", "text/xml",       "xml"),
    BIN(    "19", "application/octet-stream",       "bin"),
    PDF(    "20", "application/pdf",                "pdf"),
    RAR(    "25", "application/x-rar-compressed",   "rar"),
    TGZ(    "26", "application/x-tar-gz",           "tgz"),
    ZIP(    "27", "application/x-zip-compressed",   "zip"),
    BMP(    "32", "image/x-ms-bmp",                 "bmp"),
    CSV(    "33", "text/comma-separated-values",    "csv"),
    WPS(    "34", "application/kswps",              "wps"),
    ET(     "35", "application/kset",               "et"),
    DPS(    "36", "application/ksdps",              "dps"),
    XLS(    "15", "application/excel",              "xls"),
    PPT(    "17", "application/vnd.ms-powerpoint",  "ppt"),
    DOC(    "18", "application/vnd.msword",         "doc"),
    XPS(    "37", "application/vnd.ms-xpsdocument", "xps");

    /**
     * <p> 根据文件对象确定MimeType
     *
     * @param file 源文件
     * @return mimetype
     * */
    public static MimeType valueOf( File file) {
        if ( notNul( file)) { throw TkpoleException.of( TkpoleExceptionPredictable.ERR_PARAMS); }
        return getMimeType( ResourceUtil.getExtensionName( file.getName()));
    }
    /**
     * <p> 根据文件路径对象确定MimeType
     *
     * @param path 源文件路径
     * @return mimetype
     * */
    public static MimeType valueOf( Path path) {
        if ( !( notNull( path) && !Files.exists( path) && Files.isRegularFile( path) && notNull( path.getFileName()))) { throw TkpoleException.of( TkpoleExceptionPredictable.ERR_PARAMS); }
        return getMimeType( FilenameUtils.getExtension( path.getFileName().toString()));
    }
    /**
     * <p> 根据文件路径对象确定MimeType
     *
     * @param fileName 源文件名
     * @return mimetype
     * */
    public static MimeType valueOfFileName( String fileName) {
        if ( !notNul( fileName)) { throw new IllegalArgumentException( String.format( "fileName[%s]不能为空", fileName)); }
        return getMimeType( ResourceUtil.getExtensionName( fileName));
    }
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param mimeTypeName 描述此参数的作用
     * @return 描述返回值
     * */
    public static String mimeTypeName2extensionName( String mimeTypeName) {
        for ( MimeType mimeType : MimeType.values()) {
            if ( mimeTypeName.equals( mimeType.getType())) {
                return mimeType.getExtension();
            }
        }
        throw TkpoleException.of(
                TkpoleExceptionPredictable.ERR_PARAMS,
                "不受ECM支持的文件格式",
                "请检查com.tkp.guojy24.tkpole.external.raw.ecm.ECMUtil.MimeType中对支持的文件拓展名的规定或联系ECM伙伴确定受支持的拓展名范围");
    }


    //==== 华丽的分割线 === 私有资源
    /**
     * 对应到ECM的编码
     * */
    @Getter
    private String code;
    /**
     * 类型/子类型
     * */
    @Getter
    private String type;
    /**
     * 拓展名
     * */
    @Getter
    private String extension;

    /**
     * <p> 主要是用来替代原生的valueOf(String):MimeType
     *
     * @param mimeType 描述此参数的作用
     * @return 描述返回值
     * */
    private static MimeType getMimeType( String mimeType) {
        if ( !notNul( mimeType)) { throw new IllegalArgumentException( String.format("mimeType[%s]不能为空", mimeType)); }
        try {
            return valueOf( mimeType.toUpperCase());
        } catch ( IllegalArgumentException e) {
            log.error( e.getMessage(), e);
            throw new IllegalArgumentException( String.format("不受ECM支持的文件格式:[%s]%n请在http://wiki.taikang.com/display/DSD2/ECM++mimecode中查看对支持的文件拓展名的规定或联系ECM伙伴确定受支持的拓展名范围", mimeType.toUpperCase()));
        }
    }
}