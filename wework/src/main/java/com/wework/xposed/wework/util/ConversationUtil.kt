package com.wework.xposed.wework.util

import com.wework.xposed.common.bean.ContactGroup
import com.wework.xposed.common.bean.GroupMember
import com.wework.xposed.common.bean.Member
import com.wework.xposed.core.Logger
import com.wework.xposed.wework.hooker.MessageHooker
import com.wework.xposed.wework.wxapi.MemberApi
import de.robv.android.xposed.XposedHelpers
import java.util.ArrayList

object ConversationUtil {
    /**
     * 获取联系嗯信息
     */
    fun covertConversionItemInfoToContactGroup(conversionItemInstance: Any): ContactGroup? {
        //avd 就是一个Conversation实例
        val aVd = XposedHelpers.callMethod(conversionItemInstance, "aVd")
        val group = getGroupInfoByConversionGroupEntry(aVd) ?: return null
        val members = getUserListByConversionGroupEntry(aVd)
        group.members = members
//        val localId = XposedHelpers.callMethod(conversionItemInstance, "getLocalId") as Long
//        val remoteId = XposedHelpers.callMethod(conversionItemInstance, "getRemoteId") as Long
//        val name = XposedHelpers.callMethod(conversionItemInstance, "cgN") as String
////        val contact = ContactGroup()
//        contact.id = remoteId
//        contact.localId = localId
//        contact.name = name
        return group
    }


    /**
     * profile 种就是UserInfo
     * @param userInfo com.tencent.wework.foundation.model.pb.WwUser\$UserInfo;
     * @return 返回实体
     */
    fun convertUserInfoToMember(userInfo: Any): Member {
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
        member.gender = gender

        return member
    }

    /**
     * 通过群聊会话获取所有的用户列表
     * 这里面的数据不携带复杂数据
     * @param convItm Conversaion model
     */
    private fun getMemberListByConversionGroupEntry(convItm: Any): List<GroupMember> {
        val listMembers = ArrayList<GroupMember>()
        val members = XposedHelpers.callMethod(convItm, "getMembers") as Array<*>?
        members?.forEach { mItm ->
            val mname = XposedHelpers.getObjectField(mItm, "name") as String
            val nickName = XposedHelpers.getObjectField(mItm, "nickName") as String
            //企业ID
            val userCorpId = XposedHelpers.getLongField(mItm, "userCorpId")
            //用户的真实ID
            val userRemoteId = XposedHelpers.getLongField(mItm, "userRemoteId")
            val operatorRemoteId = XposedHelpers.getLongField(mItm, "operatorRemoteId")
            val avatorUrl = XposedHelpers.getObjectField(mItm, "avatorUrl") as String
            val joinTime = XposedHelpers.getLongField(mItm, "joinTime")
            val englishName = XposedHelpers.getObjectField(mItm, "englishName") as String

            val me = GroupMember()
            me.id = userRemoteId
            me.corpId = userCorpId
            me.nickname = nickName ?: ""
            me.name = mname ?: ""
            me.englishName = englishName
            me.joinTime = joinTime
            me.avatar = avatorUrl
            me.optId = operatorRemoteId

            listMembers.add(me)
        }
        return listMembers
    }

    /**
     * 获取群组用户的详情数据
     * @param convItm 对应类：model.Conversation
     */
    public fun getUserListByConversionGroupEntry(convItm: Any): List<GroupMember> {
        val members = getMemberListByConversionGroupEntry(convItm)
        val listMembers = ArrayList<GroupMember>()

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
            val member = MemberApi.convertUserToMember(userInfo)
            val gpm = GroupMember()
            gpm.id = member.id
            gpm.acctid = member.acctid
            gpm.avatar = member.avatar
            gpm.name = member.name
            gpm.realname = member.realname
            gpm.mobile = member.mobile
            listMembers.add(gpm)
        }
        return listMembers
    }

    /**
     * 解析Group信息
     * @param convItm 联系会话实例 Conversation 类对象
     */
    public fun getGroupInfoByConversionGroupEntry(convItm: Any): ContactGroup? {
        //model.pd.$WwConversation
        val info = XposedHelpers.callMethod(convItm, "getInfo") ?: return null
        //验证类型
        //验证是否是群聊
        val id = XposedHelpers.getLongField(info, "id")
        val remoteId = XposedHelpers.getLongField(info, "remoteId")
        val type = XposedHelpers.getIntField(info, "type")
        val name = XposedHelpers.getObjectField(info, "name") as String
        val memberCount = XposedHelpers.callMethod(convItm, "getMemberCount") as Int

        if (type != 1) {
            return null
        }
        val group = ContactGroup()
        group.id = remoteId
        group.remoteId = remoteId
        group.name = name
        group.type = type
        group.localId = id
        group.memberCount = memberCount
        return group
    }
}