package com.godtoy.alipay.bill.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import kotlin.text.Charsets;

/**
 * Created by Administrator on 2018/4/24.
 */

public class HttpHelper {
    private static final Object object = new Object();

    public static void postRequest(String url, Map<String, String> params, OnResponseListener listener) {
        synchronized (object) {
            StringBuilder sb = new StringBuilder();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
            }
            if (listener != null) {
                try {
                    URL path = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) path.openConnection();
                    con.setRequestProperty("connection", "Keep-Alive");
                    con.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");
                    con.setRequestProperty("accept", "*/*");
                    con.setRequestMethod("POST");
                    con.setConnectTimeout(10000);
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    byte[] bytes = sb.toString().getBytes();
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(bytes);
                    outputStream.close();
                    int ret = con.getResponseCode();
                    if (ret == 200) {
                        onSuccessRespond(Charsets.UTF_8, listener, con);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onError(listener, e);
                }
            }
        }
    }

    private static void onError(OnResponseListener listener, Exception onError) {
        if (null != listener) {
            listener.onError(onError.toString());
        }
    }

    private static void onSuccessRespond(Charset encode, OnResponseListener listener, HttpURLConnection con) throws IOException {
        InputStream inputStream = con.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();//创建内存输出流
        int len = 0;
        byte[] bytes = new byte[1024];
        if (inputStream != null) {
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            String str = new String(outputStream.toByteArray(), encode);
            if (null != listener) {
                listener.onSuccess(str);
            }
        }
    }
}
