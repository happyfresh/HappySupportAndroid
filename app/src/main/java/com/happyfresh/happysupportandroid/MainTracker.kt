package com.happyfresh.happysupportandroid

import com.happyfresh.happytracker.annotations.Event
import com.happyfresh.happytracker.annotations.Provider

@Provider(MainTrackerAdapter::class)
interface MainTracker {

    @Event("ABC")
    fun test()
}