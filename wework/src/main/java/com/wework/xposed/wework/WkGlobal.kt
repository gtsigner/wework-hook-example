package com.wework.xposed.wework

import android.annotation.SuppressLint
import android.content.Context
import com.wework.aidl.IWeWorkService
import com.wework.xposed.base.WaitChannel
import com.wework.xposed.core.Logger
import com.wework.xposed.core.WeWorkService
import com.wework.xposed.wework.hooker.DatabaseHooker
import com.wework.xposed.wework.hooker.InitHooker
import com.wework.xposed.wework.hooker.MessageHooker
import com.wework.xposed.wework.hooker.NotificationHooker
import com.wework.xposed.wework.util.AppUtil
import de.robv.android.xposed.callbacks.XC_LoadPackage


@SuppressLint("StaticFieldLeak")
object WkGlobal {
    var hooked = false
    /**
     * Hooker
     */
    private val hookers = arrayOf(
            InitHooker,
            DatabaseHooker,
            MessageHooker,
            NotificationHooker
    )

    @Synchronized
    fun start(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!WkObject.APP_DEBUG && lpparam.processName != WkGlobal.X_WK_PROCESS_NAME || hooked) {
            return
        }
        val versionName = AppUtil.getApplicationVersion(lpparam.packageName)
        val str = "支持企业微信版本:${WkGlobal.X_WK_supportedVersion},当前版本:$versionName,进程名称:${lpparam.processName}"
        if (WkGlobal.X_WK_supportedVersion != versionName) {
            Logger.info("微信版本不支持请更新到新版", str)
            return
        }
        Logger.info("Hook挂在成功", str)

        //全局
        WkGlobal.classLoader = lpparam.classLoader
        WkGlobal.workLoader = lpparam.classLoader
        WkGlobal.xpParam = lpparam

        //挂在所有得Hooker
        hookers.forEach {
            it.executeHook()
        }
        WkGlobal.taskHandler.start()
        hooked = true
    }


    /**
     * 用于防止其他线程在初始化完成之前访问 WechatGlobal的变量
     */
    private val initChannel = WaitChannel()

    /**
     * 若初始化操作耗费2秒以上, 视作初始化失败, 直接让微信开始正常运行
     */
    @Suppress("MemberVisibilityCanBePrivate")
    const val INIT_TIMEOUT = 2000L // ms
    /**
     * 单元测试模式的开关, 只应该在单元测试中打开
     */
    @Volatile
    var wxUnitTestMode: Boolean = false


    var wkFinishLoaded = false


    lateinit var classLoader: ClassLoader

    /**
     * 微信 APK 所使用的 ClassLoader, 用于加载 Class 对象
     *
     * 如果初始化还未完成的话, 访问该对象的线程会自动阻塞 [INIT_TIMEOUT] ms
     */
    @Volatile
    var workLoader: ClassLoader? = null
        get() {
            if (!wxUnitTestMode) {
                initChannel.wait(INIT_TIMEOUT)
                initChannel.done()
            }
            return field
        }

    lateinit var xpParam: XC_LoadPackage.LoadPackageParam
    lateinit var ctx: Context

    //全局包
    const val X_PACKAGE_NAME: String = "com.wework.xposed"

    const val X_WK_PACKAGE_NAME: String = "com.tencent.wework"
    //企业微信进程名
    private const val X_WK_PROCESS_NAME: String = "com.tencent.wework"
    //支持企业微信的版本
    private const val X_WK_supportedVersion = "2.7.2"

    /**
     * WorkService
     */
    val weWorkService = WeWorkService.getService() as IWeWorkService
    private val taskHandler = TaskHandler

}
