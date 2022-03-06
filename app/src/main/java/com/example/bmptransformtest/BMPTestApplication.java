package com.example.bmptransformtest;

import android.app.Application;
import android.content.Context;

public class BMPTestApplication extends Application {

    //全局唯一的context
    private static BMPTestApplication application;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static BMPTestApplication getApplication() {
        return application;
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

}
