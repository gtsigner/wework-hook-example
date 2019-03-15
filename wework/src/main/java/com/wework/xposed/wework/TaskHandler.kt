package com.wework.xposed.wework

import android.os.Handler
import android.os.Message
import com.alibaba.fastjson.JSON
import com.wework.aidl.IWeWorkEmitListener
import com.wework.xposed.common.bean.TaskMessage
import com.wework.xposed.core.Logger
import com.wework.xposed.core.WkClientType
import com.wework.xposed.core.WkEvent
import com.wework.xposed.wework.wxapi.ConversationApi
import com.wework.xposed.wework.wxapi.ProfileApi
import kotlin.concurrent.thread

/**
 * Handler
 */
object TaskHandler {
    object MessageType {
        const val IntvalTime = 1
        const val MessageCall = 2
    }

    init {

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

    private fun handleMessage(mess: Message) {
        ConversationApi.sync()
        when (mess.what) {
            MessageType.IntvalTime -> {
                val user = ProfileApi.getCurrentLoginUserProfile()
                //发送数据到指定的客户端
                //WkGlobal.weWorkService.send(WkClientType.Client, "UserLogin", JSON.toJSONString(user))
            }
            MessageType.MessageCall -> {
                val taskMessage = mess.obj as TaskMessage
                //获取具体的行为
//                when (taskMessage.event) {
//                    //获取联系人列表
//                    WkEvent.WK_CMD_GET_CONVERSATION_LIST -> {
//                        val cls = ConversationApi.getConversationList()
//                        WkGlobal.weWorkService.send(WkClientType.Client, "ConversationList", JSON.toJSONString(cls))
//                    }
//                    //获取用户信息
//                    WkEvent.WK_CMD_GET_LOGIN_INFO -> {
//                        val user = ProfileApi.getCurrentLoginUserProfile()
//                        WkGlobal.weWorkService.send(WkClientType.Client, "UserLogin", JSON.toJSONString(user))
//                    }
//                    //获取
//                    WkEvent.WK_CMD_GET_CONVERSATION_BY_ID -> {
//                        val contact = ConversationApi.getConversationItemFromRemoteId(taskMessage.jsonObj.getLong("id"))
//                        WkGlobal.weWorkService.send(WkClientType.Client, "ConversationItem", JSON.toJSONString(contact))
//                    }
//                    //获取用户信息
//                    WkEvent.WK_CMD_GET_CONVERSATION_USERS_BY_ID -> {
//                        val contact = ConversationApi.getConversationItemFromRemoteId(taskMessage.jsonObj.getLong("id"))
//                        WkGlobal.weWorkService.send(WkClientType.Client, "ConversationItem", JSON.toJSONString(contact))
//                    }
//                    //发送消息
//                    WkEvent.WK_CMD_SEND_MESSAGE -> {
//                        val msg = JSON.parseObject(taskMessage.data, com.wework.xposed.common.bean.Message::class.java)
//                        //检测id
//                        if (msg.reciverId == 0.toLong()) {
//                            val cls = ConversationApi.getConversationList()
//                            cls.forEach {
//                                com.wework.xposed.wework.services.Message.sendTextMessageToGroup(it.id, msg.message)
//                            }
//                        } else {
//                            com.wework.xposed.wework.services.Message.sendTextMessageToGroup(msg.reciverId, msg.message)
//                        }
//                    }
//                }
            }

        }
    }

    private val handler = Handler(object : Handler.Callback {
        override fun handleMessage(mess: Message?): Boolean {
            if (mess != null) {
                TaskHandler.handleMessage(mess)
            }
            return true
        }
    })
    private var isStarted = false
    //5s 一次手动同步
    private var interval = 10 * 1000

    /**
     * 启动任务
     */
    fun start() {
        thread(true) {
            while (true) {
                Thread.sleep(interval.toLong())
                if (!WkGlobal.wkFinishLoaded) return@thread
                handler.sendEmptyMessage(1)
            }
        }
    }

}