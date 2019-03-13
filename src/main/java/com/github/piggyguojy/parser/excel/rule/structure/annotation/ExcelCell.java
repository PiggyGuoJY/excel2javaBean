
package com.github.piggyguojy.parser.excel.rule.structure.annotation;

import com.github.piggyguojy.parser.rule.structure.inherit.OverrideRule;

import java.lang.annotation.*;

/**
 * 单元格类型
 *
 * 备注:
 *  1. 该注解可以配合{@code ExcelBean}使用, 也可以单独使用
 *
 * <p> 创建时间：2018/10/29
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
@Documented
@Retention( RetentionPolicy.RUNTIME)
@Target( {ElementType.FIELD, ElementType.TYPE})
public @interface ExcelCell {

    /**
     * @return sheet页名称;不设置时使用第一页;可继承(优先使用sheet(当其存在时))
     * */
    String sheetName() default "";

    /**
     * @return sheet页号码(从1开始);不设置时使用1;可继承
     * */
    int sheet() default -1;

    /**
     * @return 单元格所在列的列名(优先使用column(当其存在时))
     * */
    String columnName() default  "";

    /**
     * @return 单元格所在的列位置(以1开始)
     * */
    int column() default -1;

    /**
     * @return 单元格所在的行位置(以1开始)
     * */
    int row() default -1;

    /**
     * @return 单元格位置(总是以绝对定位对待里面的值, 这就意味着A1和$A$1都代表$A$1, 优先使用row, column和columnName的组合(当其存在且有效时))
     * */
    String address() default "";

    /**
     * @return 设置继承属性(默认冲突时优先使用子配置)
     * */
    OverrideRule overrideRule() default OverrideRule.PARENT_FORCE;

}
