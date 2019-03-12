package com.example.xposed.wework.main


import com.example.xposed.wework.MainHooker
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage


class HookMain : IXposedHookLoadPackage {
    private val hotLoad = true

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        //通过share 来配置是否使用hot
        //HotXposed.hook(HookerDispatcher::class.java, lpparam)
        MainHooker.execute(lpparam)
    }

}