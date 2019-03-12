// IWeWorkEmitListener.aidl
package com.example.aidl;

// Declare any non-default types here with import statements

/** {@hide} */
interface IWeWorkEmitListener {
//    /**
//     * Demonstrates some basic types that you can use as parameters
//     * and return values in AIDL.
//     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
    //消息回调，发送的客户端，事件，信息
    void callback(int senderType,String event,String data);
}
