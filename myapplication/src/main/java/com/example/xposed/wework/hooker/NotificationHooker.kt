package com.example.xposed.wework.hooker

import com.example.xposed.wework.WkGlobal
import com.example.xposed.wework.WkObject
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

object NotificationHooker : Hooker {
    override fun executeHook() {
        hookNotificationInfo()

    }

    private fun hookNotificationInfo() {
        val notificationInfo = WkObject.getClass("com.tencent.wework.foundation.notification.NotificationInfo")
        val msgCls = WkObject.getClass("com.tencent.wework.foundation.model.Message")

        /**
         * pushService
         */
        val pushService = XposedHelpers.findClass("com.tencent.wework.foundation.logic.PushService", WkGlobal.workLoader)

        /**
         * 系统通知收到消息后会创建一个对象，这个对象参数第一个是Message，第二个是一个字符串
         */
        XposedHelpers.findAndHookConstructor(notificationInfo, Any::class.java, Any::class.java, Long::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val thisObject = param.thisObject
                //model.Message
                val message = param.args[0] as Any
                val jid = param.args[2] as Long
                val str = param.args[1] as String
                val msg = MessageHooker.getMessageInfoFromMessageEntry(message)

                MessageHooker.wxGetConversionRemoteIdByMessage(message)

                //解析数据
                XposedBridge.log(" NotificationInfo->$thisObject,$message,$str,$jid")
            }
        })


        //MessageItem下钩子
        val efdCls = XposedHelpers.findClass("efd", WkGlobal.workLoader)
        //ConversationEngine
        val conversationEngine = XposedHelpers.findClass("ecz", WkGlobal.workLoader)

        //router
        val appRouter = XposedHelpers.findClass("com.tencent.wework.api.config.AppRouter", WkGlobal.workLoader)
        arrayOf("kj").forEach {
            MessageHooker.hookAndDebugAllMethods(appRouter, it, "AppRouter")
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