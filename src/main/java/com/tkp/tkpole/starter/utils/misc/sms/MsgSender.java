package com.tkp.tkpole.starter.utils.misc.sms;

import com.tkp.tkpole.starter.utils.misc.sms.model.SmsRequest;
import com.tkp.tkpole.starter.utils.model.Msg;

import java.util.List;

/**
 * 短信发送接口
 *
 * <p> 创建时间：2018/8/10
 *
 * @author guojy24
 * @version 1.0
 * */
public interface MsgSender {
    /**
     * <p> 发送短信
     *
     * @param smsRequest smsRequest
     * @return 描述返回值
     * */
    Msg<Boolean> sendSmsMsg( SmsRequest smsRequest);

    /**
     * <p> 批量发送短信
     *
     * @param smsRequests smsRequests
     * @return 描述返回值
     * */
    List<Msg<Boolean>> sendSmsMsgs( List<SmsRequest> smsRequests);
}