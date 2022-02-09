package com.shangying.JiYin.ui.maps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.shangying.JiYin.R;
import com.shangying.JiYin.ui.fragment.dashboard.MyHelper;
import com.shangying.JiYin.ui.maps.Run.RunActivity;
import com.shangying.JiYin.ui.maps.Run.RunResultActivity;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * 创建日期：2021/6/10 17:08
 * @author 林凯
 * 文件名称： RunFragment.java
 * 类说明：运动对应的 Fragment
 */
public class RunFragment extends Fragment implements View.OnClickListener {

    TextView tv_date_today, run_fragment_year_month_day_2;       // 今日日期
    CalendarView calendarView;
    CalendarLayout calendarLayout;
    Button run_btn_startrun;

    // RecycleView
    RecyclerView recyclerView;
    RecycleViewAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    // 数据库操作
    SQLiteDatabase db;
    MyHelper dbHelper;

    ArrayList<MyPath> myPathArrayList;

    public RunFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_run, container, false);
    }

    // 在 Fragment 与 Activity 通信时，必须要等到所有的Fragment都已经初始化完成之后，才能够开始进行，所以要写在 onActivityCreated 里面
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // 初始化数据，即从数据库中获取 myPathArrayList
        initData(System.currentTimeMillis());

        // 初始化控件
        initWidget();

        initCalendarViewListener();


    }

    // 从数据库中获取数据
    private void initData(long time) {

        dbHelper = new MyHelper(getContext());
        db = dbHelper.getReadableDatabase();
        myPathArrayList = new ArrayList<>();

        // 获取今天零点时间和 23:59:59秒的时间，转换为格式字符串
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        long time = System.currentTimeMillis(); //当前时间的时间戳

        long zero = time/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset();
        long end = zero + 23 * (1000 * 60 * 60) + 59 * (1000 * 60) + 59 * 1000;

        Date dateZero = new Date(zero);
        Date dateEnd = new Date(end);
        String dateZeroFormat = sdf.format(dateZero);
        String dateEndFormat = sdf.format(dateEnd);


        // 获取数据，显示用户名
        SharedPreferences mSharedPreferences = this.getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String u_id = mSharedPreferences.getString("u_id", "");

        // 根据时间查询今天的运动记录
        Cursor cursor = db.rawQuery("select * from mypath where u_id = ? and c_time between ? and ? order by c_time desc", new String[]{u_id, dateZeroFormat, dateEndFormat});
        if (cursor.getCount() == 0) {
//            Toast.makeText(getContext(), "没有数据", Toast.LENGTH_SHORT).show();
        } else {
            cursor.moveToFirst();
            do {
                // p_id     u_id        path        c_time
                // 运动轨迹id   用户id    运动轨迹    创建时间

                byte path[] = cursor.getBlob(2);

                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(path);
                try {
                    ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
                    MyPath myPath = (MyPath) inputStream.readObject();
                    myPathArrayList.add(myPath);
                    inputStream.close();
                    arrayInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }

    }

    // 实例化控件
    private void initWidget() {
        // 控件实例化
        tv_date_today = this.getActivity().findViewById(R.id.run_fragment_year_month_day);
        run_fragment_year_month_day_2 = this.getActivity().findViewById(R.id.run_fragment_year_month_day_2);
        calendarView = this.getActivity().findViewById(R.id.calendarView);
        calendarLayout = this.getActivity().findViewById(R.id.calendarLayout);
        run_btn_startrun = this.getActivity().findViewById(R.id.run_btn_startrun);
        tv_date_today.setText(String.valueOf(calendarView.getCurYear()) + "年" + String.valueOf(calendarView.getCurMonth()) + "月" + String.valueOf(calendarView.getCurDay()) + "日");
        run_fragment_year_month_day_2.setText("运动记录：" + tv_date_today.getText().toString());

        // RecycleView 相关
        recyclerView = this.getActivity().findViewById(R.id.run_recycleview);
        //设置固定大小
        recyclerView.setHasFixedSize(true);
        //创建线性布局
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        //垂直方向
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //给RecyclerView设置布局管理器
        recyclerView.setLayoutManager(mLayoutManager);

        //创建适配器，并且设置监听事件
        mAdapter = (RecycleViewAdapter) new RecycleViewAdapter(getContext(), new RecycleViewAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView tv_time = (TextView) view.findViewById(R.id.run_recycleview_tv_1);
                TextView tv_distance = (TextView) view.findViewById(R.id.run_recycleview_tv_2);
                Toast.makeText(getContext(), tv_time.getText().toString(), Toast.LENGTH_SHORT).show();

                MyPath myPath = myPathArrayList.get(position);
                Intent intent = new Intent(getActivity(), RunResultActivity.class);
                intent.putExtra("myPath", JSON.toJSONString(myPath));
                startActivity(intent);
            }
        });


        // 将数据添加到适配器中（将 myPath 的对象数组加入进去）
        mAdapter.setPathList(myPathArrayList);
        recyclerView.setAdapter(mAdapter);


        // 添加监听事件
        run_btn_startrun.setOnClickListener(this);
    }

    public void initCalendarViewListener() {

        // 日历控件的监听事件
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(com.haibin.calendarview.Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(com.haibin.calendarview.Calendar calendar, boolean isClick) {

                // 需要将日期进行处理一下，转换成格式化的字符串，方便根据日期查询数据
                String monthStr = String.valueOf(calendar.getMonth());
                if (calendar.getMonth() < 10) {
                    monthStr = "0" + String.valueOf(calendar.getMonth());
                }

                String dayStr = String.valueOf(calendar.getDay());
                if (calendar.getDay() < 10) {
                    dayStr = "0" + String.valueOf(calendar.getDay());
                }

                tv_date_today.setText(String.valueOf(calendar.getYear()) + "年" + String.valueOf(calendar.getMonth()) + "月" + String.valueOf(calendar.getDay()) + "日");
                run_fragment_year_month_day_2.setText("运动记录：" + tv_date_today.getText().toString());

                Date tempDate = null;
                try {
                    tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(calendar.getYear() + "-" + monthStr + "-" + dayStr + " " + "12:00:00");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                initData(tempDate.getTime());

                mAdapter.setPathList(myPathArrayList);
                recyclerView.setAdapter(mAdapter);
//                selectDateFormatBegin = calendar.getYear() + "-" + monthStr + "-" + dayStr + " " + "00:00:00";
//                selectDateFormatEnd = calendar.getYear() + "-" + monthStr + "-" + dayStr + " " + "23:59:59";
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initData(System.currentTimeMillis());
        // 将数据添加到适配器中（将 myPath 的对象数组加入进去）
        mAdapter.setPathList(myPathArrayList);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.run_btn_startrun:
                Intent intent = new Intent(this.getActivity(), RunActivity.class);
                startActivity(intent);
                break;
        }
    }
}