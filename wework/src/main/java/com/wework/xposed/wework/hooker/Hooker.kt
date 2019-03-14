package com.wework.xposed.wework.hooker

import com.wework.xposed.wework.util.HookUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

interface Hooker {
    fun executeHook()
    /**
     * 直接Hook所有方法
     */
    fun hookAndDebugAllMethods(cls: Class<*>, method: String, tag: String?) {
        XposedBridge.hookAllMethods(cls, method, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                if (param == null) {
                    return
                }
                HookUtil.printLogs(cls.javaClass.name + "/" + tag, param)
            }
        })
    }
}