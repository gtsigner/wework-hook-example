package com.example.xposed.wework

import com.bugfender.sdk.Bugfender
import com.example.aidl.IWeWorkEmitListener
import com.example.xposed.core.Logger
import com.example.xposed.core.WeWorkService
import com.example.xposed.core.WkClientType
import com.example.xposed.core.WkEvent
import com.example.xposed.main.MainActivity
import com.example.xposed.wework.main.HookVersion
import com.example.xposed.wework.hooker.MessageHooker
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object MainHooker {
    private lateinit var lpparam: XC_LoadPackage.LoadPackageParam
    /**
     * 执行真实的hook
     */
    fun execute(lpparam: XC_LoadPackage.LoadPackageParam) {

        //获取包
        if (lpparam.packageName == HookVersion.X_PACKAGE_NAME) {
            try {
                val cls = lpparam.classLoader.loadClass(MainActivity::class.java.name)
                startHook(lpparam, cls)
                XposedBridge.log("Hook Success：" + lpparam.packageName)
            } catch (e: ClassNotFoundException) {
                XposedBridge.log("Hook Fail:" + "主类无法找到")
                Bugfender.d("主类无法找到", "123")
                return
            }
        }

        /**
         * 企业微信
         */
        if (lpparam.packageName == "com.tencent.wework" && lpparam.processName == "com.tencent.wework") {
            weworkHandle(lpparam)
        }
    }


    /**
     * 处理企业微信的Hook
     */
    private fun weworkHandle(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            XposedHelpers.findClass(WkObject.Message.C.SQLiteDatabase, lpparam.classLoader)
            //全局
            WkGlobal.classLoader = lpparam.classLoader
            WkGlobal.workLoader = lpparam.classLoader
            WkGlobal.xpParam = lpparam
            MainHooker.lpparam = lpparam
            startHookWeWork(lpparam)

        } catch (ex: Exception) {
            XposedBridge.log(ex.message)
        }
    }


    /**
     * 启动weWorkHook
     *
     * @param lpparam
     */
    @Throws(ClassNotFoundException::class)
    private fun startHookWeWork(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedBridge.log("企业微信版本号: 0.1----Xposed版本号:${XposedBridge.getXposedVersion()}")
        TaskHandler.start()
        val service = WeWorkService.getService()

        arrayOf("ecz").forEach {
            try {
                findClasses(lpparam, it)
                XposedBridge.log("---Find Success：$it")
            } catch (ex: Exception) {
                XposedBridge.log("类没有找到：" + ex.message + "   ---- " + it)
            }
        }

        //消息Hook
        MessageHooker.executeHook()
    }


    @Throws(ClassNotFoundException::class)
    private fun findClasses(lpparam: XC_LoadPackage.LoadPackageParam, className: String) {
        val logCls = lpparam.classLoader.loadClass(className)
        XposedHelpers.findAndHookConstructor(logCls, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                super.beforeHookedMethod(param)
            }
        })
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
        XposedHelpers.findAndHookMethod(clazz, "showToast ", object : XC_MethodHook() {
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
        XposedHelpers.findAndHookMethod(clazz, "fuck", String::class.java, object : XC_MethodReplacement() {

            @Throws(Throwable::class)
            override fun replaceHookedMethod(param: XC_MethodHook.MethodHookParam): Any? {
                XposedBridge.log("FuckYou")
                return null
            }
        })
    }


}