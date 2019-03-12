
package com.github.piggyguojy.parser.rule.structure.annotation;

import com.github.piggyguojy.Msg;
import com.github.piggyguojy.parser.rule.parse.AbstractParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;

import static com.github.piggyguojy.Msg.msg;
import static java.lang.String.format;

/**
 * 程序员（guojy）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/2/19
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
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
        return msg(Msg.MsgError.ILLEGAL_STATE_INIT.getE());
    }
    @Override
    public Msg<?> onType(
            Class aClass,
            Annotation annotation,
            AbstractParser abstractParser,
            Object... args
    ) {
        return msg(new IllegalStateException(format(
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
        return msg(new IllegalStateException(format(
                "没有找到对应注解 %s 的处理器",
                annotation.getClass().getName())));
    }



    static final DefaultAnnotationHandler DEFAULT_ANNOTATION_HANDLER
            = new DefaultAnnotationHandler();
}