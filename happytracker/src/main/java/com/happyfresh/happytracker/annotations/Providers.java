package com.happyfresh.happytracker.annotations;

import com.happyfresh.happytracker.Adapter;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Providers {

    Class<? extends Adapter>[] value();
}
