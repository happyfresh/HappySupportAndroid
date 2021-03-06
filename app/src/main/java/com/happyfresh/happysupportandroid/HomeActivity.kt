package com.happyfresh.happysupportandroid

import android.os.Bundle
import android.os.Parcelable
import android.widget.TextView
import com.happyfresh.happyrouter.annotations.Extra
import com.happyfresh.happyrouter.annotations.Route
import com.happyfresh.happyrouter.annotations.SaveExtra

@Route
class HomeActivity : BaseActivity() {

    @JvmField
    @SaveExtra(key = "title")
    @Extra(key = "title")
    internal var title: String? = null

    @JvmField
    @Extra(key = "subtitle")
    internal var subtitle: String? = null

    @JvmField
    @Extra(key = "list1")
    internal var list1: List<Int>? = null

    @JvmField
    @Extra(key = "list2", required = false)
    internal var list2: List<String>? = null

    @JvmField
    @SaveExtra(key = "list3")
    @Extra(key = "list3")
    internal var list3: List<CharSequence>? = null

    @JvmField
    @Extra(key = "list4", required = false)
    internal var list4: List<People>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setTitle(title)

        val tvName = findViewById<TextView>(R.id.name)
        tvName.text = people?.name
    }
}