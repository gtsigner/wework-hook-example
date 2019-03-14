package com.wework.xposed.main

import com.wework.aidl.IWeWorkEmitListener
import com.wework.xposed.core.Logger
import com.wework.xposed.core.WeWorkService
import com.wework.xposed.core.WkClientType
import io.socket.client.Ack

object Mainer {
    val service = WeWorkService.getService()
    lateinit var socker: Socker
    fun start() {
        socker = Socker()
        socker.connect()

        // 绑定回调事件
        service?.all(WkClientType.Client, object : IWeWorkEmitListener.Stub() {
            override fun callback(senderType: Int, event: String?, data: String?) {
                //直接转发上云
                val str = "$senderType,$event,$data"
                Logger.debug("Socket端转发消息", str)
                if (null != event && null != data) {
                    socker.getSocket().emit(event, data, Ack {
                        Logger.debug("回复已确认", "$it")
                    })
                }
            }
        })
    }
}