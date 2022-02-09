package com.shangying.JiYin.function;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.shangying.JiYin.R;

public class Webviewxy extends AppCompatActivity {
private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewxy);
        webView = (WebView) findViewById(R.id.webViewxy);
        //设置WebView属性，能够执行Javascript脚本
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        //即asserts文件夹下有一个a.html
        webView.loadUrl("file:///android_asset/yhxy.html");
    }
}