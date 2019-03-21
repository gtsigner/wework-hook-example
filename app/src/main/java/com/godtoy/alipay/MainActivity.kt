package com.godtoy.alipay

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.godtoy.alipay.bill.api.XShare
import com.godtoy.alipay.utils.FileUtils
import android.content.pm.PackageManager
import android.os.Build
import android.app.Activity


object AppPermission {
    fun isGrantExternalRW(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            return false
        }
        return true
    }
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //获取
        val share = getSharedPreferences("config", Context.MODE_PRIVATE)
//        val uri = share.getString(XShare.HOST, "http://192.168.10.161:3008/ashx/AutoCZ.ashx") as String
//        val key = share.getString(XShare.KEY, "649000") as String
        //获取
        val uri = if (FileUtils.readFileSdcardFile(XShare.HOST) == "") "http://47.52.31.204/ashx/AutoCZ.ashx" else FileUtils.readFileSdcardFile(XShare.HOST)
        val key = if (FileUtils.readFileSdcardFile(XShare.KEY) == "") "649000" else FileUtils.readFileSdcardFile(XShare.KEY)

        val etHost = findViewById(R.id.et_host) as EditText
        val etKey = findViewById(R.id.et_key) as EditText
        etHost.setText(uri)
        etKey.setText(key)

        AppPermission.isGrantExternalRW(this)

        val btnSave = findViewById(R.id.btn_save) as Button

        btnSave.setOnClickListener {
            val newUri = etHost.text.toString()
            val newKey = etKey.text.toString()
            val editor = share.edit()
            editor.putString(XShare.HOST, newUri)
            editor.putString(XShare.KEY, newKey)

            FileUtils.writeFileSdcardFile(XShare.HOST, newUri)
            FileUtils.writeFileSdcardFile(XShare.KEY, newKey)

            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
        }
    }

}
