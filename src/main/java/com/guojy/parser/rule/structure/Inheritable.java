package com.guojy.parser.rule.structure;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 * 
 * <p> 创建时间：2019/2/23
 * 
 * @author guojy24
 * @version 1.0
 * */
public interface Inheritable<X> {
    X decideRuleOnParentFirst(X son, X parent);
    X decideRuleOnParentForce(X son, X parent);
    X decideRuleOnSonFirst(X son, X parent);
    X decideRuleOnSonForce(X son, X parent);

    default X decideRule(X son, X parent, OverrideRule overrideRule) {
        switch (overrideRule) {
            case PARENT_FIRST: return decideRuleOnParentFirst(son, parent);
            case PARENT_FORCE: return decideRuleOnParentForce(son, parent);
            case SON_FIRST: return decideRuleOnSonFirst(son, parent);
            case SON_FORCE: return decideRuleOnSonForce(son, parent);
            default: return decideRuleOnParentForce(son, parent);
        }
    }
}
