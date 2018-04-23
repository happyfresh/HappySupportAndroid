package com.happyfresh.happysupportandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.widget.TextView
import com.happyfresh.happysupport.helper.StringHelper
import com.happyfresh.happysupportkotlinextentions.helper.getStringHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val tv = findViewById<TextView>(R.id.hallo)

        tv.text = Html.fromHtml(
                StringHelper.getString(this, R.string.hallo_2, arrayOf("Happy", 2), arrayOf("Fresh")))

        // Using kotlin extentions
        tv.text = Html.fromHtml(
                getStringHelper(R.string.hallo_2, arrayOf("Happy", 2), arrayOf("Fresh")))
    }
}
