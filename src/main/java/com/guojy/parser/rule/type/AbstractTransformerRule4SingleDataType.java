package com.guojy.parser.rule.type;

import com.guojy.model.Msg;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.guojy.Assert.isNull;
import static com.guojy.Assert.notNull;
import static com.guojy.model.Msg.msg;
import static java.lang.String.format;

/**
 * 单数据源类型转换器基类
 *
 * <p> 创建时间：2019/2/19
 *
 * @author guojy
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTransformerRule4SingleDataType<O>
        implements TransformableAndRuleAddable<AbstractTransformerRule4SingleDataType> {

    @Override @SuppressWarnings("unchecked")
    public <G> AbstractTransformerRule4SingleDataType addRule4Transformer(
            Class<G> gClass,
            Class zlass,
            Function<?, G> ogFunction
    ) {
        if(notNull(customerTransformerRule.put(gClass,(Function<O,?>)ogFunction))) {
            log.debug("类 {} 的自定义转换规则已被替换", gClass);
        }
        return this;
    }
    @Override @SuppressWarnings("unchecked")
    public <G> Msg<G> transform(
            Object object,
            Class<G> gClass
    ) {
        if (isNull(object)||isNull(gClass)) {
            return transform(gClass);
        }
        Function<O,?> transformer;
        G g;
        // 1. 先去自定义的转换规则里查
        transformer = customerTransformerRule.get(gClass);
        if (notNull(transformer)) {
            g = (G)transformer.apply((O)object);
            if (notNull(g)) { return msg(g); }
        }
        // 2. 再去默认的规则里查
        transformer = defaultTransformerRule.get(gClass);
        if (notNull(transformer)) {
            g = (G) transformer.apply((O)object);
            if (notNull(g)){ return msg(g); }
        }
        // 3. 如果都查不到的话, 返回异常
        log.warn(
                "未能在默认转换规则和自定义转换规则中找到对应类型 {} 的转换规则或虽找到规则但解析过程异常", 
                gClass.getName());
        return msg(new IllegalStateException(format(
                "未能在默认转换规则和自定义转换规则中找到对应类型 %s 的转换规则或虽找到规则但解析过程异常", 
                gClass.getName())));
    }



    protected  <G> void addDefaultRule4Transformer(
            Class<G> gClass,
            Function<O,G> ogFunction
    ) {
        if (notNull(defaultTransformerRule.put(gClass,ogFunction))) {
            log.debug("类 {} 的默认转换规则已被替换", gClass);
        }
    }



    @Setter(AccessLevel.PROTECTED) @Getter(AccessLevel.PROTECTED)
    private Map<Class<?>,Function<O,?>> defaultTransformerRule
            = new HashMap<>(8);
    private Map<Class<?>,Function<O,?>> customerTransformerRule
            = new HashMap<>(4);
}