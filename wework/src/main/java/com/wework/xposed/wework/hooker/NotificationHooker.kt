package com.wework.xposed.wework.hooker

import com.wework.xposed.core.Logger
import com.wework.xposed.wework.WkGlobal
import com.wework.xposed.wework.WkObject
import com.wework.xposed.wework.util.HookUtil
import com.wework.xposed.wework.util.MessageUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

object NotificationHooker : Hooker {
    const val TAG = "NotificationHooker"
    private var hooked = false
    override fun executeHook() {
        if (hooked) return
        val cls = WkObject.getClass("com.tencent.wework.foundation.notification.WeworkNotificationListener")
        /**
         * 微信所有通知都会走这儿
         */
        XposedBridge.hookAllMethods(cls, "onObserve", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                if (param?.args == null || param.args.size != 2) return
                //判断是否是Message
                val type = param.args[0] as Int
                val notificationInstance = param.args[1]
                HookUtil.printLogs(TAG, param)
                var message = XposedHelpers.getObjectField(notificationInstance, "mDetail")
                val str = XposedHelpers.getObjectField(notificationInstance, "mDetail2") ?: "none"
                if (type == 2 && message != null) {
                    //收到消息
                    message = MessageUtil.convertWwMessageIMessageToWwRichMessageIRichMessage(message)
                    Logger.info("收到消息通知", "$message 内容:$message")
                    WkGlobal.weWorkService.clientApi?.onReciveMessage(100L, "Fuckyou$message")
                }
            }
        })
        //hookNotificationInfo()
        hooked = true
    }
}