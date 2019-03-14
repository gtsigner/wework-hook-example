package com.wework.xposed.wework

import com.alibaba.fastjson.JSON
import com.wework.xposed.IWeWorkHandler
import com.wework.xposed.wework.wxapi.ProfileApi

class WkHandler : IWeWorkHandler.Stub() {
    override fun sayHello(): String {
        val user = ProfileApi.getCurrentLoginUserProfile()
        val str = JSON.toJSONString(user)
        return str
    }
}