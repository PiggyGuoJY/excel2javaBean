package com.tkp.tkpole.starter.utils.misc.sms.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tkp.tkpole.starter.utils.gson.TkpoleGsonBean;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 标准短信请求实体
 *
 * <p> 创建时间：2018/8/7
 *
 * @author guojy24
 * @version 1.0
 * */
@Data @AllArgsConstructor
@TkpoleGsonBean
@XmlRootElement( name = "SmsRequest") @XmlAccessorType( XmlAccessType.FIELD)
public class SmsRequest implements Serializable, Cloneable {

    @Expose @SerializedName("mobile")
    @XmlElement( name = "mobile")
    private String phoneNo;

    @Expose @SerializedName("msgContent")
    @XmlElement( name = "content")
    private String content;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
