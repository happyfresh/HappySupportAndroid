package com.happyfresh.happysupportandroid

import com.happyfresh.happyrouter.annotations.Extra
import com.happyfresh.happyrouter.annotations.Route

@Route
class HomeFragment : BaseFragment() {

    @JvmField
    @Extra(key = "test")
    internal var test: String? = null
}