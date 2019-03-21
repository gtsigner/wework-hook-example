package com.godtoy.alipay.bill

import com.godtoy.alipay.core.Logger
import com.godtoy.alipay.hooker.Hooker
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 *
 */
class HookMain : IXposedHookLoadPackage {
    /**
     *HookMain
     */
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == Hooker.Version105.ALIPAY_PKG_NAME) {
            Logger.debug("xposed", "进程：${lpparam.processName} Hook")
            //HotXposed.hook(HookerDispatcher::class.java, lpparam)
            Hooker.execute(lpparam)
            return
        }
    }
}