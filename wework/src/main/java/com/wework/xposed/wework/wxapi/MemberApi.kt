package com.wework.xposed.wework.wxapi

import com.wework.xposed.common.bean.Member
import com.wework.xposed.wework.util.ServiceUtil
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

object MemberApi {

    /**
     * 获取用户信息
     * @param userId 用户的ID
     * @param conversionLocalId 企业的本地ID
     */
    fun getMemberInfoById(userId: Long, conversionLocalId: Any): Member? {
        val engineInstance = ServiceUtil.getConversationEngineInstance()
        //eda中的实例
        val user = XposedHelpers.callMethod(engineInstance, "Q", userId, conversionLocalId)
                ?: return null
        XposedBridge.log("User:$user")
        return convertEngineDefineCToMember(user)
    }

    /**
     * 解析用户信息
     * @param user ConversationEngineDefine@c
     *
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
        //member.nickname=name
        member.nickname = nickname
        member.avatar = avatar
        return member
    }

    /**
     * 获取用户信息
     * @param userId 用户的ID
     */
    fun getMemberInfoById(userId: Long): Member? {
        val engineInstance = ServiceUtil.getConversationEngineInstance()
        val user = XposedHelpers.callMethod(engineInstance, "hT", userId) ?: return null
        XposedBridge.log("User:$user")
        return convertEngineDefineCToMember(user)
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

}