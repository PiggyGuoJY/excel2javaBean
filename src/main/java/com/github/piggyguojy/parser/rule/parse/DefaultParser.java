
package com.github.piggyguojy.parser.rule.parse;

import com.github.piggyguojy.parser.rule.structure.StructureHandler;
import com.github.piggyguojy.parser.rule.type.TransformableAndRuleAddable;
import com.github.piggyguojy.util.Msg;
import com.github.piggyguojy.util.model.Params;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 默认实现解析器
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefaultParser<P extends DefaultParser>
        extends AbstractParser<P> {

    protected DefaultParser(
            StructureHandler<P> structureHandler,
            TransformableAndRuleAddable abstractDataTypeTransformerRule) {
        super(structureHandler, abstractDataTypeTransformerRule);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> Msg<T> beforeParse(Params params) { return Msg.msg(); }
    /**
     * {@inheritDoc}
     */
    @Override @SuppressWarnings("unchecked")
    protected <T> Msg<T> doParse(Params params) {
        Msg<?> msg = structureHandler.handle(
                (Class<T>) params.getZlass(),
                (P) params.getParser(),
                params.getArgs(),
                params.getReturnMsg());
        return (Msg<T>)msg;
    }
    /**
     * {@inheritDoc}
     */
    @Override @SuppressWarnings("unchecked")
    protected <T> Msg<T> afterParse(Params params) { return (Msg<T>)params.getReturnMsg(); }
}
