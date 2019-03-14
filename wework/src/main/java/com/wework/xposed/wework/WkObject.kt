package com.wework.xposed.wework

import de.robv.android.xposed.XposedHelpers

/**
 * 全局方法
 */
object WkObject {
    const val APP_DEBUG = false

    object Message {
        object C {
            const val SQLiteDatabase = "com.tencent.wcdb.database.SQLiteDatabase"
            const val SQLiteDatabaseCursorFactory = "com.tencent.wcdb.database.SQLiteDatabase\$CursorFactory"
            const val SQLiteErrorHandler = "com.tencent.wcdb.DatabaseErrorHandler"
            const val SQLiteCancellationSignal = "com.tencent.wcdb.support.CancellationSignal"
        }

        object M {
            const val QUERY = "rawQueryWithFactory"
            const val INSERT = "insertWithOnConflict"
            const val UPDATE = "updateWithOnConflict"
            const val UPDATE_update = "update"
            const val EXECSQL = "execSQL"

        }
    }

    /**
     * 会话引擎
     */
    object ConversationEngine {
        object C {
            const val ConversationEngine = "ecz"
        }

        object M {
            const val GetInstance = "cfh"
            const val GetConversationService = "aAh"
            const val GetDepartmentService = "getDepartmentService"
        }
    }

    object MessageSender {
        object C {
            const val MessageManager = "com.tencent.wework.msg.model.MessageManager"
            const val MessageModel = "com.tencent.wework.foundation.model.Message"
            const val ExternalGroupMessageListActivity = "com.tencent.wework.msg.controller.ExternalGroupMessageListActivity"
        }

        object M {
            const val GroupTextSend = "a"
        }
    }

    object Services {
        object C {
            const val ConversationService = "com.tencent.wework.foundation.logic.ConversationService"
            const val ContaceService = "com.tencent.wework.foundation.logic.ContactService"
        }

        object M {

        }
    }

    object Models {

    }

//    val conversationService = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.logic.ConversationService")
//    //联系人服务
//    val contactService = XposedHelpers.findClass("com.tencent.wework.foundation.logic.ContactService", WkGlobal.workLoader)
//    val conversation = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.model.Conversation")
//    val userCls = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.model.User")
//    val iCommonConversationOperateCallback = WkGlobal.classLoader.loadClass("com.tencent.wework.foundation.callback.ICommonConversationOperateCallback")
//    val conversationItem = WkGlobal.classLoader.loadClass("com.tencent.wework.msg.model.ConversationItem")


    /**
     * 获取类
     */
    fun getClass(className: String): Class<*>? {
        return XposedHelpers.findClass(className, WkGlobal.workLoader)
    }

    /**
     * 获取单例模式得实例
     */
    fun getInstanceByStaticMethod(className: String, instanceMethod: String): Any? {
        val cls = getClass(className)
        return XposedHelpers.callStaticMethod(cls, instanceMethod)
    }

    fun getInstanceNew() {

    }
}
