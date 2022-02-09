package com.shangying.JiYin.ui.fragment.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.alibaba.fastjson.JSON;
import com.shangying.JiYin.MyApplication;
import com.shangying.JiYin.R;
import com.shangying.JiYin.Utils.OkCallback;
import com.shangying.JiYin.Utils.OkHttp;
import com.shangying.JiYin.Utils.ToastUtilPuls;
import com.shangying.JiYin.ui.MainActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建日期：2021/6/13 14:52
 *
 * @author 林凯
 * 文件名称： ShareActivity.java
 * 类说明：发布动态的 Activity
 */
public class ShareActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     *  获取用户id
     */
    private final static String GETID = MyApplication.S_IP+"user/getid";
    /**
     *     //    插入动态
     */
    private final static String INSDADT = MyApplication.S_IP+"dynamic/insdata";
    /**
     * 定义返回值   成功  1   （拒绝魔法值）
     */
    private final static String  OK="1";
    /**
     * 定义返回值  失败   0    （拒绝魔法值）
     */
    private final static String NO="0";
    ImageView share_back;
    Button share_send;
    EditText et_title, et_content;
    Spinner spinner;

    String title, content, privacy;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initWidget();
        getId();
        // 设置有颜色的状态栏
        setStatusBarColor(this, Color.parseColor("#1292d0"));
    }

    private void initWidget() {
        share_back = findViewById(R.id.share_back);
        share_send = findViewById(R.id.share_send);
        spinner = findViewById(R.id.share_spinner);
        et_title = findViewById(R.id.share_et_title);
        et_content = findViewById(R.id.share_et_content);
        share_send.setOnClickListener(this::onClick);
        share_back.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_send: {
                title = et_title.getText().toString();
                content = et_content.getText().toString();
                privacy = spinner.getSelectedItem().toString();
//                获取用户id
                SharedPreferences mSharedPreferences = this.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                final String u_id = mSharedPreferences.getString("userid", "");
//                插入数据
                insData(u_id,title,content,privacy);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //do something
                        startActivity(new Intent(ShareActivity.this, MainActivity.class));
                        // 销毁当前活动
                        finish();
                    }
                    //延时1s执行
                }, 1000);

                break;
            }
            case R.id.share_back:{
                // 销毁当前活动
                finish();
                break;
            }
            default:
            }
    }

    /**
     * 绘制状态栏透明
     * @param activity   activity
     * @param statusColor   statusColor
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(statusColor);
        //设置系统状态栏处于可见状态
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        //让view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }
    //获取用户id
    private void getId(){
        Map<String, Object> params = new HashMap<>(16);
        //        获取保存的用户名和密码
        SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        String userId=sharedPreferences.getString("username","");
        params.put("username", userId);
        OkHttp.post(GETID, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                //Map<String, Object> dummyMap =  JSON.parseObject(ssss, LinkedHashMap.class, Feature.OrderedField);//关键的地方，转化为有序map
                if (!"".equals(response)) {
                    Map maps = (Map) JSON.parse(response);
//                    获取用户名
                    Object getname = maps.get("id");  //获取指定键所映射的值
                    SharedPreferences sharedPreferences= getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    ToastUtilPuls.showShort(getApplicationContext(),"获取成功！");
//                    登录成功后传递账户名和密码到下一个页面
                //    System.out.println((String) getname+"==========用户id");
                    editor.putString("userid", String.valueOf((Integer)getname));
                    editor.putInt("d", (Integer)getname);
                    editor.commit();
                }
                return response;
            }
            @Override
            public void onFailure(String error) {
                ToastUtilPuls.showShort(getApplicationContext(),"服务器错误！");
            }
        });

    }
    //插入动态数据
    private void insData(String u_id,String title,String content,String privacy){
        Map<String, Object> params = new HashMap<>();
//          * @param id  用户id
//     * @param title  标题
//     * @param content   内容
//     * @param privacy   隐私权限
        params.put("id", u_id);
        params.put("title", title);
        params.put("content", content);
        params.put("privacy", privacy);
        OkHttp.post(INSDADT, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                if (OK.equals(response)) {
                    ToastUtilPuls.showShort(getApplicationContext(),"发表成功！");
                } else if(NO.equals(response)){
                    ToastUtilPuls.showShort(getApplicationContext(),"发表失败！");
                }
                return null;
            }
            @Override
            public void onFailure(String error) {
                ToastUtilPuls.showShort(getApplicationContext(),"服务器错误！");
            }
        });

    }



}