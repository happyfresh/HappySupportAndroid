package com.happyfresh.happytracker.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Property {

    /*
    Key of property
     */
    String[] value();

    /*
    If property is optional and value is null then not included in properties
     */
    boolean optional() default false;

    /*
    If property is overwrite then existing property will be replace
     */
    boolean overwrite() default true;
}
