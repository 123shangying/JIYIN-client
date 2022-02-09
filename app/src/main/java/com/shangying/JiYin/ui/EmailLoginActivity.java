package com.shangying.JiYin.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.shangying.JiYin.MyApplication;
import com.shangying.JiYin.R;
import com.shangying.JiYin.Utils.Choice;
import com.shangying.JiYin.Utils.Log;
import com.shangying.JiYin.Utils.OkCallback;
import com.shangying.JiYin.Utils.OkHttp;
import com.shangying.JiYin.Utils.ToastUtilPuls;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class EmailLoginActivity<fname> extends Activity {
    //    获取验证码
    private final static String EMAILURL = MyApplication.EMAIL_IP;
    //    验证验证码
    private final static String SETEMAIL = MyApplication.S_IP+"email/setemail";
//    获取查找用户名
    private final static String FNAME = MyApplication.S_IP+"user/fname";
    /**
     * 定义返回值   成功  1   （拒绝魔法值）
     */
    private final static String  OK="1";
    /**
     * 定义返回值  失败   0    （拒绝魔法值）
     */
    private final static String NO="0";
    private Button btnGetcode;
    private Button loginemail;
    private EditText email,emailcode;
    private EmailLoginActivity.TimeCount time;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        setContentView(R.layout.activity_email_login);
        //        数据绑定
        context = getApplicationContext();
        time = new TimeCount(60000, 1000);
        btnGetcode=findViewById(R.id.getemailcode);
        loginemail=findViewById(R.id.emaillogin);
        email=findViewById(R.id.loginemail);
        emailcode=findViewById(R.id.codeemail);

        //获取验证码
        findViewById(R.id.getemailcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Choice.isEmail( email) ) {
                    Sss();//调用获取验证码
                    time.start();//按钮倒计时
                }else {
                    ToastUtilPuls.showShort(context,"邮箱格式错误！");
                }
            }
        });
        //验证验证码
        findViewById(R.id.emaillogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Butren();
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
            btnGetcode.setBackgroundColor(Color.parseColor("#B6B6D8"));
            btnGetcode.setClickable(false);
            btnGetcode.setText("("+millisUntilFinished / 1000 +") 秒后可重新发送");
        }

        @Override
        public void onFinish() {
            btnGetcode.setText("重新获取验证码");
            btnGetcode.setClickable(true);
            btnGetcode.setBackgroundColor(Color.parseColor("#4EB84A"));

        }
    }
    //获取验证码
    private void Sss(){
        Map<String, Object> params = new HashMap<>();
        params.put("emailAddress", email.getText().toString());
        OkHttp.post(EMAILURL, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                Log.e("成功返回", response);
                System.out.println("返回："+response);
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
                Log.e("错误返回", error);
                ToastUtilPuls.showLong(context,"服务器连接失败！请检查设备网络！！");
                // Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

            }
        });
    }
    //                验证验证码
    private void Butren() {
        Map<String, Object> params = new HashMap<>();
        params.put("qq",email.getText().toString());
        params.put("code", emailcode.getText().toString());
        OkHttp.post(SETEMAIL, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                Log.e("成功返回", response);
                System.out.println("返回："+response);
                if(OK.equals(response)){
                    Fname();
//                    Intent intent=new Intent(EmailLoginActivity.this, MainActivity.class);
//                    startActivity(intent);
////                                关闭页面
//                    finish();
                }else if(NO.equals(response)){
                    Toast.makeText(context, "验证码验证失败！", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "服务器维护中！！！", Toast.LENGTH_SHORT).show();
                }

                return response;
            }
            @Override
            public void onFailure(String error) {
                Log.e("错误返回", error);
                ToastUtilPuls.showLong(context,"服务器连接失败！请检查设备网络！！");
                // Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

            }
        });
    }
    //                获取用户名
    private void Fname() {
        Map<String, Object> params = new HashMap<>();
        params.put("name",email.getText().toString());
        OkHttp.post(FNAME, params, new OkCallback() {

            @Override
            public String onResponse(String response) {
                Map<String, Object> dummyMap =  JSON.parseObject(response, LinkedHashMap.class, Feature.OrderedField);//关键的地方，转化为有序map
                if (!"".equals(response)) {
                    Map maps = (Map) JSON.parse(response);
                    System.out.println("这个是用JSON类来解析JSON字符串!!!");
                    for (Object map : maps.entrySet()) {
                        System.out.println(((Map.Entry) map).getKey() + "   |  " + ((Map.Entry) map).getValue());
                        //步骤1：创建一个SharedPreferences对象

                        SharedPreferences sharedPreferences= getSharedPreferences("data",Context.MODE_PRIVATE);
                        //步骤2： 实例化SharedPreferences.Editor对象
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        //步骤3：将获取过来的值放入文件
                        editor.putString("username", (String) ((Map.Entry) map).getValue());
                        //步骤4：提交
                        editor.commit();
                        ToastUtilPuls.showShort(getApplicationContext(),"登录成功！");
                        Intent intent = new Intent(EmailLoginActivity.this, MainActivity.class);
                        startActivity(intent);
//                                关闭页面
                        finish();
                    }
                    //其他操作


                } else {
                    ToastUtilPuls.showLong(context, "未知错误！！");
                }

                return response;
            }
            @Override
            public void onFailure(String error) {
                Log.e("错误返回", error);
                ToastUtilPuls.showLong(context,"服务器连接失败！请检查设备网络！！");
                // Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

            }
        });
    }
}