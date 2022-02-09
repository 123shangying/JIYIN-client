package com.shangying.JiYin.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.shangying.JiYin.MyApplication;
import com.shangying.JiYin.R;
import com.shangying.JiYin.Utils.Choice;
import com.shangying.JiYin.Utils.MD5;
import com.shangying.JiYin.Utils.OkCallback;
import com.shangying.JiYin.Utils.OkHttp;
import com.shangying.JiYin.Utils.ToastUtilPuls;

import java.util.HashMap;
import java.util.Map;
/**
 * Created with IntelliJ IDEA.
 * User: shangying.
 * Email: shangying611@gmail.com
 * Blog:  https://shangying.host/
 * Date: 2021/08/28.
 * Time: 17:02.
 * Explain:登录业务层
 */
public class LoginActivity extends Activity {
    /**
     * 登录接口
     */
    private final static String LOGIN = MyApplication.S_IP+"user/login";
    /**
     * 定义返回值   成功  1   （拒绝魔法值）
     */
    private final static String  OK="1";
    /**
     * 定义返回值  失败   0    （拒绝魔法值）
     */
    private final static String NO="0";
    ImageView imageView;
    TextView textView;
    int count = 0;
    private EditText username;
    private EditText password;
    MD5 md5=new MD5();
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        获取SharedPreferences保存内容
        SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
//      初始化
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //        数据绑定
        setContentView(R.layout.activity_login);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        username = findViewById(R.id.loginusername);
        password = findViewById(R.id.loginpassword);
//        界面滑动动画
        imageView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {

            @Override
            public void onSwipeTop() {
            }
//右滑动
            @Override
            public void onSwipeRight() {
                if (count == 0) {
                    imageView.setImageResource(R.drawable.good_night_img);
                    textView.setText("Night");
                    count = 1;
                } else {
                    imageView.setImageResource(R.drawable.good_morning_img);
                    textView.setText("Morning");
                    count = 0;
                }
            }
//左滑动
            @Override
            public void onSwipeLeft() {
                if (count == 0) {
                    imageView.setImageResource(R.drawable.good_night_img);
                    textView.setText("Night");
                    count = 1;
                } else {
                    imageView.setImageResource(R.drawable.good_morning_img);
                    textView.setText("Morning");
                    count = 0;
                }
            }
            @Override
            public void onSwipeBottom() {
            }

        });
        //点击跳转注册界面
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, RegstActivity.class); startActivity(intent);
            }
        });
        //点击跳转查找密码界面
        findViewById(R.id.findwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, FindswActivity.class); startActivity(intent);
            }
        });
        //点击跳转到邮箱登录界面
        findViewById(R.id.email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, EmailLoginActivity.class); startActivity(intent);
            }
        });
//登录点击事件
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                判断长度
                if(Choice.len(username)&&Choice.len(password)){
//                            判断非空
                    if(Choice.nNull(username)&&Choice.nNull(password)){
                        //调用登录方法
                       login();

                    }else {
                        ToastUtilPuls.showShort(getApplicationContext(),"账号或密码不能为空！");
                    }

                }else {
                    ToastUtilPuls.showShort(getApplicationContext(),"账号或密码不能少于8位大于16位！");
                }
            }
        });
    }
    //登录方法
    private void login(){
        Map<String, Object> params = new HashMap<>();
//        设置传递内容
        params.put("username", username.getText().toString());
        //                    password通过MD5加密传入接口

        String md5pwd= md5.MD5(password.getText().toString());
        params.put("password", md5pwd);
        //System.out.println(MD5.MD5(password.getText().toString()));
//        传递
        OkHttp.post(LOGIN, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
//                判断返回结果
                if(OK.equals(response)){
//                    这里使用context()会报空数据异常，所以用getApplicationContext()获取
                    SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    ToastUtilPuls.showShort(getApplicationContext(),"登录成功！");
//                    登录成功后传递账户名和密码到下一个页面
                    editor.putString("username",username.getText().toString());
                    editor.commit();
//                    跳转到主页
                    Intent intent=new Intent(LoginActivity.this, MainActivity.class); startActivity(intent);
                    finish();
                }else if(NO.equals(response)){
                    ToastUtilPuls.showLong(getApplicationContext(),"账号或密码错误！");
                }else {
                    ToastUtilPuls.showShort(getApplicationContext(),"服务器维护中！");
                }

                return response;
            }
            @Override
            public void onFailure(String error) {
                ToastUtilPuls.showShort(getApplicationContext(),"数据异常，请联系开发者:shangying611@Gmail.com！！！");

            }
        });
    }
}
