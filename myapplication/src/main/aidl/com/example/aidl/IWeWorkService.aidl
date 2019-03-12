// IWeWorkService.aidl
package com.example.aidl;

// Declare any non-default types here with import statements
import com.example.aidl.IWeWorkCallBack;

/** {@hide} */
interface IWeWorkService {

    void weWorkReady();
    void clientReady();

    //通过这个客户端进行数据发送
    boolean sendMessage(int what,int messageType,String message);

    //注册回调函数
    void registerCallback(IWeWorkCallBack callback);
    void unregisterCallback(IWeWorkCallBack callback);
}
