// IWeWorkCallback.aidl
package com.example.aidl;

// Declare any non-default types here with import statements

/** {@hide} */
interface IWeWorkCallBack {
//    /**
//     * Demonstrates some basic types that you can use as parameters
//     * and return values in AIDL.
//     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
    //消息回调
    void onMessage(int what,int messageType,String message);
}
