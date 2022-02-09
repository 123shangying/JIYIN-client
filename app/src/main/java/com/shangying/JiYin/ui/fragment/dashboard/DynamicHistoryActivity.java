package com.shangying.JiYin.ui.fragment.dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shangying.JiYin.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 创建日期：2021/6/19 15:49
 *
 * @author 林凯
 * 文件名称： MyhistoryDynamicActivity.java
 * 类说明：
 * 用来展示我的历史动态界面
 * 可以在这里对动态进行删除
 */
public class DynamicHistoryActivity extends AppCompatActivity {

    // 用来展示评论数据的 RecyclerView
    RecyclerView recyclerView;
    // recyclerview 的 Adapter
    HistoryDynamicRecycleViewAdapter recycleViewAdapter;
    // RecyclerView 的布局管理器
    LinearLayoutManager mLayoutManager;

    // 数据库操作
    SQLiteDatabase db;
    MyHelper dbHelper;

    // 动态数据
    ArrayList<HashMap<String, String>> allDynamics;       // 所有的动态数据
    HashMap<String, String> dynamicItem;      // 每一条动态数据

    // 对话框
    AlertDialog alert = null;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_history);

        // 查询所有动态数据
        initData();

        // 设置 RecycleView
        recyclerView = findViewById(R.id.dynamic_history_recyclerview);
        //设置固定大小
        recyclerView.setHasFixedSize(true);
        //创建线性布局
        mLayoutManager = new LinearLayoutManager(this);
        //垂直方向
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //给RecyclerView设置布局管理器
        recyclerView.setLayoutManager(mLayoutManager);

        //
        /*
            由于要在监听事件中弹出提示框，然后重新设置 Adapter 更新数据。
            所以这里和 RunFragment 不一样，我们不再 Adapter 的构造方法里面实例化 Listener
        * */
        recycleViewAdapter = new HistoryDynamicRecycleViewAdapter(getApplicationContext());


        HistoryDynamicRecycleViewAdapter.OnRecyclerItemClickListener onRecyclerItemClickListener = new HistoryDynamicRecycleViewAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, HistoryDynamicRecycleViewAdapter.ViewName viewName, int position) {
                switch (view.getId()) {
                    case R.id.dynamic_history_btn_delete: {


                        // 如果直接在这里面写弹框逻辑，可能会找不到 Activity报错，所以抽取出来放在外面
                        showAlert(position);

                        break;
                    }
                    default: {
                        System.out.println("点击了item");
                    }
                }
            }

        };


        // 为 Adapter 设置监听器，就是我们上面实例化的监听器
        recycleViewAdapter.setOnRecyclerItemClickListener(onRecyclerItemClickListener);

        // 为 myAdapter 设置数据
        recycleViewAdapter.setDynamicsData(allDynamics);

        recyclerView.setAdapter(recycleViewAdapter);


    }

    public void showAlert(int position) {
        builder = new AlertDialog.Builder(this);

        alert = builder.setIcon(R.mipmap.icon_warning)
                .setTitle("系统提示：")
                .setMessage("确定要删除吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();

                         deleteData(position);



                        // 更新数据，同时重新设置 Adapter
                        initData();
                        // 为 myAdapter 设置数据
                        recycleViewAdapter.setDynamicsData(allDynamics);
                        recyclerView.setAdapter(recycleViewAdapter);
                    }
                }).create();             //创建AlertDialog对象
        alert.show();                    //显示对话框
    }

    // 根据 postion 在数据库中删除数据
    public void deleteData(int position) {

        dbHelper = new MyHelper(this);
        db = dbHelper.getWritableDatabase();

        String d_id = allDynamics.get(position).get("d_id");
        db.execSQL("delete from dynamic where d_id = ?", new String[]{d_id});
        db.close();
    }


    private void initData() {

        // 从 SharedPreferences 获取当前用户 id
        SharedPreferences mSharedPreferences = this.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        final String u_id = mSharedPreferences.getString("u_id", "1");

        dbHelper = new MyHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from dynamic where u_id = ?", new String[]{u_id});

        // 初始化保存动态数据的变量
        allDynamics = new ArrayList<>();

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {

                dynamicItem = new HashMap<>();

                dynamicItem.put("d_id", cursor.getString(0));
                dynamicItem.put("u_id", "1");
                dynamicItem.put("title", cursor.getString(2));
                dynamicItem.put("content", cursor.getString(3));
                dynamicItem.put("privacy", cursor.getString(4));
                dynamicItem.put("c_time", cursor.getString(5));

                allDynamics.add(dynamicItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();


    }
}