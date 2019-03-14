// IClientApi.aidl
package com.wework.xposed;

// Declare any non-default types here with import statements

interface IClientApi {
    String onReciveMessage(long conversationId,String message);
}
