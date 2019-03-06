package com.github.piggyguojy.parser.excel.rule.structure.annotation.handler;

import com.github.piggyguojy.Assert;
import com.github.piggyguojy.ClassUtil;
import com.github.piggyguojy.model.Msg;
import com.github.piggyguojy.parser.excel.rule.parse.ExcelParser;
import com.github.piggyguojy.parser.rule.structure.StructureHandler;
import com.github.piggyguojy.parser.rule.structure.annotation.AbstractAnnotationHandler;
import com.google.common.collect.ImmutableMap;
import com.github.piggyguojy.ClassUtil;
import com.github.piggyguojy.model.Msg;
import com.github.piggyguojy.parser.excel.rule.parse.ExcelParser;
import com.github.piggyguojy.parser.excel.rule.structure.annotation.ExcelBean;
import com.github.piggyguojy.parser.excel.rule.structure.annotation.ExcelColumn;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.piggyguojy.Assert.isNull;
import static com.github.piggyguojy.Assert.notNull;
import static java.lang.String.format;

/**
 * 程序员（guojy）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/2/15
 *
 * @author guojy
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelColumnHandler extends ExcelAnnotationHandler<ExcelColumn> {
    @Override @SuppressWarnings("unchecked")
    public <G> Msg<?> onField(Class<G> gClass, ExcelColumn excelColumn, ExcelParser excelParser, Object ... args) {
        onFieldHandler((Class<Collection>)gClass, excelColumn, excelParser, args);
        return Msg.msg();
    }
    @Override
    public <G> Msg<?> onType(Class<G> gClass, ExcelColumn excelColumn, ExcelParser excelParser, Object... args) {
        return onTypeHandler(gClass, excelColumn, excelParser, args);
    }
    @Override
    protected Map<String, Object> getCustomerInheritableField() { return ExcelColumnHandler.INHERITABLE_FIELD; }

    static { AbstractAnnotationHandler.register(ExcelColumn.class,new ExcelColumnHandler());}

    @SuppressWarnings("unchecked")
    private <E, G extends Collection<E>> void onFieldHandler(Class<G> gClass, ExcelColumn excelColumn, ExcelParser excelParser, Object ... args) {
        ExcelBean excelBeanParent = getAnnotationParent(ExcelBean.class,args);
        args[ANNOTATION_PARENT] = Assert.notNull(excelBeanParent) ? decideBiRule(excelColumn, excelBeanParent, excelBeanParent.overrideRule()) : excelColumn;
        if ( !Collection.class.isAssignableFrom(gClass)) {
            log.warn("属性的类型不是 {} 的实现, 不予解析", Collection.class.getCanonicalName());
            return;
        }
        Class<E> rawClass = (Class<E>) ClassUtil.getGenericClass((Field) args[StructureHandler.FIELD_REF],0);
        Msg<Collection<E>> msg = onTypeHandler(rawClass, Assert.notNull(rawClass)?rawClass.getDeclaredAnnotation(ExcelColumn.class):null,excelParser,args);
        if ( msg.isException()) { return; }
        ClassUtil.set((Field) args[StructureHandler.FIELD_REF], args[StructureHandler.GOAL_INST], msg.getT());
    }
    @SuppressWarnings("unchecked")
    private <G> Msg<Collection<G>> onTypeHandler(Class<G> gClass, ExcelColumn excelColumn, ExcelParser excelParser, Object ... args) {
        if ( Assert.isNull(gClass)) { return Msg.msg(new IllegalStateException("无法获取容器泛型参数"));}
        ExcelColumn excelColumnParent = getAnnotationParent(ExcelColumn.class,args);
        excelColumn = Assert.notNull(excelColumnParent) ? decideRule(excelColumn, excelColumnParent, excelColumnParent.overrideRule()) : excelColumn;
        if ( Assert.isNull(excelColumn)) {
            return Msg.msg(
                    new IllegalArgumentException(
                            format("类型 %s 应该使用注解 %s 标注", gClass.getCanonicalName(), ExcelColumn.class.getCanonicalName())));
        }
        final Sheet sheet = ExcelParser.ExcelParserHelper.decideSheet(excelColumn.sheet(), excelColumn.sheetName(), excelParser.getWorkbook());
        if ( Assert.isNull( sheet)) { return Msg.msg( new IllegalStateException("无法找到Sheet")); }
        Map<String,Integer> mapping = ExcelColumnHandler.ExcelColumnHandlerHelper.getMapFromExcelColumn(gClass,excelColumn);
        Collection<Object> objectCollection = new LinkedList<>();
        for (int columnIndex = ExcelParser.ExcelParserHelper.decideColumnNo(excelColumn.columnNameBegin(), excelColumn.columnBegin()),
             expectantColumnEnd = ExcelParser.ExcelParserHelper.decideColumnNo(excelColumn.columnNameEnd(), excelColumn.columnEnd()),
             columnEnd = expectantColumnEnd<0?Integer.MAX_VALUE:expectantColumnEnd; columnIndex<=columnEnd; columnIndex++) {
//            // todo ... 也可以不这么停止, 具体可以看以后的情况
            // 准备泛型参数的实例
            final Object gInstance = ClassUtil.instanceT( gClass);
            if ( Assert.isNull( gInstance)) {
                objectCollection.clear();
                return Msg.msg(new IllegalStateException("无法通过反射实例化泛型参数的实例"));
            }
            final int finalColumnIndex = columnIndex;
            mapping.forEach( (fieldNameSelf, rowNoSelf) -> {
                try {
                    Field field = gClass.getDeclaredField( fieldNameSelf);
                    Cell cell = ExcelParser.ExcelParserHelper.decideCell(finalColumnIndex, rowNoSelf, sheet);
                    if ( Assert.isNull( cell)) { log.warn("未能获取到Cell"); return;}
                    ClassUtil.set(field, gInstance, excelParser.transform(cell, field.getType()).getT());
                } catch ( NoSuchFieldException e) { log.error(e.getMessage(), e);}
            });
            objectCollection.add( gInstance);

        }
        return Msg.msg((Collection<G>) objectCollection);
    }

    private static class ExcelColumnHandlerHelper {
        private static final String REGEX_EXCEL_COLUMN_MAP = "^(?=\\d)(([0-9]+)->[_$a-zA-Z0-9]+;)*(([0-9]+)->[_$a-zA-Z0-9]+(?=$))$";
        private static final String SEPARATOR = ";";
        private static final String MAPPER = "->";

        private static <G> Map<String,Integer> getMapFromExcelColumn(Class<G> gClass, ExcelColumn excelColumn) {
            String map = excelColumn.map().trim().replaceAll("\\s","");
            if ( map.matches(REGEX_EXCEL_COLUMN_MAP)) {
                return Stream.of( map.split( SEPARATOR)).map(mapsSelf -> mapsSelf.split( MAPPER)).collect( Collectors.toMap(mapsSelf -> mapsSelf[1], mapsSelf -> ExcelParser.ExcelParserHelper.decideColumnNo( mapsSelf[0])));
            }
            log.warn( "映射关系解析失败: 来自属性上ExcelColumn的map {} 不符合语法, 尝试从属性类型上获取配置", map);
            return getMapFromExcelColumn(gClass);
        }
        private static <G> Map<String,Integer> getMapFromExcelColumn(Class<G> gClass) {
            ExcelColumn excelColumn = gClass.getDeclaredAnnotation(ExcelColumn.class);
            if ( Assert.notNull(excelColumn)) {
                String map = excelColumn.map().trim().replaceAll("\\s","");
                if ( map.matches(REGEX_EXCEL_COLUMN_MAP)) {
                    return Stream.of( map.split( SEPARATOR)).map(mapsSelf -> mapsSelf.split( MAPPER)).collect( Collectors.toMap(mapsSelf -> mapsSelf[1], mapsSelf -> ExcelParser.ExcelParserHelper.decideColumnNo( mapsSelf[0])));
                } else {
                    log.warn( "映射关系解析失败: 来自属性类型上ExcelColumn的map {} 不符合语法, 尝试使用默认配置", map);
                    val result = ImmutableMap.<String,Integer>builder();
                    int rowPointer = excelColumn.rowBegin() > 0 ? excelColumn.rowBegin() : 1;
                    for(Field field : gClass.getDeclaredFields()) {
                        ExcelColumn.Skip skip = field.getDeclaredAnnotation(ExcelColumn.Skip.class);
                        if ( Assert.notNull(skip)) {
                            rowPointer = changeRowPointrt(rowPointer,skip);
                        } else {
                            result.put(field.getName(), rowPointer);
                            rowPointer++;
                        }
                    }
                    return result.build();
                }
            } else {
                log.warn( "映射关系解析失败: {} 未使用@ExcelColumn标注, 不能取得映射关系", gClass.getCanonicalName());
                return Collections.emptyMap();
            }
        }
        private static int changeRowPointrt(int rowPointer, ExcelColumn.Skip skip) {
            return skip.skipTo() > 0 ?
                    skip.skipTo() :
                    ( skip.skip()>0 ? rowPointer + skip.skip() : rowPointer );
        }
    }
    private static final Map<String,Object> INHERITABLE_FIELD =
            ImmutableMap.<String,Object>builder()
                    .put("sheetName","")
                    .put("sheet",-1)
                    .put("columnBegin", -1)
                    .put("columnEnd", -1)
                    .put("columnNameBegin", "")
                    .put("columnNameEnd", "")
                    .put("map", "")
                    .put("rowBegin", -1)
                    .build();
}
