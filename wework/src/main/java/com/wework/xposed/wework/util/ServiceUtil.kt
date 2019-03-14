package com.wework.xposed.wework.util

import com.wework.xposed.wework.WkGlobal
import com.wework.xposed.wework.WkObject
import de.robv.android.xposed.XposedHelpers


object ServiceUtil {
    /**
     * 获取会话引擎
     * @return Conver
     */
    public fun getConversationEngineInstance(): Any {
        val conversationEngine = XposedHelpers.findClass(WkObject.ConversationEngine.C.ConversationEngine, WkGlobal.workLoader)
        val instance = XposedHelpers.callStaticMethod(conversationEngine, WkObject.ConversationEngine.M.GetInstance)
        return instance as Any
    }

    /**
     * 获取会话服务
     */
    public fun getConversationServiceInstance(): Any {
        //通过引擎去拿service
        val conversationEngine = WkObject.getClass(WkObject.ConversationEngine.C.ConversationEngine)
        val service = XposedHelpers.callStaticMethod(conversationEngine, WkObject.ConversationEngine.M.GetConversationService)
        return service as Any
    }

    /**
     * 部门服务
     */
    public fun getDepartmentService(): Any {
        val conversationEngine = XposedHelpers.findClass(WkObject.ConversationEngine.C.ConversationEngine, WkGlobal.workLoader)
        val service = XposedHelpers.callStaticMethod(conversationEngine, WkObject.ConversationEngine.M.GetDepartmentService)
        return service as Any
    }
}