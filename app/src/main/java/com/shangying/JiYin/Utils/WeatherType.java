package com.shangying.JiYin.Utils;

import java.util.ArrayList;

/**
 * 创建日期：2021/6/18 16:07
 * @author 林凯
 * 文件名称： WeatherType.java
 * 类说明： 存放天气类型的类，用来显示天气
 */
public class WeatherType {

    ArrayList<String> weatherSun;
    ArrayList<String> weatherRain;
    ArrayList<String> weatherSnow;
    ArrayList<String> weatherSmog;

    public WeatherType() {

        weatherSun = new ArrayList<String>(){{
            add("晴");
            add("少云");
            add("晴间多云");
            add("多云");
            add("有风");
            add("平静");
            add("微风");
            add("和风");
            add("清风");
            add("强风/劲风");
            add("疾风");
            add("大风");
            add("烈风");
            add("风暴");
            add("狂爆风");
            add("飓风");
            add("热带风暴");
            add("热");
            add("未知");
        }};

        weatherRain = new ArrayList<String>(){{
            add("阴");
            add("阵雨");
            add("雷阵雨");
            add("雷阵雨并伴有冰雹");
            add("小雨");
            add("中雨");
            add("大雨");
            add("暴雨");
            add("大暴雨");
            add("特大暴雨");
            add("强阵雨");
            add("强雷阵雨");
            add("极端降雨");
            add("毛毛雨/细雨");
            add("雨");
            add("小雨-中雨");
            add("中雨-大雨");
            add("大雨-暴雨");
            add("暴雨-大暴雨");
            add("大暴雨-特大暴雨");
            add("雨雪天气");
            add("雨夹雪");
            add("阵雨夹雪");
            add("冻雨");
            add("冷");
        }};

        weatherSnow = new ArrayList<String>(){{
           add("雪");
           add("阵雪");
           add("小雪");
           add("中雪");
           add("大雪");
           add("暴雪");
           add("小雪-中雪");
           add("中雪-大雪");
           add("大雪-暴雪");
        }};

        weatherSmog = new ArrayList<String>(){{
            add("霾");
            add("中度霾");
            add("重度霾");
            add("严重霾");
            add("浮尘");
            add("扬沙");
            add("沙尘暴");
            add("强沙尘暴");
            add("龙卷风");
            add("雾");
            add("浓雾");
            add("强浓雾");
            add("轻雾");
            add("大雾");
            add("特强浓雾");
            add("扬沙");
            add("扬沙");

        }};

    }

    public ArrayList<String> getWeatherSun() {
        return weatherSun;
    }

    public void setWeatherSun(ArrayList<String> weatherSun) {
        this.weatherSun = weatherSun;
    }

    public ArrayList<String> getWeatherRain() {
        return weatherRain;
    }

    public void setWeatherRain(ArrayList<String> weatherRain) {
        this.weatherRain = weatherRain;
    }

    public ArrayList<String> getWeatherSnow() {
        return weatherSnow;
    }

    public void setWeatherSnow(ArrayList<String> weatherSnow) {
        this.weatherSnow = weatherSnow;
    }

    public ArrayList<String> getWeatherSmog() {
        return weatherSmog;
    }

    public void setWeatherSmog(ArrayList<String> weatherSmog) {
        this.weatherSmog = weatherSmog;
    }
}
