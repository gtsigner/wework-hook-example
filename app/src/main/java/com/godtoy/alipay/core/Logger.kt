package com.godtoy.alipay.core

import android.util.Log

object Logger {
    fun info(tag: String, message: String) {
        //XposedBridge.log("$tag@$message")
        Log.i("Xposed", "$tag@$message")
    }

    fun error(tag: String, message: String) {
        Log.d("Xposed", "$tag@$message")
    }

    fun debug(tag: String, message: String) {
        Log.d("Xposed", "$tag@$message")
    }
}
