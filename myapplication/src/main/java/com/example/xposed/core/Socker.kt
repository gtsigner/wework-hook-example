package com.example.xposed.core

import io.socket.client.IO
import io.socket.client.Socket
import kotlin.concurrent.thread

object Socker {
    object Event {
        const val EVENT_GET_USER_INFO = "wx.get_user_info"
        const val EVENT_WX_SEND_MESSAGE = ""
    }

    const val TAG = "Socker"

    /**
     * SocketIo实例
     */
    private val socket: Socket
    /**
     * 服务器地址
     */
    private var uri: String = "http://192.168.10.161:3008"

    // 是否链接
    val isConnected: Boolean
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

    }

    /**
     *包装事件
     */
    private fun wrapper(socket: Socket) {
        socket.on(Socket.EVENT_DISCONNECT) { args ->
            //123
        }

        socket.on(Socket.EVENT_CONNECT) {
            Logger.info("SocketIO", "服务端已经链接成功了")
        }
        socket.on(Socket.EVENT_PONG) { args ->
        }

    }


    fun connect(): Boolean {
        Logger.info(TAG, "自动链接到服务器：$uri")
        socket.connect()
        return true
    }

    fun disconnect() {
        Logger.info(TAG, "断开服务器链接：$uri")
        socket.disconnect()
    }

    fun id(): String {
        return socket.id()
    }
}
