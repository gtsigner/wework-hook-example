package com.example.xposed.main

import android.app.Application
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
    }

    val socker = Socker
    val service = WeWorkService.getService() as IWeWorkService

    override fun onCreate() {
        super.onCreate()
        instance = this
        Socker.connect()
    }

    init {
        //初始化
        service.all(WkClientType.Client, object : IWeWorkEmitListener.Stub() {
            override fun callback(senderType: Int, event: String?, data: String?) {
                Logger.debug("客户端收到消息", "$senderType,$event,$data")
            }
        })
    }
}
