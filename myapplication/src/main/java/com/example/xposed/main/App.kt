package com.example.xposed.main

import android.app.Application

import com.example.xposed.core.Socker

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Socker.connect()
    }
}
