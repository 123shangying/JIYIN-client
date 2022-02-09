package com.shangying.JiYin.ui.maps.Run;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.*;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.particle.ParticleOverlay;
import com.amap.api.maps.model.particle.ParticleOverlayOptions;
import com.amap.api.maps.model.particle.ParticleOverlayOptionsFactory;
import com.shangying.JiYin.MyApplication;
import com.shangying.JiYin.R;
import com.shangying.JiYin.Service.MyService;
import com.shangying.JiYin.Utils.OkCallback;
import com.shangying.JiYin.Utils.OkHttp;
import com.shangying.JiYin.Utils.ToastUtilPuls;
import com.shangying.JiYin.Utils.WeatherType;
import com.shangying.JiYin.ui.fragment.dashboard.MyHelper;
import com.shangying.JiYin.ui.maps.MyPath;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 创建日期：2021/6/16 14:37
 * @author shangying
 * 文件名称： RunActivity.java
 * 类说明： 跑步界面的 Activity，
 *      （1） 网络功能：调用高德地图API获得天气信息
 *      （2） 服务Service：启动一个音乐播放的服务
 *      （3） 展示通知
 */
public class RunActivity extends AppCompatActivity implements LocationSource, AMapLocationListener, View.OnClickListener {

    //是否需要检测后台定位权限，设置为true时，如果用户没有给予后台定位权限会弹窗提示
    private boolean needCheckBackLocation = false;
    //如果设置了target > 28，需要增加这个权限，否则不会弹出"始终允许"这个选择框
    private static String BACK_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    /**
     * 修改运动数据
     */
    private final static String INSPATH = MyApplication.S_IP + "mypath/inspath";
    /**
     *  获取用户id
     */
    private final static String GETID = MyApplication.S_IP+"user/getid";
    /**
     * 定义返回值   成功  1   （拒绝魔法值）
     */
    private final static String  OK="1";
    /**
     * 定义返回值  失败   0    （拒绝魔法值）
     */
    private final static String NO="0";
    TextView tv_time;

    // 跑步的开始时间和结束时间
    Date startTime, endTime;
    // 点击“继续运行” 按钮的时间，需要记录这个时间，用来计算整体的运动时长，同时在开始运动是要进行初始化
    Date resumeTime;
    Long continueTime;      // 整个跑步过程的持续时间


    private AMap aMap;
    private MapView mapView;
    // 地图的ui交换的对象（处理各种手势）
    private UiSettings mUiSettings;

    // 存放定位过程的，因为存在暂停，因此先将点存在 tempPointsList 里面，点击完成之后再寄到 pointsList里面
    ArrayList<LatLng> tempPointsList;
    ArrayList<ArrayList<LatLng>> pointsList;

    // 存储所有点的经度和纬度的信息， LatLng 没有实现 Serializable 接口，不能存入数据库中，所以需要存储对应的经度和纬度信息
    HashMap<String, ArrayList<Double>> pointMap;

    public int count = 0;

    // 存放运动路径的类，到时候直接将这个类存入数据库中
    MyPath myPath;

    private Marker mOriginStartMarker, mOriginEndMarker;
    private PolylineOptions polylineOptions;


    // 定位发生的改变的 Listener
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    // 控件按钮
    Button btn_finish, btn_pause, btn_continue;
    ImageView run_iv_music;
    // 动画
    private ObjectAnimator animator;
    boolean musicIsPlay;
    Intent intentMusic;

    private long baseTimer;

    // 当前是否属于暂停状态，暂停状态不计算路程，但是定位照常
    boolean isPause = false;

    // 数据库操作
    SQLiteDatabase db;
    MyHelper dbHelper;
    ContentValues values;


