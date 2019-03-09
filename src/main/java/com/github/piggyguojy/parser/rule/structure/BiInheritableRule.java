/* Copyright (c) 2019, Guo Jinyang. All rights reserved. */
package com.github.piggyguojy.parser.rule.structure;

/**
 * 程序员（guojy）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/2/23
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
public interface BiInheritableRule<X,Y> {

    /**
     * 按如下方式进行规则覆盖
     *
     * 子: 有配置, 父: 有配置 = 父
     * 子: 无配置, 父: 有配置 = 父
     * 子: 有配置, 父: 无配置 = 子
     * 子: 无配置, 父: 无配置 = 父
     *
     * @param son 子规则
     * @param parent 父规则
     * @return 最终子规则
     */
    X decideBiRuleOnParentFirst(X son, Y parent);
    /**
     * 强制使用父规则覆盖
     *
     * @param son 子规则
     * @param parent 父规则
     * @return 最终子规则
     */
    X decideBiRuleOnParentForce(X son, Y parent);
    /**
     * 按如下方式进行规则覆盖
     *
     * 子: 有配置, 父: 有配置 = 子
     * 子: 无配置, 父: 有配置 = 父
     * 子: 有配置, 父: 无配置 = 子
     * 子: 无配置, 父: 无配置 = 子
     *
     * @param son 子规则
     * @param parent 父规则
     * @return 最终子规则
     */
    X decideBiRuleOnSonFirst(X son, Y parent);
    /**
     * 强制使用子规则覆盖
     *
     * @param son 子规则
     * @param parent 父规则
     * @return 最终子规则
     */
    default X decideBiRuleOnSonForce(X son, Y parent) {
        return son;
    }

    /**
     * 按指定方式进行规则覆盖
     *
     * @param son 子规则
     * @param parent 父规则
     * @param overrideRule 覆盖方式
     * @return 最总子规则
     */
    default X decideBiRule(X son, Y parent, OverrideRule overrideRule) {
        switch (overrideRule) {
            case PARENT_FIRST: return decideBiRuleOnParentFirst(son,parent);
            case PARENT_FORCE: return decideBiRuleOnParentForce(son, parent);
            case SON_FIRST: return decideBiRuleOnSonFirst(son, parent);
            case SON_FORCE: return decideBiRuleOnParentForce(son, parent);
            default: return decideBiRuleOnParentForce(son, parent);
        }
    }

}
