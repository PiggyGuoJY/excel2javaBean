
package com.github.piggyguojy.parser.rule.structure;

import com.github.piggyguojy.Msg;
import com.github.piggyguojy.parser.rule.parse.AbstractParser;

/**
 * 规则处理器(用于处理定义数据来源的规则.一般而言,需要使用解析器传递某些中间参数)
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
public interface StructureHandler<P extends AbstractParser> {

    int ARGS_INIT = 0;
    int VALUE_RETURNED = 1;
    int GOAL_INST = 2;
    int FIELD_REF = 3;

    /**
     * 处理器
     *
     * @param <G> 实体类泛型
     * @param gClass 实体类类型
     * @param p 解析器
     * @param args 附加参数
     * @return 解析消息
     * */
    <G> Msg<G> handle(Class<G> gClass, P p, Object ... args);

    /**
     * 处理器
     *
     * @param <G> 源类型
     * @param gClass 实体类类型
     * @param p 解析器
     * @return 解析消息
     * */
    default <G> Msg<G> handle(Class<G> gClass, P p) {
        return handle(gClass,p,null, null,null,null,null,null);
    }
}
