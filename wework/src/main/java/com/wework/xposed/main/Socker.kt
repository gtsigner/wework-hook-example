package com.wework.xposed.main

import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.wework.xposed.common.bean.ContactGroup
import com.wework.xposed.common.bean.Member
import com.wework.xposed.common.bean.SocketSettings
import com.wework.xposed.core.Logger
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.lang.Exception
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList

class Socker(val setting: SocketSettings) {
    companion object Event {
        const val EVENT_GET_USER_INFO = "wx.get_user_info"
        const val EVENT_WX_SEND_MESSAGE = ""
        const val TAG = "Socker"
    }

    /**
     * SocketIo实例
     */
    private val socket: Socket

    // 是否链接
    var isConnected: Boolean = false
        get() {
            return socket.connected()
        }


    //是否已经授权
    var isAuthorization = false
    val options = IO.Options()

    init {
        options.query = "client_type=wework&appid=${setting.appId}"
        options.reconnection = setting.reconnected
        options.forceNew = false

        //链接
        socket = IO.socket(setting.uri, this.options)
        wrapper(socket)
        events(socket)
    }

    fun auth(username: String, password: String) {
        Logger.info(TAG, "授权登录:$username,$password")
    }

    fun auth(accessToken: String) {

    }

    private fun events(socket: Socket) {
        socket.on("wk.cmd.send_message") { args ->
            Logger.debug("send_message", "$args")
        }
    }

    /**
     *包装事件
     */
    private fun wrapper(socket: Socket) {
        socket.on(Socket.EVENT_DISCONNECT) {
            isConnected = false
            Logger.info(TAG, "服务器断开了链接")
        }
        socket.on(Socket.EVENT_CONNECT) {
            Logger.info(TAG, "服务端已经链接成功了")
        }
        socket.on(Socket.EVENT_PONG) { args ->
        }
        socket.on("pong") {
            Logger.debug("pong", "life is facticticl")
        }
        /**
         * 获取会话的列表
         */
        socket.on("wk.get_conversation_list") {
            if (it.isEmpty()) return@on
            Logger.debug("get_conversation_list", "get_conversation_list,$it")
            val list = Mainer.getWorkApi()?.conversationList
            val ack = it[it.size - 1]
            Logger.debug("wk.get_conversation_list", "$list")
            val json = JSON.parseArray(list, ArrayList<ContactGroup>()::class.java) as ArrayList<*>
            if (ack is Ack) {
                ack.call(json)
            }
        }
        /**
         * 通过id获取会话
         * 参数：
         * {id:LONG}
         */
        socket.on("wk.get_conversation_by_id") {
            if (it.isEmpty()) return@on
            var res = ""
            try {
                val param = it[0] as JSONObject
                val id = param.getLong("id")
                res = Mainer.getWorkApi()?.getConversationById(id)!!
                Logger.debug("wk.get_conversation_by_id", "$id,$res")

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            val ack = it[it.size - 1]
            if (ack is Ack) {
                val contact = JSON.parseObject(res, ContactGroup::class.java) as ContactGroup
                ack.call(contact)
            }
        }

        socket.on("wk.get_login_user") {
            if (it.isEmpty()) return@on
            val user = Mainer.getWorkApi()?.loginUser
            val ack = it[it.size - 1]
            if (ack is Ack) {
                val json = JSON.parseObject(user)
                ack.call(json)
            }
        }
        socket.on("wk.refresh_app") {
            if (it.isEmpty()) return@on
            val user = Mainer.getWorkApi()?.loginUser
            val rooms = Mainer.getWorkApi()?.conversationList
            //返回Obj
            //json.put("user", JSON.parse(user))
            val json = JSONObject()
            json.put("data", "")
            json.put("user", user)
            json.put("list", rooms)
            socket.emit("wk.register", json)
        }

        socket.on("wk.send.text_message") {
            if (it.isEmpty()) return@on
            val param = it[0] as JSONObject
            val id = param.getLong("id")
            val message = param.getString("message")
            val res = Mainer.getWorkApi()?.sendTextMessage(id, message)
            //ACK
            val ack = it[it.size - 1]
            val json = JSONObject()
            json.put("success", res)
            if (ack is Ack) {
                ack.call(json)
            }
        }
    }

    fun getSocket(): Socket {
        return socket
    }

    fun connect(): Boolean {
        socket.connect()
        return true
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun id(): String {
        return socket.id()
    }

}
