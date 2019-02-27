package com.tkp.tkpole.starter.utils.parser.rule.parse;

import com.tkp.tkpole.starter.utils.model.Msg;
import com.tkp.tkpole.starter.utils.parser.rule.structure.StructureHandler;
import com.tkp.tkpole.starter.utils.parser.rule.type.AbstractDataTypeTransformerRule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.tkp.tkpole.starter.utils.model.Msg.msg;

/**
 * 默认实现解析器
 *
 * <p> 创建时间：2019/2/23
 *
 * @author guojy24
 * @version 1.0
 * */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefaultParser<P extends DefaultParser> extends AbstractParser<P> {

    protected DefaultParser(
            StructureHandler<P> structureHandler,
            AbstractDataTypeTransformerRule abstractDataTypeTransformerRule) {
        super(structureHandler, abstractDataTypeTransformerRule);
    }

    @Override
    protected <T> Msg<T> beforeParse(Object... args) { return msg(); }
    @Override @SuppressWarnings("unchecked")
    protected <T> Msg<T> doParse(Object... args) {
        Msg<?> msg = structureHandler.handle((Class<T>) args[GOAL_CLASS], (P) args[PARSER_SELF], args[ARGS_INIT], args[VALUE_RETURNED]);
        return (Msg<T>)msg;
    }
    @Override @SuppressWarnings("unchecked")
    protected <T> Msg<T> afterParse(Object... args) { return (Msg<T>)args[VALUE_RETURNED]; }
}
