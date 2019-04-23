package com.happyfresh.happytracker;

import com.happyfresh.happytracker.annotations.Event;
import com.happyfresh.happytracker.annotations.Property;
import com.happyfresh.happytracker.annotations.Provider;
import com.happyfresh.happytracker.annotations.SaveProperties;

@Provider(MainTrackerAdapter.class)
public interface MainTrackerEvent {

    @SaveProperties("View Home Screen")
    void setSourceViewHomeScreen(@Property("source") String source);

    @Event("View Home Screen")
    void viewHomeScreen(@Property("source") String source);
}
