package com.example.xposed.wework.hooker

import com.example.xposed.wework.WkGlobal
import com.example.xposed.common.bean.Member
import com.example.xposed.wework.hook.HookUtil
import com.example.xposed.wework.hook.ServiceUtil
import com.example.xposed.wework.wxapi.ConversationApi
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

object MessageHooker : Hooker {
    override fun executeHook() {
        //绘画服务
        val conversationService = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.logic.ConversationService")
        //联系人服务
        val contactService = XposedHelpers.findClass("com.tencent.wework.foundation.logic.ContactService", WkGlobal.workLoader)
        val conversation = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.model.ConversationApi")
        val userCls = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.model.User")
        val iCommonConversationOperateCallback = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.callback.ICommonConversationOperateCallback")
        val conversationItem = WkGlobal.classLoader.loadClass("com.tencent.wework.msg.model.ConversationItem")
    }


    /**
     * 类下相互转化
     * 放入实体类Model：com.tencent.wework.foundation.model.Message
     * 返回数据类型：com.tencent.wework.foundation.model.pb.WwRichMessage$RichMessage
     */
    private fun convertWwMessageIMessageToWwRichMessageIRichMessage(message: Any): Any {
        val msg = XposedHelpers.callMethod(message, "getInfo") as Any
        val mNativeHandle = XposedHelpers.getLongField(message, "mNativeHandle")
        val bytes = XposedHelpers.callMethod(message, "nativeGetInfo", mNativeHandle) as ByteArray

        //富文本类
        val richMessage = XposedHelpers.findClass("com.tencent.wework.foundation.model.pb.WwRichmessage\$RichMessage", WkGlobal.workLoader)
        val textMessage = XposedHelpers.findClass("com.tencent.wework.foundation.model.pb.WwRichmessage\$TextMessage", WkGlobal.workLoader)
        val riMessage = XposedHelpers.findClass("com.tencent.wework.foundation.model.pb.WwRichmessage\$Message", WkGlobal.workLoader)
        val atMessage = XposedHelpers.findClass("com.tencent.wework.foundation.model.pb.WwRichmessage\$AtMessage", WkGlobal.workLoader)


        //解析
        var res = XposedHelpers.callStaticMethod(textMessage, "parseFrom", bytes)
        var res2 = XposedHelpers.callStaticMethod(richMessage, "parseFrom", bytes)
        var res3 = XposedHelpers.callStaticMethod(riMessage, "parseFrom", bytes)
        var res4 = XposedHelpers.callStaticMethod(atMessage, "parseFrom", bytes)
        XposedBridge.log("Rich:$res#Text:$res2#Rim:$res3#At:$res4")

        //后面的参数是字节
        val content = XposedHelpers.getObjectField(msg, "content") as ByteArray
        res = XposedHelpers.callStaticMethod(textMessage, "parseFrom", content)
        res2 = XposedHelpers.callStaticMethod(richMessage, "parseFrom", content)
        res3 = XposedHelpers.callStaticMethod(riMessage, "parseFrom", content)
        res4 = XposedHelpers.callStaticMethod(atMessage, "parseFrom", content)

        return res
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
        MessageHooker.getUserInfoFromConversationEngine(sender)
        MessageHooker.getUserInfoFromConversationEngine(sender, convLocalId)

        //然后解析
        if (contentType == 2) {
            //文本消息

        } else if (contentType == 14) {
            //图片消息
        }
        convertWwMessageIMessageToWwRichMessageIRichMessage(message)

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
        val res = ConversationApi.getCoversationItemFromRemoteId(conversationId)
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
     * 调试
     */
    public fun hookAndDebugAllMethods(cls: Class<*>, method: String, tag: String?) {
        XposedBridge.hookAllMethods(cls, method, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                if (param == null) {
                    return
                }
                HookUtil.printLogs(cls.javaClass.name + "/" + tag, param)
            }
        })
    }


    /**
     * 测试消息相互转化的钩子
     */
    private fun testHookMessage() {
        //消息发送Hook
//        val messageManager = WkGlobal.classLoader.loadClass("com.tencent.wework.msg.model.MessageManager")
//        XposedHelpers.findAndHookMethod(messageManager, "a", Context::class.java, Long::class.java, CharSequence::class.java, Boolean::class.java, object : XC_MethodHook() {
//            override fun afterHookedMethod(param: MethodHookParam) {
//                val thisObject = param.thisObject
//                XposedBridge.log(" MessageManager->a:${param.result}")
//            }
//        })
//        XposedBridge.hookAllMethods(messageManager, "a", object : XC_MethodHook() {
//            override fun afterHookedMethod(param: MethodHookParam) {
//                val thisObject = param.thisObject
//                XposedBridge.log(" MessageManager->all:${param.result}")
//            }
//        })
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


    /**
     *这个类在把微信的类转化成实体类Member
     * @param user package com.tencent.wework.foundation.model.pb.WwUser\$User;
     * @return 返回本地实体
     */
    fun convertUserToMember(user: Any): Member {
        val member = Member()
        val acctid = XposedHelpers.getObjectField(user, "acctid") as String
        val iconurl = XposedHelpers.getObjectField(user, "avatorUrl") as String
        val mobile = XposedHelpers.getObjectField(user, "mobile") as String
        val name = XposedHelpers.getObjectField(user, "name") as String
        //唯一ID
        val remoteId = XposedHelpers.getLongField(user, "remoteId")
        //extras 里面包含了真实姓名

        //val realName = String(XposedHelpers.getObjectField(user, "realName") as ByteArray)
        val unionid = XposedHelpers.getObjectField(user, "unionid") as String

        //性别
        val gender = XposedHelpers.getIntField(user, "gender")
        val corpid = XposedHelpers.getLongField(user, "corpid") as Long?


        member.id = remoteId
        member.acctid = acctid
        member.avatar = iconurl
        member.nickname = name
        member.realname = name
        member.corpId = corpid as Long
        member.mobile = mobile
        member.gender = gender

        return member
    }


    /**
     * 获取用户信息
     * @param userId 用户的ID
     * @param conversionLocalId 企业的本地ID
     */
    fun getUserInfoFromConversationEngine(userId: Long, conversionLocalId: Any): Member {
        val engineInstance = ServiceUtil.getCoversationEngineInstance()
        val user = XposedHelpers.callMethod(engineInstance, "Q", userId, conversionLocalId)
        XposedBridge.log("User:$user")
        val member = Member()
        member.id = userId
        return member
    }

    /**
     * 解析用户信息
     * @param user ConversationEngineDefine@c
     */
    private fun convertEngineDefineCToMember(user: Any): Member {
        val corpId = XposedHelpers.callMethod(user, "getCorpId") as Long
        val phone = XposedHelpers.callMethod(user, "getMobilePhone") as String
        val userId = XposedHelpers.callMethod(user, "getUserId") as Long
        val name = XposedHelpers.callMethod(user, "getName") as String
        val nickname = XposedHelpers.callMethod(user, "getNickName") as String
        val avatar = XposedHelpers.callMethod(user, "getPhotoUrl") as String
        //回放信息
        val member = Member()
        member.id = userId
        member.corpId = corpId
        member.mobile = phone
        member.name = name
        member.nickname = nickname
        member.avatar = avatar
        return member
    }

    /**
     * 获取用户信息
     * @param userId 用户的ID
     */
    fun getUserInfoFromConversationEngine(userId: Long): Member? {
        val engineInstance = ServiceUtil.getCoversationEngineInstance()
        val user = XposedHelpers.callMethod(engineInstance, "hT", userId) ?: return null
        XposedBridge.log("User:$user")
        val member = convertEngineDefineCToMember(user)
        member.id = userId
        return member
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
