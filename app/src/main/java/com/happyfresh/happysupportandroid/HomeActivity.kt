package com.happyfresh.happysupportandroid

import android.os.Bundle
import android.widget.TextView
import com.happyfresh.happyrouter.annotations.Extra
import com.happyfresh.happyrouter.annotations.Route

@Route
class HomeActivity : BaseActivity() {

    @JvmField
    @Extra(key = "title")
    internal var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setTitle(title)

        val tvName = findViewById<TextView>(R.id.name)
        tvName.text = people?.name
    }
}