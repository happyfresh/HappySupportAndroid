package com.happyfresh.happysupportandroid

import com.happyfresh.happytracker.annotations.Event
import com.happyfresh.happytracker.annotations.Property
import com.happyfresh.happytracker.annotations.Providers
import com.happyfresh.happytracker.annotations.SaveProperties

@Providers(MainTrackerAdapter::class, SecondaryTrackerAdapter::class)
interface MainTracker {

    @SaveProperties("ABC")
    fun testSaveProperties(@Property("XYZ") xyz: String)

    @Event("DEF")
    fun test()
}