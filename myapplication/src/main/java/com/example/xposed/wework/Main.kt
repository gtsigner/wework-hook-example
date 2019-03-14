package com.example.xposed.wework

import com.example.xposed.core.Logger
import com.example.xposed.wework.hooker.MessageHooker
import com.example.xposed.wework.hooker.NotificationHooker
import com.example.xposed.wework.util.AppUtil
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object Main {
    /**
     * Hooker
     */
    private val hookers = arrayOf(
            MessageHooker,
            NotificationHooker
    )

    private lateinit var lpparam: XC_LoadPackage.LoadPackageParam
    /**
     * 执行真实的hook
     */
    fun execute(lpparam: XC_LoadPackage.LoadPackageParam) {
        //获取插件的包
        if (lpparam.packageName == WkGlobal.X_PACKAGE_NAME) {

        }
        //企业微信
        if (lpparam.packageName == WkGlobal.X_WK_PACKAGE_NAME && lpparam.processName == WkGlobal.X_WK_PROCESS_NAME) {
            handle(lpparam)
        }
    }


    /**
     * 处理企业微信的Hook
     */
    private fun handle(lpparam: XC_LoadPackage.LoadPackageParam) {
        val versionName = AppUtil.getApplicationVersion(lpparam.packageName)
        if (WkGlobal.X_WK_supportedVersion != versionName) {
            Logger.info("微信版本不支持", "支持企业微信版本:${WkGlobal.X_WK_supportedVersion},当前版本:$versionName")
            return
        }
        XposedBridge.log("企业微信版本号:$versionName,Xposed版本:${XposedBridge.getXposedVersion()}")

        //全局
        WkGlobal.classLoader = lpparam.classLoader
        WkGlobal.workLoader = lpparam.classLoader
        WkGlobal.xpParam = lpparam
        Main.lpparam = lpparam


        //挂在所有得Hooker
        hookers.forEach {
            it.executeHook()
        }

        TaskHandler.start()
    }

}