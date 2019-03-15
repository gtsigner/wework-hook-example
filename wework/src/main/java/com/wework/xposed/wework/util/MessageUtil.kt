package com.wework.xposed.wework.util

import com.wework.xposed.wework.WkGlobal
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

object MessageUtil {
    /**
     * 类下相互转化
     * 放入实体类Model：com.tencent.wework.foundation.model.Message
     * 返回数据类型：com.tencent.wework.foundation.model.pb.WwRichMessage$RichMessage
     */
    fun convertWwMessageIMessageToWwRichMessageIRichMessage(message: Any): Any {
        val msg = XposedHelpers.callMethod(message, "getInfo") as Any
        val mNativeHandle = XposedHelpers.getLongField(message, "mNativeHandle")
        val bytes = XposedHelpers.callMethod(message, "nativeGetInfo", mNativeHandle) as ByteArray

        //富文本类
        val richMessage = XposedHelpers.findClass("com.tencent.wework.foundation.model.pb.WwRichmessage\$RichMessage", WkGlobal.workLoader)
        val textMessage = XposedHelpers.findClass("com.tencent.wework.foundation.model.pb.WwRichmessage\$TextMessage", WkGlobal.workLoader)
        val riMessage = XposedHelpers.findClass("com.tencent.wework.foundation.model.pb.WwRichmessage\$Message", WkGlobal.workLoader)
        val atMessage = XposedHelpers.findClass("com.tencent.wework.foundation.model.pb.WwRichmessage\$AtMessage", WkGlobal.workLoader)
        val rMessage = XposedHelpers.findClass("com.tencent.wework.foundation.model.pb.WwMessage\$Message", WkGlobal.workLoader)


        //解析
        var res = XposedHelpers.callStaticMethod(textMessage, "parseFrom", bytes)
        var res2 = XposedHelpers.callStaticMethod(richMessage, "parseFrom", bytes)
        var res3 = XposedHelpers.callStaticMethod(riMessage, "parseFrom", bytes)
        var res4 = XposedHelpers.callStaticMethod(atMessage, "parseFrom", bytes)
        var res5 = XposedHelpers.callStaticMethod(rMessage, "parseFrom", bytes)


        //后面的参数是字节
        val content = XposedHelpers.getObjectField(msg, "content") as ByteArray
        XposedBridge.log("Rich:$res#Text:$res2#Rim:$res3#At:$res4,$content,$res5")

        return res
    }
}