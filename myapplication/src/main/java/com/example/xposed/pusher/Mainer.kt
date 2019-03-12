package com.example.xposed.pusher

import com.example.xposed.core.Socker

object Mainer {
    var socker: Socker = Socker

    init {
        socker.connect()
    }
}