package com.example.xposed.main

import android.app.Application
import android.widget.Toast
import com.example.aidl.IWeWorkCallBack
import com.example.aidl.IWeWorkEmitListener
import com.example.aidl.IWeWorkService
import com.example.xposed.core.Logger

import com.example.xposed.core.Socker
import com.example.xposed.core.WeWorkService
import com.example.xposed.core.WkClientType

class App : Application() {

    companion object {
        lateinit var instance: Application
        const val ERR_MSG = "请在Xposed中安装开启本模块"
    }

    val socker = Socker
    val service = WeWorkService.getService()

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (service == null) {
            Toast.makeText(this, Companion.ERR_MSG, Toast.LENGTH_SHORT).show()
            return
        }
        Socker.connect()
        service.all(WkClientType.Client, object : IWeWorkEmitListener.Stub() {
            override fun callback(senderType: Int, event: String?, data: String?) {
                Logger.debug("客户端收到消息", "$senderType,$event,$data")
            }
        })
    }

    fun getWkService(): IWeWorkService? {
        if (service == null) {
            Toast.makeText(this, ERR_MSG, Toast.LENGTH_SHORT).show()
            return null
        }
        return service
    }
}
