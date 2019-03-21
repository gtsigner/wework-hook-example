package com.godtoy.alipay.bill.api

import com.godtoy.alipay.core.Logger
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.regex.Pattern

object TradeApi {
    fun parseMsgData(msgData: String): HashMap<String, String> {
        val arr = JSONArray(msgData)
        val plStr = arr.getJSONObject(0).getString("pl")
        return parsePlToObj(plStr)
    }

    private fun parsePlToObj(plStr: String): HashMap<String, String> {
        val plObj = JSONObject(plStr)
        val tempType = plObj.getString("templateType")

        if (tempType == "S") {

        }
        val extraInfo = JSONObject(plObj.getString("extraInfo"))
        val content = JSONObject(plObj.getString("content"))

        val link = plObj.getString("link")
        //content
        val mark = extraInfo.getString("assistMsg2")
        val assistMsg1 = extraInfo.getString("assistMsg1") ?: ""
        if (assistMsg1.contains("转账成功")) {
            throw Exception("不用提交")
        }
        //转账成功
        val money = content.getString("money")

        val tradeNo = parseLinkTradeNo(link)
        val map = HashMap<String, String>()
        map.put("money", money)
        map.put("mark", mark)
        map.put("tradeNo", tradeNo)
        Logger.debug("PLStr:", "$plStr@\n\nmoney=$money,tradeNo=$tradeNo,mark=$mark")
        return map

    }

    /**
     * 解析link中的订单号
     */
    fun parseLinkTradeNo(link: String): String {
        val matcher = Pattern.compile("tradeNO=(\\d+)&").matcher(link)
        while (matcher.find()) { //匹配器进行匹配
            return matcher.group(1)
        }
        return ""
    }
}