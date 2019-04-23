package com.happyfresh.happytracker;

import org.junit.Before;
import org.junit.Test;

public class TrackerTest {

    private MainTrackerEvent mainTrackerEvent;

    @Before
    public void before() {
        mainTrackerEvent = Tracker.create(null, MainTrackerEvent.class);
    }

    @Test
    public void event_test() {
        mainTrackerEvent.viewHomeScreen("Sign Up");
    }

    @Test
    public void saveProperties_test() {
        mainTrackerEvent.setSourceViewHomeScreen("Login");
        mainTrackerEvent.viewHomeScreen("Sign Up");
    }
}
