package com.wework.xposed.wework.hooker

import com.wework.xposed.core.Logger
import com.wework.xposed.wework.WkGlobal
import com.wework.xposed.wework.WkObject
import com.wework.xposed.wework.util.HookUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

object NotificationHooker : Hooker {
    const val TAG = "NotificationHooker"
    override fun executeHook() {
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
                val message = XposedHelpers.getObjectField(notificationInstance, "mDetail")
                val str = XposedHelpers.getObjectField(notificationInstance, "mDetail2") ?: "none"
                if (type == 2 && message != null) {
                    //收到消息
                    Logger.info("收到消息通知", "$message 内容:$str")
                }
            }
        })
        //hookNotificationInfo()
    }

    private fun hookNotificationInfo() {
        val notificationInfo = WkObject.getClass("com.tencent.wework.foundation.notification.NotificationInfo")
        val msgCls = WkObject.getClass("com.tencent.wework.foundation.model.Message")

        /**
         * pushService
         */
        val pushService = XposedHelpers.findClass("com.tencent.wework.foundation.logic.PushService", WkGlobal.workLoader)


        //MessageItem下钩子
        val efdCls = XposedHelpers.findClass("efd", WkGlobal.workLoader)

        //router
        val appRouter = XposedHelpers.findClass("com.tencent.wework.api.config.AppRouter", WkGlobal.workLoader)
        arrayOf("kj").forEach {
            hookAndDebugAllMethods(appRouter, it, "AppRouter")
        }

//        arrayOf("a", "b", "d").forEach {
//            hookAndDebugAllMethods(conversationEngine, it, "ConversationEngine")
//        }
////        arrayOf("a", "b", "d").forEach {
////            hookAndDebugAllMethods(efdCls, it, "MessageItem")
////        }
//        val messageManager = WkGlobal.classLoader.loadClass("com.tencent.wework.msg.model.MessageManager")
//        arrayOf("onTPFEvent", "ba").forEach {
//            hookAndDebugAllMethods(messageManager, it, "MessageManager")
//        }

    }

}