package com.guojy.parser.rule.type;

import com.guojy.model.Msg;

import static com.guojy.model.Msg.msg;

/**
 * 可转换的
 *
 *
 * <p> 创建时间：2019/2/19
 *
 * @author guojy
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
     * @date 2019/03/02
     * */
    default <G> Msg<G> transform(Class<G> gClass) {
        return msg((G)null);
    }
}
