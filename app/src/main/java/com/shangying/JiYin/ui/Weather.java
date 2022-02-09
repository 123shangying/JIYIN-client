package com.shangying.JiYin.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.shangying.JiYin.R;

public class Weather extends AppCompatActivity {

    private WebView webView;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {
            //设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        //设置WebView属性,运行执行js脚本
        webView.getSettings().setJavaScriptEnabled(true);
        //调用loadUrl方法为WebView加入链接
        webView.loadUrl("https://widget-page.qweather.net/h5/index.html?md=012346&bg=1&lc=auto&key=a1035bd660524572ae41db3519056d44&v=_1632831670234");
        //调用Activity提供的setContentView将webView显示出来
        setContentView(webView);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}


