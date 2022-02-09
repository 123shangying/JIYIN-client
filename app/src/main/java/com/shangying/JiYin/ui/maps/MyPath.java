package com.shangying.JiYin.ui.maps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 创建日期：2021/6/15 19:49
 * @author 林凯
 * 文件名称： MyPath.java
 * 类说明： 存放运动轨迹的类，直接将这个类存储到数据库中。需要实现 Serializable 接口
 */
public class MyPath implements Serializable {

    // 由于 LatLng 没有实现 Serializable 接口，所以不能存入数据库中的采用其他方法
    //运动轨迹,考虑到运动过程中会暂停，所以采用 ArrayList 嵌套的方法存储所有的点，方便后面计算距离
    //    private ArrayList<ArrayList<LatLng>> pointList = new ArrayList<>();

    /*
    *   存储所有点的经度和纬度的信息， LatLng 没有实现 Serializable 接口，不能存入数据库中，
    *   所以需要存储对应的经度和纬度信息。
    *   有2个key：
    *       latitude    维度
    *       longitude   经度
    *
    * */
    HashMap<String, ArrayList<Double>> pointMap;

    //运动距离
    private Double distance;
    //运动时长
    private Long time;
    //运动开始时间
    private Long startTime;
    //运动结束时间
    private Long endTime;
    //平均时速(m/s)
    private Double speed1;
    //平均配速(分钟/公里)
    private Double speed2;

    @Override
    public String toString() {
        return "MyPath{" +
                ", pointMap=" + pointMap +
                ", distance=" + distance +
                ", time=" + time +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", speed1=" + speed1 +
                ", speed2=" + speed2 +
                '}';
    }

    public MyPath() {
    }

    public MyPath(HashMap<String, ArrayList<Double>> pointMap, Double distance, Long time, Long startTime, Long endTime, Double speed1, Double speed2) {
        this.pointMap = pointMap;
        this.distance = distance;
        this.time = time;
        this.startTime = startTime;
        this.endTime = endTime;
        this.speed1 = speed1;
        this.speed2 = speed2;
    }

    public HashMap<String, ArrayList<Double>> getPointMap() {
        return pointMap;
    }

    public void setPointMap(HashMap<String, ArrayList<Double>> pointMap) {
        this.pointMap = pointMap;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Double getSpeed1() {
        return speed1;
    }

    public void setSpeed1(Double speed1) {
        this.speed1 = speed1;
    }

    public Double getSpeed2() {
        return speed2;
    }

    public void setSpeed2(Double speed2) {
        this.speed2 = speed2;
    }
}