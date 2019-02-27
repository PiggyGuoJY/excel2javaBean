package com.tkp.tkpole.starter.utils.misc.ecm;

import com.tkp.tkpole.starter.utils.misc.ecm.model.Respone4Ecm;
import com.tkp.tkpole.starter.utils.model.Msg;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 泰康影像件系统(文件存储)便捷工具
 *
 * <p> 创建时间：2018/8/8
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j
public class Ecm {

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param cardType 描述此参数的作用
     * @param cardName 描述此参数的作用
     * @param file 描述此参数的作用
     * @return 描述返回值
     * */
    public Msg<Respone4Ecm.Upload> upload(String cardType, String cardName, File file, String ... args) {
        return ecmUtil.upload( cardType, cardName, file, args);
    }

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param cardType 描述此参数的作用
     * @param kv 描述此参数的作用
     * @return 描述返回值
     * */
    public Msg<Respone4Ecm.Download> download(String cardType, String kv, String ... args) {
        return ecmUtil.download( cardType, kv, args);
    }

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param cardType 描述此参数的作用
     * @param kv 描述此参数的作用
     * @return 描述返回值
     * */
    public Msg<Boolean> testExistence(String cardType, String kv) {
        return ecmUtil.testExistence( cardType, kv);
    }

    public Ecm(EcmUtil ecmUtil) {
        this.ecmUtil = ecmUtil;
    }

    //==== 华丽的分割线 === 私有资源

    private EcmUtil ecmUtil;
}
