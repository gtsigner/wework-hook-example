package com.example.xposed.core

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import com.example.aidl.IWeWorkCallBack

import com.example.aidl.IWeWorkService

import de.robv.android.xposed.XposedHelpers

/**
 * 自定义的系统服务
 */
class WeWorkService(private val context: Context) : IWeWorkService.Stub() {

    private var weworkReady = false
    private var clientReady = false


    /**
     * 发送消息
     * @param what 类型
     * @param messageType 日志
     * @param message
     */
    override fun sendMessage(what: Int, messageType: Int, message: String): Boolean {
        callbacks.forEach {
            it.onMessage(CALLBACK_WHAT_WEWORK, MessageType.Send, message)
        }
        return true
    }

    /**
     * 注册
     * @param callback 注册回调函数
     */
    override fun registerCallback(callback: IWeWorkCallBack) {
        callbacks.add(callback)
    }

    /**
     * 卸载回调
     * @param callback 注册广播通知回调函数
     */
    override fun unregisterCallback(callback: IWeWorkCallBack) {
        callbacks.remove(callback)
    }

    /**
     * 回调
     * @val callbacks
     */
    private val callbacks = ArrayList<IWeWorkCallBack>()

    init {
        Logger.debug(TAG, "已经实例化")
    }

    override fun weWorkReady() {
        Logger.debug(TAG, "企业微信初始化")
        weworkReady = true
    }

    override fun clientReady() {
        Logger.debug(TAG, "客户端已初始化")
        clientReady = true
    }

    companion object {


        private const val TAG = "WeWorkService"
        @SuppressLint("StaticFieldLeak")
        private var service: IWeWorkService? = null

        const val CALLBACK_WHAT_WEWORK = 1
        const val CALLBACK_WHAT_CLIENT = 2

        object MessageType {
            //发送消息
            const val Send = 1
        }


        /**
         * 注册服务
         *
         * @param context
         * @param classLoader
         */
        fun register(context: Context, classLoader: ClassLoader) {
            //注册
            val svcManger = XposedHelpers.findClass("android.os.ServiceManager", classLoader)
            val wkService = WeWorkService(context)
//            XposedHelpers.callStaticMethod(svcManager,
//                    /* methodName */"addService",
//                    /* name       */getServiceName(),
//                    /* service    */ customService,
//                    /* allowIsolated */ true);
            //注册系统服务
            XposedHelpers.callStaticMethod(svcManger, "addService", serviceName, wkService, true)
            Logger.debug("系统服务注册", "注册成功")
        }

        /**
         * 获取服务名称
         *
         * @return 服务名称
         */
        private val serviceName: String
            get() = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) "user." else "") + "WeWorkService"


        @SuppressLint("PrivateApi")
        fun getService(): IWeWorkService? {
            if (service == null) {
                val svcManager = Class.forName("android.os.ServiceManager")
                val method = svcManager.getDeclaredMethod("getService", String::class.java)
                //IBinder
                val binder = method.invoke(null, serviceName) ?: return null
                service = IWeWorkService.Stub.asInterface(binder as IBinder) as IWeWorkService
            }
            return service
        }
    }

}
