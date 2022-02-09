package com.shangying.JiYin.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shangying.JiYin.MyApplication;
import com.shangying.JiYin.R;
import com.shangying.JiYin.Utils.Choice;
import com.shangying.JiYin.Utils.Log;
import com.shangying.JiYin.Utils.MD5;
import com.shangying.JiYin.Utils.OkCallback;
import com.shangying.JiYin.Utils.OkHttp;
import com.shangying.JiYin.Utils.ToastUtilPuls;
import com.shangying.JiYin.function.Webview;
import com.shangying.JiYin.function.Webviewxy;

import java.util.HashMap;
import java.util.Map;
/**
 * Created with IntelliJ IDEA.
 * Email: shangying611@gmail.com
 * Blog:  https://shangying.host/
 * Date: 2021/09/28.
 * Time: 17:02.
 * Explain:注册业务层
 * @author shangying
 */
public class RegstActivity extends Activity {
    /**
     * 注册接口
     */
    private final static String REGISTER = MyApplication.S_IP+"user/register";
    /**
     * 获取验证码
     */
    private final static String EMAIL = MyApplication.EMAIL_IP;
    /**
     * 验证验证码
     */
    private final static String SETEMAIL = MyApplication.S_IP+"email/setemail";
    /**
     * 定义返回值   成功  1   （拒绝魔法值）
     */
    private final static String  OK="1";
    /**
     * 定义返回值  失败   0    （拒绝魔法值）
     */
    private final static String NO="0";
    MD5 md5=new MD5();
    private Context context;
    private TimeCount time;
    private Button btnGetCode;
    private EditText username;
    private EditText password;
    private EditText passwordend;
    private EditText emails;
    private EditText etemail;
    private TextView mRadioButton_1,mRadioButton_2;
    RadioGroup mRadioGroup;
//    初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        setContentView(R.layout.activity_regst);
//        数据绑定
        context = getApplicationContext();
        time = new TimeCount(60000, 1000);
        btnGetCode=findViewById(R.id.getemail);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        passwordend = findViewById(R.id.passwordend);
        emails = findViewById(R.id.email);
        etemail = findViewById(R.id.setemail);
        mRadioButton_1 =findViewById(R.id.yhxy);
        mRadioButton_2 =findViewById(R.id.ysxy);
//        验证验证码和注册(验证通过才注册)
        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Choice.isEmail(emails) ) {
                    if(Choice.nNull(username)&&Choice.nNull(password)&&Choice.nNull(password)&&Choice.nNull(etemail)){
                        //                调用验证验证码和注册
//                        判断长度
                        if(Choice.len(username)&&Choice.len(password)&&Choice.len(passwordend)){
//                            判断相同
                            if(Choice.identical(password,passwordend)){
                                Butren();
                            }else {
                                ToastUtilPuls.showShort(context,"两次密码输入不一致！");
                            }
                        }else {
                            ToastUtilPuls.showShort(context,"用户名和密码长度必须大于8位小于16位！");
                        }

                    }else {
                        ToastUtilPuls.showShort(context,"输入不能为空！");
                    }
                }else {
                    ToastUtilPuls.showShort(context,"邮箱格式错误！");
            }
            }
        });
        //获取验证码
        findViewById(R.id.getemail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Choice.isEmail(emails) ) {
                    Sss();//调用获取验证码
                    time.start();//按钮倒计时
                }else {
                    ToastUtilPuls.showShort(context,"邮箱格式错误！");
                }
            }
        });
        //协议
        findViewById(R.id.ysxy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegstActivity.this, Webview.class); startActivity(intent);
            }
        });
        //协议
        findViewById(R.id.yhxy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegstActivity.this, Webviewxy.class); startActivity(intent);
            }
        });
    }
    //倒计时按钮
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnGetCode.setBackgroundColor(Color.parseColor("#B6B6D8"));
            btnGetCode.setClickable(false);
            btnGetCode.setText("("+millisUntilFinished / 1000 +") 秒后可重新发送");
        }
        @Override
        public void onFinish() {
            btnGetCode.setText("重新获取验证码");
            btnGetCode.setClickable(true);
            btnGetCode.setBackgroundColor(Color.parseColor("#4EB84A"));
        }
    }

    //                验证验证码
    private void Butren() {
        Map<String, Object> params = new HashMap<>();
        params.put("qq",emails.getText().toString());
        params.put("code", etemail.getText().toString());
        OkHttp.post(SETEMAIL, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                if(OK.equals(response)){
//                            验证码验证成功，走注册流程
                    Map<String, Object> params = new HashMap<>();
                    params.put("username",username.getText().toString());
//                    password通过MD5加密传入接口
                    String md5pwd= md5.MD5(password.getText().toString());
                    params.put("password", md5pwd);
                    params.put("qq", emails.getText().toString());
                    OkHttp.post(REGISTER, params, new OkCallback() {
                        @Override
                        public String onResponse(String response) {
                            Log.e("成功返回", response);
                            System.out.println("返回："+response);
                            if(OK.equals(response)){
                                ToastUtilPuls.showShort(context,"注册成功,正在跳转登录！！！");

                                Intent intent=new Intent(RegstActivity.this, LoginActivity.class);
                                startActivity(intent);
//                                关闭页面
                                finish();
                            }else if(NO.equals(response)){
                                ToastUtilPuls.showLong(context,"用户名已被注册！或者该邮箱已注册,您可直接用邮箱登录！");
                            }else  {
                                ToastUtilPuls.showShort(context,"系统维护中！");
                            }

                            return response;
                        }
                        @Override
                        public void onFailure(String error) {
                            Log.e("错误返回", error);
                            ToastUtilPuls.showShort(context,"服务器连接失败！请检查设备网络！！");

                        }
                    });
                }else if(NO.equals(response)){
                    ToastUtilPuls.showLong(context,"验证码验证失败！");

                }else {
                    ToastUtilPuls.showShort(context,"服务器维护中！！！");
                }

                return response;
            }
            @Override
            public void onFailure(String error) {
                ToastUtilPuls.showShort(context,"服务器连接失败！请检查设备网络！！");

            }
        });
    }
    //验证验证码
    private void Sss(){
        Map<String, Object> params = new HashMap<>();
        params.put("emailAddress", emails.getText().toString());
        OkHttp.post(EMAIL, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                if(OK.equals(response)){
                    ToastUtilPuls.showShort(context,"验证码获取成功！请注意查看邮箱！");
                }else if(NO.equals(response)){
                    ToastUtilPuls.showShort(context,"验证码获取失败！请重新尝试！");
                }else {
                    ToastUtilPuls.showShort(context,"服务器维护中！！！");
                }

                return response;
            }
            @Override
            public void onFailure(String error) {
                ToastUtilPuls.showLong(context,"服务器连接失败！请检查设备网络！！");

            }
        });
    }
}
