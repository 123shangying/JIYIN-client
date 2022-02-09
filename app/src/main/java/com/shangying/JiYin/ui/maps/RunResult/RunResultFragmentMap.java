package com.shangying.JiYin.ui.maps.RunResult;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.shangying.JiYin.R;
import com.shangying.JiYin.ui.maps.MyPath;
import com.shangying.JiYin.ui.maps.Run.RunResultActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 创建日期：2021/6/17 9:42
 * @author 林凯
 * 文件名称： RunResultFragmentMap.java
 * 类说明： 跑步结果界面，用来显示轨迹地图的 Fragment
 */
public class RunResultFragmentMap extends Fragment {


    private AMap aMap;
    private MapView mapView;
    // 地图的ui交互的对象，就是地图上的各种控件
    private UiSettings mUiSettings;
    MyPath myPath;

    private Marker mOriginStartMarker, mOriginEndMarker;
    CameraUpdate cameraUpdate;
    private PolylineOptions polylineOptions;
    ArrayList<LatLng> poinstList;


    public RunResultFragmentMap() {
        // Required empty public constructor
    }

    public static RunResultFragmentMap newInstance(String param1, String param2) {
        RunResultFragmentMap fragment = new RunResultFragmentMap();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_run_result_map, container, false);
    }


    // 在 Fragment 与 Activity 通信时，必须要等到所有的Fragment都已经初始化完成之后，才能够开始进行，所以要写在 onActivityCreated 里面
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 1. 初始化数据
        initData();

        // 2. 初始化控件
        //  2.1 获取地图控件引用
        mapView = (MapView) getActivity().findViewById(R.id.run_result_map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        //  2.2  实例化 aMap
        aMap = mapView.getMap();
        mUiSettings = aMap.getUiSettings();

        // 2.3  设置一下 aMap 的属性
        setUpMap();

        // 2.4  画出轨迹线段
        drawLine();

    }

    private void initData() {
        // 1. 从 RunResultActivity 或 myPath 对象
        RunResultActivity activity = (RunResultActivity) this.getActivity();
        myPath =  activity.getMyPath();

        // 2. 将 myPath 对象中的 pointMap 获取出来，还原成 LatLng 数组，存入 poinstList 里面
        int length = myPath.getPointMap().get("latitude").size();
        HashMap<String, ArrayList<Double>> pointMap = myPath.getPointMap();
        LatLng latLng;
        poinstList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            latLng = new LatLng(pointMap.get("latitude").get(i), pointMap.get("longitude").get(i));
            poinstList.add(latLng);
        }
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {

        // 1. 设置 amap 的 ui 界面的一些属性，主要是一些ui界面上的控件是否显示
        mUiSettings.setMyLocationButtonEnabled(true);   // 设置默认定位按钮是否显示
        mUiSettings.setCompassEnabled(true);// 设置指南针是否显示
        mUiSettings.setZoomControlsEnabled(false);// 设置缩放按钮是否显示
        mUiSettings.setScaleControlsEnabled(true);// 设置比例尺是否显示
        mUiSettings.setRotateGesturesEnabled(false);// 设置地图旋转是否可用
        mUiSettings.setTiltGesturesEnabled(false);// 设置地图倾斜是否可用

        aMap.setMyLocationEnabled(false);    // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.showIndoorMap(true);       //true：显示室内地图；false：不显示；
//        aMap.setMaxZoomLevel(18);   // 设置最大缩放几倍

        // 设置镜头的初始位置和放大倍数
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myPath.getStartPoint(), 18);
        if (poinstList.size() != 0) {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(poinstList.get(0), 18);
            aMap.animateCamera(cameraUpdate);
        }

    }

    // 绘制轨迹
    private void drawLine() {

        // 实例化一个 PolylineOptions 对象，表示一条线段
        polylineOptions = new PolylineOptions();

//        ArrayList<ArrayList<LatLng>> pointList = myPath.getPointList();

        for (int i = 0; i < poinstList.size() - 1; i++) {
            polylineOptions.addAll(poinstList);    //将所有点加入到该线段的坐标集合中
        }
        polylineOptions.geodesic(true);     // 是否话大地曲线，默认false
        polylineOptions.setDottedLine(false);       // 设置是否画虚线，默认为false，画实线。
        polylineOptions.color(Color.GREEN);        // 设置线段颜色
        polylineOptions.useGradient(true);      // 是否使用渐变色
        polylineOptions.width(20);          // 设置线段宽度
        polylineOptions.visible(true);

        if (poinstList.size() != 0) {
            // 设置线段起点和终点图标
            mOriginStartMarker = aMap.addMarker(new MarkerOptions().position(poinstList.get(0))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_start)));
            mOriginEndMarker = aMap.addMarker(new MarkerOptions().position(poinstList.get(poinstList.size() - 1))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_end)));
        }


        // 将该线段添加到 AMap 对象中
        aMap.addPolyline(polylineOptions);

    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}