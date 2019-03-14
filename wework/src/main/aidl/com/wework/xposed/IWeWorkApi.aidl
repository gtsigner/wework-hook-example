// IWeWorkApi.aidl
package com.wework.xposed;

// Declare any non-default types here with import statements

interface IWeWorkApi {
    String sayHello();
    String getLoginUser();
    String getConversationList();
    String getConversationById(long id);
    String getConversationUsers(long id);
    //发送消息
    Boolean sendMessage(long id,String message);
}
