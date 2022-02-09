package com.shangying.JiYin.ui.maps.Run;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.shangying.JiYin.MyWidget.MyAMapScrollViewPager;
import com.shangying.JiYin.R;
import com.shangying.JiYin.ui.fragment.dashboard.MyHelper;
import com.shangying.JiYin.ui.maps.MyPath;
import com.shangying.JiYin.ui.maps.RunResultFragmentPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RunResultActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener {


    // 自定义的 ViewPager
    MyAMapScrollViewPager aMapScrollViewPager;
    RadioButton rb_path, rb_detail;
    RadioGroup rg_tab_bar;
    ImageView run_result_menu;
    // 对话框
    AlertDialog alert = null;
    AlertDialog.Builder builder;
    // 数据库操作
    SQLiteDatabase db;
    MyHelper dbHelper;
    ContentValues values;
    //几个代表页面的常量
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    RunResultFragmentPagerAdapter myAdapter;
    MyPath myPath;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_result);

        /*
             1. 从 Intent 中 获取 DateFormat 和 MyPath 对象，在这里就不进行数据库查询了，只负责显示
             即在其他的 Activity 中查询数据库，将 myPath 对象传递过来即可。
         */
        Intent intent = getIntent();
        myPath = JSON.parseObject(intent.getStringExtra("myPath"), MyPath.class);


        /*
            3. 设置数据适配器
        * */
        myAdapter = new RunResultFragmentPagerAdapter(getSupportFragmentManager(), 1);
        aMapScrollViewPager = findViewById(R.id.run_result_ampviewpager);
        aMapScrollViewPager.setAdapter(myAdapter);
        aMapScrollViewPager.setCurrentItem(0);
        aMapScrollViewPager.addOnPageChangeListener(this);

        run_result_menu = findViewById(R.id.run_result_menu);
        rb_path = findViewById(R.id.run_result_rb_path);
        rb_path.setChecked(true);
        rb_detail = findViewById(R.id.run_result_rb_detail);
        rg_tab_bar = findViewById(R.id.run_result_bar);
        rg_tab_bar.setOnCheckedChangeListener(this::onCheckedChanged);

        run_result_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(RunResultActivity.this, run_result_menu);
                popup.getMenuInflater().inflate(R.menu.menu_pop, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_item_delete:

                                System.out.println("click");

                                // 从数据库中删除数据
                                deletePath();

                                Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

//                                                        startActivity(new Intent(RunResultActivity.this, HomeActivity.class));
                                        // 销毁当前活动
                                        finish();
                                    }
                                }, 500);    //延时1s执行

                                break;
                            default:
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        // 设置有颜色的状态栏
        setStatusBarColor(this, Color.parseColor("#1292d0"));

        // 1. 从 MainActivity 获得线段的所有点，存入 pointList 里面
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        ArrayList<Parcelable> bundleList = bundle.getParcelableArrayList("myPathList");
//        myPath = (MyPath) bundleList.get(0);


//        MyPath myPath = new MyPath();
//        RunResultFragmentDetail fragment = new RunResultFragmentDetail();
//        Bundle bundle = new Bundle();
//        bundle.putString("mypath", JSON.toJSONString(myPath));

//        Fragment instantiate = Fragment.instantiate(this, RunResultFragmentDetail.class.getName(), bundle);
//
//        android.app.Fragment instantiate1 = android.app.Fragment.instantiate(this, RunResultFragmentDetail.class.getName(), bundle);
//
//
//        fragment.setArguments(bundle);//数据传递到fragment中
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////        fragmentTransaction.replace(,fragment);
////        fragmentTransaction.add(fragment);
//        fragmentTransaction.commit();
    }

    // 从数据库中读取数据,没有用到
//    private void initData() {
//
//
//        // 1. 从 MainActivity 获得线段的所有点，存入 pointList 里面
//        Intent intent = getIntent();
//        dateFormat = intent.getStringExtra("dateFormat");
//
//        // 获取 PointList
////        Bundle bundle = intent.getExtras();
////        ArrayList<Parcelable> bundleList = bundle.getParcelableArrayList("pointList");
////        poinstList = (ArrayList<ArrayList<LatLng>>) bundleList.get(0);
//
//
//
//
//
//        dbHelper = new MyHelper(this);
//        db = dbHelper.getReadableDatabase();
//
//        Cursor cursor = db.rawQuery("select * from mypath where c_time = ?", new String[]{this.dateFormat});
//        if (cursor.getCount() == 0) {
//            Toast.makeText(getApplicationContext(), "没有数据", Toast.LENGTH_SHORT).show();
//        } else {
//            cursor.moveToFirst();
//            do {
//                byte data[] = cursor.getBlob(cursor.getColumnIndex("path"));
//                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
//                try {
//                    ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
//                    myPath = (MyPath) inputStream.readObject();
//                    System.out.println(myPath);
//                    inputStream.close();
//                    arrayInputStream.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } while (cursor.moveToNext());
//            cursor.close();
//            db.close();
//        }
//
//
//
//    }


    public MyPath getMyPath() {
        return myPath;
    }

    // 从数据库中删除这条跑步记录
    public void deletePath() {
        dbHelper = new MyHelper(getApplicationContext());

        db = dbHelper.getWritableDatabase();

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(myPath.getStartTime());
        String dateFormat = sdf.format(date);


        System.out.println("c_time");
        System.out.println(dateFormat);
        db.execSQL("delete from mypath where c_time = ?", new String[]{dateFormat});
        db.close();

    }

    // 单选按钮发生改变，设置 viewpage 的选中情况
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            // 轨迹
            case R.id.run_result_rb_path:
                aMapScrollViewPager.setCurrentItem(PAGE_ONE);
                System.out.println(aMapScrollViewPager);
                System.out.println("COmm");
                break;

            // 详情
            case R.id.run_result_rb_detail:
                aMapScrollViewPager.setCurrentItem(PAGE_TWO);
                System.out.println(aMapScrollViewPager);
                System.out.println("Run");
                break;
        }
    }

    // 滑动状态发生改变
    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (aMapScrollViewPager.getCurrentItem()) {
                case PAGE_ONE:
                    rb_path.setChecked(true);
                    break;
                case PAGE_TWO:
                    rb_detail.setChecked(true);
                    break;
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }


    // 设置有颜色的状态栏
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(statusColor);
        //设置系统状态栏处于可见状态
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        //让view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }
}

