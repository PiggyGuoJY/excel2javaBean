package com.guojy.parser.rule.type;

import com.guojy.model.Msg;
import com.tkp.tkpole.starter.utils.model.Msg;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.tkp.tkpole.starter.utils.Assert.notNull;
import static com.tkp.tkpole.starter.utils.model.Msg.msg;

/**
 * 多数据源类型转换器基类
 * 
 * <p> 创建时间：2019/2/19
 * 
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractMultiDataTypeTransformerRule extends AbstractDataTypeTransformerRule<AbstractMultiDataTypeTransformerRule> {
    private Map<Class<?>, AbstractSingleDataTypeTransformerRule<?>> defaultTransformRule = new HashMap<>();
    private Map<Class<?>, AbstractSingleDataTypeTransformerRule<?>> customerTransformRule = new HashMap<>();

    protected abstract <O> AbstractSingleDataTypeTransformerRule<O> newOabstractSingleDataTypeTransformerRule();

    protected <O,G> AbstractMultiDataTypeTransformerRule addDefaultDataTypeTransformRule(Class<G> gClass, Class<O> oClass, Function<O, G> ogFunction) {
        AbstractSingleDataTypeTransformerRule<?> abstractSingleDataTypeTransformerRule = defaultTransformRule.get(oClass);
        if ( notNull(abstractSingleDataTypeTransformerRule)) {
            abstractSingleDataTypeTransformerRule.addCustomerDataTypeTransformRule(gClass,oClass,ogFunction);
        } else {
            abstractSingleDataTypeTransformerRule = newOabstractSingleDataTypeTransformerRule().addCustomerDataTypeTransformRule(gClass, oClass, ogFunction);
            customerTransformRule.put(oClass,abstractSingleDataTypeTransformerRule);
        }
        return this;
    }

    @Override
    public <G> AbstractMultiDataTypeTransformerRule addCustomerDataTypeTransformRule(Class<G> gClass, Class zlass, Function<?, G> ogFunction) {
        AbstractSingleDataTypeTransformerRule<?> abstractSingleDataTypeTransformerRule = customerTransformRule.get(zlass);
        if ( notNull(abstractSingleDataTypeTransformerRule)) {
            abstractSingleDataTypeTransformerRule.addCustomerDataTypeTransformRule(gClass,zlass,ogFunction);
        } else {
            abstractSingleDataTypeTransformerRule = newOabstractSingleDataTypeTransformerRule().addCustomerDataTypeTransformRule(gClass, zlass, ogFunction);
            customerTransformRule.put(zlass,abstractSingleDataTypeTransformerRule);
        }
        return this;
    }

    @Override
    public <G> Msg<G> transform(Object object, Class<G> gClass) {
        AbstractSingleDataTypeTransformerRule<?> abstractSingleDataTypeTransformerRule;
        Msg<G> gMsg = Msg.msg(new IllegalStateException(""));
        if ( notNull(abstractSingleDataTypeTransformerRule = customerTransformRule.get(object.getClass())) && !(gMsg = abstractSingleDataTypeTransformerRule.transform(object,gClass)).isException()) { return gMsg;}
        if ( notNull(abstractSingleDataTypeTransformerRule = defaultTransformRule.get(object.getClass())) && !(gMsg = abstractSingleDataTypeTransformerRule.transform(object,gClass)).isException()) { return gMsg;}
        return gMsg;
    }
}
