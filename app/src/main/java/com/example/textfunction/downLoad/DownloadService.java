package com.example.textfunction.downLoad;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.textfunction.downLoad.entity.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

//控制DownloadTask开始和暂停
public class DownloadService extends Service {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATA = "ACTION_UPDATA";
    public static final String ACTION_ERRO = "ACTION_ERRO";
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ADEMO";
    public static final int MSG_INIT = 0;
    private DownloadTask downloadTask;
    //下载任务的集合
    public static Map<Long, DownloadTask> mTasks = new LinkedHashMap<>();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    Log.e("DownloadService", "MSG_INIT:" + fileInfo);
                    //启动下载任务
                    downloadTask = new DownloadTask(DownloadService.this, fileInfo);
                    downloadTask.download();
                    mTasks.put(fileInfo.getId(), downloadTask);
                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获得Activity的参数
        if (ACTION_START.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            new InitThread(fileInfo).start();
        } else if (ACTION_STOP.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            //从集合中取出下载任务
            DownloadTask downloadTask = mTasks.get(fileInfo.getId());
            if (downloadTask != null) {
                downloadTask.isPause = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class InitThread extends Thread {
        private FileInfo mFileInfo = null;

        public InitThread(FileInfo mFileInfo) {
            this.mFileInfo = mFileInfo;
        }

        @Override
        public void run() {

            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(mFileInfo.getUrl())
                        .build();
                Call call = client.newCall(request);
                Response response = call.execute();

                long length = -1;
                if (response.code() == 200) {
                    //连接服务器成功
                    ResponseBody body = response.body();
                    length = body.contentLength();
                    Log.e("TAG", "文件大小：" + body.contentLength());
                }
                File dir = new File(DOWNLOAD_PATH);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                mFileInfo.setLength((int) length);
                Message message = handler.obtainMessage(MSG_INIT, mFileInfo);
                message.sendToTarget();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
