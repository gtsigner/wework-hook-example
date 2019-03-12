package com.example.xposed.wework.tests

import okhttp3.OkHttpClient
import okhttp3.Request


object Request {
    val http = OkHttpClient()

    fun run(url: String): String {
        val request = Request.Builder()
                .url(url)
                .build()
        val resp = http.newCall(request).execute()
        return resp.body().toString()
    }
}