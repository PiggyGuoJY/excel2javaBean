package com.tkp.tkpole.starter.utils.parser.rule.type;

import com.tkp.tkpole.starter.utils.model.Msg;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.tkp.tkpole.starter.utils.Assert.isNull;
import static com.tkp.tkpole.starter.utils.Assert.notNull;
import static com.tkp.tkpole.starter.utils.model.Msg.msg;
import static java.lang.String.format;

/**
 * 单数据源类型转换器基类
 *
 * <p> 创建时间：2019/2/19
 *
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSingleDataTypeTransformerRule<O> extends AbstractDataTypeTransformerRule<AbstractSingleDataTypeTransformerRule> {

    private Map<Class<?>,Function<O,?>> defaultTransformRule = new HashMap<>(16);
    private Map<Class<?>,Function<O,?>> customerTransformRule = new HashMap<>(4);

    protected  <G> AbstractSingleDataTypeTransformerRule addDefaultDataTypeTransformRule(Class<G> gClass, Function<O, G> ogFunction) {
        if ( notNull(defaultTransformRule.put(gClass,ogFunction))) { log.info("类 {} 的默认转换规则已被替换", gClass); }
        return this;
    }

    @Override @SuppressWarnings("unchecked")
    public <G> AbstractSingleDataTypeTransformerRule addCustomerDataTypeTransformRule(Class<G> gClass, Class zlass, Function<?, G> ogFunction) {
        if( notNull(customerTransformRule.put(gClass,(Function<O,?>)ogFunction))) { log.info("类 {} 的自定义转换规则已被替换", gClass);}
        return this;
    }

    @Override @SuppressWarnings("unchecked")
    public <G> Msg<G> transform(Object object, Class<G> gClass) {
        G g = null;
        if ( isNull(object)) { return msg( g);}
        // 1. 先去自定义的转换规则里查
        Function<O,?> transformer;
        if ( notNull( transformer = customerTransformRule.get( gClass)) && notNull( g = ( G)transformer.apply( (O)object))) { return msg( g); }
        // 2. 再去默认的规则里查
        if ( notNull( transformer = defaultTransformRule.get( gClass)) && notNull( g = ( G) transformer.apply( (O)object))) { return msg( g); }
        // 3. 如果都查不到的话, 返回异常
        log.warn( " 未能在默认规则集合和自定义规则集合中找到对应类型 {} 的规则或解析过程异常", gClass.getName());
        return msg(new IllegalStateException(format( " 未能在默认规则集合和自定义规则集合中找到对应类型[%s]的规则或解析过程异常", gClass.getName())));
    }
}
