package com.github.piggyguojy.parser.rule.structure.inherit;

/**
 * 继承规则
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * @see BiInheritableRule
 * @see Inheritable
 */
public enum OverrideRule {

  /**
   * 当配置冲突时, 优先使用父配置
   */
  PARENT_FIRST,
  /**
   * 强制使用父配置
   */
  PARENT_FORCE,
  /**
   * 当配置冲突时, 优先使用子配置
   */
  SON_FIRST,
  /**
   * 强制使用子配置
   */
  SON_FORCE
}
