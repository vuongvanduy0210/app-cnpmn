package com.kma_kit.smarthome.ui;

import android.app.Application;
import android.content.Context;

public class SmartHomeApplication extends Application {

    private static SmartHomeApplication instance;

    public static synchronized SmartHomeApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

}
