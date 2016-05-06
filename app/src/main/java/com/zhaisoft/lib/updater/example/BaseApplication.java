package com.zhaisoft.lib.updater.example;

import android.app.Application;

import com.zhaisoft.lib.updater.exception.CrashWoodpecker;

public class BaseApplication extends Application {
    String TAG = "BaseApplication";
    private static BaseApplication instance;


    public void onCreate() {
        super.onCreate();
        instance = this;
        //打开日志开关

        CrashWoodpecker.init(this);


    }


}
