package com.godtoy.alipay.hooker

import android.app.Application
import android.content.Context
import com.godtoy.alipay.bill.alipay.utils.HookUtil
import com.godtoy.alipay.bill.api.TradeApi
import com.godtoy.alipay.bill.api.XShare
import com.godtoy.alipay.bill.bean.Trade
import com.godtoy.alipay.bill.tools.HttpHelper
import com.godtoy.alipay.bill.tools.OnResponseListener
import com.godtoy.alipay.bill.tools.ThreadPoolManager
import com.godtoy.alipay.core.Logger
import com.godtoy.alipay.utils.FileUtils
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.HashMap

object Hooker {

    private val TAG = "Xposed"
    //alipay
    private val CUS_XP_KEY = "aliPayZhiFuBao"
    private lateinit var app: Application
    private var version: Any? = null

    fun execute(lpparam: XC_LoadPackage.LoadPackageParam) {
        this.classLoader = lpparam.classLoader
//        HotXposed.hook(HookerDispatcher::class.java, lpparam)
        if (lpparam.isFirstApplication) {
            findAndHookMethod(Application::class.java, "onCreate", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                    app = param!!.thisObject as Application
                    XposedBridge.log("Alipay save application")
                    AlipayHooker.hook(classLoader, app)
                }
            })
        }

        ciHook(lpparam)
        aliHook(lpparam)
        return
    }

    private lateinit var classLoader: ClassLoader
    private fun getClass(cls: String): Class<*>? {
        return XposedHelpers.findClass(cls, classLoader)
    }

    object Version105 {
        const val ALIPAY_PKG_NAME = "com.eg.android.AlipayGphone"

        object Cls {
            const val Synce = "com.alipay.android.phone.messageboxstatic.biz.sync.e"
            //不变
            const val SyncMessage = "com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage"
        }

        object CI {
            const val Ci = "com.alipay.mobile.base.security.CI"
        }
    }

    object Version105138 {
        const val ALIPAY_PKG_NAME = "com.eg.android.AlipayGphone"

        object Cls {
            const val Synce = "com.alipay.android.phone.messageboxstatic.biz.sync.d"
            //不变
            const val SyncMessage = "com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage"
        }

        object CI {
            const val Ci = "com.alipay.mobile.base.security.CI"
        }
    }

    /**
     *@param lpparam
     */
    private fun ciHook(lpparam: XC_LoadPackage.LoadPackageParam) {
        //1.检测
        val xpCheckClass = getClass(Version105.CI.Ci)
        if (xpCheckClass != null) {
            findAndHookMethod(xpCheckClass, "a", Class::class.java, String::class.java, String::class.java, object : XC_MethodHook() {
                override fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam) {
                    param.args[1] = CUS_XP_KEY
                    HookUtil.printLogs("Alipay->a", param)
                }
            })
        }
    }

    /**
     * 支付宝hook
     */
    private fun aliHook(lpparam: XC_LoadPackage.LoadPackageParam) {
        val receiveClass = getClass(Version105138.Cls.Synce)
        //接收模型
        val receiveModelClass = getClass(Version105138.Cls.SyncMessage)
        //收到消息
        if (receiveClass != null && receiveModelClass != null) {
            findAndHookMethod(receiveClass, "onReceiveMessage", receiveModelClass, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam) {
                    HookUtil.printLogs("Alipay->收到消息->${lpparam.processName}", param)
                    if (param.args != null && param.args.isNotEmpty()) {
                        val recMsg = param.args[0]
                        val str = param.args[0].toString()
                        try {
                            convertSyncMessageToTrade(recMsg)
                            receiveCall(str)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                }
            })
        }
        //hook
        //test
        //getBillListRpcService(app)
    }

    private fun getCRpcService(context: Context): Any? {
        val obj: Any? = null
        return try {
            val findClass = XposedHelpers.findClass("com.alipay.mobile.beehive.util.ServiceUtil", context.classLoader)
            val findClass2 = XposedHelpers.findClass("com.alipay.mobile.framework.service.common.RpcService", context.classLoader)
            val findClass3 = XposedHelpers.findClass("com.alipay.transferprod.rpc.CollectMoneyRpc", context.classLoader)
            XposedHelpers.callMethod(XposedHelpers.callStaticMethod(findClass, "getServiceByInterface", findClass2), "getRpcProxy", *arrayOf<Any>(findClass3))
        } catch (e: Exception) {
            e.printStackTrace()
            obj
        }

    }


    private fun getBillListRpcService(context: Context): Any? {
        val obj: Any? = null
        return try {
            val alipayApplication = XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication", context.classLoader)
            //app实例
            val appInstance = XposedHelpers.callStaticMethod(alipayApplication, "getInstance")
            //find
            val findClass2 = XposedHelpers.findClass("com.alipay.mobilebill.biz.rpc.bill.v9.BillListRPCService", context.classLoader)
            //ctx
            val microAppCtxInstan = XposedHelpers.callMethod(appInstance, "getMicroApplicationContext")
            //rpc
            val rpcService = XposedHelpers.callMethod(microAppCtxInstan, "findServiceByInterface", "com.alipay.mobile.framework.service.common.RpcService")
            XposedHelpers.callMethod(rpcService, "getRpcProxy", findClass2)
        } catch (th: Throwable) {
            th.printStackTrace()
            obj
        }

    }

    private fun getRpcProxy(context: Context): Any? {
        val obj: Any? = null
        return try {
            //app
            val findClass = XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication", context.classLoader)
            val alipayApplicationInstan = XposedHelpers.callStaticMethod(findClass,
                    "getInstance", *arrayOfNulls(0))
            val ctx2 = XposedHelpers.callMethod(alipayApplicationInstan, "getMicroApplicationContext",
                    *arrayOfNulls(0))
            val instance3 = XposedHelpers.callMethod(ctx2, "findServiceByInterface", "com.alipay.mobile.framework.service.common.RpcService")
            //rc
            val findClass2 = XposedHelpers.findClass("com.alipay.mobilebill.biz.rpc.bill.QueryWealthBillInfoRPCService", context.classLoader)
            XposedHelpers.callMethod(instance3, "getRpcProxy", findClass2)
        } catch (th: Throwable) {
            th.printStackTrace()
            obj
        }
    }

    private fun receiveCall(message: String) {


    }


    //key payno payje paymore
    /**
     * 发送请求
     * @param mark
     * @param money
     * @param tradeNo
     */
    private fun postRequest(mark: String, money: String, tradeNo: String) {
        val map = HashMap<String, String>()
//        val host = XShare.getHost()
//        val key = XShare.getKey()
        //v2
        val host = FileUtils.readFileSdcardFile(XShare.HOST)
        var key = FileUtils.readFileSdcardFile(XShare.KEY)
//        val host = "http://47.52.31.204/ashx/AutoCZ.ashx"
//        val key = "649000"
        map["key"] = key
        map["ddh"] = tradeNo
        map["money"] = money
        map["name"] = mark
        val myRunnable = MyRunnable(host, map)
        Logger.debug("SendRequest:", "$key,$tradeNo,$money,$map,$host")
        ThreadPoolManager.newInstance().addExecuteTask(myRunnable)
    }

    internal class MyRunnable(private val host: String, map: Map<String, String>) : Runnable {

        private val mMap: Map<String, String> = map

        override fun run() {
            HttpHelper.postRequest(this.host, mMap, object : OnResponseListener {
                override fun onSuccess(response: String) {
                    Logger.debug("Response：", response)
                }

                override fun onError(error: String) {
                    Logger.debug("Response：", error)
                }
            })
        }
    }


    private fun convertSyncMessageToTrade(recv: Any) {
        val userId = XposedHelpers.getObjectField(recv, "userId") as String
        val biz = XposedHelpers.getObjectField(recv, "biz") as String
        //重要数据
        val pushData = XposedHelpers.getObjectField(recv, "pushData") as String
        val id = XposedHelpers.getObjectField(recv, "id") as String
        val msgData = XposedHelpers.getObjectField(recv, "msgData") as String
        //if(msgData.contains(""))
        val obj = TradeApi.parseMsgData(msgData)
        //post提交
        val mark = obj.get("mark") as String
        val money = obj.get("money") as String
        val tradeNo = obj.get("tradeNo") as String
        postRequest(mark, money, tradeNo)

    }
}