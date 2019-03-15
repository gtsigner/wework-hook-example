package com.wework.xposed.wework.hooker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.wework.xposed.core.Logger
import com.wework.xposed.core.WeWorkService
import com.wework.xposed.wework.WkGlobal
import com.wework.xposed.wework.WkObject
import com.wework.xposed.wework.WorkApi
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object InitHooker : Hooker {
    override fun executeHook() {
        //hookApplication
        val app = WkObject.getClass("com.tencent.wework.launch.WwApplicationLike")
        XposedHelpers.findAndHookMethod(app, "onCreate", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                WkGlobal.ctx = param?.thisObject as Context
                Logger.info("Application", "企业微信初始化成功")
                WkGlobal.wkFinishLoaded = true

                val service = WeWorkService.getService()

                service?.workApi = WorkApi()


                val reciver = object : BroadcastReceiver() {
                    override fun onReceive(p0: Context?, p1: Intent?) {
                        Logger.info("接受", "$p1")
                    }
                }
                val intentFilter = IntentFilter("com.myapplication.xposed.broadcast.TEST")
                WkGlobal.ctx.registerReceiver(reciver, intentFilter)
            }
        })
    }

}