package com.common.api.excel;

import java.lang.annotation.*;


@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface  Column {
    String value() default "";

    String dictType() default "";
}
