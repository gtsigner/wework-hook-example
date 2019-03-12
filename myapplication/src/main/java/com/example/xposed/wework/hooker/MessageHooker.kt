package com.example.xposed.wework.hooker

import com.example.xposed.wework.WkGlobal
import com.example.xposed.wework.bean.ContactGroup
import com.example.xposed.wework.bean.GroupMember
import com.example.xposed.wework.bean.Member
import com.example.xposed.wework.hook.HookUtil
import com.example.xposed.wework.hook.ServiceUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.util.*

object MessageHooker : BaseHooker {
    override fun executeHook() {
        //绘画服务
        val conversationService = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.logic.ConversationService")
        //联系人服务
        val contactService = XposedHelpers.findClass("com.tencent.wework.foundation.logic.ContactService", WkGlobal.workLoader)
        val conversation = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.model.Conversation")
        val userCls = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.model.User")
        val iCommonConversationOperateCallback = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.callback.ICommonConversationOperateCallback")
        val conversationItem = WkGlobal.classLoader.loadClass("com.tencent.wework.msg.model.ConversationItem")

        hookNotificationInfo()
        //testHookMessage()

        //获取联系人列表
        //hookLog()

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
        XposedBridge.log("Content@Rich:$res#Text:$res2#Rim:$res3#At:$res4")

        return res
    }

    /**
     * 放入实体类Model：com.tencent.wework.foundation.model.Message
     * 返回数据类型：com.tencent.wework.foundation.model.pb.WwMessage$Message
     *
     * @param message com.tencent.wework.foundation.model.Message
     * @return com.tencent.wework.foundation.model.pb.WwMessage$Message
     */
    private fun getMessageInfoFromMessageEntry(message: Any): Any {
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
        XposedBridge.log("Content:$content,contentType=$contentType,conversationId=$conversationId,conversationType=$conversationType,sender=$sender,state=$state")


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

    private fun hookNotificationInfo() {
        val notificationInfo = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.notification.NotificationInfo")
        val msgCls = XposedHelpers.findClass("com.tencent.wework.foundation.model.Message", WkGlobal.workLoader)


        //hook会话引擎的解析代码
//        XposedBridge.hookAllMethods(conversationEngine, "i", object : XC_MethodHook() {
//            override fun afterHookedMethod(param: MethodHookParam?) {
//                //打印
//                if (param?.args == null) {
//                    return
//                }
//                var str = ""
//                param.args.forEach {
//                    str += "\t--$it@${it.javaClass}"
//                }
//                XposedBridge.log("Engine@i->$param,${param.args}###$str")
//            }
//        })

        //pushService
        val pushService = XposedHelpers.findClass("com.tencent.wework.foundation.logic.PushService", WkGlobal.workLoader)
//        //hook会话引擎的解析代码
//        XposedBridge.hookAllMethods(conversationEngine, "a", object : XC_MethodHook() {
//            override fun afterHookedMethod(param: MethodHookParam?) {
//                //打印
//                if (param?.args == null) {
//                    return
//                }
//                XposedBridge.log("Engine@a->$param,${param.args}")
//            }
//        })

        /**
         * 系统通知收到消息后会创建一个对象，这个对象参数第一个是Message，第二个是一个字符串
         */
        XposedHelpers.findAndHookConstructor(notificationInfo, Any::class.java, Any::class.java, Long::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val thisObject = param.thisObject
                //model.Message
                val message = param.args[0] as Any
                val jid = param.args[2] as Long
                val str = param.args[1] as String
                val msg = getMessageInfoFromMessageEntry(message)
                //解析数据
                XposedBridge.log(" NotificationInfo->$thisObject,$message,$str,$jid")

            }
        })


        //MessageItem下钩子
        val efdCls = XposedHelpers.findClass("efd", WkGlobal.workLoader)
        //ConversationEngine
        val conversationEngine = XposedHelpers.findClass("ecz", WkGlobal.workLoader)

