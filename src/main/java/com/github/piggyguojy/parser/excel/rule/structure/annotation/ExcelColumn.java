
package com.github.piggyguojy.parser.excel.rule.structure.annotation;

import com.github.piggyguojy.parser.rule.structure.OverrideRule;

import java.lang.annotation.*;

/**
 * 列类型
 *
 * <p> 创建时间：2018/10/29
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
@Documented
@Retention( RetentionPolicy.RUNTIME)
@Target( {ElementType.FIELD,ElementType.TYPE})
public @interface ExcelColumn {

    /**
     * @return sheet页名称;不设置时使用第一页;可继承(优先使用sheet(当其存在时))
     * */
    String sheetName() default "";

    /**
     * @return sheet页号码(从1开始);不设置时使用1;可继承
     * */
    int sheet() default -1;

    /**
     * @return 起始列列数(以1开始)
     * */
    int columnBegin() default -1;

    /**
     * @return 终止行行数(以1开始, -1表示直至行内容为NULL)
     * */
    int columnEnd() default -1;

    /**
     * @return 起始列列名(优先使用columnBegin(当其存在时))
     * */
    String columnNameBegin() default "";
    /**
     * @return 终止列列名(优先使用columnEnd(当其存在时))
     * */
    String columnNameEnd() default "";

    /**
     * @return 属性名到列的映射
     * */
    String map() default  "";

    /**
     * @return 起始行(优先使用map(当其存在且有效时))
     * */
    int rowBegin() default -1;

    /**
     * @return 设置继承属性(默认冲突时优先使用子配置)
     * */
    OverrideRule overrideRule() default OverrideRule.PARENT_FORCE;



    @Retention( RetentionPolicy.RUNTIME)
    @Target( {ElementType.FIELD})
    @interface Skip{
        /**
         * @return 程序员（guojy）很懒，关于这个属性，ta什么也没写╮(╯▽╰)╭
         * */
        int skip() default -1;
        /**
         * @return 程序员（guojy）很懒，关于这个属性，ta什么也没写╮(╯▽╰)╭
         * */
        int skipTo() default -1;
    }

}
