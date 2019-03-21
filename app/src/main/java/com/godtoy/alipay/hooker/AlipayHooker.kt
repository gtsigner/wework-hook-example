package com.godtoy.alipay.hooker

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XC_MethodHook
import android.content.Intent
import de.robv.android.xposed.XposedBridge
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Button


object AlipayHooker {

    fun hook(classLoader: ClassLoader, context: Context) {
        securityCheckHook(classLoader)
        try {
            val insertTradeMessageInfo = XposedHelpers.findClass("com.alipay.android.phone.messageboxstatic.biz.dao.TradeDao", classLoader)
            XposedBridge.hookAllMethods(insertTradeMessageInfo, "insertMessageInfo", object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    try {
                        XposedBridge.log("======start=========")
                        //获取content字段
                        //            			String content=(String) XposedHelpers.getObjectField(param.args[0], "content");
                        //            			XposedBridge.log(content);
                        //获取全部字段
                        val `object` = param!!.args[0]
                        val messageInfo = XposedHelpers.callMethod(`object`, "toString") as String
                        XposedBridge.log(messageInfo)

//                        val content = StringUtils.getTextCenter(MessageInfo, "content='", "'")
//                        if (content.contains("二维码收款") || content.contains("收到一笔转账")) {
//                            val jsonObject = JSONObject(content)
//                            val money = jsonObject.getString("content")
//                            val mark = jsonObject.getString("assistMsg2")
//                            val tradeNo = StringUtils.getTextCenter(MessageInfo, "tradeNO=", "&")
//                            XposedBridge.log("收到支付宝支付订单：$tradeNo==$money==$mark")
//
//                            val broadCastIntent = Intent()
//                            broadCastIntent.putExtra("bill_no", tradeNo)
//                            broadCastIntent.putExtra("bill_money", money)
//                            broadCastIntent.putExtra("bill_mark", mark)
//                            broadCastIntent.putExtra("bill_type", "alipay")
//                            broadCastIntent.action = BILLRECEIVED_ACTION
//                            context.sendBroadcast(broadCastIntent)
//                        }
                        XposedBridge.log("======end=========")
                    } catch (e: Exception) {
                        XposedBridge.log(e.message)
                    }

                    super.beforeHookedMethod(param)
                }
            })


            // hook设置金额和备注的onCreate方法，自动填写数据并点击
            XposedHelpers.findAndHookMethod("com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity", classLoader, "onCreate", Bundle::class.java, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam?) {
                    XposedBridge.log("Hook支付宝开始......")
                    val jinErField = XposedHelpers.findField(param!!.thisObject.javaClass, "b")
                    val jinErView = jinErField.get(param.thisObject)
                    val beiZhuField = XposedHelpers.findField(param.thisObject.javaClass, "c")
                    val beiZhuView = beiZhuField.get(param.thisObject)
                    val intent = (param.thisObject as Activity).intent
                    val mark = intent.getStringExtra("mark")
                    val money = intent.getStringExtra("money")
                    //设置支付宝金额和备注
                    XposedHelpers.callMethod(jinErView, "setText", money)
                    XposedHelpers.callMethod(beiZhuView, "setText", mark)
                    //点击确认
                    val quRenField = XposedHelpers.findField(param.thisObject.javaClass, "e")
                    val quRenButton = quRenField.get(param.thisObject) as Button
                    quRenButton.performClick()
                }
            })

            // hook获得二维码url的回调方法
            XposedHelpers.findAndHookMethod("com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity", classLoader, "a",
                    XposedHelpers.findClass("com.alipay.transferprod.rpc.result.ConsultSetAmountRes", classLoader), object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam?) {

                    val moneyField = XposedHelpers.findField(param!!.thisObject.javaClass, "g")
                    val money = moneyField.get(param.thisObject) as String

                    val markField = XposedHelpers.findField(param.thisObject.javaClass, "c")
                    val markObject = markField.get(param.thisObject)
                    val mark = XposedHelpers.callMethod(markObject, "getUbbStr") as String

                    val consultSetAmountRes = param.args[0]
                    val consultField = XposedHelpers.findField(consultSetAmountRes.javaClass, "qrCodeUrl")
                    val payurl = consultField.get(consultSetAmountRes) as String

                    XposedBridge.log("$money  $mark  $payurl")
                    XposedBridge.log("调用增加数据方法==>支付宝")
                    val broadCastIntent = Intent()
                    broadCastIntent.putExtra("money", money)
                    broadCastIntent.putExtra("mark", mark)
                    broadCastIntent.putExtra("type", "alipay")
                    broadCastIntent.putExtra("payurl", payurl)
                    broadCastIntent.action = QRCODERECEIVED_ACTION
                    context.sendBroadcast(broadCastIntent)
                }
            })

        } catch (e: Error) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun securityCheckHook(classLoader: ClassLoader) {
        try {
            val securityCheckClazz = XposedHelpers.findClass("com.alipay.mobile.base.security.CI", classLoader)
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", String::class.java, String::class.java, String::class.java, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val `object` = param!!.result
                    XposedHelpers.setBooleanField(`object`, "a", false)
                    param.result = `object`
                    super.afterHookedMethod(param)
                }
            })

            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", Class::class.java, String::class.java, String::class.java, object : XC_MethodReplacement() {
                @Throws(Throwable::class)
                override fun replaceHookedMethod(param: MethodHookParam): Any {
                    return 1.toByte()
                }
            })
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", ClassLoader::class.java, String::class.java, object : XC_MethodReplacement() {
                @Throws(Throwable::class)
                override fun replaceHookedMethod(param: MethodHookParam): Any {
                    return 1.toByte()
                }
            })
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", object : XC_MethodReplacement() {
                @Throws(Throwable::class)
                override fun replaceHookedMethod(param: MethodHookParam): Any {
                    return false
                }
            })

        } catch (e: Error) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    var BILLRECEIVED_ACTION = "com.tools.payhelper.billreceived"
    var QRCODERECEIVED_ACTION = "com.tools.payhelper.qrcodereceived"
}