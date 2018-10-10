package com.happyfresh.happysupportandroid

import android.support.v4.app.Fragment
import com.happyfresh.happyrouter.annotations.Extra
import com.happyfresh.happyrouter.annotations.Route

@Route
class HomeFragment : Fragment() {

    @JvmField
    @Extra(key = "test")
    internal var test: String? = null
}