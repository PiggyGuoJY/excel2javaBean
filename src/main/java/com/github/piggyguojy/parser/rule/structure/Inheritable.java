package com.github.piggyguojy.parser.rule.structure;

/**
 * 可继承的
 * 
 * <p> 创建时间：2019/2/23
 * 
 * @author guojy
 * @version 1.0
 * */
public interface Inheritable<X> {

    /**
     * 优先使用父类规则
     *
     * @param son 子规则
     * @param parent 父规则
     * @return 最终规则
     */
    X decideRuleOnParentFirst(X son, X parent);
    /**
     * 强制使用父类规则
     *
     * @param son 子规则
     * @param parent 父规则
     * @return 最终规则
     */
    X decideRuleOnParentForce(X son, X parent);
    /**
     * 优先使用子类规则
     *
     * @param son 子规则
     * @param parent 父规则
     * @return 最终规则
     */
    X decideRuleOnSonFirst(X son, X parent);
    /**
     * 强制使用子类规则
     *
     * @param son 子规则
     * @param parent 父规则
     * @return 最终规则
     */
    X decideRuleOnSonForce(X son, X parent);
    /**
     * 决定规则
     *
     * @param son 子规则
     * @param parent 父规则
     * @param overrideRule 继承规则
     * @return 最终规则
     */
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