package com.happyfresh.happytracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.happyfresh.happytracker.annotations.Event;
import com.happyfresh.happytracker.annotations.Identify;
import com.happyfresh.happytracker.annotations.Property;
import com.happyfresh.happytracker.annotations.Provider;
import com.happyfresh.happytracker.annotations.SaveProperties;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class Tracker {

    private static Map<String, Map<String, Properties>> sSaveProperties;

    @Nullable
    public static Properties getSaveProperties(Class<? extends Adapter> provider, String event) {
        if (sSaveProperties == null) {
            return null;
        }

        Map<String, Properties> eventProperties = sSaveProperties.get(provider.getName());

        if (eventProperties == null) {
            return null;
        }

        return eventProperties.get(event);
    }

    @Nullable
    public static Object getSaveProperty(Class<? extends Adapter> provider, String event, String key) {
        Properties properties = getSaveProperties(provider, event);

        if (properties == null) {
            return null;
        }

        return properties.get(key);
    }

    public synchronized static <T> T create(Class<T> tracker) {
        return create(null, tracker);
    }

    @SuppressWarnings("unchecked")
    // Single-interface proxy creation guarded by parameter safety.
    public synchronized static <T> T create(@Nullable Context context, final Class<T> tracker) {
        // Set adapter first
        Adapter adapter = new Adapter();
        try {
            // Get Provider
            Provider provider = tracker.getAnnotation(Provider.class);
            if (provider == null) {
                throw new Exception("Provider not defined in " + tracker.getName());
            }
            adapter = provider.value().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            adapter.setContext(context);
        }

        final Adapter finalAdapter = adapter;

        return (T) Proxy.newProxyInstance(tracker.getClassLoader(), new Class<?>[] {tracker},
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object... args)
                            throws Throwable {
                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }

                        SaveProperties saveProperty = method.getAnnotation(SaveProperties.class);
                        Identify identify = method.getAnnotation(Identify.class);
                        Event event = method.getAnnotation(Event.class);

                        try {
                            if (saveProperty != null) {
                                doSaveProperties(finalAdapter, saveProperty, method, args);
                            }
                            else if (identify != null) {
                                doIdentify(finalAdapter, method, args);
                            }
                            else if (event != null) {
                                doEvent(finalAdapter, event, method, args);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                });
    }

    private static <T extends Adapter> void doSaveProperties(T adapter, SaveProperties savePropertiesAnnotation,
            Method method,
            Object... args)
            throws Throwable {
        String event = savePropertiesAnnotation.value();
        Properties properties = getSaveProperties(adapter.getClass(), event);

        if (properties == null) {
            properties = new Properties();
        }

        if (args.length == 1 && args[0] instanceof Properties) {
            properties.putAll((Properties) args[0]);
        }
        else {
            Annotation[][] annotations = method.getParameterAnnotations();

            if (annotations.length != args.length) {
                throw new Throwable("There are argument does not have annotation Property");
            }

            properties = createProperties(properties, annotations, args);
        }

        if (sSaveProperties == null) {
            sSaveProperties = new HashMap<>();
        }

        Map<String, Properties> saveProperties = sSaveProperties.get(adapter.getClass().getName());
        if (saveProperties == null) {
            saveProperties = new HashMap<>();
            sSaveProperties.put(adapter.getClass().getName(), saveProperties);
        }

        saveProperties.put(event, properties);

        adapter.onSaveProperties(properties);
    }

    private static <T extends Adapter> void doIdentify(T adapter, Method method, Object... args) throws Throwable {
        if (args == null || args.length == 0) {
            adapter.onIdentify(new Properties());
            return;
        }

        if (args.length == 1 && args[0] instanceof Properties) {
            adapter.onIdentify((Properties) args[0]);
            return;
        }

        Annotation[][] annotations = method.getParameterAnnotations();

        if (annotations.length != args.length) {
            throw new Throwable("There are argument does not have annotation Property");
        }

        Properties properties = createProperties(null, annotations, args);

        adapter.onIdentify(properties);
    }

    private static <T extends Adapter> void doEvent(T adapter, Event eventAnnotation, Method method, Object... args)
            throws Throwable {
        String event = eventAnnotation.value().isEmpty() ? method.getName() : eventAnnotation.value();
        Properties properties = getSaveProperties(adapter.getClass(), event);

        if (properties == null) {
            properties = new Properties();
        }

        if (args == null || args.length == 0) {
            adapter.onEvent(event, properties);
            return;
        }

        if (args.length == 1 && args[0] instanceof Properties) {
            properties.putAll((Properties) args[0]);
            adapter.onEvent(event, properties);
            return;
        }

        Annotation[][] annotations = method.getParameterAnnotations();

        if (annotations.length != args.length) {
            throw new Throwable("There are argument does not have annotation Property");
        }

        properties = createProperties(properties, annotations, args);

        adapter.onEvent(event, properties);
    }

    private static Properties createProperties(@Nullable Properties saveProperties, @NonNull Annotation[][] annotations,
            Object... args) throws Throwable {
        Properties properties = new Properties();
        for (int i = 0; i < args.length; i++) {
            // find property annotation
            if (annotations[i].length == 0) {
                throw new Throwable("There are argument does not have annotation Property");
            }

            Property property = null;

            for (int j = 0; j < annotations[i].length; j++) {
                if (annotations[i][j] instanceof Property) {
                    property = (Property) annotations[i][j];
                    break;
                }
            }

            if (property == null) {
                throw new Throwable("There are argument does not have annotation Property");
            }

            if (property.optional() && args[i] == null) {
                continue;
            }

            for (String key : property.value()) {
                if (saveProperties != null && saveProperties.containsKey(key) && !property.overwrite()) {
                    properties.put(key, saveProperties.get(key));
                } else {
                    properties.put(key, args[i]);
                }

                if (saveProperties != null && saveProperties.containsKey(key)) {
                    saveProperties.remove(key);
                }
            }
        }

        if (saveProperties != null && !saveProperties.isEmpty()) {
            properties.putAll(saveProperties);
            saveProperties.clear();
        }

        return properties;
    }
}
