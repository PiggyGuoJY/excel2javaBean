package com.guojy.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.guojy.gson.GsonBean;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 统一消息实体
 * <p> 该实体类主要作后台向前台传输数据的容器, 在程序中的不同层次之间也可以使用此类.
 *
 * 关于消息实体中状态码和状态消息的约定
 *
 *  0~100
 *
 *
 * <p> 创建时间：2018/1/4
 *
 * <p> 最近修改: 2018/5/4
 * @author guojy
 * @version 1.1
 * */

@Slf4j
@ToString( exclude = {"listEntities","setEntities","mapEntities","arrayEntities"}) @EqualsAndHashCode
@GsonBean
@XmlRootElement @XmlAccessorType( XmlAccessType.FIELD)
public final class Msg<T> implements Serializable {

    @AllArgsConstructor
    public enum MsgError {

        /**
         * 程序执行到不应该到达的位置
         * */
        ILLEGAL_STATE_SEGMENT_SHOULD_NOT_BE(msg(new IllegalStateException("程序逻辑错误, 正常情况下无法到达这里. 请根据程序执行堆栈进行排查."))),
        /**
         * 前置校验出错导致的程序提前中断
         * */
        ILLEGAL_STATE_INIT(msg(new IllegalStateException("Msg尚未赋值, 程序异常导致的提前结束")));

        @Getter
        Msg<?> msg;
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
        // 序列化为xml格式时, 针对容器化负载实体的特殊处理
        if ( t instanceof List<?>) {
            this.listEntities =( List<?>) t;
        } else if ( t instanceof Object[]) {
            this.arrayEntities = ( ( Object[]) t).clone();
        } else if ( t instanceof Set<?>) {
            this.setEntities = ( Set<?>) t;
        } else if ( t instanceof Map<?,?>) {
            this.mapEntities = ( Map<?,?>) t;
        }
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
            if ( t instanceof List<?>) {
                this.listEntities =( List<?>) t;
            } else if ( t instanceof Object[]) {
                this.arrayEntities = ( ( Object[]) t).clone();
            } else if ( t instanceof Set<?>) {
                this.setEntities = ( Set<?>) t;
            } else if ( t instanceof Map<?,?>) {
                this.mapEntities = ( Map<?,?>) t;
            }
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
            if ( t instanceof List<?>) {
                this.listEntities =( List<?>) t;
            } else if ( t instanceof Object[]) {
                this.arrayEntities = ( ( Object[]) t).clone();
            } else if ( t instanceof Set<?>) {
                this.setEntities = ( Set<?>) t;
            } else if ( t instanceof Map<?,?>) {
                this.mapEntities = ( Map<?,?>) t;
            }
        }
    }
    /**
     * 程序员（guojy）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param t 负载实体
     * @param msg 附加消息
     * */
    public Msg( T t, String msg) {
        this( CODE_SUCCESS, msg, "无详细信息", t);
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
    public T getT() {
        return t;
    }
    public void setT( T t) {
        if ( t instanceof List<?>) {
            this.listEntities =( List<?>) t;
        } else if ( t instanceof Object[]) {
            this.arrayEntities = ( ( Object[]) t).clone();
        } else if ( t instanceof Set<?>) {
            this.setEntities = ( Set<?>) t;
        } else if ( t instanceof Map<?,?>) {
            this.mapEntities = ( Map<?,?>) t;
        }
        this.t = t;
    }
    public Msg<T> clearT(){ this.t = null; return this;}
    /**
     * 用于从xml反序列化为msg时, 获取负荷实体
     *
     * @return 负荷
     * */
    public Object getT4Xml() {
        return listEntities!=null ? listEntities : ( setEntities!=null ? setEntities : ( mapEntities!=null ? mapEntities : ( arrayEntities!=null ? arrayEntities : t)));
    }

    //==== 华丽的分割线 === 私有资源

    @Getter @Setter
    @Expose @SerializedName( value = "code", alternate = {"Code"})
    @XmlElement
    private String code;
    @Getter @Setter
    @Expose @SerializedName( value = "msg", alternate = {"Msg", "message", "Message"})
    @XmlElement
    private String msg;
    @Getter @Setter
    @Expose @SerializedName( value = "detail", alternate = {"Detail"})
    @XmlElement
    private String detail;

    /*<开始>更改者: guojy 更改时间: 2019/2/15 变更原因: 泛型T的实例将不参与序列化(Java原生提供)*/

    @Expose @SerializedName( value = "entity", alternate = {"Entity", "E"})
    @XmlElement
    private transient T t;
    /*<结束>更改者: guojy 更改时间: 2019/2/15 */

    //下面的这些变量用于解决xml化Msg时, 负载实体是容器的情况

    @XmlElementWrapper( name = "listEntities") @XmlElement( name = "list")
    private transient List<?> listEntities;
    @XmlElementWrapper( name = "setEntities") @XmlElement( name = "set")
    private transient Set<?> setEntities;
    private transient Map<?,?> mapEntities;
    @XmlElementWrapper( name = "arrayEntities") @XmlElement( name = "array")
    private transient Object[] arrayEntities;

    private static final String CODE_SUCCESS = "0";
    private static final String CODE_RAW_EXCEPTION = "100";
    private static final Set<String> CODE_NOT_EXCEPTION = new HashSet<>(1);
    static {
        CODE_NOT_EXCEPTION.add( CODE_SUCCESS);
    }
}