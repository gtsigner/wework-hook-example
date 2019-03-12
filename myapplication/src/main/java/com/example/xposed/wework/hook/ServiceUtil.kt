package com.example.xposed.wework.hook

import com.example.xposed.wework.WkGlobal
import com.example.xposed.wework.WkObject
import de.robv.android.xposed.XposedHelpers


object ServiceUtil {
    /**
     * 获取会话引擎
     * @return Conver
     */
    public fun getCoversationEngineInstance(): Any {
        val conversationEngine = XposedHelpers.findClass(WkObject.CoversationEngine.C.CoversationEngine, WkGlobal.workLoader)
        val instance = XposedHelpers.callStaticMethod(conversationEngine, WkObject.CoversationEngine.M.GetInstance)
        return instance as Any
    }

    /**
     * 获取会话服务
     */
    public fun getConversationService(): Any {
        val conversationEngine = XposedHelpers.findClass(WkObject.CoversationEngine.C.CoversationEngine, WkGlobal.workLoader)
        val service = XposedHelpers.callStaticMethod(conversationEngine, WkObject.CoversationEngine.M.GetConversationService)
        return service as Any
    }

    /**
     * 部门服务
     */
    public fun getDepartmentService(): Any {
        val conversationEngine = XposedHelpers.findClass(WkObject.CoversationEngine.C.CoversationEngine, WkGlobal.workLoader)
        val service = XposedHelpers.callStaticMethod(conversationEngine, WkObject.CoversationEngine.M.GetDepartmentService)
        return service as Any
    }
}