package com.example.xposed.wework.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.Exception

object HookUtil {
    public fun printLogs(tag: String, param: XC_MethodHook.MethodHookParam) {
        var str = "$tag@${param.method}->Data->${param.result}"
        param.args.forEach {
            try {
                str += "#${it.javaClass}____$it\t"
            } catch (ex: Exception) {

            }
        }
        XposedBridge.log(str)
    }
}