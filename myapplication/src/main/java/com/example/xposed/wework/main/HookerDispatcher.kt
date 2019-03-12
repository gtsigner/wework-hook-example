package com.example.xposed.wework.main

import com.example.xposed.core.Logger
import com.example.xposed.wework.MainHooker
import de.robv.android.xposed.callbacks.XC_LoadPackage
import net.androidwing.hotxposed.IHookerDispatcher

class HookerDispatcher : IHookerDispatcher {
    /**
     * @param param
     */
    override fun dispatch(param: XC_LoadPackage.LoadPackageParam) {
        Logger.debug("HookerDispatcher", "dispatch call")
        MainHooker.execute(param)
    }
}