package com.example.testing.demo;

import android.app.Application;
import android.content.Context;

/**
 * Created by win7 on 2017/4/20.
 */

public class App extends Application {


    public   static  Context getContext() {
        return context;
    }

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

    }


}
