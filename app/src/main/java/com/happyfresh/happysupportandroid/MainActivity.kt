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

        val tvHallo = findViewById<TextView>(R.id.hallo)
        val tvWelcome1 = findViewById<TextView>(R.id.welcome_1)
        val tvWelcome2 = findViewById<TextView>(R.id.welcome_2)

        val welcome = StringHelper.getStringArray(this, R.array.welcome, arrayOf(arrayOf("Happy", 2), arrayOf("Fresh")), arrayOf(arrayOf("Fresh")))

        tvHallo.text = Html.fromHtml(
                StringHelper.getString(this, R.string.hallo_2, arrayOf("Happy", 2), arrayOf("Fresh")))

        // Using kotlin extentions
        tvHallo.text = Html.fromHtml(
                getStringHelper(R.string.hallo_2, arrayOf("Happy", 2), arrayOf("Fresh")))

        tvWelcome1.text = Html.fromHtml(welcome[0])
        tvWelcome2.text = Html.fromHtml(welcome[1])
    }
}
