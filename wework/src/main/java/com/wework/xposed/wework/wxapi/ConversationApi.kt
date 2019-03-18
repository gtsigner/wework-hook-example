package com.wework.xposed.wework.wxapi

import com.wework.xposed.common.bean.ContactGroup
import com.wework.xposed.common.bean.GroupMember
import com.wework.xposed.common.bean.Member
import com.wework.xposed.core.Logger
import com.wework.xposed.wework.WkGlobal
import com.wework.xposed.wework.WkObject
import com.wework.xposed.wework.util.ServiceUtil
import com.wework.xposed.wework.hooker.MessageHooker
import com.wework.xposed.wework.util.ConversationUtil
import de.robv.android.xposed.XposedHelpers
import java.util.ArrayList

/**
 * 封装了微信的一些Api接口
 */
object ConversationApi {

    fun sync(id: Long = 0) {
        //Sync
        Logger.info("ConversationApi", "Sync")
        val service = ServiceUtil.getConversationServiceInstance()
        XposedHelpers.callMethod(service, "Sync", id)
    }

    fun sendMessageToAll(message: String): Boolean {
        val cls = ConversationApi.getConversationList()
        cls.forEach {
            sendTextMessageByLocalId(it.id, message)
        }
        return true
    }


    /**
     * 发送文字消息
     */
    fun sendTextMessageByLocalId(lid: Long, message: String): Boolean {
        val messageManager = WkGlobal.classLoader.loadClass(WkObject.MessageSender.C.MessageManager)
        //上下文
        val externalGroupMessageListActivity = WkGlobal.classLoader.loadClass(WkObject.MessageSender.C.ExternalGroupMessageListActivity)
        val activityObj = externalGroupMessageListActivity.newInstance()
        return XposedHelpers.callStaticMethod(messageManager, WkObject.MessageSender.M.GroupTextSend, activityObj, lid, message, false) as Boolean
    }

    /**
     * 发送文字消息
     */
    fun sendTextMessageByRemoteId(rid: Long, message: String): Boolean {
        //获取会话的具体的LocalID
        val contact = getConversationItemFromRemoteId(rid) ?: return false
        val localId = contact.localId
        val messageManager = WkGlobal.classLoader.loadClass(WkObject.MessageSender.C.MessageManager)
        //上下文
        val externalGroupMessageListActivity = WkGlobal.classLoader.loadClass(WkObject.MessageSender.C.ExternalGroupMessageListActivity)
        val activityObj = externalGroupMessageListActivity.newInstance()
        return XposedHelpers.callStaticMethod(messageManager, WkObject.MessageSender.M.GroupTextSend, activityObj, localId, message, false) as Boolean
    }


    /**
     * 通过远程ID获取会话
     * List
     */
    fun getConversationContactGroupById(rid: Long): ContactGroup? {
        val list = getConversationList()
        var cver: ContactGroup? = null
        list.forEach {
            if (it.id == rid) {
                cver = it
                return cver
            }
        }
        return cver
    }


    /**
     * 获取单个会话项目
     */
    fun getConversationItemFromRemoteId(remoteId: Long): ContactGroup? {
        //会话引擎
        val contact = getContactByRemoteId(remoteId) ?: return null
        val service = ServiceUtil.getConversationServiceInstance()
        val res = XposedHelpers.callMethod(service, "GetCacheConversationByKey", 0, contact.localId)
        Logger.debug("GetCacheConversationByKey", "$res")
        //model.ConversationItem
        return contact
    }

    /**
     *通过会话获取convmember
     */
    fun getConversationConvMember(conversationId: Long, memberVid: Long): Any? {
        //call getMemberByVid
        val conversation = getConversationById(conversationId) ?: return null
        val res = XposedHelpers.callMethod(conversation, "getMemberByVid", memberVid)
        Logger.debug("getConversationConvMember", "$res")
        return res
    }

    /**
     * 获取conversation
     */
    private fun getConversationById(conversationId: Long): Any? {
        val engine = ServiceUtil.getConversationEngineInstance()
        val conversionItemInstance = XposedHelpers.callMethod(engine, "iT", conversationId)
                ?: return null
        val aVd = XposedHelpers.callMethod(conversionItemInstance, "aVd")
        return aVd;
    }

    //不反悔用户信息
    private fun getContactByRemoteId(remoteId: Long): ContactGroup? {
        val engine = ServiceUtil.getConversationEngineInstance()
        val obj = XposedHelpers.callMethod(engine, "iT", remoteId) ?: return null
        return ConversationUtil.covertConversionItemInfoToContactGroup(obj)
    }

    /**
     * 获取联系人
     */
    fun getConversationList(): ArrayList<ContactGroup> {
        val service = ServiceUtil.getConversationServiceInstance()
        //返回一个Conversation数组繪畫列爾
        val rr2 = XposedHelpers.callMethod(service, "GetConversationList")
        val conversionList = ArrayList<ContactGroup>()
        if (null != rr2) {
            //遍历一下
            val list = rr2 as Array<*>
            list.forEach { convItm ->
                if (convItm == null) return@forEach
                val group = ConversationUtil.getGroupInfoByConversionGroupEntry(convItm)
                        ?: return@forEach

                //获取群成员的用户列表
                val members = ConversationUtil.getUserListByConversionGroupEntry(convItm)
                group.members = members
                conversionList.add(group)
            }
        }
        return conversionList
    }


}