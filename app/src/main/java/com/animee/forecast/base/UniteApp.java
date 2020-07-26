package com.animee.forecast.base;

import android.app.Application;

import com.animee.forecast.db.DBManager;

import org.xutils.x;

public class UniteApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化XUtils
        x.Ext.init(this);
        //初始化数据库
        DBManager.initDB(this);
    }
}