    // 粒子效果
    List<ParticleOverlayOptions> optionsList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getId();
        // 权限部分
        if (Build.VERSION.SDK_INT > 28
                && getApplicationContext().getApplicationInfo().targetSdkVersion > 28) {
            needPermissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                    BACK_LOCATION_PERMISSION
            };
            needCheckBackLocation = true;
        }

        setContentView(R.layout.activity_run);

        dbHelper = new MyHelper(RunActivity.this);

        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();

        // 调用高德 API 接口，获取天气信息，同时设置粒子效果
        sendRequestWithHttpURLConnection();

        // 展示一个通知
        showNotification();

        // 播放音乐
        playMusicService();

        // 设置计时器（计时器暂停问题还待解决）
        final Handler startTimehandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (null != tv_time) {
                    tv_time.setText((String) msg.obj);
                }
            }
        };
        RunActivity.this.baseTimer = SystemClock.elapsedRealtime();
        new Timer("开机计时器").scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int time = (int) ((SystemClock.elapsedRealtime() - RunActivity.this.baseTimer) / 1000);
                String hh = new DecimalFormat("00").format(time / 3600);
                String mm = new DecimalFormat("00").format(time % 3600 / 60);
                String ss = new DecimalFormat("00").format(time % 60);
                String timeFormat = new String(hh + ":" + mm + ":" + ss);
                Message msg = new Message();
                msg.obj = timeFormat;
                startTimehandler.sendMessage(msg);
            }

        }, 0, 1000L);
    }

    // 启动播放音乐的服务
    private void playMusicService() {

        musicIsPlay = true;

        intentMusic = new Intent(getApplicationContext(), MyService.class);

        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intentMusic);
        } else {
            startService(intentMusic);
        }

        // 动画播放
        animator = ObjectAnimator.ofFloat(run_iv_music, "rotation", 0f, 360.0f);
        animator.setDuration(7000);  //动画旋转一周的时间为10秒
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);  //-1表示设置动画无限循环
        animator.start();
    }

    // 显示一个通知
    private void showNotification() {
        // 展示一个通知，表明当前依旧再定位
        Intent notificationIntent = new Intent(this, RunActivity.class);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);// 关键的一步，设置启动模式，两种情况


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        String channelId = "myNotification";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder;
        Notification notification = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 8.0以上需要设置 Channel
            NotificationChannel mChannel = new NotificationChannel(channelId, "JackLinMap", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);

            builder = new Notification.Builder(this);
            builder.setChannelId(channelId);
            builder.setContentTitle("提示");
            builder.setContentText("迹印位置服务正在后台运行");
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_map));
            builder.setAutoCancel(true);        //设置点击之后自动消失
            // 设置 PendingIntent
            builder.setContentIntent(pendingIntent);

            notification = builder.build();
        } else {
            builder = new Notification.Builder(this);
            builder.setContentTitle("提示");
            builder.setContentText("迹印位置服务正在后台运行");
            builder.setSmallIcon(R.mipmap.ic_launcher);
            notification = builder.build();
        }
        notificationManager.notify(2, notification);

    }

    /**
     * 初始化
     */
    private void init() {

        //初始化一些变量
        tempPointsList = new ArrayList<>();
        pointsList = new ArrayList<>();
        // 初始化开始跑步的时间
        startTime = new Date();
        // 初始化点击 “恢复运动” 按钮的时间
        resumeTime = startTime;
        continueTime = Long.valueOf(0);
        pointMap = new HashMap<>();


        // 初始化 aMap 并设置一些属性
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            setUpMap();
        }

        tv_time = findViewById(R.id.run_map_tv_time);
        btn_finish = findViewById(R.id.run_map_finish);
        btn_pause = findViewById(R.id.run_map_pause);
        btn_continue = findViewById(R.id.run_map_continue);
        run_iv_music = findViewById(R.id.run_iv_music);

        btn_finish.setOnClickListener(this::onClick);
        btn_pause.setOnClickListener(this::onClick);
        btn_continue.setOnClickListener(this::onClick);
        run_iv_music.setOnClickListener(this::onClick);

        btn_continue.setVisibility(View.INVISIBLE);
        btn_finish.setVisibility(View.INVISIBLE);

    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {

        // 1. 设置 amap 的 ui 界面的一些属性，主要是一些ui界面上的控件是否显示
        mUiSettings.setMyLocationButtonEnabled(true);   // 设置默认定位按钮是否显示
        mUiSettings.setCompassEnabled(false);// 设置指南针是否显示
        mUiSettings.setZoomControlsEnabled(false);// 设置缩放按钮是否显示
        mUiSettings.setScaleControlsEnabled(true);// 设置比例尺是否显示
        mUiSettings.setRotateGesturesEnabled(false);// 设置地图旋转是否可用
        mUiSettings.setTiltGesturesEnabled(false);// 设置地图倾斜是否可用


        // 设置定位的方式
        MyLocationStyle myLocationStyle = new MyLocationStyle();    // 初始化定位
        myLocationStyle.interval(3500);     //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.strokeColor(Color.TRANSPARENT);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色

        //连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        // 参考从高德地图API官网下载的文档，里面有这些常量的解释
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        aMap.setMyLocationStyle(myLocationStyle);       //设置定位蓝点的Style
        aMap.setLocationSource(this);// 设置定位监听
        aMap.setMyLocationEnabled(true);    // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.showIndoorMap(true);       //true：显示室内地图；false：不显示；
        aMap.setMaxZoomLevel(20);   // 设置最大缩放几倍

        // 设置镜头的初始位置和放大倍数
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(18);
        aMap.animateCamera(cameraUpdate);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.run_map_finish: {

                // 停止音乐播放的服务
                stopService(intentMusic);

                endTime = new Date();

                // 将这次记录的点添加到总的 pointsList 里面
                pointsList.add(tempPointsList);

                double distance = 0;
                for (int i = 0; i < pointsList.size() - 1; i++) {
                    distance += getDistance(pointsList.get(i));
                }

                // 获取 LatLng 对象的经纬度信息并保存
                getPointLatitudeAndLongitude(pointsList);

                myPath = new MyPath();
                myPath.setPointMap(pointMap);
                myPath.setDistance(distance);     // 计算距离 单位：m
                myPath.setTime(continueTime);
                myPath.setStartTime(startTime.getTime());
                myPath.setEndTime(endTime.getTime());

                // 运动距离大于100米且运动时间大于10秒才开始计算速度
                if (distance > 100 && continueTime >= (10 * 1000)) {
                    // 计算时速 m/s
                    double speed1 = distance / (continueTime / 3600);

                    // 计算配速 minute/km
                    double minute = continueTime / (60 * 1000);
                    double speed2 = minute / distance * 1000;

                    myPath.setSpeed1(speed1);
                    myPath.setSpeed2(speed2);
                } else {
                    myPath.setSpeed1(0.0);
                    myPath.setSpeed2(0.0);
                }



                System.out.println("mpath");
                System.out.println(myPath);

                //
                /*
                 *   将数据通过 intetnt 传递给 RunResultActivity，有很多中方法
                 * （1）可以先将类存入数据库中，然后在 RunResultActivity 再查询数据库得到信息
                 * （2）将该类之间通过 JSON 传递出去，但是类里面包含了 LatLng 对象，没有空构造器，会报错
                 * （3）将类实现 Serializable ，然后再传递出去，但是 LatLag 同样没有实现该接口
                 * （4）将参数分开传递过去，比较麻烦
                 * 综合评估，选择先将 MyPath 类存入数据库中人，然后在 RunResultActivity 读数据库取出。
                 * */


                // 进行数据库操作
                db = dbHelper.getWritableDatabase();

                //                获取用户id
                SharedPreferences mSharedPreferences = this.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                final String u_id = mSharedPreferences.getString("userid", "");
                System.out.println("u_id = " + u_id);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String dateFormat = sdf.format(date);

                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
                    objectOutputStream.writeObject(myPath);
                    objectOutputStream.flush();
                    byte data[] = arrayOutputStream.toByteArray();
                    objectOutputStream.close();
                    arrayOutputStream.close();

                    values = new ContentValues();

                    insData(u_id,data);
                    // 这里要和数据库中字段对应
//                    values.put("u_id", u_id);
//                    values.put("path", data);
//                    values.put("c_time", dateFormat);
//
//                    // 将用户id，路径的类，创建时间存入数据库中
////                    db.execSQL("insert into mpath (u_id, path, c_time) values(?,?,?)", new Object[]{u_id, data, dateFormat});
//                    db.insert("mypath", null, values);
//                    db.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                // 只需要传递该条记录创建的时间给 RunResultActivity就可以查询出该条记录了
                Intent intent = new Intent(this, RunResultActivity.class);

                // 将 myPath 对象都传递过去给 RunResultActivity
                try {
                    intent.putExtra("myPath", JSONObject.toJSONString(myPath));
                }catch (Exception e){

                }

                startActivity(intent);

                finish();
                break;

                // 将 pointList传递过去
//                Bundle bundle = new Bundle();
//                ArrayList bundleList = new ArrayList();
//                bundleList.add(pointsList);
//                bundle.putParcelableArrayList("pointList", bundleList);
//                intent.putExtras(bundle);

//                //须定义一个list用于在budnle中传递需要传递的ArrayList<Object>,这个是必须要的
//                ArrayList bundlelist = new ArrayList();
//                Bundle bundle = new Bundle();
//
//
//                bundlelist.add(myPath);
//                bundle.putParcelableArrayList("myPathList",bundlelist);
//                intent.putExtras(bundle);
//
//                intent.putExtras(intent);
            }
            case R.id.run_map_pause: {
                if (btn_finish.getVisibility() == View.INVISIBLE) {
                    btn_finish.setVisibility(View.VISIBLE);
                } else {
                    btn_finish.setVisibility(View.INVISIBLE);

                }
                if (btn_continue.getVisibility() == View.INVISIBLE) {
                    btn_continue.setVisibility(View.VISIBLE);
                } else {
                    btn_continue.setVisibility(View.INVISIBLE);

                }

                // 设置暂停变量 isPause 的值为 true，表示当前处于暂停状态
                isPause = true;

                // 同时将 tempPointsList 里面的点加入到 pointsList里面
                pointsList.add(tempPointsList);

                // 计算这一阶段的运动时间
                continueTime += new Date().getTime() - resumeTime.getTime();

                break;
            }
            case R.id.run_map_continue: {
                // 控制按钮的显示与隐藏
                if (btn_finish.getVisibility() == View.INVISIBLE) {
                    btn_finish.setVisibility(View.VISIBLE);
                } else {
                    btn_finish.setVisibility(View.INVISIBLE);
                }
                if (btn_continue.getVisibility() == View.INVISIBLE) {
                    btn_continue.setVisibility(View.VISIBLE);
                } else {
                    btn_continue.setVisibility(View.INVISIBLE);
                }

                // 设置暂停变量 isPause 的值为 false
                isPause = false;

                // 新创建一个 tempPointsList 变量，用来存储这次的说有点
                tempPointsList = new ArrayList<>();

                // 记录这次恢复按钮的点击时刻，用来计算整体跑步的时间
                resumeTime = new Date();
                break;
            }
            case R.id.run_iv_music: {

                if (musicIsPlay == true) {
                    animator.pause();
                    musicIsPlay = false;
                    mySendBroadCast("pause");
                } else if (musicIsPlay == false){
                    animator.start();
                    musicIsPlay = true;
                    mySendBroadCast("start");

                }
            }
        }
    }

    // 发送一个广播给 MyService
    public void mySendBroadCast(String option) {

        // 设置 action 要和 MyService 里面注册广播接收器的时候一样的 action
        Intent intent = new Intent("com.linkai.broadcast.MY_MUSIC");
        intent.putExtra("option", option);
        sendBroadcast(intent);
    }

    // 将 LatLng 的 List 转换为 Map
    public void getPointLatitudeAndLongitude(ArrayList<ArrayList<LatLng>> pointsList) {
        if (pointsList.size() == 0 ){
            return;
        }

        double latitude;
        double longitude;
        ArrayList<Double> latitudeList = new ArrayList<>();
        ArrayList<Double> longitudeList = new ArrayList<>();

        for (int i = 0; i < pointsList.size() - 1; i++) {
            for (int j = 0; j < pointsList.get(i).size() - 1; j++) {
                latitude = pointsList.get(i).get(j).latitude;        // 纬度
                longitude = pointsList.get(i).get(j).longitude;      // 经度
                latitudeList.add(latitude);
                longitudeList.add(longitude);
            }
        }

        pointMap.put("latitude", latitudeList);
        pointMap.put("longitude", longitudeList);

    }


    // 绘制轨迹路线
    private void drawLines() {

        // 实例化一个 PolylineOptions 对象，表示一条线段
        polylineOptions = new PolylineOptions();
        polylineOptions.addAll(tempPointsList);      //将所有点加入到该线段的坐标集合中
        polylineOptions.geodesic(true);     // 是否话大地曲线，默认false
        polylineOptions.setDottedLine(false);       // 设置是否画虚线，默认为false，画实线。
        polylineOptions.color(Color.GREEN);        // 设置线段颜色
        polylineOptions.useGradient(true);      // 是否使用渐变色
        polylineOptions.width(10);          // 设置线段宽度
        polylineOptions.visible(true);

        // 设置线段起点和终点图标
        mOriginStartMarker = aMap.addMarker(new MarkerOptions().position(tempPointsList.get(0))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_start)));


        // 将该线段添加到 AMap 对象中
        aMap.addPolyline(polylineOptions);

        // 设置镜头跟随---》 暂时不需要
        // 获取轨迹坐标点
