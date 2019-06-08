
package com.github.piggyguojy.parser.rule.parse;

import com.github.piggyguojy.util.Msg;

/**
 * 可解析的
 * @author 郭晋阳
 */
public interface Parseable {

    /**
     * 解析入口
     *
     * @param gClass 目标类
     * @param args 附加参数
     * @param <G> 目标泛型
     * @return 包含目标实例的消息
     */
    <G> Msg<G> parse(Class<G> gClass, Object ... args);
}
