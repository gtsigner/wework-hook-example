package com.wework.xposed.wework.util

import com.wework.xposed.common.bean.ContactGroup
import com.wework.xposed.common.bean.Member
import com.wework.xposed.core.Logger
import de.robv.android.xposed.XposedHelpers

object ConversationUtil {
    /**
     * 获取联系嗯信息
     */
    fun covertConversionItemInfoToContactGroup(conversionItemInstance: Any): ContactGroup? {
        val res = XposedHelpers.callMethod(conversionItemInstance, "toString") ?: ""
        Logger.debug("res", "$res")
        val contact = ContactGroup()
        //contact.id=
        return null
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

}