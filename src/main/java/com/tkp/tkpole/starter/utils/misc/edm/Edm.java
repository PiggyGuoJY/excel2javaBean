package com.tkp.tkpole.starter.utils.misc.edm;

import com.tkp.tkpole.starter.utils.misc.edm.model.EmailContent;
import com.tkp.tkpole.starter.utils.model.Msg;
import com.tkp.tkpole.starter.utils.soap.SoapFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.util.Date;

import static com.tkp.tkpole.starter.utils.Assert.notNul;
import static com.tkp.tkpole.starter.utils.Assert.notNull;
import static com.tkp.tkpole.starter.utils.exception.TkpoleException.of;
import static com.tkp.tkpole.starter.utils.exception.TkpoleExceptionPredictable.ERR_UNKOWN;
import static com.tkp.tkpole.starter.utils.model.Msg.msg;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/8/9
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j
public class Edm {

    /**
     * <p> 发送邮件
     *
     * @param emailContent 邮件请求实体
     * @return 描述返回值
     * */
    public Msg<Boolean> sendEmail(EmailContent emailContent) {
        return sendEmail( edmConfigData.getCode(), edmConfigData.getPassword(), emailContent);
    }

    public Edm(
            SoapFactory soapFactory,
            EdmConfigData edmConfigData
    ) {
        this.soapFactory = soapFactory;
        this.edmConfigData = edmConfigData;
    }

    //==== 华丽的分割线 === 私有资源

    private SoapFactory soapFactory;
    private EdmConfigData edmConfigData;

    private static final String[] DEFAULT_RECEIVERS = new String[]{};
    private static final FastDateFormat DATE_FORMAT_FULL = FastDateFormat.getInstance( "yyyy-MM-dd HH:mm:ss");
    private static final String REGEX_SUCCESS = "^100:执行成功\\[ID=[0-9]+]$";
    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @param code 描述此参数的作用
     * @param password 描述此参数的作用
     * @param emailContent 描述此参数的作用
     * @return 描述返回值
     * */
    private Msg<Boolean> sendEmail(String code, String password, EmailContent emailContent) {
        String response = soapFactory.getSoapAccessibleByName( "EDM", "sendEmail").execute(
                new Object[]{
                        code, password,
                        notNul( emailContent.getTitle()) ? emailContent.getTitle() : edmConfigData.getDefaultTitle(),
                        notNul( emailContent.getContext()) ? emailContent.getTitle() : edmConfigData.getDefaultContext(),
                        notNul( emailContent.getReceivers()) ? emailContent.getReceivers() : DEFAULT_RECEIVERS,
                        notNull( emailContent.getAttachment()) ? new DataHandler( new FileDataSource( emailContent.getAttachment())) : null,
                        notNull( emailContent.getAttachment()) ? emailContent.getAttachment().getName() : null,
                        DATE_FORMAT_FULL.format( new Date())},
                resp -> ( String)resp,
                "未知错误");
        return response.matches( REGEX_SUCCESS) ? msg( true) : msg( of( ERR_UNKOWN, response, "请根据响应信息处理"));
    }
}