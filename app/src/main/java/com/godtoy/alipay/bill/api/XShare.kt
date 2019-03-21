package com.godtoy.alipay.bill.api

import de.robv.android.xposed.XSharedPreferences


object XShare {

    val HOST = "setting_host"
    val KEY = "setting_key"
    private var intance: XSharedPreferences? = null

    fun getIntance(): XSharedPreferences? {
        if (intance == null) {
            intance = XSharedPreferences("com.godtoy.alipay", "config")
            intance!!.makeWorldReadable()
        } else {
            intance!!.reload()
        }
        return intance
    }

    fun getHost(): String {
        return getIntance()!!.getString(HOST, "http://47.52.31.204/ashx/AutoCZ.ashx") as String
    }

    fun getKey(): String {
        return getIntance()!!.getString(KEY, "649000") as String
    }
}