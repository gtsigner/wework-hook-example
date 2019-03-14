package com.example.xposed.wework.hooker

import com.example.xposed.wework.WkGlobal
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

object LogHooker : Hooker {
    override fun executeHook() {
        val css = WkGlobal.classLoader.loadClass("css")
        arrayOf("w", "d", "e", "i").forEach { m ->
            XposedBridge.hookAllMethods(css, m, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    var str = m
                    param!!.args.forEach { str += "@$it->${it.javaClass.name}\t" }
                    str += "---->" + param.args.size
                    XposedBridge.log(str)
                }
            })
        }
    }
}