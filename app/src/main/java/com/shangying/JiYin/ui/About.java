package com.shangying.JiYin.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.shangying.JiYin.R;

public class About extends AppCompatActivity implements View.OnClickListener {

    TextView developer_link_github,developer_link_gitee, developer_link_myblog, developer_link_onlinetool;

    private IntentFilter intentFilter;
private TextView versioncode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        developer_link_github = findViewById(R.id.developer_link_github);
        developer_link_gitee = findViewById(R.id.developer_link_gitee);
        developer_link_myblog = findViewById(R.id.developer_link_myblog);
        developer_link_onlinetool = findViewById(R.id.developer_link_onlinetool);
        versioncode=findViewById(R.id.versioncode);
        init();
        developer_link_github.setOnClickListener(this::onClick);
        developer_link_gitee.setOnClickListener(this::onClick);
        developer_link_myblog.setOnClickListener(this::onClick);
        developer_link_onlinetool.setOnClickListener(this::onClick);
    }
    private void init(){
        versioncode=(TextView) findViewById(R.id.versioncode);
        versioncode.setText("版本号:  "+getHandSetInfo());
    }
    private String getHandSetInfo(){
        String handSetInfo= getAppVersionName(About.this);
        return handSetInfo;
    }
    //获取当前版本号
    private String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo("com.shangying.JiYin", 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.developer_link_github:  {
                Uri uri = Uri.parse("https://github.com/123shangying");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            }
            case R.id.developer_link_gitee:  {
                Uri uri = Uri.parse("https://gitee.com/shangyingwx");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            }
            case R.id.developer_link_myblog:  {
                Uri uri = Uri.parse("https://shangying.host/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            }
            case R.id.developer_link_onlinetool:  {
                Uri uri = Uri.parse("https://gitee.com/shangyingwx/jiyin");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            }

        }

    }

}