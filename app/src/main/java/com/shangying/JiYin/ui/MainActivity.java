package com.shangying.JiYin.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shangying.JiYin.R;
import com.shangying.JiYin.databinding.ActivityMainBinding;
import com.shangying.JiYin.ui.fragment.dashboard.DashboardFragment;

/**
 * Created with IntelliJ IDEA.
 * User: shangying.
 * Email: shangying611@gmail.com
 * Blog:  https://shangying.host/
 * Date: 2021/09/28.
 * Time: 17:02.
 * Explain:逻辑主层
 */

public class MainActivity extends AppCompatActivity {
    private boolean isExit = false;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        这里解决动态展示时数据首次加载为空的问题
        DashboardFragment.getShowall();
        DashboardFragment.List.clear();
//        Showdata showdata=new Showdata();
//        showdata.showData();


        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        //        获取保存的用户名和密码
        SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        String userId=sharedPreferences.getString("username","");

  /*      //String Id=sharedPreferences.getString("password","");
//判断参数是否为空,一般能传递过来不可能为空，这里主要是首启动页面MainActivity,所以为了判断
//        是否已经登录，如果登录过了就不跳转了
*/

        if("".equals(userId)){
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
//            这里不关闭会空指针
            finish();
        }
//        主页的功能
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DashboardFragment.getShowall();
        DashboardFragment.List.clear();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        首页，历史，我的
//        页面加载
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
    // 重写Activity中onKeyDown（）方法
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {// 当keyCode等于退出事件值时
            ToQuitTheApp();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    //封装ToQuitTheApp方法
    private void ToQuitTheApp() {
        if (isExit) {
            // ACTION_MAIN with category CATEGORY_HOME 启动主屏幕
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            SharedPreferences sss= getSharedPreferences("data", Context .MODE_PRIVATE);
            SharedPreferences.Editor editor = sss.edit();
            editor.remove("s");
            editor.commit();
            System.exit(0);// 使虚拟机停止运行并退出程序
        } else {
            isExit = true;
            Toast.makeText(MainActivity.this, "再按一次退出APP", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(0, 2000);// 3秒后发送消息
        }
    }


    /**
     * 创建Handler对象，用来处理消息
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {//处理消息
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            isExit = false;
        }
    };

}