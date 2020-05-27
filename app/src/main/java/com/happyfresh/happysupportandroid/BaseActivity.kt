package com.happyfresh.happysupportandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.happyfresh.happyrouter.ExtrasBinding

import com.happyfresh.happyrouter.Router
import com.happyfresh.happyrouter.annotations.Extra
import com.happyfresh.happyrouter.annotations.Route
import com.happyfresh.happyrouter.annotations.SaveExtra

@Route
open class BaseActivity : AppCompatActivity() {

    @JvmField
    @SaveExtra(key = "people")
    @Extra(key = "people")
    internal var people: People? = null

    @JvmField
    @SaveExtra(key = "ABC")
    internal var abc: String? = null

    var extrasBinding: ExtrasBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        extrasBinding = Router.bind(this, intent.extras, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        extrasBinding?.onSaveInstanceState(this, outState)
    }
}
