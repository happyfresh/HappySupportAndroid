package com.happyfresh.happysupportandroid

import com.happyfresh.happyrouter.annotations.Extra
import com.happyfresh.happyrouter.annotations.Route

@Route
class SecondActivity : MainActivity() {

    @JvmField
    @Extra(key = "GHI")
    internal var ghi: String? = null
}