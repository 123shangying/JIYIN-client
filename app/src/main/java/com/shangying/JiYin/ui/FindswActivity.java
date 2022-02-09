package com.shangying.JiYin.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.shangying.JiYin.Utils.MD5;
import com.shangying.JiYin.Utils.OkCallback;
import com.shangying.JiYin.Utils.OkHttp;
import com.shangying.JiYin.Utils.ToastUtilPuls;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FindswActivity extends Activity {
    //    获取验证码
    private final static String EMAILURL = MyApplication.EMAIL_IP;
    //    验证验证码
    private final static String SETEMAIL = MyApplication.S_IP+"email/setemail";
    //    获取查找用户名
    private final static String FNAME = MyApplication.S_IP+"user/fname";
    /**
     * 修改密码
     */
    private final static String FINDPWD = MyApplication.S_IP + "user/findpwd";
    /**
     * 定义返回值   成功  1   （拒绝魔法值）
     */
    private final static String  OK="1";
    /**
     * 定义返回值  失败   0    （拒绝魔法值）
     */
    private final static String NO="0";

    MD5 md5=new MD5();
    private Button findlogin,passwordok_but;
    private Button findemailcode;
    private EditText findswemail, findcodeemail,passwordone,passwordok,usernull;
    private FindswActivity.TimeCount time;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        setContentView(R.layout.activity_findsw);
        context = getApplicationContext();
        time = new TimeCount(60000, 1000);
        findlogin = findViewById(R.id.findlogin);
        findemailcode = findViewById(R.id.findemailcode);
        findswemail = findViewById(R.id.findswemail);
        findcodeemail = findViewById(R.id.findcodeemail);
        passwordok_but=findViewById(R.id.passwordok_but);
        passwordone=findViewById(R.id.passwordone);
        passwordok=findViewById(R.id.passwordok);
        usernull=findViewById(R.id.usernull);

        //获取验证码
        findViewById(R.id.findemailcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Choice.isEmail(findswemail)) {
                    Sss();//调用获取验证码
                    time.start();//按钮倒计时
                } else {
                    ToastUtilPuls.showShort(context, "邮箱格式错误！");
                }
            }
        });
        //获取用户名
        findViewById(R.id.passwordok_but).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                判断非空
                if (Choice.nNull(passwordok)&&Choice.nNull(passwordone)) {
                    if (Choice.len(passwordok)&&Choice.len(passwordone)) {
                        if(Choice.identical(passwordok,passwordone)) {
                            //        获取保存的用户名和密码
                            SharedPreferences sharedPreferences = getSharedPreferences("datas", Context.MODE_PRIVATE);
                            String userId = sharedPreferences.getString("username", "");
                            updateUserpassword(userId);
                        }else {
                            ToastUtilPuls.showShort(context, "两次输入必须相等！！");
                        }
                    }else {
                        ToastUtilPuls.showShort(context, "密码长度最短8 位！！");
                    }
                } else {
                    ToastUtilPuls.showShort(context, "密码不能为空！");
                }
            }
        });

        //验证验证码
        findViewById(R.id.findlogin).setOnClickListener(new View.OnClickListener() {
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
            findemailcode.setBackgroundColor(Color.parseColor("#B6B6D8"));
            findemailcode.setClickable(false);
            findemailcode.setText("(" + millisUntilFinished / 1000 + ") 秒后可重新发送");
        }

        @Override
        public void onFinish() {
            findemailcode.setText("重新获取验证码");
            findemailcode.setClickable(true);
            findemailcode.setBackgroundColor(Color.parseColor("#4EB84A"));

        }
    }

    //获取验证码
    private void Sss() {
        Map<String, Object> params = new HashMap<>();
        params.put("emailAddress", findswemail.getText().toString());
        OkHttp.post(EMAILURL, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                Log.e("成功返回", response);
                System.out.println("返回：" + response);
                if (OK.equals(response)) {
                    ToastUtilPuls.showShort(context, "验证码获取成功！请注意查看邮箱！");
                } else if (NO.equals(response)) {
                    ToastUtilPuls.showShort(context, "验证码获取失败！请重新尝试！");
                } else {
                    ToastUtilPuls.showShort(context, "服务器维护中！！！");
                }

                return response;
            }

            @Override
            public void onFailure(String error) {
                Log.e("错误返回", error);
                ToastUtilPuls.showLong(context, "服务器连接失败！请检查设备网络！！");
                // Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

            }
        });
    }

    //                验证验证码
    private void Butren() {
        Map<String, Object> params = new HashMap<>();
        params.put("qq", findswemail.getText().toString());
        params.put("code", findcodeemail.getText().toString());
        OkHttp.post(SETEMAIL, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                Log.e("成功返回", response);
                System.out.println("返回：" + response);
                if (OK.equals(response)) {
                    usernull.setVisibility(View.GONE);
                    findlogin.setVisibility(View.GONE);
                    findemailcode.setVisibility(View.GONE);
                    findswemail.setVisibility(View.GONE);
                    findcodeemail.setVisibility(View.GONE);
                    passwordok_but.setVisibility(View.VISIBLE);
                    passwordone.setVisibility(View.VISIBLE);
                    passwordok.setVisibility(View.VISIBLE);
                    Fname();
//                    Intent intent=new Intent(EmailLoginActivity.this, MainActivity.class);
//                    startActivity(intent);
////                                关闭页面
//                    finish();
                } else if (NO.equals(response)) {
                    Toast.makeText(context, "验证码验证失败！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "服务器维护中！！！", Toast.LENGTH_SHORT).show();
                }
                return response;
            }
            @Override
            public void onFailure(String error) {
                Log.e("错误返回", error);
                ToastUtilPuls.showLong(context, "服务器连接失败！请检查设备网络！！");
                // Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

            }
        });
    }
    //                获取用户名
    private void Fname() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", findswemail.getText().toString());
        OkHttp.post(FNAME, params, new OkCallback() {

            @Override
            public String onResponse(String response) {
               //System.out.print(response + "看一bbbbb下");
                //View.INVISIBLE
                Map<String, Object> dummyMap = JSON.parseObject(response, LinkedHashMap.class, Feature.OrderedField);//关键的地方，转化为有序map
                if (!"".equals(response)) {
                    Map maps = (Map) JSON.parse(response);
                    System.out.println("这个是用JSON类来解析JSON字符串!!!");
                    for (Object map : maps.entrySet()) {
                        System.out.println(((Map.Entry) map).getKey() + "   |  " + ((Map.Entry) map).getValue());
                    String uname= (String) ((Map.Entry) map).getValue();
                     //                    这里使用context()会报空数据异常，所以用getApplicationContext()获取
                    SharedPreferences sharedPreferences= getSharedPreferences("datas", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //ToastUtilPuls.showShort(getApplicationContext(),"登录成功！");
//                    登录成功后传递账户名和密码到下一个页面
                    editor.putString("username",uname);
                    editor.commit();
                        ToastUtilPuls.showLong(context, "请更新密码！！");
                    }
                } else {
                    ToastUtilPuls.showLong(context, "获取数据失败,请重试！！");
                }
                return response;
            }
            @Override
            public void onFailure(String error) {
                Log.e("错误返回", error);
                ToastUtilPuls.showLong(context, "服务器连接失败！请检查设备网络！！");
            }
        });
    }
    //                更新密码
    private void updateUserpassword(String username) {
        Map<String, Object> params = new HashMap<>(16);
        params.put("qq", findswemail.getText().toString());
        params.put("username", username);
        String md5pwd= md5.MD5(passwordok.getText().toString());
        params.put("newpassword", md5pwd);
        OkHttp.post(FINDPWD, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                Log.e("成功返回", response);
                System.out.println("返回：" + response);
                if (OK.equals(response)) {
                    Toast.makeText(context, "密码修改成功，请用新密码登录！", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(FindswActivity.this, LoginActivity.class);
                    startActivity(intent);
//                                关闭页面
                    finish();
                } else if (NO.equals(response)) {
                    Toast.makeText(context, "密码修改失败,请重试！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "服务器维护中！！！", Toast.LENGTH_SHORT).show();
                }
                return response;
            }
            @Override
            public void onFailure(String error) {
                Log.e("错误返回", error);
                ToastUtilPuls.showLong(context, "服务器连接失败！请检查设备网络！！");
            }
        });
    }


}