        //router
        val appRouter = XposedHelpers.findClass("com.tencent.wework.api.config.AppRouter", WkGlobal.workLoader)
        arrayOf("kj").forEach {
            hookAndDebugAllMethods(appRouter, it, "AppRouter")
        }

//        arrayOf("a", "b", "d").forEach {
//            hookAndDebugAllMethods(conversationEngine, it, "ConversationEngine")
//        }
////        arrayOf("a", "b", "d").forEach {
////            hookAndDebugAllMethods(efdCls, it, "MessageItem")
////        }
//        val messageManager = WkGlobal.classLoader.loadClass("com.tencent.wework.msg.model.MessageManager")
//        arrayOf("onTPFEvent", "ba").forEach {
//            hookAndDebugAllMethods(messageManager, it, "MessageManager")
//        }

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

    private fun hookLog() {
        val css = WkGlobal.classLoader.loadClass("css")
        arrayOf("w", "d", "e", "i").forEach { m ->
            XposedBridge.hookAllMethods(css, m, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    var str = m
                    param!!.args.forEach { str += "@$it->${it.javaClass.name}\t" }
                    str += "---->" + param.args.size
                    XposedBridge.log(str)
                }
            })
        }
    }

    /**
     * 获取联系人
     */
    fun getContact() {
        //绘画服务
        val conversationService = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.logic.ConversationService")
        val conversationItem = WkGlobal.classLoader.loadClass("com.tencent.wework.msg.model.ConversationItem")

        //会话 object
        val conversationServiceInstance = XposedHelpers.callStaticMethod(conversationService, "getService")

        //返回一个Conversation数组繪畫列爾
        val rr2 = XposedHelpers.callMethod(conversationServiceInstance, "GetConversationList")
        if (null != rr2) {
            //遍历一下
            val list = rr2 as Array<*>
            list.forEach { convItm ->
                if (convItm == null) return@forEach

//              这部分数据没有什么具体用处
//                val item = XposedHelpers.callStaticMethod(conversationItem, "n", convItm)
//                if (item != null) {
//                    //打印数据
//                    val convType = XposedHelpers.getIntField(item, "bUq")
//                    val mLocalId = XposedHelpers.getLongField(item, "mLocalId")
//                    val mRemoteId = XposedHelpers.getLongField(item, "mRemoteId")
//                    val mName = XposedHelpers.getObjectField(item, "mName")
//                    XposedBridge.log("Type:$convType,mLocalId=$mLocalId,mRemoteId=$mRemoteId")
//                }

                val group = getGroupInfoByConversionGroupEntry(convItm) ?: return@forEach
                XposedBridge.log("群ID:${group.id},RID:${group.remoteId},名稱:${group.name}")
                getUserListByConversionGroupEntry(convItm)
            }
        }
    }

    /**
     * 获取当前用户的资料信息
     * @return 资料信息
     */
    fun getProfile(): Member? {
        val profileCls = XposedHelpers.findClass("com.tencent.wework.foundation.logic.Profile", WkGlobal.workLoader)
        val profileInstance = XposedHelpers.callStaticMethod(profileCls, "getInstance")
        val userInfo = XposedHelpers.callMethod(profileInstance, "getUserInfo")
        if (null != userInfo) {
            //解析
            return MessageHooker.convertUserInfoToMember(userInfo)
        }
        return null
    }

    /**
     * profile 种就是UserInfo
     * @param userInfo com.tencent.wework.foundation.model.pb.WwUser\$UserInfo;
     * @return 返回实体
     */
    private fun convertUserInfoToMember(userInfo: Any): Member {
        val member = Member()
        val acctid = String(XposedHelpers.getObjectField(userInfo, "acctid") as ByteArray)
        val iconurl = String(XposedHelpers.getObjectField(userInfo, "iconurl") as ByteArray)
        val mobile = String(XposedHelpers.getObjectField(userInfo, "mobile") as ByteArray)
        val name = String(XposedHelpers.getObjectField(userInfo, "name") as ByteArray)
        val realName = String(XposedHelpers.getObjectField(userInfo, "realName") as ByteArray)
        val unionid = String(XposedHelpers.getObjectField(userInfo, "unionid") as ByteArray)
        //long信息
        val uin = XposedHelpers.getLongField(userInfo, "uin")
        //性别
        val gender = XposedHelpers.getIntField(userInfo, "gender")
        val corpid = XposedHelpers.getLongField(userInfo, "corpid") as Long?
        val gid = XposedHelpers.getLongField(userInfo, "gid") as Long?

        member.id = uin
        member.acctid = acctid
        member.avatar = iconurl
        member.nickname = name
        member.realname = realName
        member.corpId = corpid as Long
        member.mobile = mobile

        //Emit事件
        XposedBridge.log("当前用户：$acctid,ID:$uin,Gid:$gid,性别:$gender")
        return member
    }

    /**
     *这个类在把微信的类转化成实体类Member
     * @param user package com.tencent.wework.foundation.model.pb.WwUser\$User;
     * @return 返回本地实体
     */
    private fun convertUserToMember(user: Any): Member {
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
     * 通过群聊会话获取所有的用户列表
     * 这里面的数据不携带复杂数据
     */
    private fun getMemberListByConversionGroupEntry(convItm: Any): List<GroupMember> {
        val listMembers = ArrayList<GroupMember>()
        val members = XposedHelpers.callMethod(convItm, "getMembers") as Array<*>?
        members?.forEach { mItm ->
            val mname = XposedHelpers.getObjectField(mItm, "name") as String?
            val nickName = XposedHelpers.getObjectField(mItm, "nickName") as String?
            //企业ID
            val userCorpId = XposedHelpers.getLongField(mItm, "userCorpId")
            //用户的真实ID
            val userRemoteId = XposedHelpers.getLongField(mItm, "userRemoteId")
            val joinTime = XposedHelpers.getLongField(mItm, "joinTime")
            //通过用户的ID获取用户的详情
//            XposedBridge.log("Name:$mname,$nickName,CorpId:$userCorpId,RId:$userRemoteId")
            val me = GroupMember()
            me.id = userRemoteId
            me.corpId = userCorpId
            listMembers.add(me)
        }
        return listMembers
    }

    /**
     * 解析Group信息
     */
    private fun getGroupInfoByConversionGroupEntry(convItm: Any): ContactGroup? {
        val info = XposedHelpers.callMethod(convItm, "getInfo") ?: return null
        //XposedBridge.log("GroupInfo:$info")
        //验证类型
        //验证是否是群聊
        val id = XposedHelpers.getLongField(info, "id")
        val remoteId = XposedHelpers.getLongField(info, "remoteId")
        val type = XposedHelpers.getIntField(info, "type")
        val name = XposedHelpers.getObjectField(info, "name") as String
        val memberCount = XposedHelpers.callMethod(convItm, "getMemberCount") as Int
        //3系统应用，6应用，0普通用户或者其他的官方单用户信息，1群聊
        if (type != 1) {
            return null
        }
        val group = ContactGroup()
        group.id = id
        group.remoteId = remoteId
        group.name = name
        group.type = type
        group.memberCount = memberCount
        return group
    }

    /**
     * 获取群组用户的详情数据
     */
    private fun getUserListByConversionGroupEntry(convItm: Any): List<GroupMember> {
        val members = getMemberListByConversionGroupEntry(convItm)
        val listMembers = ArrayList<GroupMember>()
        val group = getGroupInfoByConversionGroupEntry(convItm) ?: return listMembers

        //获取这个里面的id
        val ids = LongArray(members.size)
        members.forEachIndexed { i, member ->
            ids[i] = member.id
        }
        val userList = XposedHelpers.callMethod(convItm, "GetUserList", ids) as Array<*>?
                ?: return listMembers
        userList.forEach { user ->
            val userInfo = XposedHelpers.callMethod(user, "getInfo") ?: return@forEach
            //获取数据信息
            val member = MessageHooker.convertUserToMember(userInfo)

            val gpm = GroupMember()
            gpm.group = group
            gpm.id = member.id
            gpm.acctid = member.acctid
            gpm.avatar = member.avatar
            gpm.name = member.name
            gpm.realname = member.realname
            gpm.mobile = member.mobile
            val str = "群ID:${group.id},群:${group.name}->用户G:${member.acctid}${member.name},UID:${member.id},RealName:${member.realname},Mobile:${member.mobile},Avatar:${member.avatar}"
            //XposedBridge.log(str)
            //Message.sendTextMessageToGroup(group.id, str)
            listMembers.add(gpm)
        }
        return listMembers
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
