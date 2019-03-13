
package com.github.piggyguojy.parser.excel.rule.structure.annotation.handler;

import com.github.piggyguojy.ClassUtil;
import com.github.piggyguojy.Msg;
import com.github.piggyguojy.parser.excel.rule.parse.ExcelParser;
import com.github.piggyguojy.parser.excel.rule.structure.annotation.ExcelBean;
import com.github.piggyguojy.parser.rule.structure.inherit.BiInheritableRule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

import static com.github.piggyguojy.Assert.isNull;
import static com.github.piggyguojy.Assert.notNull;
import static com.github.piggyguojy.Msg.msg;
import static java.lang.String.format;

/**
 * 程序员（guojy）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/2/15
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelBeanNestedHandler
        extends ExcelAnnotationHandler<ExcelBean.Nested>
        implements BiInheritableRule<ExcelBean.Nested,ExcelBean> {

    @Override @SuppressWarnings("unchecked")
    public <G> Msg<?> onField(
            Class<G> gClass,
            ExcelBean.Nested nested,
            ExcelParser excelParser,
            Object... args
    ) {
        // 1. 检查嵌套类的类型是否满足要求
        ExcelBean excelBean = gClass.getDeclaredAnnotation(ExcelBean.class);
        if( isNull(excelBean)||!excelBean.isNestable()) {
            return msg(new IllegalStateException( format(
                    "使用ExcelBean.Nested注解标注的属性 %s, 其类型 %s 必须使用@ExcelBean注解标注且@ExcelBean.isNestable为true",
                    args[GOAL_INST].getClass().getCanonicalName() + "." + ((Field) args[FIELD_REF]).getName(),
                    gClass.getCanonicalName())));
        }
        // 2.ExcelBean.Nested只有可能从属性所在类的ExcelBean上继承属性
        ExcelBean excelBeanParent = getAnnotationParent(ExcelBean.class, args);
        args[ANNOTATION_PARENT] = notNull(excelBeanParent) ? decideBiRule(nested, excelBeanParent, excelBeanParent.overrideRule()) : nested;
        // 3.按照ExcelBean重新处理(可以一直递归)
        Msg<?> msg = getAnnotationHandlerRegistered(ExcelBean.class).onType(
                gClass, excelBean, excelParser,
                args);
        if ( !msg.isException()) {
            ClassUtil.set((Field) args[FIELD_REF], args[GOAL_INST], msg.getT());
        }
        return msg();
    }



    static { register(ExcelBean.Nested.class, new ExcelBeanNestedHandler()); }
}
