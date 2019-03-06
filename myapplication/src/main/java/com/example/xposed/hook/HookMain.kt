package com.example.xposed.hook

import com.example.xposed.main.MainActivity
import com.example.xposed.wework.HookVersion

import java.util.ArrayList

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HookMain : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedBridge.log("初始化 应用: " + lpparam.packageName)

        //获取包
        if (lpparam.packageName == HookVersion.X_PACKAGE_NAME) {
            val cls: Class<*>
            try {
                //"com.example.myapplication.MainActivity"
                cls = lpparam.classLoader.loadClass(MainActivity::class.java.name)
                startHook(lpparam, cls)
                XposedBridge.log("Hook Success：" + lpparam.packageName)
            } catch (e: ClassNotFoundException) {
                XposedBridge.log("Hook Fail:" + "主类无法找到")
                return
            }

        }

        if (lpparam.packageName == "com.tencent.wework") {
            try {
                startHookWeWork(lpparam)
            } catch (ex: Exception) {
                XposedBridge.log(ex.message)
            }

        }
    }

    /**
     * 启动Hook
     *
     * @param lpparam
     * @param clazz
     */
    private fun startHook(lpparam: XC_LoadPackage.LoadPackageParam, clazz: Class<*>) {
        /**
         * hookShowToast方法
         */
        XposedHelpers.findAndHookMethod(clazz, "showToast", object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                super.beforeHookedMethod(param)
            }

            @Throws(Throwable::class)
            override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                param!!.result = "你已被劫持"
            }
        })

        /**
         * 携带参数的方法
         */
        XposedHelpers.findAndHookMethod(clazz, "fuckYou", String::class.java, object : XC_MethodReplacement() {

            @Throws(Throwable::class)
            override fun replaceHookedMethod(param: XC_MethodHook.MethodHookParam): Any? {
                XposedBridge.log("FuckYou")
                return null
            }
        })
    }

    /**
     * 启动weWorkHook
     *
     * @param lpparam
     */
    @Throws(ClassNotFoundException::class)
    private fun startHookWeWork(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedBridge.log("企业微信,Xposed:" + XposedBridge.getXposedVersion())
        //需要检测的类
        val list = ArrayList<String>()
        list.add("defpackage.css")
        list.add("defpackage.a")
        list.add("com.tencent.wework.launch.WwMainActivity")
        list.add("com.tencent.wework.launch.WwApplicationLike")
        list.add("com.tencent.wework.remote.BootReceiver")
        list.add("com.tencent.wework.contact.model.ContactItem")
        list.add("com.tencent.wework.foundation.logic.WechatMessageService")

        for (str in list) {
            try {
                findClasses(lpparam, str)
            } catch (ex: Exception) {
                XposedBridge.log("类没有找到：" + ex.message + "   ---- " + str)
            }

        }

    }

    @Throws(ClassNotFoundException::class)
    private fun findClasses(lpparam: XC_LoadPackage.LoadPackageParam, className: String) {
        val logCls = lpparam.classLoader.loadClass(className)
        XposedHelpers.findAndHookConstructor(logCls, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                super.beforeHookedMethod(param)
                XposedBridge.log("---------HookMethod--------------\t @ $className")
            }
        })
    }
}