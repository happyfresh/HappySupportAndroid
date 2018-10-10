package com.happyfresh.happysupportandroid

import android.util.Log
import com.happyfresh.happytracker.Adapter
import com.happyfresh.happytracker.Properties

class MainTrackerAdapter : Adapter() {

    override fun onEvent(event: String?, properties: Properties?) {
        super.onEvent(event, properties)
        Log.d("MainTrackerAdapter", event)
    }
}