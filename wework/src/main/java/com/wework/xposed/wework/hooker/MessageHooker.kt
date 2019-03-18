package com.wework.xposed.wework.hooker

import com.wework.xposed.wework.WkGlobal
import com.wework.xposed.common.bean.Member
import com.wework.xposed.wework.util.MessageUtil
import com.wework.xposed.wework.util.ServiceUtil
import com.wework.xposed.wework.wxapi.ConversationApi
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

object MessageHooker : Hooker {
    override fun executeHook() {

    }


    /**
     * 放入实体类Model：com.tencent.wework.foundation.model.Message
     * 返回数据类型：com.tencent.wework.foundation.model.pb.WwMessage$Message
     *
     * @param message com.tencent.wework.foundation.model.Message
     * @return com.tencent.wework.foundation.model.pb.WwMessage$Message
     */
    fun getMessageInfoFromMessageEntry(message: Any): Any {
        val msg = XposedHelpers.callMethod(message, "getInfo") as Any

        //群ID
        val conversationId = XposedHelpers.getObjectField(msg, "conversationId") as Long
        //会话的ID
        val convLocalId = XposedHelpers.getObjectField(msg, "conversationId") as Long
        //消息类型 2 文字消息
        val contentType = XposedHelpers.getObjectField(msg, "contentType") as Int
        //聊天人类型 0 私聊，1群聊
        val conversationType = XposedHelpers.getObjectField(msg, "convType") as Int
        //消息类容
        val contentBytes = XposedHelpers.getObjectField(msg, "content") as ByteArray
        //发送人信息
        val sender = XposedHelpers.getObjectField(msg, "sender") as Long
        //消息状态
        val state = XposedHelpers.getObjectField(msg, "state") as Int

        val content = String(contentBytes, Charsets.UTF_8)

        //查询sender的用户信息
//        MessageHooker.getUserInfoFromConversationEngine(sender)
//        MessageHooker.getUserInfoFromConversationEngine(sender, convLocalId)

        //然后解析
        if (contentType == 2) {
            //文本消息

        } else if (contentType == 14) {
            //图片消息
        }
        MessageUtil.convertWwMessageIMessageToMessage(message)
        return msg
    }

    /**
     * 获取消息的联系人数据
     */
    fun wxGetConversionRemoteIdByMessage(message: Any) {
        //conversationId
        val msg = XposedHelpers.callMethod(message, "getInfo") as Any

        //群ID
        val conversationId = XposedHelpers.getObjectField(msg, "conversationId") as Long
        //通过这个ID去获取
        val res = ConversationApi.getConversationItemFromRemoteId(conversationId)
    }


    private fun hookEvents() {
        //hook，儅界面切換事件會丟失
        val wwMainActivity = WkGlobal.classLoader.loadClass("com.tencent.wework.launch.WwMainActivity")
        XposedHelpers.findAndHookMethod(wwMainActivity, "onTPFEvent", String::class.java, Int::class.java, Int::class.java,
                Int::class.java, Any::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val thisObject = param.thisObject
                val str = param.args[0] as String
                XposedBridge.log("WwMainActivity Event:$str")
                if (str.equals("wework.msg.event")) {
                    //获取最新的消息
                    XposedBridge.log("收到消息")
                }
            }
        })
    }

    //1.转化消息
    // java.lang.Class/MessageItem@public static java.lang.CharSequence efd.a(long,com.tencent.wework.foundation.model.pb.WwRichmessage$RichMessage,android.graphics.Paint)->Data->1


    /**
     * 测试消息相互转化的钩子
     */
    private fun testHookMessage() {
        //富文本
        val richMessageTextMessage = XposedHelpers.findClass("com.tencent.wework.foundation.model.pb.WwRichmessage\$TextMessage", WkGlobal.workLoader)
        XposedBridge.hookAllMethods(richMessageTextMessage, "parseFrom", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                if (param != null) {
                    XposedBridge.log(" WwRichmessage\$TextMessage :${param.result}")
                }
            }
        })

        //message
        val richMessage = XposedHelpers.findClass("com.tencent.wework.foundation.model.pb.WwRichmessage\$Message", WkGlobal.workLoader)
        XposedBridge.hookAllMethods(richMessage, "parseFrom", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                if (param != null) {
                    XposedBridge.log(" WwRichmessage\$Message :${param.result}${param.method}")
                }
            }
        })

        //AtMessage
        val richMessageAtMessage = XposedHelpers.findClass("com.tencent.wework.foundation.model.pb.WwRichmessage\$RichMessage", WkGlobal.workLoader)
        XposedBridge.hookAllMethods(richMessageAtMessage, "parseFrom", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                if (param != null) {
                    XposedBridge.log(" WwRichmessage\$RichMessage :${param.result}")
                }
            }
        })
    }





    //#region   测试方法
    private fun hookMessageCreated() {
        //接受消息的时候或者打开消息列表的时候，会出现
        val messageCls = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.model.Message")
        /**
         * 替换掉toString 的方法
         */
        XposedHelpers.findAndHookMethod(messageCls, "toString", object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam?): Any {
                val thisObject = param!!.thisObject
                if (null != thisObject) {

                }
                return ""
            }
        })
    }

    //#endregion
}
