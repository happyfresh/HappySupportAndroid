package com.happyfresh.happysupportandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.happyfresh.happyrouter.Router
import com.happyfresh.happyrouter.annotations.Extra
import com.happyfresh.happyrouter.annotations.Route

@Route
open class BaseActivity : AppCompatActivity() {

    @JvmField
    @Extra(key = "people")
    internal var people: People? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Router.bind(this, intent.extras, savedInstanceState)
    }
}
