package com.wework.xposed.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.alibaba.fastjson.JSON
import com.wework.aidl.IWeWorkEmitListener

import com.wework.xposed.R
import com.wework.xposed.common.bean.Message
import com.wework.xposed.common.bean.SocketSettings
import com.wework.xposed.core.*
import io.socket.client.Socket
import java.lang.Exception
import java.net.URI

class MainActivity : AppCompatActivity() {

    private val app = App.instance as App
    lateinit var etLogs: EditText
    lateinit var tvUsername: TextView
    private var counter = 0
    private lateinit var mBtnSave: Button
    private lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val txtInput = findViewById<View>(R.id.et_test_msg) as EditText
        tvUsername = findViewById<View>(R.id.current_username) as TextView
        val idInput = findViewById<View>(R.id.et_msg_id) as EditText
        val btnTest = findViewById<View>(R.id.btn_text_send) as Button
        val btnGetCAll = findViewById<View>(R.id.btn_get_conversion_list) as Button
        etLogs = findViewById<View>(R.id.et_logs) as EditText

        val etUri = findViewById<View>(R.id.et_uri) as EditText
        val etAppid = findViewById<View>(R.id.et_appid) as EditText
        val autoConnect = findViewById<View>(R.id.ckAutoConnect) as CheckBox
        mBtnSave = findViewById<View>(R.id.btn_save) as Button


        val share = getSharedPreferences(Mainer.SHARE_PRE_NAME, Context.MODE_PRIVATE)
        val uri = share.getString("SETTINGS_SOCKET_URI", "http://192.168.10.161:3008") as String
        val appid = share.getString("SETTINGS_SOCKET_APPID", "appid") as String
        val connected = share.getBoolean("SETTINGS_SOCKET_AUTO", true)
        //获取配置
        val setting = SocketSettings()
        setting.appId = appid
        setting.uri = URI(uri)
        setting.reconnected = connected
        app.mainer.start(setting)



        snackbar = Snackbar.make(mBtnSave, "链接断开,正在重新链接....", Snackbar.LENGTH_LONG).setAction("Action", null)
        mBtnSave.setOnClickListener {
            saveSettings(etUri.text.toString(), etAppid.text.toString(), autoConnect.isChecked)
            Toast.makeText(this, "保存成功,下次重启后生效", Toast.LENGTH_SHORT).show()
        }

        etAppid.setText(appid)
        etUri.setText(uri)
        autoConnect.isChecked = connected

        btnTest.setOnClickListener {
            try {
                Mainer.service?.workApi?.sendTextMessage(idInput.text.toString().toLong(), txtInput.text.toString())
                txtInput.text.clear()
            } catch (ex: Exception) {

            }
        }

        btnGetCAll.setOnClickListener {
            //获取联系人列表
            val str = Mainer.getWorkApi()?.conversationList ?: ""
            appendLog(str)
        }

        app.mainer.socker.getSocket().on(Socket.EVENT_CONNECT) {
            //链接成功
            runOnUiThread {
                val str = "服务器链接成功"
                mBtnSave.text = str
                snackbar.setText(str).show()
            }
        }

        app.mainer.socker.getSocket().on(Socket.EVENT_DISCONNECT) {
            runOnUiThread {
                val str = "服务器链接断开..."
                mBtnSave.text = str
                snackbar.setText(str).show()
            }
        }

        initEvents()
        initStart()
        if (connected) {
            app.mainer.connect()
        }
        testConnect()
    }

    //测试链接
    private fun testConnect() {
        app.mainer.connect()
        //获取当前用户信息
        if (app.mainer.socker.isConnected) {
            mBtnSave.text = "服务器链接成功"
            snackbar.setText("链接成功").show()
        }
        val user = Mainer.getWorkApi()?.loginUser ?: ""
        appendLog(user)
    }


    private fun initStart() {

    }

    /**
     * 保存设置
     */
    private fun saveSettings(uri: String, appid: String, connect: Boolean) {
        val share = getSharedPreferences(Mainer.SHARE_PRE_NAME, Context.MODE_PRIVATE)
        val editor = share.edit()
        editor.putString("SETTINGS_SOCKET_URI", uri)
        editor.putString("SETTINGS_SOCKET_APPID", appid)
        editor.putBoolean("SETTINGS_SOCKET_AUTO", connect)
        editor.apply()
    }


    private fun initEvents() {

    }

    //      Looper.prepare()
    private fun appendLog(str: String) {
        runOnUiThread {
            if (counter % 20 == 0) {
                etLogs.setText("自动清空")
                //每20次清空一次
            }
            etLogs.append(str + "\n")
            etLogs.setSelection(etLogs.text.length)
            counter++
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

}
