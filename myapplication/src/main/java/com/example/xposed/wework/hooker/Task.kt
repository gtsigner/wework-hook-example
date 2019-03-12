package com.example.xposed.wework.hooker

import android.app.AndroidAppHelper
import android.content.Intent
import android.os.Handler
import android.os.Message
import com.example.xposed.core.Logger
import java.util.*
import kotlin.concurrent.thread

object Task {
    const val BROADCAST_PERMISSION_DISC = "com.cn.customview.permissions.MY_BROADCAST"
    const val BROADCAST_ACTION_DISC = "com.cn.customview.permissions.my_broadcast"


    private val handler = Handler(object : Handler.Callback {
        override fun handleMessage(p0: Message?): Boolean {
            MessageHooker.getContact()
            val user = MessageHooker.getProfile()
            if (user == null) {
                Logger.info("用户登录", "当前是未登录状态")
            }

            val intent = Intent()
            intent.putExtra("data", "this is data from broadcast " + Calendar.getInstance().get(Calendar.SECOND))
            intent.action = Task.BROADCAST_ACTION_DISC   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
            //发送广播
            AndroidAppHelper.currentApplication().sendBroadcast(intent)
            val processName = AndroidAppHelper.currentProcessName()
            //
            //测试@test
//            MessageHooker.getUserInfoFromConversationEngine(1688851301404961, 0)
//            MessageHooker.getUserInfoFromConversationEngine(1688851301404438)
            com.example.xposed.wework.services.Message.sendTextMessageToGroup(1688851301404438, "@Godtoy 恭喜您您好,Time:" + Date().time)
            com.example.xposed.wework.services.Message.sendTextMessageToGroup(6667434997889862049, "@Godtoy 恭喜您您好,Time:" + Date().time)
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