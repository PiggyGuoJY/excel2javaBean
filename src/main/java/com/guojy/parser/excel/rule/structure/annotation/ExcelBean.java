package com.guojy.parser.excel.rule.structure.annotation;

import com.guojy.parser.rule.structure.OverrideRule;

import java.lang.annotation.*;

/**
 * 复合类型
 *
 * 备注:
 *  1. 类属性应该使用{@code ExcelCell}, {@code ExcelColumn}, {@code ExcelRow}或{@code ExcelBean.Nested}标注, 否则将跳过改属性
 *  2. 使用{@code ExcelBean.Skip}修饰的类属性的效果同1的否定情况
 *
 * <p> 创建时间：2018/10/29
 *
 * @author guojy
 * @version 1.0
 * */
@Documented
@Retention( RetentionPolicy.RUNTIME)
@Target( {ElementType.TYPE})
public @interface ExcelBean {

    /**
     * sheet页名称;不设置时使用第一页;可继承(优先使用sheet(当其存在时))
     * */
    String sheetName() default "";

    /**
     * sheet页号码(从1开始);不设置时使用1;可继承
     * */
    int sheet() default -1;

    /**
     * 本JavaBean是否允许作为属性被嵌套在其他使用{@code ExcelBean}注解标注的JavaBean中, 默认不允许; 本注解影响{@code ExcelBean.Nested}的使用
     * */
    boolean isNestable() default false;

    /**
     * 设置继承属性(默认冲突时优先使用子配置)
     * */
    OverrideRule overrideRule() default OverrideRule.PARENT_FORCE;


    /**
     * 忽略特定的属性
     *
     * <p> 创建时间：2019/2/13
     *
     * @author guojy
     * @version 1.0
     * */
    @Retention( RetentionPolicy.RUNTIME)
    @Target( {ElementType.FIELD})
    @interface Skip { }

    /**
     * 申明特定属性是内嵌的
     *
     * 备注:
     *  1. 该注解要求必须和{@code ExcelBean}同时出现(单独出现不生效)
     *  2. 被本注解标注的属性的类型定义必须使用{@code ExcelBean}标注且其isNestable属性为true(否则本注解不生效)
     *  3. 由形式
     *      (@ExcelBean)bean{ (@ExcelBean.Nested)field} -> (@ExcelBean)bean2{ (@ExcelBean.Nested)field} -> ...
     *     确定的关系将形成一条解析链, 相同的解析过程将递归调用
     *
     * <p> 创建时间：2019/2/13
     *
     * @author guojy
     * @version 1.0
     * */
    @Retention( RetentionPolicy.RUNTIME)
    @Target( {ElementType.FIELD})
    @interface Nested {
        /**
         * sheet页名称;不设置时使用外层{@code ExcelBean}的sheetName;可继承(优先使用sheet(当其存在时))
         * */
        String sheetName() default "";

        /**
         * sheet页号码(从1开始);不设置时使用外层{@code ExcelBean}的sheet;可继承
         * */
        int sheet() default -1;

        /**
         * 使用步进值确定下一个类型{@code ExcelBean}的sheet(前提是下一个的未设置, 否则子配置不继承)
         * */
        int stepBy() default -1;
        /**
         * 设置继承属性(默认冲突时优先使用子配置)
         * */
        OverrideRule overideRule() default OverrideRule.PARENT_FORCE;
    }
}
