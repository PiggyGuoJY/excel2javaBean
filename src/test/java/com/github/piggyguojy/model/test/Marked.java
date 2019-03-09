/*  * Copyright (c) 2019, Guo Jinyang. All rights reserved.  */  /* Copyright (c) 2019, Guo Jinyang. All rights reserved. */
package com.github.piggyguojy.model.test;

import java.lang.annotation.*;

@Documented
@Retention( RetentionPolicy.RUNTIME)
@Target( {ElementType.TYPE})
public @interface Marked {
    int[] ia() default {};
    String[] sa() default {};
}
