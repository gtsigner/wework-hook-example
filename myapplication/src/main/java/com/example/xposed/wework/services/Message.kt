package com.example.xposed.wework.services

import com.example.xposed.wework.WkGlobal
import com.example.xposed.wework.WkObject
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

object Message {
    /**
     * 发送文字消息
     */
    fun sendTextMessageToGroup(convLocalid: Long, message: String) {
        val messageManager = WkGlobal.classLoader.loadClass(WkObject.MessageSender.C.MessageManager)
        //上下文
        val externalGroupMessageListActivity = WkGlobal.classLoader.loadClass(WkObject.MessageSender.C.ExternalGroupMessageListActivity)
        val activityObj = externalGroupMessageListActivity.newInstance()
        //public static boolean a(Context context, long j, CharSequence charSequence, boolean z)
        val callRes = XposedHelpers.callStaticMethod(messageManager, WkObject.MessageSender.M.GroupTextSend, activityObj, convLocalid, message, false) as Boolean
        XposedBridge.log("调用结果：$callRes")
    }


    fun NewMessage() {

    }

}