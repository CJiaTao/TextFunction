package com.example.textfunction.downLoad;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.textfunction.downLoad.entity.FileInfo;
import com.example.textfunction.downLoad.entity.ThreadInfo;
import com.example.textfunction.downLoad.greendao.DaoSession;
import com.example.textfunction.downLoad.greendao.ThreadInfoDao;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

//下载任务类
public class DownloadTask {
    private Context context = null;
    private FileInfo fileInfo = null;
    private DaoManager daoManager = null;
    private ThreadInfoDao dao = null;
    public boolean isPause = false;
    private OkHttpClient client = new OkHttpClient();

    public DownloadTask(Context context, FileInfo fileInfo) {
        this.context = context;
        this.fileInfo = fileInfo;
        this.daoManager = new DaoManager().init(context);
        DaoSession daoSession = daoManager.getDaoSession();
        this.dao = daoSession.getThreadInfoDao();
    }

    public void download() {
        //读取数据库的线程任务信息
        ThreadInfo threadInfo = dao.queryBuilder()
                .where(ThreadInfoDao.Properties.Url.eq(fileInfo.getUrl())).unique();
        if (threadInfo == null) {
            //如果为空，初始化线程任务对象
            threadInfo = new ThreadInfo(fileInfo.getId(), fileInfo.getUrl(), fileInfo.getLength(), 1, fileInfo.getFileName());
        }
        //创建子线程下载
        new DownloadThread(threadInfo).start();
    }

    //下载线程
    class DownloadThread extends Thread {
        ThreadInfo threadInfo = null;
        private File file;

        public DownloadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
            //向数据库插入线程任务信息
            if (dao.queryBuilder().where(
                    ThreadInfoDao.Properties.Id.eq(threadInfo.getId()),
                    ThreadInfoDao.Properties.Url.eq(threadInfo.getUrl())).count() == 0) {
                //之前不存在该线程任务，添加到数据库中
                dao.insert(threadInfo);
            }

            RandomAccessFile raf = null;
            InputStream inputStream = null;
            try {
                Intent intent=new Intent(DownloadService.ACTION_UPDATA);
                //设置文件写入位置，同时判断手机中是否存在该文件
                file = new File(DownloadService.DOWNLOAD_PATH, fileInfo.getFileName());

                if (file.length() == fileInfo.getLength()) {
                    //已经有这个文件了
                    //下载完成之后发送广播
                    intent.putExtra("finished", 100 + "");
                    intent.putExtra("fileId", fileInfo.getId());
                    context.sendBroadcast(intent);
                    //下载完成之后更新线程任务信息
                    threadInfo.setState(3);
                    dao.update(threadInfo);
                    return;
                }

                raf = new RandomAccessFile(file, "rwd");
                raf.seek(file.length());

                // 断点续传：重新开始下载的位置：file.length()
                String range = String.format(Locale.CHINESE, "bytes=%d-", file.length());
                Request request = new Request.Builder()
                        .url(threadInfo.getUrl())
                        .header("range", range)
                        .build();
                //使用OkHttp请求服务器
                Call call = client.newCall(request);
                Response response = call.execute();
                //连接服务器成功
                ResponseBody body = response.body();
                Log.e("TAG", "文件大小：" + body.contentLength());
                //开始断点续传
                inputStream = body.byteStream();
                byte[] bytes = new byte[1024];
                int len;
                long time = System.currentTimeMillis();
                while ((len = inputStream.read(bytes)) != -1) {
                    //写入文件
                    raf.write(bytes, 0, len);
                    //下载进度发送广播给activity
                    if (System.currentTimeMillis() - time > 300) {
                        time = System.currentTimeMillis();
                        intent.putExtra("finished", file.length() * 100 / fileInfo.getLength() + "");
                        intent.putExtra("fileId", fileInfo.getId());
                        context.sendBroadcast(intent);
                    }
                    //下载暂停保存下载进度
                    if (isPause) {
                        threadInfo.setState(3);
                        dao.update(threadInfo);
                        return;
                    }
                    Log.e("TAG", "已下载字节：" + file.length());
                }
                //下载完成之后发送广播
                intent.putExtra("finished", 100 + "");
                intent.putExtra("fileId", fileInfo.getId());
                context.sendBroadcast(intent);
                //下载完成之后更新线程任务信息
                threadInfo.setState(3);
                dao.update(threadInfo);
                Log.e("TAG", "文件下载完毕：" + raf.getFilePointer());

            } catch (Exception e) {
                e.printStackTrace();
                isPause = true;
                threadInfo.setState(2);
                dao.update(threadInfo);
                Intent intent = new Intent(DownloadService.ACTION_ERRO);
                intent.putExtra("fileId", fileInfo.getId());
                context.sendBroadcast(intent);
            } finally {
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
