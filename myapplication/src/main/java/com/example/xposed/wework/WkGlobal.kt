package com.example.xposed.wework

import com.example.aidl.IWeWorkService
import com.example.xposed.base.WaitChannel
import com.example.xposed.core.WeWorkService
import de.robv.android.xposed.callbacks.XC_LoadPackage


object WkGlobal {

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

    /**
     * WorkService
     */
    val weWorkService = WeWorkService.getService() as IWeWorkService
}
