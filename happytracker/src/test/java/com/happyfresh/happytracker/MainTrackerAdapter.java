package com.happyfresh.happytracker;

public class MainTrackerAdapter extends Adapter {

    @Override
    protected void onEvent(String event, Properties properties) {
        System.out.println(event + ": " + properties.toString());
    }
}
