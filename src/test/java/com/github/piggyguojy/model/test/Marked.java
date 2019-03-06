package com.github.piggyguojy.model.test;

import java.lang.annotation.*;

@Documented
@Retention( RetentionPolicy.RUNTIME)
@Target( {ElementType.TYPE})
public @interface Marked {
    int[] ia() default {};
    String[] sa() default {};
}
