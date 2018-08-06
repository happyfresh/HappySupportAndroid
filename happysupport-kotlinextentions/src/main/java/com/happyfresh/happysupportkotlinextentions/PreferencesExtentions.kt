package com.happyfresh.happysupportkotlinextentions

import android.annotation.SuppressLint
import android.content.SharedPreferences

@SuppressLint("ApplySharedPref")
fun SharedPreferences.editorCommit(l: (SharedPreferences.Editor) -> Unit) {
    val editor = edit()
    l(editor)
    editor.commit()
}

fun SharedPreferences.editorApply(l: (SharedPreferences.Editor) -> Unit) {
    val editor = edit()
    l(editor)
    editor.apply()
}