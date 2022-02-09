package com.shangying.JiYin;


import android.app.Application;

import com.shangying.JiYin.Utils.Log;
import com.shangying.JiYin.Utils.OkHttp;

/**
 * @author shangying
 * @since 2021/8/8.
 * @effect  全局数据配置层
 */

public class MyApplication<s> extends Application {
    //
//    master
    /**
     * 设置全局ip或者域名 s
     */
    //public static String S_IP="https://xxxxxxxxxz/sportapi/";
    public static String s="https://xxxxxxxxxxxx/sportapi/";
    public static String EMAIL_IP="https://xxxxxxxxxxxxxxx/email";
//    dev
    public static String S_IP="http://192.168.0.51:8081/sportapi/";



    @Override
    public void onCreate() {
//        Okhttp相关配置
        super.onCreate();
        Log.DEBUG = true;
        OkHttp.setConnectTimeOut(30);
        OkHttp.setReadTimeOut(30);
        OkHttp.setWriteTimeOut(30);
        OkHttp.setUploadReadTimeOut(30);
        OkHttp.setUploadWriteTimeOut(30);
        OkHttp.setErrorStatus("state", "0000");
    }

}
