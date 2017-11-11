package com.example.hp.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 *      OkHttpClient发送请求
 *      enqueue()子线程 去执行http请求 讲请求结果回调至callback中
 *      resp = client.newCall(request).execute()； 是同步方法 耗时
 *
 *
 * Created by hp on 2017/11/11.
 */

public class HttpUtil {

    public static  void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();   //创建请求
        client.newCall(request).enqueue(callback);
    }
}
