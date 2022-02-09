package com.shangying.JiYin.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.shangying.JiYin.R;
import com.shangying.JiYin.ui.fragment.dashboard.DashboardFragment;

/**
 * Created with IntelliJ IDEA.
 * User: shangying.
 * Email: shangying611@gmail.com
 * Blog:  https://shangying.host/
 * Date: 2021/08/28.
 * Time: 17:02.
 * Explain:开屏动画
 */

public class WelcomeActivity extends Activity implements BoxLid.OnBoxLidOpenedListener, BoxLid.OnBoxLidClosedListener {
    private BoxLid vBoxLid;
    private int keyBackClickCount = 0;
    private static final int HANDLER_BOXLID_LOADING_COMPLETE = 0;
    private static final int HANDLER_BOXLID_OPEN = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_BOXLID_LOADING_COMPLETE:
                    try {
                        vBoxLid.loadingComplete();
                    }catch (Exception e){

                    }

                    sendEmptyMessageDelayed(HANDLER_BOXLID_OPEN, 200);
                    break;
                case HANDLER_BOXLID_OPEN:
                    vBoxLid.open();
                    break;
                default:
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //步骤1：创建一个SharedPreferences对象
        SharedPreferences sss= getSharedPreferences("data",Context.MODE_PRIVATE);
//步骤2： 实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = sss.edit();
//步骤3：将获取过来的值放入文件
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        DashboardFragment.getShowall();
        setContentView(R.layout.activity_welcome);
        vBoxLid = (BoxLid) findViewById(R.id.main_boxlid);

        vBoxLid.setBottomHalfTitle("Welcome! \njoin\n迹印");
        vBoxLid.setBoxLidTitleTextSize(R.dimen.textSize_body);

        vBoxLid.setCenterOrnamental(R.mipmap.lll);
        vBoxLid.setBoxLidColor(getResources().getColor(R.color.titleBarBackgroundColor));
        vBoxLid.setBoxLidTitleColor(getResources().getColor(R.color.titleColor));
        vBoxLid.setOnBoxLidOpenedListener(this);
        vBoxLid.setOnBoxLidClosedListener(this);
        vBoxLid.loading();

        String u=sss.getString("s","");
        if("".equals(u)){
            mHandler.sendEmptyMessageDelayed(HANDLER_BOXLID_LOADING_COMPLETE, 2000);
        }else {
        mHandler.sendEmptyMessageDelayed(HANDLER_BOXLID_LOADING_COMPLETE, 50);
        }
        editor.putString("s", "1");
        editor.commit();
    }

    @Override
    public void onBoxLidOpened() {

        vBoxLid.setVisibility(View.INVISIBLE);
        //打开盒子后重置返回键统计
        Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    public void onBoxLidClosed() {
        //如果在Closing状态中连按2次以上返回键则直接退出
        if (keyBackClickCount >= 2) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (vBoxLid.getBoxLidStatus() == BoxLid.BOXLID_STATUS_OPENING) {
                return true;
            }
            keyBackClickCount++;
            //App启动加载数据时，点击返回直接退出
            if (vBoxLid.getBoxLidStatus() == BoxLid.BOXLID_STATUS_LOADING) {
                finish();
            } else if (vBoxLid.getBoxLidStatus() != BoxLid.BOXLID_STATUS_CLOSING && vBoxLid.getBoxLidStatus() != BoxLid.BOXLID_STATUS_CLOSED) {
                //第一次点击的时候提示，并关闭盒子
                //Toast.makeText(getApplicationContext(), getResources().getString(R.string.exit_to_app), Toast.LENGTH_SHORT).show();
                vBoxLid.setVisibility(View.VISIBLE);
                vBoxLid.close();
            } else if (vBoxLid.getBoxLidStatus() == BoxLid.BOXLID_STATUS_CLOSED) {
                //关闭盒子状态下，点击返回则退出
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
