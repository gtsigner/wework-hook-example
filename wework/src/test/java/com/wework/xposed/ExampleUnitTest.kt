package com.wework.xposed

import org.junit.Test

import org.junit.Assert.*

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
        val bytes = byteArrayOf(12, 7, 10, 0, 22, 3, 1, 9, 11)
        System.out.println("\\012\\007\\010\\000\\022\\003\\012\\0011:" + String(bytes) + 0x12)
    }
}