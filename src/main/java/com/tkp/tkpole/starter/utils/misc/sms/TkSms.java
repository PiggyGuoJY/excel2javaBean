package com.tkp.tkpole.starter.utils.misc.sms;

import com.google.common.base.Charsets;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tkp.tkpole.starter.utils.gson.TkpoleGsonBean;
import com.tkp.tkpole.starter.utils.misc.sms.model.SmsRequest;
import com.tkp.tkpole.starter.utils.model.Msg;
import com.tkp.tkpole.starter.utils.soap.RestFactory;
import com.tkp.tkpole.starter.utils.soap.RestRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tkp.tkpole.starter.utils.exception.TkpoleException.of;
import static com.tkp.tkpole.starter.utils.exception.TkpoleExceptionPredictable.ERR_RETURN_FAILURE;
import static com.tkp.tkpole.starter.utils.model.Msg.msg;

/**
 * 泰康集团95522短信服务
 *
 * <p> 创建时间：2018/6/22
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j
public class TkSms implements MsgSender, MsgSenderLoged {

    /**
     * <p> 短信发送
     *
     * @param smsRequest 通用短信请求
     * @return 描述返回值
     * */
    @Override
    public Msg<Boolean> sendSmsMsg( final SmsRequest smsRequest) {
        return sendSmsMsg( new SmsReq4TkSms( smsRequest));
    }

    @Override
    public List<Msg<Boolean>> sendSmsMsgs( final List<SmsRequest> smsRequests) {
        return sendSmsMsgsWithsmsReq4TkSms( smsRequests.stream().map( SmsReq4TkSms::new).collect( Collectors.toList()));
    }

    /**
     * <p> 短信发送
     *
     * @param smsReq4TKSms 集团短信请求
     * @return 描述返回值
     * */
    public Msg<Boolean> sendSmsMsg(final SmsReq4TkSms smsReq4TKSms) {
        log.debug( smsReq4TKSms.toString());
        Map<String,String> queryValues = new HashMap<>(3);
        queryValues.put( "phoneNo", smsReq4TKSms.getSmsRequest().getPhoneNo());
        queryValues.put( "content", smsReq4TKSms.getSmsRequest().getContent());
        queryValues.put( "businessType", smsReq4TKSms.getBusinessType());
        String result = restFactory.getRestAccessibleByName( "TkSms", "sendSmsMsg").execute(
                Collections.singletonList(
                        new RestRequest(null, null, queryValues, null)),
                resp -> IOUtils
                        .readLines( resp.getEntity().getContent(), Charsets.UTF_8)
                        .parallelStream()
                        .reduce( "", ( e, e2) -> e+e2),
                "-1")
                .get( 0);
        log.debug( result);
        return SUCCESS.equals( result) ? msg( true) : msg( of( ERR_RETURN_FAILURE, result, "请根据返回码确定问题(返回码意义请咨询集团短信平台)"));
    }

    @SuppressWarnings( "unchecked")
    private List<Msg<Boolean>> sendSmsMsgsWithsmsReq4TkSms( final List<SmsReq4TkSms> smsReq4TkSmses) {
        List<String> results = restFactory.getRestAccessibleByName( "TkSms", "sendSmsMsg").execute(
                smsReq4TkSmses
                        .stream()
                        .map( smsReq4TkSms -> {
                            Map<String,String> queryValues = new HashMap<>(3);
                            queryValues.put( "phoneNo", smsReq4TkSms.getSmsRequest().getPhoneNo());
                            queryValues.put( "content", smsReq4TkSms.getSmsRequest().getContent());
                            queryValues.put( "businessType", smsReq4TkSms.getBusinessType());
                            return queryValues; })
                        .map( map -> new RestRequest( null,null, map, null))
                        .collect( Collectors.toList()),
                resp -> IOUtils
                        .readLines( resp.getEntity().getContent(), Charsets.UTF_8)
                        .stream()
                        .reduce( "", ( e, e2) -> e+e2),
                "-1");
        return results.stream().map( result -> (Msg<Boolean>)(SUCCESS.equals( result) ? msg( true) : msg( of( ERR_RETURN_FAILURE, result, "请根据返回码确定问题(返回码意义请咨询集团短信平台)")))).collect( Collectors.toList());
    }

    public TkSms(
            RestFactory restFactory
    ) {
        this.restFactory = restFactory;
    }

    //==== 华丽的分割线 === 私有资源

    private RestFactory restFactory;

    private static final String SUCCESS = "0";

    /**
     * 适用集团的短信请求
     *
     * <p> 创建时间：2018/8/9
     *
     * @author guojy24
     * @version 1.0
     * */
    @Data @AllArgsConstructor
    @TkpoleGsonBean
    @XmlRootElement( name = "SmsReq4TkSms") @XmlAccessorType( XmlAccessType.FIELD)
    public static class SmsReq4TkSms implements Serializable {

        private SmsReq4TkSms( SmsRequest smsRequest) {
            this.smsRequest = smsRequest;
        }

        /**
         * 短信业务类型( 默认使用TIABD), 这块之后还得和李淑娴那边再商量下, 明确一个通用业务类型
         * */
        @Expose @SerializedName("businessType")
        @XmlElement
        private String businessType = "TIABD";

        /**
         * 通用短信请求
         * */
        @Expose @SerializedName("smsMsg")
        @XmlElement
        private SmsRequest smsRequest;
    }
}
