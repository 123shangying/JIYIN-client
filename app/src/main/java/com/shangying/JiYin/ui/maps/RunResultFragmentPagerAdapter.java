package com.shangying.JiYin.ui.maps;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.shangying.JiYin.ui.maps.RunResult.RunResultFragmentDetail;
import com.shangying.JiYin.ui.maps.RunResult.RunResultFragmentMap;


/**
 * 创建日期：2021/6/17 9:20
 * @author 林凯
 * 文件名称： RunResultFragmentPagerAdapter.java
 * 类说明： 显示跑步结果界面时，用到的 Fragment Adapter
 */
public class RunResultFragmentPagerAdapter extends FragmentPagerAdapter {


    private final int PAGER_COUNT = 2;
    private RunResultFragmentMap fragmentMap;
    private RunResultFragmentDetail fragmentDetail;


    public RunResultFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        fragmentMap = new RunResultFragmentMap();
        fragmentDetail = new RunResultFragmentDetail();

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return null;
    }

//    @NonNull
//    @Override
//    public Fragment getItem(int position) {
//        Fragment fragment = null;
//        switch (position) {
//            case HomeActivity.PAGE_ONE:
//                fragment = fragmentMap;
//                break;
//            case HomeActivity.PAGE_TWO:
//                fragment = fragmentDetail;
//                break;
//        }
//        return fragment;
//    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

}

