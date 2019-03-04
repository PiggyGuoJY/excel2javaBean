package com.guojy.parser.rule.parse;

import com.guojy.model.Msg;
import com.guojy.parser.rule.structure.StructureHandler;
import com.guojy.parser.rule.type.TransformableAndRuleAddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 默认实现解析器
 *
 * <p> 创建时间：2019/2/23
 *
 * @author guojy
 * @version 1.0
 * */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefaultParser<P extends DefaultParser> extends AbstractParser<P> {

    protected DefaultParser(
            StructureHandler<P> structureHandler,
            TransformableAndRuleAddable abstractDataTypeTransformerRule) {
        super(structureHandler, abstractDataTypeTransformerRule);
    }
    @Override
    protected <T> Msg<T> beforeParse(Object... args) { return Msg.msg(); }
    @Override @SuppressWarnings("unchecked")
    protected <T> Msg<T> doParse(Object... args) {
        Msg<?> msg = structureHandler.handle(
                (Class<T>) args[GOAL_CLASS],
                (P) args[PARSER_SELF],
                args[ARGS_INIT],
                args[VALUE_RETURNED]);
        return (Msg<T>)msg;
    }
    @Override @SuppressWarnings("unchecked")
    protected <T> Msg<T> afterParse(Object... args) { return (Msg<T>)args[VALUE_RETURNED]; }
}
