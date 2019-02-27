package com.guojy.parser.rule.structure.annotation;

import com.guojy.model.Msg;
import com.guojy.parser.rule.parse.AbstractParser;
import com.tkp.tkpole.starter.utils.model.Msg;
import com.tkp.tkpole.starter.utils.parser.rule.parse.AbstractParser;
import com.tkp.tkpole.starter.utils.parser.rule.structure.OverrideRule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;

import static com.tkp.tkpole.starter.utils.model.Msg.msg;
import static java.lang.String.format;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/2/19
 *
 * @author guojy24
 * @version 1.0
 * */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultAnnotationHandler extends AbstractAnnotationHandler{
    static final DefaultAnnotationHandler DEFAULT_ANNOTATION_HANDLER = new DefaultAnnotationHandler();
    @Override
    public Msg handle(Class aClass, AbstractParser abstractParser, Object... args) { return Msg.MsgError.IllegalState_PROC.getMsg(); }
    @Override
    public Msg<?> onType(Class aClass, Annotation annotation, AbstractParser abstractParser, Object... args) { return Msg.msg(new IllegalStateException(format("没有找到对应注解 %s 的处理器", annotation.getClass().getName()))); }
    @Override
    public Msg<?> onField(Class aClass, Annotation annotation, AbstractParser abstractParser, Object... args) { return Msg.msg(new IllegalStateException(format("没有找到对应注解 %s 的处理器", annotation.getClass().getName()))); }
}