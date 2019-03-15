// IWeWorkService.aidl
package com.wework.aidl;

// Declare any non-default types here with import statements
import com.wework.aidl.IWeWorkCallBack;
import com.wework.aidl.IWeWorkEmitListener;
//处理器
import com.wework.xposed.IWeWorkApi;
import com.wework.xposed.IClientApi;

/** {@hide} */
interface IWeWorkService {

    void setWorkApi(IWeWorkApi api);
    IWeWorkApi getWorkApi();

    void setClientApi(IClientApi api);
    IClientApi getClientApi();
}
