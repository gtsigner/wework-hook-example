package com.example.xposed.common.bean

//用户信息
class Member {
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
}
