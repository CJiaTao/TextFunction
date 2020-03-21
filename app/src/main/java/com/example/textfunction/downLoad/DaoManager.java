package com.example.textfunction.downLoad;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.textfunction.downLoad.entity.ThreadInfo;
import com.example.textfunction.downLoad.greendao.DaoMaster;
import com.example.textfunction.downLoad.greendao.DaoSession;
import com.example.textfunction.downLoad.greendao.ThreadInfoDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

//用于创建数据库、创建数据库表
public class DaoManager {
    private static final String TAG = DaoManager.class.getSimpleName();
    private static final String DB_NAME = "download.db";
    private Context context;
    //多线程中被共享的使用volatile关键字修饰
    private volatile static DaoManager manager = new DaoManager();
    private static DaoMaster daoMaster;
    private static DaoMaster.DevOpenHelper helper;
    private static DaoSession daoSession;

    /**
     * 单例模式获得操作数据库对象
     *
     * @return
     */
    public static DaoManager getInstance() {
        return manager;
    }

    public DaoManager init(Context context) {
        this.context = context;
        return manager;
    }

    /**
     * 判断是否存在数据库，如果没有则创建
     *
     * @return
     */
    public DaoMaster getDaoMaster() {
        if (daoMaster == null) {
//            DaoMaster.DevOpenHelper helper =new DaoMaster.DevOpenHelper(context,DB_NAME,null);
//            SQLiteDatabase db = helper.getWritableDatabase();
//            daoMaster =new DaoMaster(db);

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 完成对数据库的添加/删除/修改/查询等操作
     *
     * @return
     */
    public DaoSession getDaoSession() {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    /**
     * 打开输出日记，默认关闭
     */
    public void setDebug() {
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    public void closeConnection() {
        closeHelper();
        closeDaoSession();
    }

    private void closeDaoSession() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

    private void closeHelper() {
        if (helper != null) {
            helper.close();
            helper = null;
        }
    }


    private void operatorDao(Context context) {
        //创建数据库
        DaoMaster.DevOpenHelper daoHelper = new DaoMaster.DevOpenHelper(context, "Down.db", null);
        //获取可写数据库
        SQLiteDatabase db = daoHelper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        DaoSession daoSession = daoMaster.newSession();
        //使用daoSession操作数据库，实现增删改查
        ThreadInfoDao infoDao = daoSession.getThreadInfoDao();

        ThreadInfo info = new ThreadInfo(1, "url", 1000, 1, "111");
//        public ThreadInfo(long id, String url, String fileName, int state, String title)
        //操作数据库
        //增
        infoDao.insert(info);
        //删
        infoDao.deleteByKey(info.getId());
        infoDao.queryBuilder().where(ThreadInfoDao.Properties.Url.eq("111"))
                .buildDelete().executeDeleteWithoutDetachingEntities();//对应字段值 删除
        //改
        infoDao.update(info);
        //查
        List<ThreadInfo> infos = infoDao.loadAll();
        infos = infoDao.queryBuilder().where(ThreadInfoDao.Properties.Url.eq("11"))
                .orderDesc(ThreadInfoDao.Properties.State).list();
    }
}
