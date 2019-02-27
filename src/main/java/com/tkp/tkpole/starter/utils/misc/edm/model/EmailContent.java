package com.tkp.tkpole.starter.utils.misc.edm.model;

import com.tkp.tkpole.starter.utils.ResourceUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;

import static com.tkp.tkpole.starter.utils.Assert.notNul;

/**
 * 邮件内容对象
 *
 * <p> 创建时间：2018/8/8
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @ToString @EqualsAndHashCode
public class EmailContent {

    public String[] getReceivers() {
        return receivers.clone();
    }

    public void setReceivers( String[] receivers) {
        this.receivers = receivers.clone();
    }

    public EmailContent( String title, String context, String[] receivers, File attachment) {
        setTitle( title);
        setContext( context);
        setReceivers( receivers);
        setAttachment( attachment);
    }

    /**
     * 邮件主题
     * */
    @Getter @Setter
    private String title;
    /**
     * 邮件正文
     * */
    @Getter @Setter
    private String context;
    /**
     * 邮件收信人
     * */
    private String[] receivers;
    /**
     * 附件
     * */
    @Getter @Setter
    private File attachment;

    public boolean setAttachmentPath( Path path) {
        attachment = ResourceUtil.path2File( path);
        return notNul( attachment);
    }

    public boolean setAttachmentPath ( Path path, String position) {
        attachment = ResourceUtil.path2File( path, position);
        return notNul( attachment);
    }
}
