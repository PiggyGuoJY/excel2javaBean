package com.guojy.parser.rule.structure;

/**
 * 可以自继承的
 *
 * @author guojy
 */
public interface SelfInheritable<X extends SelfInheritable> extends Inheritable<X> { }
