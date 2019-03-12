package com.github.piggyguojy.parser.rule.type;

import java.util.function.Function;

/**
 * 可转换并可增加转换规则的
 * <p>
 *     允许增加用户自定义的类型转换规则
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * @since JDK1.8
 * */
public interface TransformableAndRuleAddable<T extends TransformableAndRuleAddable>
        extends Transformable {

    /**
     * 增加自定义规则
     *
     * @param <G> 目标泛型
     * @param gClass 目标类型
     * @param zlass 源类型
     * @param zgFunction 转换函数
     * @return 转换器
     * */
    <G> T addRule4Transformer(Class<G> gClass, Class zlass, Function<?,G> zgFunction);
}
