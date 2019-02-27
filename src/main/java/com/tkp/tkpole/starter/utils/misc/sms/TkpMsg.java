package com.tkp.tkpole.starter.utils.misc.sms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tkp.tkpole.starter.utils.gson.TkpoleGsonBean;
import com.tkp.tkpole.starter.utils.misc.sms.model.SmsRequest;
import com.tkp.tkpole.starter.utils.misc.sms.model.SmsResponse;
import com.tkp.tkpole.starter.utils.model.Msg;
import com.tkp.tkpole.starter.utils.soap.RestFactory;
import com.tkp.tkpole.starter.utils.soap.RestService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static com.tkp.tkpole.starter.utils.exception.TkpoleException.of;
import static com.tkp.tkpole.starter.utils.exception.TkpoleExceptionPredictable.ERR_RETURN_FAILURE;
import static com.tkp.tkpole.starter.utils.model.Msg.msg;

/**
 * 泰康养老接口服务组 短信服务
 *
 * <p> 创建时间：2018/6/22
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j
public class TkpMsg implements MsgSender, MsgSenderLoged {

    /**
     * <p> 发送短信
     *
     * @param smsRequest 通用短信请求
     * @return 描述返回值
     * */
    @Override
    public Msg<Boolean> sendSmsMsg ( SmsRequest smsRequest) {
        return sendSmsMsg(
                new SmsReq4TkpMsg( "", smsRequest, new TkSystem( tkSystemConfigData.getSystemId(), tkSystemConfigData.getSystemPassword())));
    }

    @Override
    public List<Msg<Boolean>> sendSmsMsgs(List<SmsRequest> smsRequests) {
        return sendSmsMsgsWithSmsReq4TkpMsgs(
                smsRequests
                        .stream()
                        .map( smsRequest -> new SmsReq4TkpMsg( "", smsRequest, new TkSystem( tkSystemConfigData.getSystemId(), tkSystemConfigData.getSystemPassword())))
                        .collect( Collectors.toList()));
    }

    /**
     * <p> 发送短信
     *
     * @param smsReq4TKpMsg 短信请求实体
     * @return 描述返回值
     * */
    public Msg<Boolean> sendSmsMsg(SmsReq4TkpMsg smsReq4TKpMsg) {
        log.debug( smsReq4TKpMsg.toString());
        List<SmsResponse> smsResponses = restFactory.getRestAccessibleByName("TkpMsg", "smsMsgSend").execute(
                RestService.javaBeans2Restquests( smsReq4TKpMsg),
                response -> RestService.response2JavaBean( response, SmsResponse.class),
                new SmsResponse());
        log.debug( smsResponses.get( 0).toString());
        return SUCCESS.equals( smsResponses.get( 0).getCode()) ? new Msg<>( true) : msg( of( ERR_RETURN_FAILURE, smsResponses.get( 0).getMsg(), "请根据返回码作相应处理"));
    }

    public List<Msg<Boolean>> sendSmsMsgsWithSmsReq4TkpMsgs( List<SmsReq4TkpMsg> smsReq4TkpMsgs) {
        List<SmsResponse> smsResponses = restFactory.getRestAccessibleByName("TkpMsg", "smsMsgSend").execute(
                RestService.javaBeans2Restquests( smsReq4TkpMsgs.toArray( new SmsReq4TkpMsg[]{})),
                response -> RestService.response2JavaBean( response, SmsResponse.class),
                new SmsResponse());
        return smsResponses
                .stream()
                .map( smsResponse -> (Msg<Boolean>)(SUCCESS.equals( smsResponse.getCode()) ? new Msg<>( true) : msg( of( ERR_RETURN_FAILURE, smsResponses.get( 0).getMsg(), "请根据返回码作相应处理"))))
                .collect(Collectors.toList());
    }

    public TkpMsg(
            TkSystemConfigData tkSystemConfigData,
            RestFactory restFactory
    ) {
        this.tkSystemConfigData = tkSystemConfigData;
        this.restFactory = restFactory;
    }

    //==== 华丽的分割线 === 私有资源

    private TkSystemConfigData tkSystemConfigData;
    private RestFactory restFactory;

    private static final String SUCCESS = "0";

    /**
     * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
     *
     * <p> 创建时间：2018/8/9
     *
     * @author guojy24
     * @version 1.0
     * */
    @Data @AllArgsConstructor
    @TkpoleGsonBean
    @XmlRootElement( name = "SmsReq4TkMsg") @XmlAccessorType( XmlAccessType.FIELD)
    public static class SmsReq4TkpMsg implements Serializable, Cloneable {

        public SmsReq4TkpMsg( SmsRequest smsRequest) {
            this.smsRequest = smsRequest;
        }

        /**
         * 适用短信接口组的业务类型参数
         * */
        @Expose
        @XmlElement
        private String businessType;

        /**
         * 通用短信请求
         * */
        @Expose @SerializedName("smsMsg")
        @XmlElement
        private SmsRequest smsRequest;

        /**
         * 适用短信接口组的附加参数(系统信息)
         * */
        @Expose
        @XmlElement
        private TkSystem tkSystem;

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    /**
     * 适用养老接口组的附加内容
     *
     * <p> 创建时间：2018/8/9
     *
     * @author guojy24
     * @version 1.0
     * */
    @Data @AllArgsConstructor
    @TkpoleGsonBean
    @XmlRootElement @XmlAccessorType( XmlAccessType.FIELD)
    private static class TkSystem implements Serializable, Cloneable {

        @Expose
        @XmlElement
        private String systemId;

        @Expose
        @XmlElement
        private String systemPassword;

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
