package com.example.douyin.base

import android.util.Log
import com.example.douyin.hooker.HookUtil
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HookMain : IXposedHookLoadPackage {
    //是否使用hotload
    private val hotLoad = true
    private lateinit var classLoader: ClassLoader


    //package com.ss.sys.ces.c;
    //package com.bytedance.common.utility.DeviceUtils;
    object Version5 {
        const val C = "com.ss.sys.ces.c"
        const val DeviceUtils = "com.bytedance.common.utility.DeviceUtils"

        object Method {
            const val IsInstallXposed = "isInstallXposed"
        }

        object Cls {

        }
    }

    class HookerClassMethod(var cls: String, var methods: Array<String>)

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        //通过share 来配置是否使用hot
        //HotXposed.hook(HookerDispatcher::class.java, lpparam)
        ///data/app/com.ss.android.ugc.aweme-1
        if (lpparam.packageName == "com.ss.android.ugc.aweme") {
            Log.d("xposed", "Hook抖音${lpparam.processName}")
            this.classLoader = lpparam.classLoader

            //hook
            val list = ArrayList<HookerClassMethod>()
            //list.add(HookerClassMethod())
            list.add(HookerClassMethod("com.ss.sys.ces.c.a", arrayOf("a", "b", "c", "d")))
            list.add(HookerClassMethod("com.cmic.sso.sdk.d.q", arrayOf("a")))
//            list.add(HookerClassMethod("com.ss.android.ugc.aweme.account.login.loginlog.a", arrayOf("a")))
            //list.add(HookerClassMethod("com.ss.android.ugc.aweme.common.j", arrayOf("a")))
            //list.add(HookerClassMethod("com.meituan.robust.PatchProxy", arrayOf("isSupport")))
//            list.add(HookerClassMethod("com.bytedance.common.utility.DeviceUtils", arrayOf("isInstallXposed")))

            val acls = findClass("com.ss.sys.ces.c.a")

            XposedHelpers.findAndHookMethod(acls, "a", object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    if (param != null) {
                        HookUtil.printLogs("DouYin->a", param)
                    }
                    return false
                }
            })
            XposedHelpers.findAndHookMethod(acls, "b", object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    if (param != null) {
                        HookUtil.printLogs("DouYin->b", param)
                    }
                    return false
                }
            })


//            list.forEach {
//                val cls = findClass(it.cls)
//                Log.d("xposed", "Hook:${it.cls}")
//                it.methods.forEach { method ->
//                    XposedBridge.hookAllMethods(cls, method, object : XC_MethodHook() {
//                        override fun afterHookedMethod(param: MethodHookParam?) {
//                            if (param != null) {
//                                HookUtil.printLogs("DouYin-${it.cls}->$method：", param)
//                            }
//                        }
//                    })
//                }
//            }

        }
    }

    private fun findClass(cls: String): Class<*>? {
        return XposedHelpers.findClass(cls, this.classLoader)
    }

}