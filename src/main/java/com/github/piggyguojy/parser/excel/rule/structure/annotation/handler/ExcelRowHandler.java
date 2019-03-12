
package com.github.piggyguojy.parser.excel.rule.structure.annotation.handler;

import com.github.piggyguojy.ClassUtil;
import com.github.piggyguojy.Msg;
import com.github.piggyguojy.parser.excel.rule.parse.ExcelParser;
import com.github.piggyguojy.parser.excel.rule.structure.annotation.ExcelBean;
import com.github.piggyguojy.parser.excel.rule.structure.annotation.ExcelRow;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.piggyguojy.Assert.*;
import static java.lang.String.format;

/**
 * 程序员（guojy）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/2/15
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
@Slf4j @NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelRowHandler
        extends ExcelAnnotationHandler<ExcelRow> {

    @Override @SuppressWarnings("unchecked")
    public <G> Msg<?> onField(
            Class<G> gClass,
            ExcelRow excelRow,
            ExcelParser excelParser,
            Object ... args
    ) {
        onFieldHandler((Class<Collection>)gClass,excelRow,excelParser,args);
        return Msg.msg();
    }
    @Override
    public <G> Msg<?> onType(
            Class<G> gClass,
            ExcelRow excelRow,
            ExcelParser excelParser,
            Object... args
    ) {
        return onTypeHandler(gClass,excelRow,excelParser,args);
    }



    @Override
    protected Map<String, Object> getCustomerInheritableField() { return ExcelRowHandler.INHERITABLE_FIELD; }



    static { register(ExcelRow.class,new ExcelRowHandler());}



    @SuppressWarnings("unchecked")
    private <E, G extends Collection<E>> void onFieldHandler(
            Class<G> gClass,
            ExcelRow excelRow,
            ExcelParser excelParser,
            Object ... args
    ) {
        ExcelBean excelBeanParent = getAnnotationParent(ExcelBean.class,args);
        args[ANNOTATION_PARENT] = notNull(excelBeanParent) ? decideBiRule(excelRow, excelBeanParent, excelBeanParent.overrideRule()) : excelRow;
        if ( !Collection.class.isAssignableFrom(gClass)) {
            log.warn("属性的类型不是 {} 的实现, 不予解析", Collection.class.getCanonicalName());
            return;
        }
        Class<E> rawClass = (Class<E>)ClassUtil.getGenericClass((Field) args[FIELD_REF],0);
        Msg<Collection<E>> msg = onTypeHandler(rawClass, notNull(rawClass)?rawClass.getDeclaredAnnotation(ExcelRow.class):null, excelParser,args);
        if ( msg.isException()) { return; }
        ClassUtil.set((Field) args[FIELD_REF], args[GOAL_INST], msg.getT());
    }
    @SuppressWarnings("unchecked")
    private <G> Msg<Collection<G>> onTypeHandler(
            Class<G> gClass,
            ExcelRow excelRow,
            ExcelParser excelParser,
            Object ... args
    ) {
        if ( isNull(gClass)) { return Msg.msg(new IllegalStateException("无法获取容器泛型参数"));}
        ExcelRow excelRowParent = getAnnotationParent(ExcelRow.class,args);
        final ExcelRow finalExcelRow = notNull(excelRowParent) ? decideRule(excelRow, excelRowParent, excelRowParent.overrideRule()) : excelRow;
        if ( isNull(finalExcelRow)) { return Msg.msg(new IllegalArgumentException(format(
                "类型 %s 应该使用注解 %s 标注", gClass.getCanonicalName(), ExcelRow.class.getCanonicalName()))); }
        final Sheet sheet = ExcelParser.ExcelParserHelper.decideSheet(finalExcelRow.sheet(), finalExcelRow.sheetName(), excelParser.getWorkbook());
        if ( isNull( sheet)) { return Msg.msg( new IllegalStateException("无法找到Sheet")); }
        Map<String,Integer> mapping = ExcelRowHandlerHelper.getMapFromExcelRow(gClass,finalExcelRow);
        Collection<Object> objectCollection = new LinkedList<>();
        for (int rowIndex = finalExcelRow.rowBegin(), expectantRowEnd = finalExcelRow.rowEnd(), rowEnd = expectantRowEnd<0?Integer.MAX_VALUE:expectantRowEnd; rowIndex<=rowEnd; rowIndex++) {
            // todo ... 也可以不这么停止, 具体可以看以后的情况
            Row row = sheet.getRow( rowIndex-1);
            if ( isNull( row )) { log.warn("检测到空行, 停止解析..."); break; }
            // 准备泛型参数的实例
            final Object gInstance = ClassUtil.instanceT( gClass);
            if ( isNull( gInstance)) {
                objectCollection.clear();
                return Msg.msg(new IllegalStateException("无法通过反射实例化泛型参数的实例"));
            }
            final int finalRowIndex = rowIndex;
            mapping.forEach( (fieldNameSelf, columnNoSelf) -> {
                try {
                    Field field = gClass.getDeclaredField( fieldNameSelf);
                    Cell cell = ExcelParser.ExcelParserHelper.decideCell(columnNoSelf, finalRowIndex, sheet);
                    if ( isNull( cell)) { log.warn("未能获取到Cell"); return;}
                    ClassUtil.set(field, gInstance, excelParser.transform(cell, field.getType()).getT());
                } catch ( NoSuchFieldException e) { log.error(e.getMessage(), e);}
            });
            objectCollection.add( gInstance);

        }
        return Msg.msg((Collection<G>) objectCollection);
    }
    private static class ExcelRowHandlerHelper {
        private static final String REGEX_EXCEL_ROW_MAP = "^(?!\\d)(([A-Z]+)->[_$a-zA-Z0-9]+;)*(([A-Z]+)->[_$a-zA-Z0-9]+(?=$))$";
        private static final String SEPARATOR = ";";
        private static final String MAPPER = "->";

        private static <G> Map<String,Integer> getMapFromExcelRow(Class<G> gClass, ExcelRow excelRow) {
            String map = excelRow.map().trim().replaceAll("\\s","");
            if ( map.matches(REGEX_EXCEL_ROW_MAP)) {
                return Stream.of( map.split( SEPARATOR)).map(mapsSelf -> mapsSelf.split( MAPPER)).collect( Collectors.toMap(mapsSelf -> mapsSelf[1], mapsSelf -> ExcelParser.ExcelParserHelper.decideColumnNo( mapsSelf[0])));
            }
            log.warn( "映射关系解析失败: 来自属性上ExcelRow的map {} 不符合语法, 尝试从属性类型上获取配置", map);
            return getMapFromExcelRow(gClass);
        }
        private static <G> Map<String,Integer> getMapFromExcelRow(Class<G> gClass) {
            ExcelRow excelRow = gClass.getDeclaredAnnotation(ExcelRow.class);
            if ( notNull(excelRow)) {
                String map = excelRow.map().trim().replaceAll("\\s","");
                if ( map.matches(REGEX_EXCEL_ROW_MAP)) {
                    return Stream.of( map.split( SEPARATOR)).map(mapsSelf -> mapsSelf.split( MAPPER)).collect( Collectors.toMap(mapsSelf -> mapsSelf[1], mapsSelf -> ExcelParser.ExcelParserHelper.decideColumnNo( mapsSelf[0])));
                } else {
                    log.warn( "映射关系解析失败: 来自属性类型上ExcelRow的map {} 不符合语法, 尝试使用默认配置", map);
                    val result = ImmutableMap.<String,Integer>builder();
                    int columnPointer = ExcelParser.ExcelParserHelper.decideColumnNo(excelRow.columnNameBegin(), excelRow.columnBegin());
                    for(Field field : gClass.getDeclaredFields()) {
                        ExcelRow.Skip skip = field.getDeclaredAnnotation(ExcelRow.Skip.class);
                        if ( notNull(skip)) {
                            columnPointer = changeColumnPointrt(columnPointer,skip);
                        } else {
                            result.put(field.getName(), columnPointer);
                            columnPointer++;
                        }
                    }
                    return result.build();
                }
            } else {
                log.warn( "映射关系解析失败: {} 未使用@ExcelRow标注, 不能取得映射关系", gClass.getCanonicalName());
                return Collections.emptyMap();
            }
        }
        private static int changeColumnPointrt(int columnPointer, ExcelRow.Skip skip) {
            return notNul(skip.skipTo()) ?
                    ExcelParser.ExcelParserHelper.decideColumnNo(skip.skipTo()) :
                    ( skip.skip()>0 ? columnPointer + skip.skip() : columnPointer);
        }
    }
    private static final Map<String,Object> INHERITABLE_FIELD =
            ImmutableMap.<String,Object>builder()
                    .put("sheetName","")
                    .put("sheet",-1)
                    .put("rowBegin", -1)
                    .put("rowEnd", -1)
                    .put("map", "")
                    .put("columnBegin", -1)
                    .put("columnNameBegin", "")
                    .build();
}