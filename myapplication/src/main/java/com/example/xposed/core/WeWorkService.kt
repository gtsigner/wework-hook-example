package com.example.xposed.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.*
import com.example.aidl.IWeWorkCallBack
import com.example.aidl.IWeWorkEmitListener

import com.example.aidl.IWeWorkService

import de.robv.android.xposed.XposedHelpers
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.collections.ArrayList

object WkEvent {
    const val EVENT_ALL = "*"
    const val WK_CMD_SEND_MESSAGE = "wk.cmd.send_message"
}

object WkClientType {
    const val Client = 1
    const val WeWork = 2
}

/**
 * 自定义的系统服务
 */
class WeWorkService(private val context: Context) : IWeWorkService.Stub() {

    /**
     * 创建Listener
     * @param reciverType
     * @param event
     * @param callback
     * @param hashId
     */
    inner class Listener(var reciverType: Int, var event: String, var callback: IWeWorkEmitListener, private val hashId: String) {}

    companion object {
        private const val TAG = "WeWorkService"
        @SuppressLint("StaticFieldLeak")
        private var service: IWeWorkService? = null

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

    private val obClients = ArrayList<Listener>()

    /**
     * 发送消息给指定人
     * @param reciver 接受者客户端类型
     * @param event 事件
     * @param data 数据
     */
    override fun send(reciver: Int, event: String, data: String) {
        Logger.debug("send", "reciver=$reciver,event=$event,data=$data")
        obClients.forEach {
            Logger.debug("查看:$reciver", "$it")
            if ((it.event == event || it.event == WkEvent.EVENT_ALL) && it.reciverType == reciver) {
                call(it, 0, event, data)
            }
        }
    }

    private fun call(listener: Listener, senderType: Int, event: String, data: String) {
        try {
            listener.callback.callback(0, event, data)
        } catch (ex: Exception) {
            ex.printStackTrace()
            //如果这里出现了任何异常，那么消息会直接丢失，因为会导致远程异常无法捕获
            obClients.remove(listener)
        }
    }

    /**
     * 广播给所有客户都能接受
     * @param event
     * @param data
     */
    override fun boastcast(event: String, data: String) {
        Logger.debug("boastcast", "event=$event,data=$data")
        obClients.forEach {
            if (it.event == event || it.event == WkEvent.EVENT_ALL) {
                call(it, 0, event, data)
            }
        }
    }

    /**
     * 发送数据到其他的所有客户端推送
     * @param reciver 接受者
     * @param event 事件
     * @param data 数据原封不动进行推送
     */
    override fun emit(senderType: Int, event: String, data: String) {
        Logger.debug("emit", "$senderType,event=$event,data=$data")
        obClients.forEach {
            if ((it.event == event || it.event == WkEvent.EVENT_ALL) && it.reciverType != senderType) {
                call(it, senderType, event, data)
            }
        }
    }

    /**
     * 添加监听器
     */
    override fun on(event: String, reciverType: Int, callback: IWeWorkEmitListener) {
        val listener = Listener(reciverType, event, callback, "")
        obClients.add(listener)
        Logger.debug("注册WeWorkService", "$reciverType,event=$event,count=${obClients.size}")
    }

    /**
     * 监听所有事件信息消息
     */
    override fun all(reciverType: Int, callback: IWeWorkEmitListener) {
        val listener = Listener(reciverType, WkEvent.EVENT_ALL, callback, "")
        obClients.add(listener)
        Logger.debug("注册WeWorkService", "$reciverType,event=*,count=${obClients.size}")
    }

    /**
     * 解绑所有listener
     */
    override fun un(event: String) {

    }

    override fun clear() {
        obClients.clear()
    }


    /**
     * 回调
     * @val callbacks
     */
    private val callbacks = ArrayList<IWeWorkCallBack>()


    private var weworkReady = false
    private var clientReady = false

    /**
     * 注册
     * @param callback 注册回调函数
     */
    override fun registerCallback(reciver: Int, callback: IWeWorkCallBack) {
        callbacks.add(callback)
    }

    /**
     * 卸载回调
     * @param callback 注册广播通知回调函数
     */
    override fun unregisterCallback(reciver: Int, callback: IWeWorkCallBack) {
        callbacks.remove(callback)
    }


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


}
