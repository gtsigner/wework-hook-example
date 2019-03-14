package com.wework.xposed.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.wework.xposed.core.WeWorkService
import com.wework.xposed.wework.WkGlobal
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

/**
 * 操作系统初始化Hook系统层面的东西
 */
class AppInitHook : IXposedHookZygoteInit {
    /**
     *
     */
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        if (startupParam == null) return
        //系统层的Hook
        //检测版本


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // android 5.0+
            registerUp()
        } else {
            registerLover()
            return
        }
    }

    @SuppressLint("PrivateApi")
    private fun registerUp() {
        val activityThread = Class.forName("android.app.ActivityThread")
        val amsClsName = "com.android.server.am.ActivityManagerService"
        XposedBridge.hookAllMethods(activityThread, "systemMain", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {

                val classLoader = Thread.currentThread().contextClassLoader//进程的classLoader

                //注册系统服务
                val ams = XposedHelpers.findClass(amsClsName, classLoader)
                if (ams == null) {
                    XposedBridge.log("系统中没有找到类：$amsClsName,注册失败")
                    return
                }
                XposedHelpers.findAndHookConstructor(ams, Context::class.java, object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        //注册自己的服务
                        if (param == null) {
                            return
                        }
                        val context = param.args[0] as Context
                        WeWorkService.register(context, classLoader)
                    }
                })
            }
        })
    }

    /**
     * 低版本
     */
    @SuppressLint("PrivateApi")
    private fun registerLover() {
        //寻找服务类
        val ams = XposedHelpers.findClass("com.android.server.am.ActivityManagerService", WkGlobal.workLoader)
        val classLoader = Thread.currentThread().contextClassLoader
        XposedBridge.hookAllMethods(ams, "main", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                //注册自己的服务
                if (param == null) {
                    return
                }
                val context = param.args[0] as Context
                WeWorkService.register(context, classLoader)
            }
        })
    }

}