//        LatLngBounds.Builder b= LatLngBounds.builder();
//        for (int i = 0; i< points.size(); i++) {
//            b.include(points.get(i));
//        }
//        LatLngBounds bounds= b.build();
//
//        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 100);//
//        aMap.animateCamera(update);

    }

    /****************************************** 网络功能 （下面部分）******************************************************/

    // 请求高德地图API，获取天气信息，然后设置粒子效果
    private void sendRequestWithHttpURLConnection() {

        // 开启新线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL("https://restapi.amap.com/v3/weather/weatherInfo?city=360100&key=91d01733ad26a1ebf48d3367916cb4ff");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputStream = connection.getInputStream();

                    // 下面对获取到的输入流进行读取
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }

                    String weatherResponse = getWeatherResponse(response.toString());

//                    //解决在子线程中调用Toast的异常情况处理
//                    Looper.prepare();
//                    try {
//                        Toast.makeText(getApplicationContext(), "当前天气为：" + weatherResponse,Toast.LENGTH_SHORT).show();
//                    }catch (Exception e) {
//                    }
//                    Looper.loop();
//                    System.out.println("当前天气为：" + weatherResponse);

                    // 使用 Hander 在子线程中进行弹窗处理
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "当前天气为：" + weatherResponse,Toast.LENGTH_SHORT).show();
                        }
                    });

                    WeatherType weatherType = new WeatherType();
                    if (weatherType.getWeatherSun().contains(weatherResponse)) {
                        // Sun 晴天
                        optionsList = ParticleOverlayOptionsFactory.defaultOptions(ParticleOverlayOptionsFactory.PARTICLE_TYPE_SUNNY);
                    } else if (weatherType.getWeatherRain().contains(weatherResponse)) {
                        // Rain 雨天
                        optionsList = ParticleOverlayOptionsFactory.defaultOptions(ParticleOverlayOptionsFactory.PARTICLE_TYPE_RAIN);
                    } else if(weatherType.getWeatherSnow().contains(weatherResponse)) {
                        // Snow 雪
                        optionsList = ParticleOverlayOptionsFactory.defaultOptions(ParticleOverlayOptionsFactory.PARTICLE_TYPE_SNOWY);
                    } else if (weatherType.getWeatherSnow().contains(weatherResponse)) {
                        // Smog 雾霾
                        optionsList = ParticleOverlayOptionsFactory.defaultOptions(ParticleOverlayOptionsFactory.PARTICLE_TYPE_HAZE);
                    } else {
                        // 都不是，则设置为晴天
                        optionsList = ParticleOverlayOptionsFactory.defaultOptions(ParticleOverlayOptionsFactory.PARTICLE_TYPE_SUNNY);
                    }


                    // 给 aMap 设置天气
                    for (ParticleOverlayOptions options : optionsList) {
                        ParticleOverlay particleOverlay = aMap.addParticleOverlay(options);
                        particleOverlay.setVisible(true);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

            }
        }).start();
    }

    // 处理得到的响应结果
    public String getWeatherResponse(final String response) {

        /*
            jsonObject 如下所示
        {"lives":[{"province":"江西","city":"南昌市","adcode":"360100","windpower":"4","weather":"阴","temperature":"32","humidity":"71","reporttime":"2021-06-18 12:34:01","winddirection":"西南"}],
        "count":"1",
        "infocode":"10000",
        "status":"1",
        "info":"OK"}
        * */

        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray jsonArray = (JSONArray) jsonObject.get("lives");
        JSONObject weatherObject = (JSONObject) jsonArray.get(0);

        return weatherObject.get("weather").toString();
    }


