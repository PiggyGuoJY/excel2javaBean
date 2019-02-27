package com.tkp.tkpole.starter.utils.misc.sms.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tkp.tkpole.starter.utils.gson.TkpoleGsonBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 标准短信响应实体
 *
 * <p> 创建时间：2018/8/9
 *
 * @author guojy24
 * @version 1.0
 * */
@Data @NoArgsConstructor @AllArgsConstructor
@TkpoleGsonBean
@XmlRootElement( name = "SmsResponse") @XmlAccessorType( XmlAccessType.FIELD)
public class SmsResponse implements Serializable, Cloneable {

    @Expose @SerializedName("resultMsg")
    @XmlElement( name = "msg")
    private String msg;

    @Expose @SerializedName("resultCode")
    @XmlElement( name = "code")
    private String code;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
