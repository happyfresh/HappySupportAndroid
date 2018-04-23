package com.happyfresh.happysupportkotlinextentions.helper

import android.content.Context
import android.support.annotation.StringRes
import com.happyfresh.happysupport.helper.StringHelper

fun Context.getStringHelper(@StringRes stringResId: Int, vararg objects: Array<Any>): String {
    return StringHelper.getString(this, stringResId, *objects)
}