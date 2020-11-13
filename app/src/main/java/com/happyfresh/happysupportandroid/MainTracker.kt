package com.happyfresh.happysupportandroid

import com.happyfresh.happytracker.annotations.Event
import com.happyfresh.happytracker.annotations.Property
import com.happyfresh.happytracker.annotations.Provider
import com.happyfresh.happytracker.annotations.SaveProperties

@Provider(MainTrackerAdapter::class)
interface MainTracker {

    @SaveProperties("ABC")
    fun testSaveProperties(@Property("XYZ") xyz: String)

    @Event("ABC")
    fun test()
}