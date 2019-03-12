package com.example.xposed.main

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.alibaba.fastjson.JSON
import com.example.aidl.IWeWorkService

import com.example.xposed.R
import com.example.xposed.common.bean.Message
import com.example.xposed.core.Socker
import com.example.xposed.core.WeWorkService
import com.example.xposed.core.WkClientType
import com.example.xposed.core.WkEvent
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val app = App.instance as App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        val txtInput = findViewById<View>(R.id.et_test_msg) as EditText
        val idInput = findViewById<View>(R.id.et_msg_id) as EditText
        val btnTest = findViewById<View>(R.id.btn_text_send) as Button
        val btnSendAll = findViewById<View>(R.id.btn_send_all) as Button

        btnSendAll.setOnClickListener {
            val message = Message()
            message.reciverId = 0
            message.message = txtInput.text.toString()
            app.service.send(WkClientType.WeWork, WkEvent.WK_CMD_SEND_MESSAGE, JSON.toJSONString(message))
            txtInput.text.clear()
        }


        btnTest.setOnClickListener {
            val message = Message()
            message.reciverId = idInput.text.toString().toLong()
            message.message = txtInput.text.toString()
            app.service.send(WkClientType.WeWork, WkEvent.WK_CMD_SEND_MESSAGE, JSON.toJSONString(message))
            txtInput.text.clear()
        }
        fab.setOnClickListener { view ->
            val snackbar = Snackbar.make(view, "正在测试服务器链接...", Snackbar.LENGTH_LONG).setAction("Action", null)
            snackbar.show()
            app.socker.connect()
            if (app.socker.isConnected) {
                snackbar.setText("服务器已链接")
            }
            //测试发送消息
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
