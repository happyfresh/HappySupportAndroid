package com.happyfresh.happyrouter;

import android.os.Bundle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Router {

    private static List<Class<? extends TypeConverter>> typeConverters;

    public static <T, E extends ExtrasBinding> E bind(T target, Bundle bundle, Bundle... optionals) {
        Class<?> targetClass = target.getClass();
        String targetName = targetClass.getName();
        try {
            Class<?> bindingClass = target.getClass().getClassLoader().loadClass(targetName + "_ExtrasBinding");
            Constructor<?> constructor = bindingClass.getConstructor(targetClass, Bundle.class, Bundle[].class);
            return (E) constructor.newInstance(target, bundle, optionals);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addTypeConverter(Class<? extends TypeConverter> classTypeConverter) {
        if (typeConverters == null) {
            typeConverters = new ArrayList<>();
        }

        if (typeConverters.contains(classTypeConverter)) {
            return;
        }

        typeConverters.add(classTypeConverter);
    }

    static TypeConverter getTypeConverter(Class<?> originalClass) {
        try {
            if (typeConverters == null || typeConverters.isEmpty()) {
                return null;
            }

            for (Class<? extends TypeConverter> typeConverter : typeConverters) {
                ParameterizedType parameterizedType = (ParameterizedType) typeConverter.getGenericSuperclass();
                Type[] types = parameterizedType.getActualTypeArguments();

                if (types == null || types.length == 0 || types.length < 2) {
                    continue;
                }

                if (originalClass.isAssignableFrom((Class<?>) types[0])) {
                    return typeConverter.newInstance();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
