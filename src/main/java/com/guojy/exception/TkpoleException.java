package com.guojy.exception;

import com.tkp.tkpole.starter.utils.Assert;
import lombok.Getter;

import static java.lang.String.format;

/**
 * 养老客服项目自定义异常(后期可能会去掉)
 *
 * <p> 创建时间：2018/5/4
 *
 * @author guojy24
 * @version 1.0
 * @deprecated 不建议使用, 在未来版本中可能会删除; 建议直接使用{@Code IllegalStateException}, {@Code IllegalArgumentException}和{@Code IllegalAccessException}
 * */
@Deprecated
public class TkpoleException extends RuntimeException {

    /**
     * 自定义异常构造方法
     *
     * @param tkpoleExceptionPredictable 预定义的错误
     * @return 项目自定义异常
     * */
    public static TkpoleException of( TkpoleExceptionPredictable tkpoleExceptionPredictable) {
        return new TkpoleException( tkpoleExceptionPredictable, tkpoleExceptionPredictable.getDesc(), null);
    }
    /**
     * 自定义异常构造方法
     *
     * @param tkpoleExceptionPredictable 预定义的错误
     * @param detail 错误细节(为什么会出现这个错误)
     * @return 项目自定义异常
     * */
    public static TkpoleException of( TkpoleExceptionPredictable tkpoleExceptionPredictable, String detail) {
        return new TkpoleException( tkpoleExceptionPredictable, detail, null);
    }
    /**
     * 自定义异常构造方法
     *
     * @param tkpoleExceptionPredictable 预定义错误
     * @param detail 错误细节(为什么会出现这个错误)
     * @param tips 提示(可能的修复措施)
     * @return 项目自定义异常
     * */
    public static TkpoleException of( TkpoleExceptionPredictable tkpoleExceptionPredictable, String detail, String tips) {
        return new TkpoleException( tkpoleExceptionPredictable, detail, tips);
    }

    //==== 华丽的分割线 === 私有资源

    @Getter
    private TkpoleExceptionPredictable tkpoleExceptionPredictable;
    @Getter
    private String detail;
    @Getter
    private String tips;

    private TkpoleException( TkpoleExceptionPredictable tkpoleExceptionPredictable, String detail, String tips) {
        super( format( "%s [ %s ]", tkpoleExceptionPredictable.getDesc(), detail), null, true, false);
        this.tkpoleExceptionPredictable = tkpoleExceptionPredictable;
        this.detail = detail;
        this.tips = Assert.notNul( tips) ? tips : tkpoleExceptionPredictable.getTips();
    }
}
