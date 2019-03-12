package com.example.xposed.wework.util

import android.content.Context
import com.example.xposed.wework.HookVersion

object AppUtil {
    /**
     * 获取企业微信版本
     *
     * @param context 上下文
     * @return
     */
    public fun getWkVersion(context: Context): String {
        try {
            val manager = context.packageManager
            val info = manager.getPackageInfo(HookVersion.X_WK_PACKAGE_NAME, 0)
            return info.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }
}
