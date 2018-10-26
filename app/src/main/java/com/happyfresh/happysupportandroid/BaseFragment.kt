package com.happyfresh.happysupportandroid

import android.support.v4.app.Fragment
import com.happyfresh.happyrouter.annotations.Extra
import com.happyfresh.happyrouter.annotations.Route

@Route
open class BaseFragment : Fragment() {

    @JvmField
    @Extra(key = "abc")
    internal var abc: String? = null
}