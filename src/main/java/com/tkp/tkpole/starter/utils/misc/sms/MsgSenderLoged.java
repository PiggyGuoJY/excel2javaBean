package com.tkp.tkpole.starter.utils.misc.sms;

import com.tkp.tkpole.starter.utils.ResourceUtil;
import com.tkp.tkpole.starter.utils.misc.sms.model.SmsRequest;
import com.tkp.tkpole.starter.utils.model.Msg;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.function.BiConsumer;

import static java.lang.String.format;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/1/15
 *
 * @author guojy24
 * @version 1.0
 * */
public interface MsgSenderLoged extends MsgSender {

    /**
     * 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param smsRequest 描述此参数的作用
     * @param success 描述此参数的作用
     * @param failure 描述此参数的作用
     * */
     default void sendSmsMsg( SmsRequest smsRequest, @NonNull BiConsumer<SmsRequest,Msg<?>> success, @NonNull BiConsumer<SmsRequest,Msg<?>> failure) {
        Msg<Boolean> msg = sendSmsMsg(smsRequest);
        if (msg.isException()) {
            failure.accept(smsRequest, msg);
        }
        if (msg.getT()) {
            success.accept(smsRequest, msg);
        } else {
            failure.accept(smsRequest, msg);
        }
     }

     @Slf4j
     public static final class DefaultConsumer {
         /**
          * 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
          *
          * @param jdbcTemplate 描述此参数的作用
          * @return 描述返回值
          * */
         public static BiConsumer<SmsRequest, Msg<?>> defaultSuccessConsumer( final JdbcTemplate jdbcTemplate) {
             return ( smsRequest, msg) -> {
                 try {
                     jdbcTemplate.execute(format("INSERT INTO Log_InstantMsgSend VALUES ('%s','%s','%s','%s')", smsRequest.getPhoneNo(), smsRequest.getContent(), "发送成功", ResourceUtil.getCurrentDate()  + " " + ResourceUtil.getCurrentTime()));
                 } catch ( Exception e) { log.error( e.getMessage(), e); }
             };
         }
         /**
          * 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
          *
          * @param jdbcTemplate 描述此参数的作用
          * @return 描述返回值
          * */
         public static BiConsumer<SmsRequest, Msg<?>> defaultFailureConsumer( final JdbcTemplate jdbcTemplate) {
             return ( smsRequest, msg) -> {
                 try {
                     jdbcTemplate.execute(format("INSERT INTO Log_InstantMsgSend VALUES ('%s','%s','%s','%s')", smsRequest.getPhoneNo(), smsRequest.getContent(), msg.getMsg(), ResourceUtil.getCurrentDate() + " " + ResourceUtil.getCurrentTime()));
                 } catch ( Exception e) { log.error( e.getMessage(), e); }
             };
         }
     }
}
