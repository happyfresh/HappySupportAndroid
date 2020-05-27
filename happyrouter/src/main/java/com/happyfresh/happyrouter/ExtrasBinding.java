package com.happyfresh.happyrouter;

import android.os.Bundle;

public class ExtrasBinding {

    private Bundle bundle;

    private Bundle[] optionals;

    public ExtrasBinding(Bundle bundle, Bundle... optionals) {
        this.bundle = bundle;
        this.optionals = optionals;
    }

    public void onSaveInstanceState(Object target, Bundle outState) {

    }

    protected <T> T getTypeConverterExtraValue(Object value, Class<?> targetClass) {
        TypeConverter typeConverter = Router.getTypeConverter(targetClass);
        if (typeConverter != null) {
            return (T) typeConverter.getExtraValue(value);
        }

        return (T) value;
    }

    protected Object get(String key, Object defaultValue, Class<?> targetClass) {
        Object result = null;
        if (bundle != null) {
            result = get(key, bundle, -1);
        }
        else if (optionals != null && optionals.length > 0) {
            result = get(key, optionals[0], 0);
        }

        if (result == null) {
            return defaultValue;
        }

        TypeConverter typeConverter = Router.getTypeConverter(targetClass);
        if (typeConverter != null) {
            return typeConverter.getOriginalValue(result);
        }

        return result;
    }

    private Object get(String key, Bundle bundle, int index) {
        if (bundle == null) {
            return null;
        }

        try {
            Object result = bundle.get(key);
            if (result == null && optionals != null && index < optionals.length) {
                return get(key, optionals[index + 1], index + 1);
            }

            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
