
package com.github.piggyguojy.parser.rule.structure.annotation;

import com.github.piggyguojy.parser.rule.parse.AbstractParser;
import com.github.piggyguojy.util.Msg;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;

import static com.github.piggyguojy.util.Msg.msg;
import static java.lang.String.format;

/**
 * 程序员（guojy）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultAnnotationHandler
        extends AbstractAnnotationHandler{

    /**
     * {@inheritDoc}
     */
    @Override
    public Msg handle(
            Class aClass,
            AbstractParser abstractParser,
            Object... args
    ) {
        return msg(Msg.MsgError.ILLEGAL_STATE_INIT.getE());
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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