/*  * Copyright (c) 2019, Guo Jinyang. All rights reserved.  */
package com.github.piggyguojy.model.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Marked {

  int[] ia() default {};

  String[] sa() default {};
}
