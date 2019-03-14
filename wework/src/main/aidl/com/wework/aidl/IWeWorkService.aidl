// IWeWorkService.aidl
package com.wework.aidl;

// Declare any non-default types here with import statements
import com.wework.aidl.IWeWorkCallBack;
import com.wework.aidl.IWeWorkEmitListener;
//处理器
import com.wework.xposed.IWeWorkHandler;

/** {@hide} */
interface IWeWorkService {

    void setWorkhHandler(IWeWorkHandler handelr);
    IWeWorkHandler getWorkHandler();

    void clientReady();

    //1.发送给指定接收者类型和事件
    void send(int reciverType,String event,String data);
    //2.广播给所有人包括自己注册的事件
    void boastcast(String event,String data);
    //3.推送,除了自己注册的事件收不到以外
    void emit(int senderType,String event,String data);

    //注册回调函数
    void registerCallback(int reciver,IWeWorkCallBack callback);
    void unregisterCallback(int reciver,IWeWorkCallBack callback);

    //emit函数支持
    void on(String event,int reciverType,IWeWorkEmitListener callback);
    //绑定所有事件
    void all(int reciverType,IWeWorkEmitListener callback);

    //卸载指定事件
    void un(String event);

    //清空所有服务
    void clear();

}
