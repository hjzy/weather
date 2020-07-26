package com.animee.forecast.base;

import android.app.Application;
import android.content.Context;

/**
 * 用于获取上下文，使用见Android第一行代码
 */

public class MyApplication  extends Application {
    private static Context context;
    @Override
    public void onCreate() {
    context=getApplicationContext();
        super.onCreate();
    }
    public static Context getContext(){
        return context;
    }

}
