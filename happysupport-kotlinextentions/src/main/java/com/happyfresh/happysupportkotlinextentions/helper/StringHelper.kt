package com.happyfresh.happysupportkotlinextentions.helper

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import com.happyfresh.happysupport.helper.StringHelper

fun Context.getStringHelper(@StringRes stringResId: Int, vararg objects: Array<Any>): String {
    return StringHelper.getString(this, stringResId, *objects)
}

fun Context.getStringArrayHelper(@ArrayRes stringResId: Int, vararg objects: Array<Array<Any>>): Array<String> {
    return StringHelper.getStringArray(this, stringResId, *objects)
}