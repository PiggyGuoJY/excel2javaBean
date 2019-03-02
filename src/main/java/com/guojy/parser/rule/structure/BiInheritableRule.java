package com.guojy.parser.rule.structure;

/**
 * 程序员（guojy）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/2/23
 *
 * @author guojy
 * @version 1.0
 * */
public interface BiInheritableRule<X,Y> {

    /**
     * 子: 有配置, 父: 有配置 -> 父
     * 子: 无配置, 父: 有配置 -> 父
     * 子: 有配置, 父: 无配置 -> 子
     * 子: 无配置, 父: 无配置 -> 父
     * */
    X decideBiRuleOnParentFirst(X son, Y parent);

    /**
     * 强制使用父配置
     * */
    X decideBiRuleOnParentForce(X son, Y parent);

    /**
     * 子: 有配置, 父: 有配置 -> 子
     * 子: 无配置, 父: 有配置 -> 父
     * 子: 有配置, 父: 无配置 -> 子
     * 子: 无配置, 父: 无配置 -> 子
     * */
    X decideBiRuleOnSonFirst(X son, Y parent);

    /**
     * 强制使用子配置
     * */
    default X decideBiRuleOnSonForce(X son, Y parent) {
        return son;
    }

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
