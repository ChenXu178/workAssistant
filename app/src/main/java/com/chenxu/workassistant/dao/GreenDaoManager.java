package com.chenxu.workassistant.dao;

import com.chenxu.workassistant.config.Application;

public class GreenDaoManager {
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;

    private GreenDaoManager() {
        init();
    }

    /**
     * 静态内部类，实例化对象使用
     */
    private static class SingleInstanceHolder {
        private static final GreenDaoManager INSTANCE = new GreenDaoManager();
    }

    /**
     * 对外唯一实例的接口
     *
     * @return
     */
    public static GreenDaoManager getInstance() {
        return SingleInstanceHolder.INSTANCE;
    }

    /**
     * 初始化数据
     */
    private void init() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(Application.getInstance(), "workassistant.db");
        mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoMaster getDaoMaster() {
        return mDaoMaster;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public DaoSession getNewSession() {
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }

    public static HistoryEntityDao getHistoryEntityDao(){
        if (mDaoSession != null){
            return mDaoSession.getHistoryEntityDao();
        }else {
            return getInstance().getDaoSession().getHistoryEntityDao();
        }
    }

    public static CollectionEntityDao getCollectionEntityDao(){
        if (mDaoSession != null){
            return mDaoSession.getCollectionEntityDao();
        }else {
            return getInstance().getDaoSession().getCollectionEntityDao();
        }
    }

    public static EnclosureEntityDao getEnclosureEntityDao(){
        if (mDaoSession != null){
            return mDaoSession.getEnclosureEntityDao();
        }else {
            return getInstance().getDaoSession().getEnclosureEntityDao();
        }
    }

    public static SearchEntityDao getSearchEntityDao(){
        if (mDaoSession != null){
            return mDaoSession.getSearchEntityDao();
        }else {
            return getInstance().getDaoSession().getSearchEntityDao();
        }
    }
}
