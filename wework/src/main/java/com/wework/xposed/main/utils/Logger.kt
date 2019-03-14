package com.wework.xposed.main.utils

import android.util.Log
import de.robv.android.xposed.XposedBridge

object Logger {
    fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    fun d(message: String) {
        Log.d("Log", message)
        XposedBridge.log(message)
    }
}
