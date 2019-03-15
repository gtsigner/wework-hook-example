package com.wework.xposed.wework

import android.os.Looper
import com.alibaba.fastjson.JSON
import com.wework.xposed.IWeWorkApi
import com.wework.xposed.common.utils.CheckUtil
import com.wework.xposed.wework.wxapi.ConversationApi
import com.wework.xposed.wework.wxapi.ProfileApi
import java.lang.Exception

class WorkApi : IWeWorkApi.Stub() {
    /**
     * 发送文本消息
     * @param cid conversation_id 也就是remoteID
     * @param message message
     */
    override fun sendTextMessage(cid: Long, message: String?): Boolean {
        CheckUtil.prepare()
        return if (cid == 0.toLong()) {
            ConversationApi.sendMessageToAll(message ?: "")
        } else {
            ConversationApi.sendTextMessageByRemoteId(cid, message ?: "空")
        }
    }

    override fun getConversationList(): String {
        CheckUtil.prepare()
        val list = ConversationApi.getConversationList()
        return JSON.toJSONString(list)
    }

    /**
     * 获取回话群组信息
     */
    override fun getConversationById(cid: Long): String {
        CheckUtil.prepare()
        val list = ConversationApi.getConversationItemFromRemoteId(cid)
        return JSON.toJSONString(list)
    }

    /**
     * 获取用户列表
     */
    override fun getConversationUsers(id: Long): String {
        CheckUtil.prepare()
        val list = ConversationApi.getConversationItemFromRemoteId(id)
        return JSON.toJSONString(list)
    }

    override fun getLoginUser(): String {
        CheckUtil.prepare()
        val user = ProfileApi.getCurrentLoginUserProfile()
        return JSON.toJSONString(user)
    }

    override fun sayHello(): String {
        CheckUtil.prepare()
        return ""
    }
}