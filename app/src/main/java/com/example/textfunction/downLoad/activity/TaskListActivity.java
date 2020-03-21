package com.example.textfunction.downLoad.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.textfunction.R;
import com.example.textfunction.downLoad.DaoManager;
import com.example.textfunction.downLoad.DownloadService;
import com.example.textfunction.downLoad.adapter.TaskListAdapter;
import com.example.textfunction.downLoad.entity.FileInfo;
import com.example.textfunction.downLoad.entity.ThreadInfo;
import com.example.textfunction.downLoad.greendao.DaoSession;
import com.example.textfunction.downLoad.greendao.ThreadInfoDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity implements TaskListAdapter.ItemButtonListener {

    private ListView listView;
    private TaskListAdapter taskListAdapter;
    private List<ThreadInfo> allThreads;
    private List<Integer> progressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        initData();
        initView();
        setAndroidNativeLightStatusBar(this, true);
    }


    //设置状态栏字体颜色
    private static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    private void initData() {
        DaoManager manager = new DaoManager().init(this);
        DaoSession daoSession = manager.getDaoSession();
        ThreadInfoDao dao = daoSession.getThreadInfoDao();
        allThreads = dao.loadAll();
        progressList = new ArrayList<>();
        //强行杀死进程重启之后筛选未完成的任务继续下载
        if (DownloadService.mTasks.size() == 0) {
            List<ThreadInfo> allContinueThreads = dao.queryBuilder().where(ThreadInfoDao.Properties.State.eq(1)).list();
            for (ThreadInfo allContinueThread : allContinueThreads) {
                Intent intent = new Intent(this, DownloadService.class);
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(allContinueThread.getTitle());
                fileInfo.setId(allContinueThread.getId());
                fileInfo.setUrl(allContinueThread.getUrl());
                intent.putExtra("fileInfo", fileInfo);
                intent.setAction(DownloadService.ACTION_START);
                startService(intent);
            }
        }
    }

    private void initView() {
        listView = findViewById(R.id.lv);
        taskListAdapter = new TaskListAdapter();
        for (int i = 0; i < allThreads.size(); i++) {
            File file = new File(DownloadService.DOWNLOAD_PATH, allThreads.get(i).getTitle());
            long currentlength = file.length();
            int currentProgress = Integer.valueOf(currentlength * 100 / allThreads.get(i).getFileLength() + "");
            progressList.add(currentProgress);
        }
        taskListAdapter.setFileInfoList(this, allThreads, progressList, this);
        listView.setAdapter(taskListAdapter);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_UPDATA);
        intentFilter.addAction(DownloadService.ACTION_ERRO);
        registerReceiver(mReciver, intentFilter);
    }

    BroadcastReceiver mReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadService.ACTION_UPDATA.equals(intent.getAction())) {
                String finishedstr = intent.getStringExtra("finished");
                int fileId = intent.getIntExtra("fileId", 0);
                taskListAdapter.updateProgress(fileId, Integer.valueOf(finishedstr));
            } else if (DownloadService.ACTION_ERRO.equals(intent.getAction())) {
                Log.d("TaskListActivity", "456");
                int fileId = intent.getIntExtra("fileId", 0);
                for (int i = 0; i < allThreads.size(); i++) {
                    if (allThreads.get(i).getId() == fileId) {
                        //异常了
                        allThreads.get(i).setState(2);
                        taskListAdapter.setFileInfoList(TaskListActivity.this, allThreads, progressList, TaskListActivity.this);
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReciver);
    }

    @Override
    public void onListButtonStart(ThreadInfo threadInfo) {
        Intent intent = new Intent(this, DownloadService.class);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(threadInfo.getTitle());
        fileInfo.setId(threadInfo.getId());
        fileInfo.setUrl(threadInfo.getUrl());
        intent.putExtra("fileInfo", fileInfo);
        intent.setAction(DownloadService.ACTION_START);
        startService(intent);
    }

    @Override
    public void onListButtonPause(ThreadInfo threadInfo) {
        Intent intent = new Intent(this, DownloadService.class);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(threadInfo.getId());
        intent.putExtra("fileInfo", fileInfo);
        intent.setAction(DownloadService.ACTION_STOP);
        startService(intent);
    }

    @Override
    public void onLongClick(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskListActivity.this);
        builder.setTitle("确定要删除").setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DaoManager manager = new DaoManager().init(TaskListActivity.this);
                DaoSession daoSession = manager.getDaoSession();
                ThreadInfoDao dao = daoSession.getThreadInfoDao();
                dao.queryBuilder().where(ThreadInfoDao.Properties.Url.eq(allThreads.get(position).getUrl()),ThreadInfoDao.Properties.Id.eq(allThreads.get(position).getId()))
                    .buildDelete().executeDeleteWithoutDetachingEntities();
                File file = new File(DownloadService.DOWNLOAD_PATH, allThreads.get(position).getTitle());
                if (file.exists()) {
                    file.delete();
                }
                allThreads.remove(position);
                progressList.remove(position);
                taskListAdapter.setFileInfoList(TaskListActivity.this, allThreads, progressList, TaskListActivity.this);
            }
        }).show();
    }


}
