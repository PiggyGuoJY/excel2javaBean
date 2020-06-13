package com.github.piggyguojy.parser.rule.type;

import static com.github.piggyguojy.util.Assert.notNull;
import static com.github.piggyguojy.util.Msg.msg;

import com.github.piggyguojy.util.Msg;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 多数据源类型转换器基类
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * @see AbstractTransformerRule4SingleDataType
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTransformerRule4MultiDataType
    implements TransformableAndRuleAddable<AbstractTransformerRule4MultiDataType> {

  private Map<Class<?>, AbstractTransformerRule4SingleDataType<?>> defaultTransformerRule
      = new HashMap<>(8);
  private Map<Class<?>, AbstractTransformerRule4SingleDataType<?>> customerTransformerRule
      = new HashMap<>(4);

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public <G> AbstractTransformerRule4MultiDataType addRule4Transformer(
      Class<G> gClass,
      Class zlass,
      Function<?, G> ogFunction
  ) {
    AbstractTransformerRule4SingleDataType<?> abstractSingleDataTypeTransformerRule
        = customerTransformerRule.get(zlass);
    if (notNull(abstractSingleDataTypeTransformerRule)) {
      abstractSingleDataTypeTransformerRule.addRule4Transformer(gClass, zlass, ogFunction);
    } else {
      abstractSingleDataTypeTransformerRule
          = newTransformerRule4SingleDataType().addRule4Transformer(gClass, zlass, ogFunction);
      customerTransformerRule.put(zlass, abstractSingleDataTypeTransformerRule);
    }
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public <G> Msg<G> transform(
      Object object,
      Class<G> gClass
  ) {
    AbstractTransformerRule4SingleDataType<?> abstractSingleDataTypeTransformerRule;
    Msg<G> gMsg = msg(Msg.MsgError.ILLEGAL_STATE_INIT.getE());
    abstractSingleDataTypeTransformerRule = customerTransformerRule.get(object.getClass());
    if (notNull(abstractSingleDataTypeTransformerRule)) {
      gMsg = abstractSingleDataTypeTransformerRule.transform(object, gClass);
      if (!gMsg.isException()) {
        return gMsg;
      }
    }
    abstractSingleDataTypeTransformerRule = defaultTransformerRule.get(object.getClass());
    if (notNull(abstractSingleDataTypeTransformerRule)) {
      gMsg = abstractSingleDataTypeTransformerRule.transform(object, gClass);
      if (!gMsg.isException()) {
        return gMsg;
      }
    }
    return gMsg;
  }

  /**
   * 生成单数据源转换规则的一个实现
   *
   * @param <T> 泛型
   * @return AbstractTransformerRule4SingleDataType的实现
   */
  protected abstract <T extends AbstractTransformerRule4SingleDataType<?>> T newTransformerRule4SingleDataType();

  protected <O, G> AbstractTransformerRule4MultiDataType addDefaultRule4Transformer(
      Class<G> gClass,
      Class<O> oClass,
      Function<O, G> ogFunction
  ) {
    AbstractTransformerRule4SingleDataType<?> abstractTransformerRule4SingleDataType
        = defaultTransformerRule.get(oClass);
    if (notNull(abstractTransformerRule4SingleDataType)) {
      abstractTransformerRule4SingleDataType.addRule4Transformer(gClass, oClass, ogFunction);
    } else {
      abstractTransformerRule4SingleDataType =
          newTransformerRule4SingleDataType().addRule4Transformer(gClass, oClass, ogFunction);
      defaultTransformerRule.put(oClass, abstractTransformerRule4SingleDataType);
    }
    return this;
  }
}
