package com.wework.xposed.common.bean


class Message {
    var senderId: Long = 0
    var sender: Member? = null

    var senderType: Int = 0
    var reciverId: Long = 0
    val reciverType: Int = 0
    var conversationId: Long = 0
    var conversation: ContactGroup? = null

    var id: Long = 0
    var sendTime: Int = 0

    var messageType: Int = 0
    var conversationType: Int = 0
    val remoteId = 0
    var message = ""
}