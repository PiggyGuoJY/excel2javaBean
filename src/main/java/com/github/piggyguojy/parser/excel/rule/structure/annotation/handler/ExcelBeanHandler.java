
package com.github.piggyguojy.parser.excel.rule.structure.annotation.handler;

import com.github.piggyguojy.ClassUtil;
import com.github.piggyguojy.Msg;
import com.github.piggyguojy.parser.excel.rule.parse.ExcelParser;
import com.github.piggyguojy.parser.excel.rule.structure.annotation.ExcelBean;
import com.github.piggyguojy.parser.excel.rule.structure.annotation.ExcelCell;
import com.github.piggyguojy.parser.excel.rule.structure.annotation.ExcelColumn;
import com.github.piggyguojy.parser.excel.rule.structure.annotation.ExcelRow;
import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.piggyguojy.Assert.isNull;
import static com.github.piggyguojy.Assert.notNull;
import static com.github.piggyguojy.Msg.msg;

/**
 * 程序员（guojy）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/2/14
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelBeanHandler extends ExcelAnnotationHandler<ExcelBean> {

    @Override @SuppressWarnings("unchecked")
    public <G> Msg<?> onType(
            Class<G> gClass,
            ExcelBean excelBean,
            ExcelParser excelParser,
            Object ... args
    ) {
        // 1.实例化容器
        final G g;
        try { g = gClass.newInstance(); } catch ( IllegalAccessException | InstantiationException e) {
            log.error( e.getMessage(), e);
            return msg( e);
        }
        // 2.ExcelBean注解只有可能从上一个ExcelBean.Nested上继承规则sheetName和sheet
        final ExcelBean.Nested nestedParent = getAnnotationParent(ExcelBean.Nested.class, args);
        final ExcelBean finalExcelBean = notNull(nestedParent) ? AbstractAnnotationHandlerHelper.decideAnnotationRule(
                excelBean, nestedParent, INHERITABLE_FIELD, nestedParent.overideRule()) : excelBean;
        if ( notNull(nestedParent) && nestedParent.stepBy()>0) {
            ClassUtil.changeAnnotationFieldValue(finalExcelBean,"sheet",finalExcelBean.sheet()+nestedParent.stepBy());
        }
        // 3.当根据sheet和sheetName不能判断Sheet页时, 停止解析
        if ( isNull(ExcelParser.ExcelParserHelper.decideSheet(finalExcelBean.sheet(),finalExcelBean.sheetName(),excelParser.getWorkbook()))) {
            return msg();
        }
        // 4.保存不可变参数, 防止被篡改
        final Object[] immutableArgs = new Object[]{null,null};
        System.arraycopy(args,0,immutableArgs,0,2);
        // 5.遍历处理是gClass的属性(未使用ExcelBean.Nested的)
        Stream.of(gClass.getDeclaredFields())
                .parallel()
                // 处理含有特定注解的属性
                .filter(fieldSelf -> notNull(ClassUtil.getTheOnlyOneAnnotation(fieldSelf,ANNOTATIONS_ON_FIELD)))
                // 不处理标注ExcelBean.Skip.class的属性
                .filter(fieldSelf -> isNull(fieldSelf.getDeclaredAnnotation(ExcelBean.Skip.class)))
                // 最后处理标注ExcelBean.Nested.class的属性
                .filter(fieldSelf -> isNull(fieldSelf.getDeclaredAnnotation(ExcelBean.Nested.class)))
                .forEach(fieldSelf -> {
                    Class<? extends Annotation> annotationClass = ClassUtil.getTheOnlyOneAnnotation(fieldSelf,ANNOTATIONS_ON_FIELD);
                    // 具体根据注解分发
                    Msg<?> msg = getAnnotationHandlerRegistered(annotationClass).onField(
                            fieldSelf.getType(), fieldSelf.getDeclaredAnnotation(annotationClass), excelParser,
                            immutableArgs[ARGS_INIT],
                            immutableArgs[VALUE_RETURNED],
                            g,
                            fieldSelf,
                            finalExcelBean);
                    if ( msg.isException()) {  }
                });
        // 6.最后处理标注ExcelBean.Nested.class的属性
        Stream.of(gClass.getDeclaredFields())
                .filter(fieldSelf -> notNull(fieldSelf.getDeclaredAnnotation(ExcelBean.Nested.class)))
                .forEach(fieldSelf -> {
                    // 具体根据注解分发
                    Msg<?> msg = getAnnotationHandlerRegistered(ExcelBean.Nested.class).onField(
                            fieldSelf.getType(), fieldSelf.getDeclaredAnnotation(ExcelBean.Nested.class), excelParser,
                            immutableArgs[ARGS_INIT],
                            immutableArgs[VALUE_RETURNED],
                            g,
                            fieldSelf,
                            finalExcelBean);
                    if ( msg.isException()) {  }
                });
        // 7. 返回实例
        return msg(g);
    }



    static { register(ExcelBean.class, new ExcelBeanHandler()); }



    private static final Set<Class<? extends Annotation>> ANNOTATIONS_ON_FIELD = ImmutableSet.of(
            ExcelCell.class,
            ExcelRow.class,
            ExcelColumn.class,
            ExcelBean.Nested.class,
            ExcelBean.Skip.class);
}
