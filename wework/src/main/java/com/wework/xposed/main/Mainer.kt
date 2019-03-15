package com.wework.xposed.main

import com.wework.xposed.IWeWorkApi
import com.wework.xposed.common.bean.SocketSettings
import com.wework.xposed.core.Logger
import com.wework.xposed.core.WeWorkService

object Mainer {
    val service = WeWorkService.getService()
    lateinit var socker: Socker
    private val clientApi = ClientApi()
    var running = false
    const val SHARE_PRE_NAME = "SETTINGS"

    init {
        clientApi.callback = object : Callback {
            override fun onReciveMessage(conversationId: Long, message: String) {
                //callback
                socker.getSocket().emit("wk.receive.message", message)
                Logger.debug("收到消息", message)
            }
        }
    }

    fun start(setting: SocketSettings) {
        socker = Socker(setting)
        service?.clientApi = clientApi
    }

    fun connect() {
        running = true
        socker.connect()
    }

    fun getWorkApi(): IWeWorkApi? {
        return service?.workApi
    }
}