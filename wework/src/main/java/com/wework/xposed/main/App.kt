package com.wework.xposed.main

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.wework.xposed.common.bean.SocketSettings
import com.wework.xposed.core.WeWorkService
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
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}
