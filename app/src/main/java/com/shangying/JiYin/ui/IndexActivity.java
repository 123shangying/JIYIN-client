package com.shangying.JiYin.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shangying.JiYin.MyApplication;
import com.shangying.JiYin.R;
import com.shangying.JiYin.Utils.Choice;
import com.shangying.JiYin.Utils.MD5;
import com.shangying.JiYin.Utils.OkCallback;
import com.shangying.JiYin.Utils.OkHttp;
import com.shangying.JiYin.Utils.ToastUtilPuls;

import java.util.HashMap;
import java.util.Map;

public class IndexActivity extends AppCompatActivity {
    /**
     * 修改密码
     */
    private final static String UPDATEPWD = MyApplication.S_IP + "user/updatepwd";
    /**
     * 定义返回值   成功  1   （拒绝魔法值）
     */
    private final static String  OK="1";
    /**
     * 定义返回值  失败   0    （拒绝魔法值）
     */
    private final static String NO="0";
    private Button pwd_but;
    private EditText passwordold, passwordnew,passwordnew_ok;
    MD5 md5=new MD5();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        pwd_but=findViewById(R.id.pwd_but);
        passwordold=findViewById(R.id.passwordold);
        passwordnew=findViewById(R.id.passwordnew);
        passwordnew_ok=findViewById(R.id.passwordnew_ok);

        //验证密码
        findViewById(R.id.pwd_but).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Choice.nNull(passwordold)&& Choice.nNull(passwordnew)&& Choice.nNull(passwordnew_ok)) {
                   if(Choice.identical(passwordnew,passwordnew_ok)){
                       //        获取保存的用户名和密码
                       SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
                       String userId=sharedPreferences.getString("username","");
                       String oldmd5pwd= md5.MD5(passwordold.getText().toString());
                       String nwemd5pwd= md5.MD5(passwordnew_ok.getText().toString());
                       updateUserpassword(userId,oldmd5pwd,nwemd5pwd);
                   }else {
                       ToastUtilPuls.showShort(getApplicationContext(), "新旧密码必须相同！");
                   }
                } else {
                    ToastUtilPuls.showShort(getApplicationContext(), "输入不能为空！");
                }
            }
        });
    }

    /**
     *  更新密码
     * @param username      用户名
     * @param oldpassword   旧密码
     * @param newpassword   新密码
     */
    private void updateUserpassword(String username,String oldpassword ,String newpassword ) {
        Map<String, Object> params = new HashMap<>(16);
        params.put("username", username);
        params.put("oldpassword", oldpassword);
        params.put("newpassword", newpassword);
        OkHttp.post(UPDATEPWD, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                if (OK.equals(response)) {
                    Toast.makeText(getApplicationContext(), "密码修改成功，请用新密码登录！", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(IndexActivity.this, LoginActivity.class);
                    startActivity(intent);
//                                关闭页面
                    finish();
                } else if (NO.equals(response)) {
                    Toast.makeText(getApplicationContext(), "密码修改失败,请重试！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "服务器维护中！！！", Toast.LENGTH_SHORT).show();
                }
                return response;
            }
            @Override
            public void onFailure(String error) {
                ToastUtilPuls.showLong(getApplicationContext(), "服务器连接失败！请检查设备网络！！");
            }
        });
    }

}