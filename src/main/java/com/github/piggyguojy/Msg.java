
package com.github.piggyguojy;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * 统一消息实体
 *
 * 该实体类主要作后台向前台传输数据的容器, 在程序中的不同层次之间也可以使用此类.
 *
 * 关于消息实体中状态码和状态消息的约定
 *
 *  0~100
 *
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * @since JDK1.8
 *
 * @see Assert
 * @see ClassUtil
 * @see JsonUtil
 * */

@Slf4j @Data @JsonUtil.GsonBean
public final class Msg<T> implements Serializable {

    @AllArgsConstructor
    public enum MsgError {

        /**
         * 程序执行到不应该到达的位置
         * */
        ILLEGAL_STATE_SEGMENT_SHOULD_NOT_BE_ACCESSED(new IllegalStateException(
                "程序逻辑错误, 正常情况下无法到达这里. 请根据程序执行堆栈进行排查.")),

        /**
         * 程序不接受空参数
         */
        ILLEGAL_ARGS_NULL(new IllegalArgumentException(
                "这里不允许使用空参数")),

        /**
         * 前置校验出错导致的程序提前中断
         * */
        ILLEGAL_STATE_INIT(new IllegalStateException(
                "Msg尚未赋值, 程序异常导致的提前结束"));

        @Getter
        Exception e;
    }
    /**
     * 全参构造器
     *
     * @param code 描述消息的类型
     * @param msg 描述消息附带的简要提示
     * @param detail 描述消息附带的更多信息
     * @param t 负载实体
     * */
    public Msg( String code, String msg, String detail, T t) {
        this.code = code;
        this.msg = msg;
        this.detail = detail;
        this.t = t;
    }
    /**
     * 默认创建一个代表成功返回的消息
     *
     * */
    public Msg() {
        this( CODE_SUCCESS, "成功访问", "无详细信息", null);
    }
    @SuppressWarnings("uncheck")
    public Msg( Msg msgInstance) {
        if ( msgInstance.isException()) {
            this.code = msgInstance.code;
            this.msg = msgInstance.msg;
            this.detail = msgInstance.detail;
        } else {
            this.code = CODE_SUCCESS;
            this.msg = "成功访问";
            this.detail = "无详细信息";
            this.t = (T)msgInstance.getT();
        }
    }
    /**
     * 使用负荷创建一个成功返回
     *
     * @param t 有效负荷
     * */
    public Msg(T t) {
        if ( t instanceof Msg && ((Msg) t).isException()) {
                this.code = ((Msg) t).code;
                this.msg = ((Msg) t).msg;
                this.detail = ((Msg) t).detail;
        } else {
            this.code = CODE_SUCCESS;
            this.msg = "成功访问";
            this.detail = "无详细信息";
            this.t = t;
        }
    }
    /**
     * 程序员（guojy）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param t 负载实体
     * @param msg 附加消息
     * */
    @SuppressWarnings("unchecked")
    public Msg( T t, String msg) {
        if ( Exception.class.isAssignableFrom(t.getClass())) {
            this.code = CODE_RAW_EXCEPTION;
            this.detail = t.toString();
            this.t = null;
        } else  {
            this.code = CODE_SUCCESS;
            this.detail = "无详细信息";
            this.t = t;
        }
        this.msg = msg;
    }
    /**
     * 使用异常创建一个失败返回
     *
     * @param e 异常
     * */
    public Msg( Exception e) {
        this.code = CODE_RAW_EXCEPTION;
        this.msg = e.getMessage();
        this.detail = e.toString();
        this.t = null;
    }
    public static <T> Msg<T> msg( String code, String msg, String detail, T t) { return new Msg<>( code, msg, detail, t);}
    public static <T> Msg<T> msg() { return new Msg<>(); }
    public static <T> Msg<T> msg( Msg msg) { return new Msg<>( msg); }
    public static <T> Msg<T> msg( T t) { return new Msg<>( t); }
    public static <T> Msg<T> msg( T t, String msg) { return new Msg<>( t, msg); }
    public static <T> Msg<T> msg( Exception e) { return new Msg<>( e); }
    /**
     * 判断消息是否代表异常
     *
     * @return 是否异常
     * */
    public final boolean isException() {
        if ( !CODE_NOT_EXCEPTION.contains(this.getCode())) {
            log.warn( this.msg);
            return true;
        } else {
            return false;
        }
    }



    @Expose @SerializedName( value = "code", alternate = {"Code"})
    private String code;
    @Expose @SerializedName( value = "msg", alternate = {"Msg", "message", "Message"})
    private String msg;
    @Expose @SerializedName( value = "detail", alternate = {"Detail"})
    private String detail;
    @Expose @SerializedName( value = "entity", alternate = {"Entity", "E"})
    private T t;
    private static final String CODE_SUCCESS = "0";
    private static final String CODE_RAW_EXCEPTION = "100";
    private static final Set<String> CODE_NOT_EXCEPTION = new HashSet<>(2);



    static {
        CODE_NOT_EXCEPTION.add( CODE_SUCCESS);
    }
}