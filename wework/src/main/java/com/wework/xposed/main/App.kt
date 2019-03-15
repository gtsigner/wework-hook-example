package com.wework.xposed.main

import android.app.Application
import android.widget.Toast
import com.wework.xposed.core.WeWorkService

class App : Application() {

    companion object {
        lateinit var instance: Application
        const val ERR_MSG = "请在Xposed中安装开启本模块"
    }

    var mainer = Mainer

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (Mainer.service == null) {
            Toast.makeText(this, Companion.ERR_MSG, Toast.LENGTH_SHORT).show()
            return
        }
        Mainer.start()
        Mainer.getWorkApi()?.sayHello()
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}
