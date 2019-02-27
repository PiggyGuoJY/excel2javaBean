package com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.handler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.tkp.tkpole.starter.utils.model.Msg;
import com.tkp.tkpole.starter.utils.parser.excel.rule.parse.ExcelParser;
import com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelBean;
import com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelCell;
import com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelColumn;
import com.tkp.tkpole.starter.utils.parser.excel.rule.structure.annotation.ExcelRow;
import com.tkp.tkpole.starter.utils.parser.rule.structure.BiInheritableRule;
import com.tkp.tkpole.starter.utils.parser.rule.structure.Inheritable;
import com.tkp.tkpole.starter.utils.parser.rule.structure.OverrideRule;
import com.tkp.tkpole.starter.utils.parser.rule.structure.annotation.AbstractAnnotationHandler;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;

import static com.tkp.tkpole.starter.utils.Assert.isNul;
import static com.tkp.tkpole.starter.utils.Assert.isNull;
import static com.tkp.tkpole.starter.utils.model.Msg.msg;
import static java.lang.String.format;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 * 
 * <p> 创建时间：2019/2/15
 * 
 * @author guojy24
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ExcelAnnotationHandler<A extends Annotation>
        extends AbstractAnnotationHandler<A, ExcelParser>
        implements Inheritable<A>, BiInheritableRule<A, ExcelBean> {
    @Override @SuppressWarnings("unchecked")
    public <G> Msg<G> handle(Class<G> gClass, ExcelParser excelParser, Object... args) {
        Class<? extends Annotation> annotationClass = AbstractAnnotationHandlerHelper.getTheOnlyOneAnnotation(gClass,ANNOTATIONS_ON_TYPE);
        if ( isNull(annotationClass)) { return msg(new IllegalArgumentException(format("%s 应使用下列注解之一标注 %s", gClass.getCanonicalName(), ANNOTATIONS_ON_TYPE.toString()))); }
        return getAnnotationHandler(annotationClass).onType(gClass, gClass.getDeclaredAnnotation(annotationClass), excelParser, args[ARGS_INIT], args[VALUE_RETURNED]);
    }
    @Override
    public <G> Msg<?> onType(Class<G> gClass, A a, ExcelParser excelParser, Object... args) { return Msg.MsgError.IllegalState_PROC.getMsg(); }
    @Override
    public <G> Msg<?> onField(Class<G> gClass, A a, ExcelParser excelParser, Object... args) { return Msg.MsgError.IllegalState_PROC.getMsg(); }

    @Override
    public A decideRuleOnParentFirst(A son, A parent) {
        return AbstractAnnotationHandlerHelper.decideAnnotationRule(son, parent, getCustomerInheritableField(), OverrideRule.PARENT_FIRST);
    }
    @Override
    public A decideRuleOnParentForce(A son, A parent) {
        return AbstractAnnotationHandlerHelper.decideAnnotationRule(son, parent, getCustomerInheritableField(), OverrideRule.PARENT_FORCE);
    }
    @Override
    public A decideRuleOnSonFirst(A son, A parent) {
        return AbstractAnnotationHandlerHelper.decideAnnotationRule(son, parent, getCustomerInheritableField(), OverrideRule.SON_FIRST);
    }
    @Override
    public A decideRuleOnSonForce(A son, A parent) {
        return AbstractAnnotationHandlerHelper.decideAnnotationRule(son, parent, getCustomerInheritableField(), OverrideRule.SON_FORCE);
    }

    @Override
    public A decideBiRuleOnParentFirst(A son, ExcelBean parent) {
        return AbstractAnnotationHandlerHelper.decideAnnotationRule(son, parent, INHERITABLE_FIELD, OverrideRule.PARENT_FIRST);
    }
    @Override
    public A decideBiRuleOnParentForce(A son, ExcelBean parent) {
        return AbstractAnnotationHandlerHelper.decideAnnotationRule(son, parent, INHERITABLE_FIELD, OverrideRule.PARENT_FORCE);
    }
    @Override
    public A decideBiRuleOnSonFirst(A son, ExcelBean parent) {
        return AbstractAnnotationHandlerHelper.decideAnnotationRule(son, parent, INHERITABLE_FIELD, OverrideRule.SON_FIRST);
    }
    @Override
    public A decideBiRuleOnSonForce(A son, ExcelBean parent) {
        return AbstractAnnotationHandlerHelper.decideAnnotationRule(son, parent, INHERITABLE_FIELD, OverrideRule.SON_FORCE);
    }

    protected Map<String,Object> getCustomerInheritableField() {
        return null;
    }
    @SuppressWarnings("unchecked")
    protected static  <A extends Annotation> A getAnnotationParent(Class<A> aClass, Object[] args) {
        if ( args!=null && args.length>=5 && args[ANNOTATION_PARENT]!=null && ((Annotation)args[ANNOTATION_PARENT]).annotationType().equals(aClass)) {
            return (A )args[ANNOTATION_PARENT];
        } else {
            return null;
        }
    }
    protected static final int ANNOTATION_PARENT = 4;
    protected static final Map<String,Object> INHERITABLE_FIELD =
            ImmutableMap.<String,Object>builder()
                    .put("sheet", -1)
                    .put("sheetName", "")
                    .build();

    private static final Set<Class<? extends Annotation>> ANNOTATIONS_ON_TYPE = ImmutableSet.of(ExcelCell.class, ExcelRow.class, ExcelColumn.class, ExcelBean.class);
}