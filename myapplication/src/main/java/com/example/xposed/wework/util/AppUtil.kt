package com.example.xposed.wework.util

import android.content.Context
import de.robv.android.xposed.XposedHelpers

object AppUtil {
    /**
     * 获取上下文
     */
    fun getSystemContext(): Context {
        val activityThreadClass = XposedHelpers.findClass("android.app.ActivityThread", null)
        val activityThread = XposedHelpers.callStaticMethod(activityThreadClass, "currentActivityThread")
        val context = XposedHelpers.callMethod(activityThread, "getSystemContext") as Context?
        return context ?: throw Error("Failed to get system context.")
    }

    /**
     * 获取企业微信版本
     *
     * @param context 上下文
     * @return
     */
    fun getApplicationVersion(packageName: String): String {
        val manager = getSystemContext().packageManager
        val info = manager.getPackageInfo(packageName, 0)
        return info.versionName
    }
}
