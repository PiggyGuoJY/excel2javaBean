package com.github.piggyguojy.parser.excel.structure.annotation.handler;

import static com.github.piggyguojy.parser.rule.structure.annotation.AbstractAnnotationHandler.AbstractAnnotationHandlerHelper.decideAnnotationRule;
import static com.github.piggyguojy.util.Assert.isNull;
import static com.github.piggyguojy.util.Msg.msg;
import static java.lang.String.format;

import com.github.piggyguojy.parser.excel.parse.ExcelParser;
import com.github.piggyguojy.parser.excel.structure.annotation.ExcelBean;
import com.github.piggyguojy.parser.excel.structure.annotation.ExcelCell;
import com.github.piggyguojy.parser.excel.structure.annotation.ExcelColumn;
import com.github.piggyguojy.parser.excel.structure.annotation.ExcelRow;
import com.github.piggyguojy.parser.rule.structure.StructureHandler;
import com.github.piggyguojy.parser.rule.structure.annotation.AbstractAnnotationHandler;
import com.github.piggyguojy.parser.rule.structure.inherit.BiInheritableRule;
import com.github.piggyguojy.parser.rule.structure.inherit.Inheritable;
import com.github.piggyguojy.parser.rule.structure.inherit.OverrideRule;
import com.github.piggyguojy.util.ClassUtil;
import com.github.piggyguojy.util.Msg;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Excel注解形式处理器
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExcelAnnotationHandler<A extends Annotation>
    extends AbstractAnnotationHandler<A, ExcelParser>
    implements Inheritable<A>, BiInheritableRule<A, ExcelBean> {

  protected static final int ANNOTATION_PARENT = 4;
  protected static final Map<String, Object> INHERITABLE_FIELD
      = ImmutableMap.<String, Object>builder()
      .put("sheet", -1)
      .put("sheetName", "")
      .build();
  private static final ExcelAnnotationHandler EXCEL_ANNOTATION_HANDLER = new ExcelAnnotationHandler();
  private static final Set<Class<? extends Annotation>> ANNOTATIONS_ON_TYPE
      = ImmutableSet.of(ExcelCell.class, ExcelRow.class, ExcelColumn.class, ExcelBean.class);

  public static ExcelAnnotationHandler of() {
    return EXCEL_ANNOTATION_HANDLER;
  }

  @SuppressWarnings("unchecked")
  protected static <A extends Annotation> A getAnnotationParent(Class<A> aClass, Object[] args) {
    if (args != null && args.length >= 5 && args[ANNOTATION_PARENT] != null
        && ((Annotation) args[ANNOTATION_PARENT]).annotationType().equals(aClass)) {
      return (A) args[ANNOTATION_PARENT];
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public <G> Msg<G> handle(
      Class<G> gClass,
      ExcelParser excelParser,
      Object... args
  ) {
    Class<? extends Annotation> annotationClass = ClassUtil
        .getTheOnlyOneAnnotation(gClass, ANNOTATIONS_ON_TYPE);
    if (isNull(annotationClass)) {
      return msg(new IllegalArgumentException(format(
          "%s 应使用下列注解之一标注 %s",
          gClass.getCanonicalName(),
          ANNOTATIONS_ON_TYPE.toString())));
    }
    return getAnnotationHandlerRegistered(annotationClass).onType(
        gClass,
        gClass.getDeclaredAnnotation(annotationClass),
        excelParser,
        args[StructureHandler.ARGS_INIT],
        args[StructureHandler.VALUE_RETURNED]);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <G> Msg<?> onType(
      Class<G> gClass,
      A a,
      ExcelParser excelParser,
      Object... args
  ) {
    return msg(Msg.MsgError.ILLEGAL_STATE_SEGMENT_SHOULD_NOT_BE_ACCESSED.getE());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <G> Msg<?> onField(
      Class<G> gClass,
      A a,
      ExcelParser excelParser,
      Object... args
  ) {
    return msg(Msg.MsgError.ILLEGAL_STATE_SEGMENT_SHOULD_NOT_BE_ACCESSED.getE());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A decideRuleOnParentFirst(A son, A parent) {
    return decideAnnotationRule(son, parent, getCustomerInheritableField(),
        OverrideRule.PARENT_FIRST);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A decideRuleOnParentForce(A son, A parent) {
    return decideAnnotationRule(son, parent, getCustomerInheritableField(),
        OverrideRule.PARENT_FORCE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A decideRuleOnSonFirst(A son, A parent) {
    return decideAnnotationRule(son, parent, getCustomerInheritableField(), OverrideRule.SON_FIRST);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A decideRuleOnSonForce(A son, A parent) {
    return decideAnnotationRule(son, parent, getCustomerInheritableField(), OverrideRule.SON_FORCE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A decideBiRuleOnParentFirst(A son, ExcelBean parent) {
    return decideAnnotationRule(son, parent, INHERITABLE_FIELD, OverrideRule.PARENT_FIRST);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A decideBiRuleOnParentForce(A son, ExcelBean parent) {
    return decideAnnotationRule(son, parent, INHERITABLE_FIELD, OverrideRule.PARENT_FORCE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A decideBiRuleOnSonFirst(A son, ExcelBean parent) {
    return decideAnnotationRule(son, parent, INHERITABLE_FIELD, OverrideRule.SON_FIRST);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A decideBiRuleOnSonForce(A son, ExcelBean parent) {
    return decideAnnotationRule(son, parent, INHERITABLE_FIELD, OverrideRule.SON_FORCE);
  }

  protected Map<String, Object> getCustomerInheritableField() {
    return null;
  }
}