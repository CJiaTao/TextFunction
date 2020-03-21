package com.example.textfunction.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.textfunction.R;
import com.example.textfunction.updateList.ListViewActivity;

/**
 * 测试 前台服务
 * com.yontoys.smcloudbox.test.updateList.ListViewActivity
 */
public class GroundService extends Service {

    private static final String TAG=GroundService.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"GroundService 执行 onCreate方法");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"GroundService 执行 onStartCommand方法");
        //创建Notification
        //获取Notification构造器
        Notification.Builder builder=new Notification.Builder(this.getApplicationContext());
        Intent intent1=new Intent(this, ListViewActivity.class);

        builder.setContentIntent(
                PendingIntent.getActivity(this,0,intent,0))//设置PendingIntent
        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.logo))//设置下拉列表中的大图标
        .setSmallIcon(R.mipmap.splash)//设置状态栏内的小图标
        .setContentTitle("下拉列表中的Title")//设置下拉列表的标题
        .setContentText("显示内容")//设置上下文内容
        .setWhen(System.currentTimeMillis());//设置该通知发生的时间

        Notification notification=builder.build();//创建Notification
        notification.defaults= Notification.DEFAULT_SOUND;//设置默认的声音

//        startForeground(110,notification);
        startForeground(0,notification);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"GroundService 执行 onBind方法");
        return null;
    }


    @Override
    public void onDestroy() {
        Log.e(TAG,"GroundService 执行 onDestroy方法");
        stopForeground(true);
        super.onDestroy();
    }
}
