package com.wework.xposed.main

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.wework.xposed.common.bean.SocketSettings
import com.wework.xposed.core.WeWorkService
import java.lang.Exception
import java.net.URI

class App : Application() {

    val mainer = Mainer

    companion object {
        lateinit var instance: Application
        const val ERR_MSG = "请在Xposed中安装开启本模块"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (Mainer.service == null) {
            Toast.makeText(this, Companion.ERR_MSG, Toast.LENGTH_SHORT).show()
            return
        }

        val share = getSharedPreferences(Mainer.SHARE_PRE_NAME, Context.MODE_PRIVATE)
        val uri = share.getString("SETTINGS_SOCKET_URI", "http://192.168.10.161:3008") as String
        val appid = share.getString("SETTINGS_SOCKET_APPID", "appid") as String
        val connected = share.getBoolean("SETTINGS_SOCKET_AUTO", true)
        
        //获取配置
        val setting = SocketSettings()
        setting.appId = appid
        try {
            setting.uri = URI(uri)
        } catch (ex: Exception) {
            setting.uri = URI("http://192.168.10.161:3008")
        }
        setting.reconnected = connected
        mainer.start(setting)
        mainer.connect()
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}
