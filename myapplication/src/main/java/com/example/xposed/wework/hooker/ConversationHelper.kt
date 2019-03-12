package com.example.xposed.wework.hooker

import com.example.xposed.common.bean.ContactGroup
import com.example.xposed.common.bean.GroupMember
import com.example.xposed.wework.WkGlobal
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.security.acl.Group
import java.util.ArrayList
import java.util.logging.Logger

object ConversationHelper {

    fun getCoversationFromLocalId(lid: Long) {

    }

    fun getCoversationFromRemoteId(remoteId: Long) {

    }

    /**
     * 获取联系人
     */
    fun getConversationList(): ArrayList<ContactGroup> {
        //绘画服务
        val conversationService = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.logic.ConversationService")
        val conversationItem = WkGlobal.classLoader.loadClass("com.tencent.wework.msg.model.ConversationItem")

        //会话 object
        val conversationServiceInstance = XposedHelpers.callStaticMethod(conversationService, "getService")

        //返回一个Conversation数组繪畫列爾
        val rr2 = XposedHelpers.callMethod(conversationServiceInstance, "GetConversationList")
        val conversionList = ArrayList<ContactGroup>()
        if (null != rr2) {
            //遍历一下
            val list = rr2 as Array<*>
            list.forEach { convItm ->
                if (convItm == null) return@forEach

                val group = getGroupInfoByConversionGroupEntry(convItm)
                        ?: return@forEach
                conversionList.add(group)
                XposedBridge.log("群ID:${group.id},RID:${group.remoteId},名稱:${group.name}")
                //获取群成员的用户列表
                getUserListByConversionGroupEntry(convItm)
            }
        }
        return conversionList
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
            com.example.xposed.core.Logger.debug("GroupMessage", str)
            //Message.sendTextMessageToGroup(group.id, str)
            listMembers.add(gpm)
        }
        return listMembers
    }

    /**
     * 解析Group信息
     */
    private fun getGroupInfoByConversionGroupEntry(convItm: Any): ContactGroup? {
        val info = XposedHelpers.callMethod(convItm, "getInfo") ?: return null
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
        group.localId = id
        group.memberCount = memberCount
        return group
    }
}
