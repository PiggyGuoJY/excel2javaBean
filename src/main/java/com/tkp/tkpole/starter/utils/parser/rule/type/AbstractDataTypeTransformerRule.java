package com.tkp.tkpole.starter.utils.parser.rule.type;

import com.tkp.tkpole.starter.utils.model.Msg;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;

/**
 * 类型转换基类
 *
 * <p> 创建时间：2019/2/1
 *
 * @author guojy24
 * @version 1.0
 * */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractDataTypeTransformerRule<D extends AbstractDataTypeTransformerRule> implements Transformable {
    /**
     * 增加自定义规则
     *
     * @param <G> 目标泛型
     * @param gClass 目标类型
     * @param zlass 源类型
     * @param ogFunction 转换函数
     * @return 转换器
     * */
    public abstract <G> D addCustomerDataTypeTransformRule(Class<G> gClass, Class zlass, Function<?,G> ogFunction);
}
