package com.wework.xposed.wework

import android.os.Looper
import com.alibaba.fastjson.JSON
import com.wework.xposed.IWeWorkApi
import com.wework.xposed.wework.wxapi.ConversationApi
import com.wework.xposed.wework.wxapi.ProfileApi

class WorkApi : IWeWorkApi.Stub() {

    override fun getConversationList(): String {
        val list = ConversationApi.getConversationList()
        return JSON.toJSONString(list)
    }

    override fun getConversationById(id: Long): String {
        val list = ConversationApi.getConversationItemFromRemoteId(id)
        return JSON.toJSONString(list)
    }

    override fun getConversationUsers(id: Long): String {
        val list = ConversationApi.getConversationItemFromRemoteId(id)
        return JSON.toJSONString(list)
    }

    override fun sendMessage(id: Long, message: String?): Int {
        if (id == 0.toLong()) {
            ConversationApi.sendMessageToAll(message ?: "")
        } else {
            ConversationApi.sendTextMessageByLocalId(id, message ?: "空")
        }
        return 1
    }

    override fun getLoginUser(): String {
        val user = ProfileApi.getCurrentLoginUserProfile()
        return JSON.toJSONString(user)
    }

    override fun sayHello(): String {
        return ""
    }
}