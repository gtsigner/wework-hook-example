package com.example.xposed.wework.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.example.xposed.wework.utils.AppUtil
import com.example.xposed.wework.utils.Logger

/**
 *消息服务
 */
class MessageService : AccessibilityService() {
    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {
        var version: String = AppUtil.getWkVersion(this)
        Logger.log("Godtoy", version)
    }

    override fun onInterrupt() {
        var version: String = AppUtil.getWkVersion(this)
        Logger.log("Godtoy", version)
    }
}
