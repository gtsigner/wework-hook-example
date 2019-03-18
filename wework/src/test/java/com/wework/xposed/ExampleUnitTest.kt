package com.wework.xposed

import org.junit.Test

import org.junit.Assert.*
import java.nio.charset.Charset

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
    fun testHex() {
        val bytes = byteArrayOf(0x12, 7, 10, 0, 22, 3, 1, 9, 11)

        //010
        //000
        //020


        //012
        //017
        System.out.print("HELLO:" + getOct(String(bytes)) + "," + String(bytes))
    }

    //"UTF-8"
    fun getOct(s: String): String {
        val `as` = s.split("\\\\".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val arr = ByteArray(`as`.size - 1)
        for (i in 1 until `as`.size) {
            var sum = 0
            var base = 64
            for (c in `as`[i].toCharArray()) {
                sum += base * (c.toInt() - '0'.toInt())
                base /= 8
            }
            if (sum >= 128) sum -= 256
            arr[i - 1] = sum.toByte()
        }
        return String(arr, Charset.defaultCharset()) //如果还有乱码，这里编码方式你可以修改下，比如试试看unicode gbk等等
    }

}