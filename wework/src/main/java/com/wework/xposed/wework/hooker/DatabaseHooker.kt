package com.wework.xposed.wework.hooker

import android.content.ContentValues
import com.bugfender.sdk.Bugfender
import com.wework.xposed.wework.WkGlobal
import com.wework.xposed.wework.WkObject
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers


object DatabaseHooker : Hooker {
    override fun executeHook() {

    }

    fun hookDb() {
        val database = XposedHelpers.findClass(WkObject.Message.C.SQLiteDatabase, WkGlobal.classLoader)
        val databaseFactory = XposedHelpers.findClass(WkObject.Message.C.SQLiteDatabaseCursorFactory, WkGlobal.classLoader)
        val databaseCancellationSignal = XposedHelpers.findClass(WkObject.Message.C.SQLiteCancellationSignal, WkGlobal.classLoader)

        val messageManager = WkGlobal.classLoader.loadClass(WkObject.MessageSender.C.MessageManager)
        val externalGroupMessageListActivity = WkGlobal.classLoader.loadClass(WkObject.MessageSender.C.ExternalGroupMessageListActivity)
        XposedHelpers.findAndHookMethod(messageManager, WkObject.MessageSender.M.GroupTextSend, externalGroupMessageListActivity::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val thisObject = param.thisObject
                val table = param.args[0] as String
                val nullColumnHack = param.args[1] as String?
                val initialValues = param.args[2] as ContentValues?
                val conflictAlgorithm = param.args[3] as Int
                XposedBridge.log("XXXX Table:$table")
            }
        })



        XposedHelpers.findAndHookMethod(database, WkObject.Message.M.INSERT, String::class.java, String::class.java, ContentValues::class.java, Int::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                val thisObject = param.thisObject
                val table = param.args[0] as String
                val nullColumnHack = param.args[1] as String?
                val initialValues = param.args[2] as ContentValues?
                val conflictAlgorithm = param.args[3] as Int
                XposedBridge.log("Insert Table:$table")

            }
        })
        XposedHelpers.findAndHookMethod(database, WkObject.Message.M.UPDATE, String::class.java, ContentValues::class.java, String::class.java, Array<String>::class.java, Int::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val thisObject = param.thisObject
                val table = param.args[0] as String
                val nullColumnHack = param.args[1] as String?
                val initialValues = param.args[2] as ContentValues?
                val conflictAlgorithm = param.args[3] as Int
                Bugfender.d("Debug", "Update Table:$table")
                param.args.forEach { it ->
                    XposedBridge.log("Params:$it")
                }
                XposedBridge.log("Update Table:$table")
            }
        })
        XposedHelpers.findAndHookMethod(database, "execSQL", String::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val thisObject = param.thisObject
                val table = param.args[0] as String
                Bugfender.d("Debug", "execSQL Table:$table")
                param.args.forEach { it ->
                    XposedBridge.log("Params:$it")
                }
                XposedBridge.log("execSQL Table:$table")
            }
        });
        XposedHelpers.findAndHookMethod(database, WkObject.Message.M.QUERY,
                databaseFactory,
                String::class.java, Array<String>::class.java, String::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val thisObject = param.thisObject
                        val table = param.args[0] as String
                        Bugfender.d("Debug", "rawQueryWithFactory Table:$table")
                        param.args.forEach { it ->
                            XposedBridge.log("Params:$it")
                        }
                    }
                })

        XposedHelpers.findAndHookMethod(database, WkObject.Message.M.QUERY,
                databaseFactory,
                String::class.java, Array<Any>::class.java, String::class.java,
                databaseCancellationSignal,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val thisObject = param.thisObject
                        val table = param.args[0] as String
                        Bugfender.d("Debug", "rawQueryWithFactory Table:$table")
                        param.args.forEach { it ->
                            XposedBridge.log("Params:$it")
                        }
                    }
                })
        XposedHelpers.findAndHookMethod(database, "executeSql", String::class.java, Array<Any>::class.java, databaseCancellationSignal::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val thisObject = param.thisObject
                val table = param.args[0] as String
                Bugfender.d("Debug", "executeSql Table:$table")
                param.args.forEach { it ->
                    XposedBridge.log("Params:$it")
                }
                XposedBridge.log("executeSql Table:$table")
            }
        })
    }
}