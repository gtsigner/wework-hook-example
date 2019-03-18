package com.wework.xposed.wework.util

import com.wework.xposed.common.bean.Message
import com.wework.xposed.core.Logger
import de.robv.android.xposed.XposedHelpers

object MessageUtil {
    /**
     * 类下相互转化
     * 放入实体类Model：com.tencent.wework.foundation.model.Message
     * 返回数据类型：com.tencent.wework.foundation.model.pb.WwRichMessage$RichMessage
     */
    fun convertWwMessageIMessageToMessage(message: Any): Message {
        val info = XposedHelpers.callMethod(message, "getInfo") as Any
        val mmsg = getWwMessageMessageToMessage(info)
        //获取
        Logger.debug("Info", "$mmsg")
        return mmsg
    }

    private fun getWwMessageMessageToMessage(info: Any): Message {
        //群ID
        val id = XposedHelpers.getObjectField(info, "id") as Long
        //会话的ID
        val conversationId = XposedHelpers.getObjectField(info, "conversationId") as Long
        //消息类型 2 文字消息
        val contentType = XposedHelpers.getObjectField(info, "contentType") as Int
        //聊天人类型 0 私聊，1群聊
        val conversationType = XposedHelpers.getObjectField(info, "convType") as Int
        //发送人信息
        val sender = XposedHelpers.getObjectField(info, "sender") as Long
        //消息状态
        val state = XposedHelpers.getObjectField(info, "state") as Int
        //发送时间
        val sendTime = XposedHelpers.getIntField(info, "sendTime")
        //后面的参数是字节
        val content = XposedHelpers.getObjectField(info, "content") as ByteArray

        //进制转化
        val fuck = ArrayList<Byte>()
        content.forEachIndexed { index, byte ->
            if (index < 8) return@forEachIndexed
            fuck.add(byte)
        }
        Logger.debug("Message", String(fuck.toByteArray()))
        val msend = Message()
        msend.id = id
        msend.message = String(fuck.toByteArray())
        msend.conversationType = conversationType
        msend.conversationId = conversationId
        msend.senderId = sender
        msend.sendTime = sendTime
        msend.messageType = contentType
        return msend
    }
}