/****************************************** 生命周期函数 （下面部分）******************************************************/

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 3个定位的监听事件
     */
    // 定位成功后回调函数
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {


        if (mListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {

                LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点

                // 绘制线条
                Log.e("Amap", aMapLocation.getLatitude() + "," + aMapLocation.getLongitude());
                tempPointsList.add(count, latLng);
                count++;
                drawLines();

            } else {
//                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
//                Log.e("AmapErr", errText);
//                Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 根据所有定位点的集合，计算距离
    public double getDistance(ArrayList<LatLng> list) {
        double distance = 0;
        if (list == null || list.size() == 0) {
            return distance;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            LatLng firstLatLng = list.get(i);
            LatLng secondLatLng = list.get(i + 1);
            double betweenDis = AMapUtils.calculateLineDistance(firstLatLng,
                    secondLatLng);
            distance = (float) (distance + betweenDis);
        }
        return distance;

    }

    // 激活定位, 实现 LocationSource 接口要重写该方法
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mLocationOption.setOnceLocation(false);
            mLocationOption.setGpsFirst(true);
            // 设置发送定位请求的时间间隔,最小值为1000ms,1秒更新一次定位信息
            mLocationOption.setInterval(10000);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationOption.setMockEnable(true);
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {

    }

    /*************************************** 权限检查******************************************************/

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            BACK_LOCATION_PERMISSION
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @Override
    protected void onResume() {
        try {
            super.onResume();
            mapView.onResume();
            if (isNeedCheck) {
                checkPermissions(needPermissions);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        // 展示一个通知
        showNotification();
    }


    /**
     * @param
     * @since 2.5.0
     */
    @TargetApi(23)
    private void checkPermissions(String... permissions) {
        try {
            if (getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    try {
                        String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                        Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class, int.class});
                        method.invoke(this, array, 0);
                    } catch (Throwable e) {

                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    @TargetApi(23)
    private List<String> findDeniedPermissions(String[] permissions) {
        try {
            List<String> needRequestPermissonList = new ArrayList<String>();
            if (getApplicationInfo().targetSdkVersion >= 23) {
                for (String perm : permissions) {
                    if (checkMySelfPermission(perm) != PackageManager.PERMISSION_GRANTED
                            || shouldShowMyRequestPermissionRationale(perm)) {
                        if (!needCheckBackLocation
                                && BACK_LOCATION_PERMISSION.equals(perm)) {
                            continue;
                        }
                        needRequestPermissonList.add(perm);
                    }
                }
            }
            return needRequestPermissonList;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private int checkMySelfPermission(String perm) {
        try {
            Method method = getClass().getMethod("checkSelfPermission", new Class[]{String.class});
            Integer permissionInt = (Integer) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return -1;
    }

    private boolean shouldShowMyRequestPermissionRationale(String perm) {
        try {
            Method method = getClass().getMethod("shouldShowRequestPermissionRationale", new Class[]{String.class});
            Boolean permissionInt = (Boolean) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        try {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        super.onRequestPermissionsResult(requestCode, permissions, paramArrayOfInt);
        try {
            if (requestCode == PERMISSON_REQUESTCODE) {
                if (!verifyPermissions(paramArrayOfInt)) {
                    showMissingPermissionDialog();
                    isNeedCheck = false;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("温馨提示");
            builder.setMessage("当前应用缺少位置权限。\n请点击\\\"设置\\\"-\\\"权限\\\"-打开所需位置权限(始终运行)\n(因为需要保持后台获取位置--小声哔哔)");

            // 拒绝, 退出应用
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                finish();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setPositiveButton("设置",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                startAppSettings();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setCancelable(false);

            builder.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        try {
            Intent intent = new Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
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

    //插入运动数据
    private void insData(String u_id,byte[] path){
        Map<String, Object> params = new HashMap<>();

        params.put("id", u_id);
        params.put("path", path);
        OkHttp.post(INSPATH, params, new OkCallback() {
            @Override
            public String onResponse(String response) {
                if (OK.equals(response)) {
                    ToastUtilPuls.showShort(getApplicationContext(),"保存成功！");
                } else if(NO.equals(response)){
                    ToastUtilPuls.showShort(getApplicationContext(),"保存失败！");
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