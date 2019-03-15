package com.wework.xposed.common.bean

import java.net.URI

class SocketSettings {
    var reconnected: Boolean = true
    var uri: URI? = null
    var appId: String = ""
    var token: String = ""
}