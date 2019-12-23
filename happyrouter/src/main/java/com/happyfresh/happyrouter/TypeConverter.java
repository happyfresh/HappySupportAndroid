package com.happyfresh.happyrouter;

/**
 * @param <O> is original type
 * @param <E> is extra type
 */
public abstract class TypeConverter<O, E> {

    public abstract O getOriginalValue(E extraValue);

    public abstract E getExtraValue(O originalValue);
}
