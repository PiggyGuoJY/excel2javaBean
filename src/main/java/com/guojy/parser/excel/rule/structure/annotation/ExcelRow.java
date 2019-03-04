package com.guojy.parser.excel.rule.structure.annotation;

import com.guojy.parser.rule.structure.OverrideRule;

import java.lang.annotation.*;

/**
 * 行类型
 *
 * 备注:
 *  1. 该注解可以配合{@code ExcelBean}使用, 也可以单独使用
 *
 * <p> 创建时间：2018/10/29
 *
 * @author guojy
 * @version 1.0
 * */
@Documented
@Retention( RetentionPolicy.RUNTIME)
@Target( {ElementType.FIELD, ElementType.TYPE})
public @interface ExcelRow {

    /**
     * sheet页名称;不设置时使用第一页;可继承(优先使用sheet(当其存在时))
     * */
    String sheetName() default "";

    /**
     * sheet页号码(从1开始);不设置时使用1;可继承
     * */
    int sheet() default -1;

    /**
     * 起始行行数(以1开始)
     * */
    int rowBegin() default -1;

    /**
     * 终止行行数(以1开始, -1表示直至行内容为NULL)
     * */
    int rowEnd() default -1;

    /**
     * 属性名到列的映射
     * */
    String map() default  "";

    /**
     * 设置继承属性(默认冲突时优先使用子配置)
     * */
    OverrideRule overrideRule() default OverrideRule.PARENT_FORCE;


    int columnBegin() default -1;
    String columnNameBegin() default "";
    @Retention( RetentionPolicy.RUNTIME)
    @Target( {ElementType.FIELD})
    @interface Skip{
        /**
         * 优先使用
         * */
        String skipTo() default "";
        int skip() default -1;
    }
}
