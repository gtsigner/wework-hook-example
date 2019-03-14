package com.wework.xposed.common.bean

import com.alibaba.fastjson.JSON
import org.json.JSONObject
import java.lang.Exception

class TaskMessage(var event: String, var data: String) {
    lateinit var jsonObj: JSONObject

    init {
        try {
            jsonObj = JSON.parse(data) as JSONObject
        } catch (e: Exception) {

        }
    }
}