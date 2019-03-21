package com.godtoy.alipay.bill.tools;

/**
 * Created by Administrator on 2018/4/24.
 */

public interface OnResponseListener {
    void onSuccess(String response);
    void onError(String error);
}
