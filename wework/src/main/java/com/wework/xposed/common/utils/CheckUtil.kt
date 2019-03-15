package com.wework.xposed.common.utils


import android.os.Looper

import com.wework.xposed.core.Logger
import java.lang.Exception

import java.lang.reflect.InvocationTargetException


object CheckUtil {
    fun prepare() {
        try {
            Looper.prepare()
        } catch (ex: Exception) {
            ex.message
        }
    }

    private val sConfig = Config

    //1. 通过ActivityThread中的currentActivityThread()方法得到ActivityThread的实例对象
    //2. 通过activityThread的getProcessName() 方法获取进程名
    val processName: String
        @Throws(ClassNotFoundException::class, NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class)
        get() {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val method = activityThreadClass.getDeclaredMethod("currentActivityThread", activityThreadClass)
            val activityThread = method.invoke(null)
            val getProcessNameMethod = activityThreadClass.getDeclaredMethod("getProcessName", activityThreadClass)
            val processName = getProcessNameMethod.invoke(activityThread)

            return processName.toString()
        }

    internal object Config {
        var IS_PUBLISH = false
        var IsOpenLog = false
    }

    fun config(z: Boolean, z2: Boolean) {
        var config = sConfig
        Config.IsOpenLog = z
        config = sConfig
        Config.IS_PUBLISH = z2
    }


    fun ensureInMainThread() {
        var z = true
        if (!Config.IS_PUBLISH) {
            if (Config.IsOpenLog) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    z = false
                }
                Logger.error("Check", "must run in main thread")
            } else if (Looper.myLooper() != Looper.getMainLooper()) {
                Logger.error("Check", "must run in main thread")
            }
        }
    }

    fun checkInMainThread() {
        val config = sConfig
        if (!Config.IS_PUBLISH && Looper.getMainLooper().thread !== Thread.currentThread()) {
            Logger.error("Check", "MUST run in main thread")
            throw IllegalThreadStateException("MUST run in main thread")
        }
    }

    fun checkInMainProc() {
        val config = sConfig
        if (!Config.IS_PUBLISH) {
            val aIa = processName
            if (aIa != "com.tencent.wework") {
                throw AssertionError("not main process use native interface")
            }
        }
    }
}
