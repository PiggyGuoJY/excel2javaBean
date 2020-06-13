package com.github.piggyguojy.parser.excel.structure.annotation;

import com.github.piggyguojy.parser.rule.structure.inherit.OverrideRule;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 行类型
 *
 * 备注: 1. 该注解可以配合{@code ExcelBean}使用, 也可以单独使用
 *
 * <p> 创建时间：2018/10/29
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ExcelRow {

  /**
   * @return sheet页名称;不设置时使用第一页;可继承(优先使用sheet(当其存在时))
   */
  String sheetName() default "";

  /**
   * @return sheet页号码(从1开始);不设置时使用1;可继承
   */
  int sheet() default -1;


  /**
   * @return 起始行行数(以1开始)
   */
  int rowBegin() default -1;

  /**
   * @return 终止行行数(以1开始, - 1表示直至行内容为NULL)
   */
  int rowEnd() default -1;

  /**
   * @return 属性名到列的映射
   */
  String map() default "";

  /**
   * @return 设置继承属性(默认冲突时优先使用子配置)
   */
  OverrideRule overrideRule() default OverrideRule.PARENT_FORCE;


  /**
   * @return 设置起始列
   */
  int columnBegin() default -1;

  /**
   * @return 设置起始列名
   */
  String columnNameBegin() default "";

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD})
  @interface Skip {

    /**
     * @return 跳至(优先使用)
     */
    String skipTo() default "";

    /**
     * @return 跳过
     */
    int skip() default -1;
  }
}
