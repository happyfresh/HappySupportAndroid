package com.happyfresh.happysupportandroid

import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.widget.TextView
import com.happyfresh.happyrouter.Router
import com.happyfresh.happyrouter.annotations.Route
import com.happyfresh.happysupport.helper.StringHelper
import com.happyfresh.happysupportkotlinextentions.helper.getStringHelper
import com.happyfresh.happytracker.Tracker
import org.parceler.Parcels

@Route
open class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Router.addTypeConverter(PeopleTypeConverter::class.java)

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

        Tracker.create(this, MainTracker::class.java).test()

        tvHallo.setOnClickListener {
            openHomeScreen(tvHallo.text.toString())
        }
        tvWelcome1.setOnClickListener {
            openHomeScreen(tvWelcome1.text.toString())
        }
        tvWelcome2.setOnClickListener {
            openHomeScreen(tvWelcome2.text.toString())
        }
    }

    private fun openHomeScreen(title: String) {
        val people = People().also { it.name = "HappyFresh" }
        val router = HomeActivityRouter(Parcels.wrap(people), title, "")
        val list1 = ArrayList<Int>()
        list1.add(1)
        val list2 = ArrayList<String>()
        list2.add("1")
        val list3 = ArrayList<CharSequence>()
        list3.add("1")
        val list4 = ArrayList<Parcelable>()
        list4.add(Parcels.wrap(people))
        router.putList1(list1)
        router.putList2(list2)
        router.putList3(list3)
        router.putList4(list4)
        startActivity(router.create(this))
    }
}
