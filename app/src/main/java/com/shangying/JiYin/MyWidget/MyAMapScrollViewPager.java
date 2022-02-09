package com.shangying.JiYin.MyWidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * 创建日期：2021/6/16 23:35
 * @author 林凯
 * 文件名称： AMapScrollViewPager.java
 * 类说明： 重写ViewPager canScroll方法，解决ViewPager和地图横向滑动冲突
 */
public class MyAMapScrollViewPager extends ViewPager {

    public MyAMapScrollViewPager(Context context) {
        super(context);
    }

    public MyAMapScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (Math.abs(dx) > 50) {
            return super.canScroll(v, checkV, dx, x, y);
        } else {
            return true;
        }
    }
}
