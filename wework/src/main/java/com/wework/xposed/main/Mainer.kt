package com.wework.xposed.main

import com.wework.xposed.IWeWorkApi
import com.wework.xposed.core.WeWorkService

object Mainer {
    val service = WeWorkService.getService()
    lateinit var socker: Socker
    fun start() {
        socker = Socker()
        socker.connect()
    }

    fun getWorkApi(): IWeWorkApi? {
        return service?.workApi
    }
}