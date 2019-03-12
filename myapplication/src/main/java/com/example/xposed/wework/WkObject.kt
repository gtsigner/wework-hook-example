package com.example.xposed.wework

/**
 * 全局方法
 */
object WkObject {

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
    object CoversationEngine {
        object C {
            const val CoversationEngine = "ecz"
        }

        object M {
            const val GetInstance = "cfh"
            const val GetConversationService="aAh"
            const val GetDepartmentService="getDepartmentService"
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

}
