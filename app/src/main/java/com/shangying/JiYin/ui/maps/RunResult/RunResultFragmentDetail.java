package com.shangying.JiYin.ui.maps.RunResult;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.shangying.JiYin.R;
import com.shangying.JiYin.ui.maps.MyPath;
import com.shangying.JiYin.ui.maps.Run.RunResultActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 创建日期：2021/6/17 9:42
 * @author 林凯
 * 文件名称： RunResultFragmentDetail.java
 * 类说明： 跑步结果界面，用来显示详情信息的 Fragment
 */
public class RunResultFragmentDetail extends Fragment {

    MyPath myPath;
    TextView tv_distance, tv_continuetime, tv_begintime, tv_endtime, tv_speed1, tv_speed2;

    public RunResultFragmentDetail() {
    }

    public static RunResultFragmentDetail newInstance(String param1, String param2) {
        RunResultFragmentDetail fragment = new RunResultFragmentDetail();
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
        return inflater.inflate(R.layout.fragment_run_result_detail, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RunResultActivity activity = (RunResultActivity) this.getActivity();
        myPath =  activity.getMyPath();

        tv_distance = this.getActivity().findViewById(R.id.run_result_detail_distance);
        tv_continuetime = this.getActivity().findViewById(R.id.run_result_detail_continuetime);
        tv_begintime = this.getActivity().findViewById(R.id.run_result_detail_begintime);
        tv_endtime = this.getActivity().findViewById(R.id.run_result_detail_endtime);
        tv_speed1 = this.getActivity().findViewById(R.id.run_result_detail_speed1);
        tv_speed2 = this.getActivity().findViewById(R.id.run_result_detail_speed2);

        // 运动距离，精确到小数点后一位
        String distanceFormat = new DecimalFormat("0.00").format(myPath.getDistance());
        tv_distance.setText("运动距离：" + distanceFormat + "米");

        // 运动时间
        int second = (int) (myPath.getTime() / 1000 % 60);
        int minute = (int) (myPath.getTime() / 1000 / 60);
        tv_continuetime.setText("运动时间：" + minute + " 分 " + second + " 秒");

        // 开始时间和结束时间
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginTime = new Date(myPath.getStartTime());
        Date endTime = new Date(myPath.getEndTime());
        String beginTimeStr = sdf.format(beginTime);
        String endTImeStr = sdf.format(endTime);
        tv_begintime.setText("开始时间：" + beginTimeStr);
        tv_endtime.setText("结束时间：" + endTImeStr);

        System.out.println(myPath.getSpeed1());
        String speedFormat = new DecimalFormat("0.00").format(myPath.getSpeed1());
        tv_speed1.setText("时速(M/s)：" + speedFormat);
        tv_speed2.setText("配速(分钟/公里)：" + myPath.getSpeed2());
    }
}