package com.shangying.JiYin.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.shangying.JiYin.MyApplication;
import com.shangying.JiYin.R;
import com.shangying.JiYin.Utils.OkCallback;
import com.shangying.JiYin.Utils.OkHttp;
import com.shangying.JiYin.Utils.ToastUtilPuls;
import com.shangying.JiYin.function.DelectActivity;
import com.shangying.JiYin.function.Upimages;
import com.shangying.JiYin.function.Webview;
import com.shangying.JiYin.function.Webviewxy;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shangying
 */
public class Showdata extends AppCompatActivity {
    /**
     * 查看用户信息接口
     */
    private final static String SHOWATA = MyApplication.S_IP+"user/show";
    /**
     * 查看用户头像
     */
    private final static String ICON = MyApplication.S_IP+"icon/showicon";
    /**
     * 存放用户id
     *
     */
    public static ArrayList<String> IMAGE= new ArrayList<String>();

public TextView showname,showqq,showtime,showstate,ysxy;
public Button upimages,delectuser;
public ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdata);
        showname = findViewById(R.id.showname);
         showqq = findViewById(R.id.showqq);
         showtime = findViewById(R.id.showtime);
        showstate = findViewById(R.id.showstate);
        ysxy=findViewById(R.id.ysxy1);
        upimages=findViewById(R.id.upimage);
        delectuser=findViewById(R.id.delectuser);
        // Android 4.0 之后不能在主线程中请求HTTP请求
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        showData();
        //得到可用的图片
        if(IMAGE.size()==0){

        }else {
            String url = "https://tu.shangying.xyz/imgs/2021/10/1%20("+IMAGE.get(0)+").png";
            Bitmap bitmap = getHttpBitmap(url);


            imageView = (ImageView)this.findViewById(R.id.imageuser);
            //显示
            imageView.setImageBitmap(bitmap);


        }



        findViewById(R.id.ysxy1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Showdata.this, Webview.class); startActivity(intent);
//                activity生命周期相关的--调转后关闭前一个activiy
                //finish();
            }
        });
        findViewById(R.id.upimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Showdata.this, Upimages.class); startActivity(intent);
//                activity生命周期相关的--调转后关闭前一个activiy
                //finish();
            }
        });
        findViewById(R.id.delectuser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Showdata.this, DelectActivity.class); startActivity(intent);
//                activity生命周期相关的--调转后关闭前一个activiy
                //finish();
            }
        });
        findViewById(R.id.yhxy1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Showdata.this, Webviewxy.class); startActivity(intent);
//                activity生命周期相关的--调转后关闭前一个activiy
                //finish();
            }
        });
    }

    /**
     * 获取用户信息
     */
    public  void showData() {
        //        获取保存的用户名和密码
        SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        String userId=sharedPreferences.getString("username","");
        Map<String, Object> params = new HashMap<>(16);
        params.put("username",userId);
        OkHttp.post(SHOWATA, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
//                String sss=response.substring(0,response.length() - 1);
//                String ssss=sss.substring(1);
                //Map<String, Object> dummyMap =  JSON.parseObject(ssss, LinkedHashMap.class, Feature.OrderedField);//关键的地方，转化为有序map
                if (!"".equals(response)) {
                    Map maps = (Map) JSON.parse(response);
//                    获取用户名
                    Object getname = maps.get("id");  //获取指定键所映射的值
                     //判断键值是否为String类型
                        int name = (int)getname;  //获取指定的value值
                        showname.setText("用户ID: "+name);

                    getusername(name);

                    //                    获取邮箱
                    Object getqq = maps.get("username");  //获取指定键所映射的值
                    //判断键值是否为String类型
                    String qq = (String)getqq;  //获取指定的value值
                    showqq.setText("用户名: "+qq);
                    //                    获取时间
                    Object gettime = maps.get("gmtCreate");  //获取指定键所映射的值
                    //判断键值是否为String类型
//                    转换时时间格式中间多了个大写T，这里去掉
                    String test  = ((String)gettime);
//                    断言
                    assert test != null;

                    //获取指定的value值
                    showtime.setText("注册时间: "+test.substring(0,10));

                    showstate.setText("账号状态: 正常");

                }

                return response;
            }
            @Override
            public void onFailure(String error) {
                ToastUtilPuls.showShort(getApplicationContext(), "获取失败请重试！");


            }
        });
    }
    /**
     * 获取网落图片资源
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 通过用户id获取头像
     * @param id  用户id
     */
    public static void getusername(int id){
IMAGE.clear();
        Map<String, Object> params = new HashMap<>();
        params.put("uid",id);
        OkHttp.post(ICON, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                                String sss=response.substring(0,response.length() - 1);
                String ssss=sss.substring(1);
                Map maps = (Map) JSON.parse(ssss);
                Object getname = maps.get("image");  //获取指定键所映射的值
                //判断键值是否为String类型
                String name = (String) getname;  //获取指定的value值

                IMAGE.add(name);

                return null;
            }
            @Override
            public void onFailure(String error) {

            }
        });


    }

}