package com.guojy.parser.excel.rule.structure.annotation;

import com.guojy.parser.rule.structure.OverrideRule;
import com.tkp.tkpole.starter.utils.parser.rule.structure.OverrideRule;

import java.lang.annotation.*;

/**
 * 列类型
 *
 * <p> 创建时间：2018/10/29
 *
 * @author guojy24
 * @version 1.0
 * */
@Documented
@Retention( RetentionPolicy.RUNTIME)
@Target( {ElementType.FIELD,ElementType.TYPE})
public @interface ExcelColumn {

    /**
     * sheet页名称;不设置时使用第一页;可继承(优先使用sheet(当其存在时))
     * */
    String sheetName() default "";

    /**
     * sheet页号码(从1开始);不设置时使用1;可继承
     * */
    int sheet() default -1;

    /**
     * 起始列列数(以1开始)
     * */
    int columnBegin() default -1;

    /**
     * 终止行行数(以1开始, -1表示直至行内容为NULL)
     * */
    int columnEnd() default -1;

    /**
     * 起始列列名(优先使用columnBegin(当其存在时))
     * */
    String columnNameBegin() default "";
    /**
     * 终止列列名(优先使用columnEnd(当其存在时))
     * */
    String columnNameEnd() default "";

    /**
     * 属性名到列的映射
     * */
    String map() default  "";

    /**
     * 起始行(优先使用map(当其存在且有效时))
     * */
    int rowBegin() default -1;

    /**
     * 设置继承属性(默认冲突时优先使用子配置)
     * */
    OverrideRule overideRule() default OverrideRule.PARENT_FORCE;


    Class<?> after() default DefaultAfterRunnableImpl.class;
    Class<?> afterAll() default DefaultAfterRunnableImpl.class;

    @Retention( RetentionPolicy.RUNTIME)
    @Target( {ElementType.FIELD})
    @interface Skip{
        /**
         * 程序员（guojy24）很懒，关于这个属性，ta什么也没写╮(╯▽╰)╭
         * */
        int skip() default -1;
        /**
         * 程序员（guojy24）很懒，关于这个属性，ta什么也没写╮(╯▽╰)╭
         * */
        int skipTo() default -1;
    }

}
