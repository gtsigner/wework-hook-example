package com.wework.xposed.wework.main

import com.wework.xposed.core.Logger
import com.wework.xposed.base.XposedInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import net.androidwing.hotxposed.IHookerDispatcher

class HookerDispatcher : IHookerDispatcher {
    /**
     * @param param
     */
    override fun dispatch(param: XC_LoadPackage.LoadPackageParam) {
        Logger.debug("HookerDispatcher", "dispatch call hot load")
        XposedInit.execute(param)
    }
}