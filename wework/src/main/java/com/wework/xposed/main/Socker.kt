package com.wework.xposed.main

import com.wework.xposed.core.Logger
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

class Socker {
    companion object Event {
        const val EVENT_GET_USER_INFO = "wx.get_user_info"
        const val EVENT_WX_SEND_MESSAGE = ""
        const val TAG = "Socker"
    }

    /**
     * SocketIo实例
     */
    private val socket: Socket
    /**
     * 服务器地址
     */
    private var uri: String = "http://192.168.10.161:3008"

    // 是否链接
    var isConnected: Boolean = false
        get() {
            return socket.connected()
        }


    //是否已经授权
    var isAuthorization = false

    init {
        //链接SocketIo
        val options = IO.Options()
        options.query = "client_type=wework"
        //链接
        socket = IO.socket(uri, options)
        wrapper(socket)
        events(socket)
    }

    fun auth(username: String, password: String) {
        Logger.info(TAG, "授权：$uri")
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
            Logger.debug("pong", "激活PONG")
            val ack = it[it.size - 1] as Ack
            ack.call("Success Pong")
        }
        socket.on("get_conversation_list") {
            Logger.debug("get_conversation_list", "get_conversation_list")
            

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
