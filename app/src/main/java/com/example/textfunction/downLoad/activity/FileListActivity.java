package com.example.textfunction.downLoad.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.textfunction.R;
import com.example.textfunction.downLoad.DaoManager;
import com.example.textfunction.downLoad.adapter.FileListAdapter;
import com.example.textfunction.downLoad.entity.FileInfo;
import com.example.textfunction.downLoad.entity.ThreadInfo;
import com.example.textfunction.downLoad.greendao.DaoSession;
import com.example.textfunction.downLoad.greendao.ThreadInfoDao;

import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends AppCompatActivity {

    private ListView lv;
    private List<FileInfo> fileInfoList = new ArrayList<>();
    private List<Long> selectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        setAndroidNativeLightStatusBar(this, true);
        initData();
        initView();
    }

    private void initData() {
        FileInfo fileInfo1 = new FileInfo(0,
                "http://cdn.xiaoxiongyouhao.com/apps/androilas.apk",
                "小熊.apk", 0);
        FileInfo fileInfo2 = new FileInfo(1,
                "http://appdl.hicloud.com/dl/appdl/application/apk/3f/3fc7e360842f44baa541f2e35e7baf3d/com.sina.weibo.1901311518.apk?sign=portal@portal1549717710273&source=portalsite&uid=null",
                "微博.apk", 0);
        FileInfo fileInfo3 = new FileInfo(2,
                "http://appdl.hicloud.com/dl/appdl/application/apk/64/647e95152dc447c288176cfd727982cc/com.sohu.tv.1809222117.apk?sign=portal@portal1548923679242&source=portalsite&uid=null",
                "搜狐视频.apk", 0);
        FileInfo fileInfo4 = new FileInfo(3,
                "http://appdl.hicloud.com/dl/appdl/application/apk/7c/7ce70b42d53441cb85a980399681ddc3/com.ss.android.ugc.live.1901251402.apk?sign=portal@portal1548923679260&source=portalsite&uid=null",
                "火山小视频.apk", 0);
        FileInfo fileInfo5 = new FileInfo(4,
                "http://appdl.hicloud.com/dl/appdl/application/apk/4a/4a0be6b98d5e4c16922cacc4d37def8f/com.imangi.templerun2.1901221827.apk?sign=portal@portal1548923679549&source=portalsite&uid=null",
                "神庙逃亡.apk", 0);
        fileInfoList.add(fileInfo1);
        fileInfoList.add(fileInfo2);
        fileInfoList.add(fileInfo3);
        fileInfoList.add(fileInfo4);
        fileInfoList.add(fileInfo5);

        selectId = new ArrayList<>();
        DaoManager manager=new DaoManager().init(this);
        DaoSession daoSession=manager.getDaoSession();
        ThreadInfoDao dao=daoSession.getThreadInfoDao();
        List<ThreadInfo> allThreads = dao.queryBuilder().list();
        if (allThreads != null || allThreads.size() > 0) {
            for (int i = 0; i < allThreads.size(); i++) {
                long id = allThreads.get(i).getId();
                selectId.add(id);
            }
        } else {
            selectId.add(-1L);
        }

    }

    private static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }


    private void initView() {
        lv = (ListView) findViewById(R.id.lv);
        FileListAdapter fileListAdapter = new FileListAdapter();
        fileListAdapter.setFileInfoList(fileInfoList, selectId);
        lv.setAdapter(fileListAdapter);
    }
}
