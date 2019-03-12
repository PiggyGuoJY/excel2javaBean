
package com.github.piggyguojy.parser.rule.type;

import com.github.piggyguojy.Msg;

import static com.github.piggyguojy.Msg.msg;

/**
 * 可转换的
 *
 *
 * <p> 创建时间：2019/2/19
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
public interface Transformable {
    /**
     * 转换方法
     *
     * @param <G> 目标泛型
     * @param object 源类型实例
     * @param gClass 目标类型
     * @return 包含目标实例的消息
     * */
    <G> Msg<G> transform(Object object, Class<G> gClass);
    /**
     * 对应object为null时的转换方法
     *
     * @param <G> 目标泛型
     * @param gClass 目标类型
     * @return 包含目标实例的消息
     * */
    default <G> Msg<G> transform(Class<G> gClass) {
        return msg((G)null);
    }
}
