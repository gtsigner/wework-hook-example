package com.example.xposed.wework

import android.os.Handler
import android.os.Message
import android.os.MessageQueue
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.example.aidl.IWeWorkEmitListener
import com.example.xposed.common.bean.TaskMessage
import com.example.xposed.core.Logger
import com.example.xposed.core.WkClientType
import com.example.xposed.core.WkEvent
import com.example.xposed.wework.hooker.ConversationHelper
import com.example.xposed.wework.hooker.MessageHooker
import java.util.*
import kotlin.concurrent.thread


object TaskHandler {
    object MessageType {
        const val IntvalTime = 1
        const val MessageCall = 2
    }

    /**
     * 构建Handler处理消息
     */
    fun buildMessage(senderType: Int, event: String, data: String): Message {
        val message = Message()
        message.what = MessageType.MessageCall
        message.arg1 = senderType
        //message.
        message.obj = TaskMessage(event = event, data = data)
        return message
    }

    init {
        WkGlobal.weWorkService.all(WkClientType.WeWork, object : IWeWorkEmitListener.Stub() {
            override fun callback(senderType: Int, event: String, data: String) {
                Logger.debug("微信端收到指令", "Sender=$senderType,Event=$event,Data=$data")
                if (event == WkEvent.WK_CMD_SEND_MESSAGE) {
                    //解析
                    TaskHandler.handler.sendMessage(TaskHandler.buildMessage(senderType, event, data))
                }
            }
        })
    }

    val handler = Handler(object : Handler.Callback {

        override fun handleMessage(mess: Message?): Boolean {
            when (mess?.what) {
                MessageType.IntvalTime -> {
                    val user = MessageHooker.getProfile()
                    Logger.info("当前用户", "$user")
                    //把这个讯息发送到客户端

                    val json = JSONObject()
                    json["user"] = user
                    json["isLogin"] = true

                    //发送数据到指定的客户端
                    WkGlobal.weWorkService.send(WkClientType.Client, "UserLogin", JSON.toJSONString(json))
                }
                MessageType.MessageCall -> {
                    val taskMessage = mess.obj as TaskMessage
                    val msg = JSON.parseObject(taskMessage.data, com.example.xposed.common.bean.Message::class.java)

                    //检测id
                    if (msg.reciverId == 0.toLong()) {
                        val cls = ConversationHelper.getConversationList()
                        cls.forEach {
                            com.example.xposed.wework.services.Message.sendTextMessageToGroup(it.id, msg.message)
                        }
                    } else {
                        com.example.xposed.wework.services.Message.sendTextMessageToGroup(msg.reciverId, msg.message)
                    }
                }
            }


            //测试@test
//            MessageHooker.getUserInfoFromConversationEngine(1688851301404961, 0)
//            MessageHooker.getUserInfoFromConversationEngine(1688851301404438)
//            com.example.xposed.wework.services.Message.sendTextMessageToGroup(1688851301404438, "@Godtoy 恭喜您您好,Time:" + Date().time)
//            com.example.xposed.wework.services.Message.sendTextMessageToGroup(6667434997889862049, "@Godtoy 恭喜您您好,Time:" + Date().time)
            return true
        }
    })
    private var isStarted = false
    private var interval = 10 * 1000

    /**
     * 启动任务
     */
    fun start() {
        thread(true) {
            while (true) {
                Thread.sleep(interval.toLong())
                handler.sendEmptyMessage(1)
            }
        }
    }

}