/* Copyright (c) 2019, Guo Jinyang. All rights reserved. */
package com.github.piggyguojy.parser.rule.structure;

/**
 * 程序员（guojy）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2019/2/22
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 * */
public enum OverrideRule {

    /**
     * 当配置冲突时, 优先使用父配置
     * */
    PARENT_FIRST,
    /**
     * 强制使用父配置
     * */
    PARENT_FORCE,
    /**
     * 当配置冲突时, 优先使用子配置
     * */
    SON_FIRST,
    /**
     * 强制使用子配置
     * */
    SON_FORCE;
}
