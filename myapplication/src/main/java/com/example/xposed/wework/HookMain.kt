package com.example.xposed.wework


import com.example.xposed.core.Logger
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import net.androidwing.hotxposed.HotXposed


class HookMain : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        Logger.debug("HookMain", "handleLoadPackage")
        HotXposed.hook(HookerDispatcher::class.java, lpparam)
    }

}