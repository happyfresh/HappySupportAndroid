package com.happyfresh.happysupportandroid

import android.os.Parcelable
import com.happyfresh.happyrouter.TypeConverter
import org.parceler.Parcels

class PeopleTypeConverter : TypeConverter<People, Parcelable>() {

    override fun getOriginalValue(extraValue: Parcelable?): People {
        return Parcels.unwrap<People>(extraValue)
    }
}