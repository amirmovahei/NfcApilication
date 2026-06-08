package com.example.amtehan;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // تنظیم MyExceptionHandler
        Thread.setDefaultUncaughtExceptionHandler(
                new MyExceptionHandler(this, error.class)
        );
    }
}
