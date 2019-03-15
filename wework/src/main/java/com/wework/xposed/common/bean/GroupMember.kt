package com.wework.xposed.common.bean

class GroupMember {
    //联系群组
    var group: ContactGroup? = null
    //ID
    var id: Long = -1
    //一个奇怪的ID,这个可以通过Api调用
    var acctid: String = ""
    //企业ID
    var corpId: Long = -1
    //名称
    var name = ""
    //昵称
    var nickname = ""
    //实际名字
    var realname = ""
    //头像地址
    var avatar = ""
    //英文名称
    var englishName = ""
    //手机号
    var mobile: String = ""
    //性别
    var gender = 0
    //加入事件
    var joinTime: Long = -1

    var optId: Long = -1
}