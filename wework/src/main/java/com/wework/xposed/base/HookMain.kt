package com.wework.xposed.base


import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage


class HookMain : IXposedHookLoadPackage {
    //是否使用hotload
    private val hotLoad = true

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        //通过share 来配置是否使用hot
        //HotXposed.hook(HookerDispatcher::class.java, lpparam)
        XposedInit.execute(lpparam)
    }

}