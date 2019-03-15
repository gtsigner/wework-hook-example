package com.wework.xposed.common.bean

//群组
class ContactGroup {
    //id
    var id: Long = -1
    //远程ID
    var remoteId: Long = -1
    //本地ID
    var localId: Long = -1
    //名称
    var name: String = ""
    //头像
    var avatar: String = ""
    //类型
    var type: Int = 0
    //会员数
    var memberCount: Int = 0
    //会员信息
    var members: List<GroupMember> = ArrayList()
}
