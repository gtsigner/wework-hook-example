package com.wework.xposed.base

import android.util.Log

/**
 * 一组 Class 对象的集合, 可以通过调用不同的 filter 函数筛选得到想要的结果
 */
class Classes(private val classes: List<Class<*>>) {
    /**
     * @suppress
     */
    private companion object {
        private const val TAG = "Reflection"
    }

    fun filterBySuper(superClass: Class<*>?): Classes {
        return Classes(classes.filter { it.superclass == superClass }.also {
            if (it.isEmpty()) {
                Log.w(TAG, "filterBySuper found nothing, super class = ${superClass?.simpleName}")
            }
        })
    }

    fun filterByEnclosingClass(enclosingClass: Class<*>?): Classes {
        return Classes(classes.filter { it.enclosingClass == enclosingClass }.also {
            if (it.isEmpty()) {
                Log.w(TAG, "filterByEnclosingClass found nothing, enclosing class = ${enclosingClass?.simpleName} ")
            }
        })
    }

    fun firstOrNull(): Class<*>? {
        if (classes.size > 1) {
            val names = classes.map { it.canonicalName }
            Log.w("Xposed", "found a signature that matches more than one class: $names")
        }
        return classes.firstOrNull()
    }
}