package com.guojy.parser.rule.type;

import com.guojy.model.Msg;
import com.tkp.tkpole.starter.utils.model.Msg;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/2/19
 *
 * @author guojy24
 * @version 1.0
 * */
public interface Transformable {

    /**
     * 转换方法
     *
     * @param <G> 目标泛型
     * @param object 源类型实例
     * @param gClass 目标类型
     * @return 目标实例消息
     * */
    <G> Msg<G> transform(Object object, Class<G> gClass);
}
