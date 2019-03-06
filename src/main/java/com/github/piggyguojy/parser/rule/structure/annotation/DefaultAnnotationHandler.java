package com.github.piggyguojy.parser.rule.structure.annotation;

import com.github.piggyguojy.parser.rule.parse.AbstractParser;
import com.github.piggyguojy.model.Msg;
import com.github.piggyguojy.parser.rule.parse.AbstractParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;

import static java.lang.String.format;

/**
 * 程序员（guojy）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/2/19
 *
 * @author guojy
 * @version 1.0
 * */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultAnnotationHandler
        extends AbstractAnnotationHandler{

    @Override
    public Msg handle(
            Class aClass,
            AbstractParser abstractParser,
            Object... args
    ) {
        return Msg.MsgError.ILLEGAL_STATE_SEGMENT_SHOULD_NOT_BE.getMsg();
    }
    @Override
    public Msg<?> onType(
            Class aClass,
            Annotation annotation,
            AbstractParser abstractParser,
            Object... args
    ) {
        return Msg.msg(new IllegalStateException(format(
                "没有找到对应注解 %s 的处理器",
                annotation.getClass().getName())));
    }
    @Override
    public Msg<?> onField(
            Class aClass,
            Annotation annotation,
            AbstractParser abstractParser,
            Object... args
    ) {
        return Msg.msg(new IllegalStateException(format(
                "没有找到对应注解 %s 的处理器",
                annotation.getClass().getName())));
    }



    static final DefaultAnnotationHandler DEFAULT_ANNOTATION_HANDLER
            = new DefaultAnnotationHandler();
}