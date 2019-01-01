package com.zmovie.app;

import android.app.Application;

import com.zmovie.app.display.DisplayAdaptive;


/**
 * Created by owen on 2017/8/8.
 * 
 */

public class ExampleApp extends Application {
    final static float DESIGN_WIDTH = 1280; //绘制页面时参照的设计图宽度
    
    @Override
    public void onCreate() {
        super.onCreate();
        DisplayAdaptive.getInstance().init(DESIGN_WIDTH, this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DisplayAdaptive.getInstance().release();
    }
}
