package com.wework.xposed.base


import com.wework.xposed.wework.WkGlobal
import de.robv.android.xposed.callbacks.XC_LoadPackage

object XposedInit {
    /**
     * 执行真实的hook
     */
    fun execute(lpparam: XC_LoadPackage.LoadPackageParam) {
        //获取插件的包
        if (lpparam.packageName == WkGlobal.X_PACKAGE_NAME) {
            //Mainer.start(lpparam)
            return
        }
        //企业微信
        if (lpparam.packageName == WkGlobal.X_WK_PACKAGE_NAME) {
            WkGlobal.start(lpparam)
            return
        }
    }
}