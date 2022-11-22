package com.happyfresh.happysupportandroid

import android.util.Log
import com.happyfresh.happytracker.Adapter
import com.happyfresh.happytracker.Properties

class SecondaryTrackerAdapter: Adapter() {

    override fun onEvent(event: String?, properties: Properties?) {
        super.onEvent(event, properties)
        Log.d("Secondary Adapter", event)
    }
}