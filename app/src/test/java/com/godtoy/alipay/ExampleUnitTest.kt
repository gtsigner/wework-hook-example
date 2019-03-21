package com.godtoy.alipay

import com.godtoy.alipay.bill.api.TradeApi
import org.junit.Test

import org.junit.Assert.*
import java.util.regex.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, (2 + 2).toLong())
    }


    @Test
    fun testParse() {
        //[{"mk":190359232709200003,"st":1,"isSc":0,"mct":1553009229875,"pl":"{\"templateType\":\"S\",\"commandType\":\"UPDATE\",\"expireLink\":\"\",\"msgType\":\"NOTICE\",\"icon\":\"https:\/\/gw.alipayobjects.com\/zos\/mwalletmng\/XYNZOTVpZxkryjGaSsoi.png\",\"link\":\"https:\/\/render.alipay.com\/p\/z\/merchant-mgnt\/simple-order.html?source=mdb_new_sqm_card\",\"msgId\":\"f5577f544ab148a3da9f04756dd746340009\",\"templateCode\":\"pep_MDailyBill_new_Push\",\"title\":\"商家服务·收款到账\",\"gmtCreate\":1553009229871,\"content\":\"{\\\"templateId\\\": \\\"WALLET-BILL@mbill-pay-fwc-dynamic-v2\\\", \\\"mainTitle\\\": \\\"收款金额\\\", \\\"mainAmount\\\": \\\"0.01\\\", \\\"contentList\\\": [{\\\"label\\\": \\\"汇总\\\", \\\"content\\\": \\\"今日第5笔收款，共计￥11500.03\\\", \\\"isHighlight\\\": false}, {\\\"label\\\": \\\"备注\\\", \\\"content\\\": \\\"用花呗收钱，可支持顾客使用花呗红包\\\", \\\"isHighlight\\\": true}], \\\"actionTitle\\\": \\\"商家服务\\\", \\\"actionName\\\": \\\"收款记录\\\", \\\"actionLink\\\": \\\"alipays:\/\/platformapi\/startapp?appId=60000081\\\", \\\"cardLink\\\": \\\"https:\/\/render.alipay.com\/p\/z\/merchant-mgnt\/simple-order.html?source=mdb_new_sqm_card\\\", \\\"content\\\": \\\"收款金额￥0.01\\\", \\\"link\\\": \\\"https:\/\/render.alipay.com\/p\/z\/merchant-mgnt\/simple-order.html?source=mdb_new_sqm_card\\\", \\\"assistMsg1\\\": \\\"今日第5笔收款，共计￥11500.03\\\", \\\"assistMsg2\\\": \\\"用花呗收钱，可支持顾客使用花呗红包\\\"}\",\"linkName\":\"\",\"gmtValid\":1555601229865,\"operate\":\"SEND\",\"bizName\":\"商家服务·收款到账\",\"templateName\":\"商家服务收款到账\",\"homePageTitle\":\"商家服务: ￥0.01 收款到账通知\",\"status\":\"\",\"extraInfo\":\"{\\\"expireLink\\\":\\\"https:\/\/render.alipay.com\/p\/f\/fd-jblxfp45\/pages\/home\/index.html\\\",\\\"actionTitle\\\":\\\"商家服务\\\",\\\"link\\\":\\\"https:\/\/render.alipay.com\/p\/z\/merchant-mgnt\/simple-order.html?source=mdb_new_sqm_card\\\",\\\"mainAmount\\\":\\\"0.01\\\",\\\"templateId\\\":\\\"WALLET-BILL@mbill-pay-fwc-dynamic-v2\\\",\\\"actionLink\\\":\\\"alipays:\/\/platformapi\/startapp?appId=60000081\\\",\\\"content\\\":\\\"收款金额￥0.01\\\",\\\"assistMsg2\\\":\\\"用花呗收钱，可支持顾客使用花呗红包\\\",\\\"assistMsg1\\\":\\\"今日第5笔收款，共计￥11500.03\\\",\\\"gmtValid\\\":1555601229865,\\\"cardLink\\\":\\\"https:\/\/render.alipay.com\/p\/z\/merchant-mgnt\/simple-order.html?source=mdb_new_sqm_card\\\",\\\"mainTitle\\\":\\\"收款金额\\\",\\\"contentList\\\":[{\\\"isHighlight\\\":false,\\\"label\\\":\\\"汇总\\\",\\\"content\\\":\\\"今日第5笔收款，共计￥11500.03\\\"},{\\\"isHighlight\\\":true,\\\"label\\\":\\\"备注\\\",\\\"content\\\":\\\"用花呗收钱，可支持顾客使用花呗红包\\\"}],\\\"actionName\\\":\\\"收款记录\\\"}\"}"}]
//        val str = "{\"templateType\":\"BN\",\"commandType\":\"UPDATE\",\"expireLink\":\"\",\"msgType\":\"NOTICE\",\"icon\":\"https://gw.alipayobjects.com/zos/rmsportal/EMWIWDsKUkuXYdvKDdaZ.png\",\"link\":\"alipays://platformapi/startapp?appId=20000003&actionType=toBillDetails&tradeNO=20190319200040011100780015090628&bizType=D_TRANSFER?tagid=MB_SEND_PH\",\"businessId\":\"PAY_HELPER_CARD_2088412370900091\",\"msgId\":\"3a5685c69c149b8840f678fd3a94c8860009\",\"templateCode\":\"00059_00094_zfzs001\",\"templateId\":\"WALLET-BILL@BLPaymentHelper\",\"title\":\"收到一笔转账\",\"gmtCreate\":1553000895297,\"content\":\"{\\\"status\\\":\\\"收到一笔转账\\\",\\\"date\\\":\\\"03月19日\\\",\\\"amountTip\\\":\\\"\\\",\\\"money\\\":\\\"0.01\\\",\\\"unit\\\":\\\"元\\\",\\\"infoTip\\\":\\\"\\\",\\\"failTip\\\":\\\"\\\",\\\"goto\\\":\\\"alipays://platformapi/startapp?appId=20000003&actionType=toBillDetails&tradeNO=20190319200040011100780015090628&bizType=D_TRANSFER\\\",\\\"content\\\":[{\\\"title\\\":\\\"付款方：\\\",\\\"content\\\":\\\"岚岚 157******03\\\"},{\\\"title\\\":\\\"转账备注：\\\",\\\"content\\\":\\\"转账\\\"},{\\\"title\\\":\\\"到账时间：\\\",\\\"content\\\":\\\"2019-03-19 21:08\\\"}],\\\"ad\\\":[],\\\"actions\\\":[{\\\"name\\\":\\\"\\\",\\\"url\\\":\\\"\\\"},{\\\"name\\\":\\\"查看详情\\\",\\\"url\\\":\\\"alipays://platformapi/startapp?appId=20000003&actionType=toBillDetails&tradeNO=20190319200040011100780015090628&bizType=D_TRANSFER\\\"}]}\",\"linkName\":\"\",\"gmtValid\":1555592895293,\"operate\":\"SEND\",\"bizName\":\"支付助手\",\"templateName\":\"支付助手\",\"homePageTitle\":\"支付助手: ￥0.01 收到一笔转账\",\"status\":\"\",\"extraInfo\":\"{\\\"content\\\":\\\"￥0.01\\\",\\\"assistMsg1\\\":\\\"收到一笔转账\\\",\\\"assistMsg2\\\":\\\"转账\\\",\\\"linkName\\\":\\\"\\\",\\\"buttonLink\\\":\\\"\\\",\\\"templateId\\\":\\\"WALLET-FWC@remindDefaultText\\\"}\"}"
        val link = "alipays://platformapi/startapp?appId=20000003&actionType=toBillDetails&tradeNO=20190319200040011100780015090628&bizType=D_TRANSFER?tagid=MB_SEND_PH-BN-"
        print(TradeApi.parseLinkTradeNo(link))
    }
}