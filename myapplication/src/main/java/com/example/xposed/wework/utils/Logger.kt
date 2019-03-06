package com.example.xposed.wework.utils

import android.util.Log

import de.robv.android.xposed.XposedBridge

/**
 * 日志
 */
object Logger {
    /**
     * 记录日志
     */
    @Synchronized
    fun log(tag: String, str: String) {
        XposedBridge.log("$tag # $str")
    }
}
