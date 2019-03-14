
package com.github.piggyguojy.parser.rule.structure.inherit;

/**
 * 可以自继承的
 *
 * @author <a href="https://github.com/PiggyGuoJY" target="_blank">PiggyGuoJY</a>
 * @version 1.0
 */
public interface SelfInheritable<X extends SelfInheritable>
        extends Inheritable<X> {

}
