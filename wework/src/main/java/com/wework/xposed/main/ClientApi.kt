package com.wework.xposed.main

import com.wework.xposed.IClientApi
import com.wework.xposed.common.utils.CheckUtil
import java.lang.Exception


interface Callback {
    fun onReciveMessage(conversationId: Long, message: String)
}

class ClientApi : IClientApi.Stub() {

    var callback: Callback? = null
    /**
     * 接受的到消息
     */
    override fun onReciveMessage(conversationId: Long, message: String?): String {
        CheckUtil.prepare()
        try {
            callback!!.onReciveMessage(conversationId, message ?: "")
        } catch (ex: Exception) {

        }
        return "Success"
    }
}