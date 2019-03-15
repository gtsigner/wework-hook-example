package com.wework.xposed.core

import android.annotation.SuppressLint
import android.content.Context
import android.os.*


import com.wework.aidl.IWeWorkService
import com.wework.xposed.IClientApi
import com.wework.xposed.IWeWorkApi

import de.robv.android.xposed.XposedHelpers

object WkEvent {
    const val EVENT_ALL = "*"
    const val WK_CMD_SEND_MESSAGE = "wk.cmd.send_message"
    const val WK_CMD_GET_LOGIN_INFO = "wk.cmd.get_login_info"
    const val WK_CMD_GET_CONVERSATION_LIST = "wk.cmd.get_conversation_list"
    const val WK_CMD_GET_CONVERSATION_BY_ID = "wk.cmd.get_conversation_by_id"
    const val WK_CMD_GET_CONVERSATION_USERS_BY_ID = "wk.cmd.get_conversation_user_by_id"
}

object WkClientType {
    const val Client = 1
    const val WeWork = 2
}

/**
 * 自定义的系统服务
 */
class WeWorkService(private val context: Context) : IWeWorkService.Stub() {
    private var workApi: IWeWorkApi? = null
    private var clientApi: IClientApi? = null
    override fun setWorkApi(api: IWeWorkApi?) {
        workApi = api
    }

    override fun getWorkApi(): IWeWorkApi? {
        return workApi
    }

    override fun setClientApi(api: IClientApi?) {
        clientApi = api
    }

    override fun getClientApi(): IClientApi? {
        return clientApi
    }

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
