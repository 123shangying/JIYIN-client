package com.shangying.JiYin.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.shangying.JiYin.R;


public class MyService extends Service {

    MediaPlayer player1;
    MyBroadcastReceiver broadcastReceiver;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String id = "jacklinmap";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(id, "迹印", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this, id)
                    .setAutoCancel(true)
                    .build();

            startForeground(1, notification);
        }

        // 注册一个广播接收器
        //动态注册广播接收器
        broadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.linkai.broadcast.MY_MUSIC");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    public void pauseMusic() {
        player1.pause();
    }

    public void continueMusic() {
        player1.start();
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "receive broadcast", Toast.LENGTH_SHORT).show();

            String option = intent.getStringExtra("option");
            if (option.equals("pause")) {
                pauseMusic();

            } else if (option.equals("start")) {
                continueMusic();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 音乐播放
        player1 = MediaPlayer.create(this, R.raw.music_03);
        player1.setLooping(true);
        player1.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // 销毁服务
    @Override
    public void onDestroy() {
        Log.i("hello","onDestroy() is running......");
        Toast.makeText(this,"销毁服务 onDestroy",Toast.LENGTH_LONG).show();

        if(player1 != null){
            player1.release();
            player1 = null;
        }

        super.onDestroy();

        // 同时还要记得取消 动态注册的广播接收器
        unregisterReceiver(broadcastReceiver);

    }


}