package com.godtoy.alipay.utils

import android.os.Environment

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by ZengYinan.
 * Date: 2019/3/19 23:23
 * Email: 498338021@qq.com
 * Desc:
 */
object FileUtils {

    val sdCardPath: String
        get() = Environment.getExternalStorageDirectory().toString() + "/"

    //写数据到SD中的文件
    fun writeFileSdcardFile(fileName: String, write_str: String) {
        try {
            val file = File(sdCardPath + fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            val fout = FileOutputStream(sdCardPath + fileName)
            val bytes = write_str.toByteArray()

            fout.write(bytes)
            fout.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //读SD中的文件
    fun readFileSdcardFile(fileName: String): String {
        var res = ""
        try {
            val file = File(sdCardPath + fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            val fin = FileInputStream(sdCardPath + fileName)

            val length = fin.available()

            val buffer = ByteArray(length)
            fin.read(buffer)

            res = String(buffer)

            fin.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return res
    }
}
