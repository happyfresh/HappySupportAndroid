package com.happyfresh.happytracker;

import java.util.HashMap;
import java.util.Map;

public class Properties {

    private Map<String, Object> data = new HashMap<>();

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public void putAll(Properties properties) {
        putAll(properties.getData());
    }

    public void putAll(Map<String, Object> data) {
        this.data.putAll(data);
    }

    public Object get(String key) {
        if (data == null) {
            return null;
        }

        return data.get(key);
    }

    public void remove(String key) {
        data.remove(key);
    }

    public void clear() {
        data.clear();
    }

    public Map<String, Object> getData() {
        return data;
    }

    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public String toString() {
        if (data == null) {
            return "";
        }

        StringBuilder value = new StringBuilder("{\n");

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            value.append("\t");
            value.append(entry.getKey());
            value.append(": ");
            value.append(entry.getValue());
            value.append("\n");
        }

        value.append("}");

        return value.toString();
    }
}
