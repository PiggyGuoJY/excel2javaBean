package com.guojy.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * <p> 异常原因枚举类(后期可能会去掉)
 * <p> 创建时间：2018/1/9
 *
 * @author guojy24
 * @version 1.0
 * @deprecated 不建议使用, 在未来版本中可能会删除;
 * */
@Deprecated
@AllArgsConstructor @ToString
public enum  TkpoleExceptionPredictable {

    /**
     * 未知错误
     * */
    ERR_UNKOWN(             "99",     "未知错误",             "系统繁忙, 请稍后再试"),
    /**
     * 初始化错误(一般用于未能根据配置数据构造出特定功能时)
     * */
    ERR_INIT(               "98",     "初始化错误",           "系统异常, 请联系运维人员"),
    /**
     * 入参错误(一般用于方法的入参检查)
     * */
    ERR_PARAMS(             "97",     "入参错误",             "请检查输入项是否正确, 完整"),
    /**
     * 调用超时
     * */
    ERR_TIMEOUT(            "96",     "调用超时",             "系统繁忙, 请稍后再试"),
    /**
     * 调用成功但返回失败(请谨慎使用, 一般用于消极返回)
     * */
    ERR_RETURN_FAILURE(     "95",     "调用成功但返回失败",    "系统繁忙, 请稍后再试"),
    /**
     * 逻辑错误(一般性的程序中断)
     * */
    ERR_LOGIC(              "94",     "逻辑错误",             "业务逻辑异常, 请联系业务人员"),
    /**
     * 数据约束不完整
     * */
    ERR_DATA_CONSTRAINTS(   "92",     "数据约束不完整",       "请检查数据的完整性"),
    /**
     * 数据配置异常(一般用于配置文件数据错误配置)
     * */
    ERR_CONFIG(             "91",     "数据配置异常",         "请检查配置文件或数据库配置项");

    public String getDesc( String additional) {
        return this.desc + " [ " + additional + " ] ";
    }

    //==== 华丽的分割线 === 私有资源

    /**
     * 错误代码
     * */
    @Getter
    private String errCode;
    /**
     * 对本错误的描述
     * */
    @Getter
    private String desc;
    /**
     * 发往前台的报错信息(需要考虑话述的合理性)
     * */
    @Getter
    private String tips;
}
