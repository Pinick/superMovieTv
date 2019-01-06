package com.zmovie.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.zmovie.app.display.DisplayAdaptive;


/**
 * Created by owen on 2017/8/8.
 * 
 */

public class APP extends Application {
    final static float DESIGN_WIDTH = 1280; //绘制页面时参照的设计图宽度
    
    @Override
    public void onCreate() {
        super.onCreate();
        DisplayAdaptive.getInstance().init(DESIGN_WIDTH, this);

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DisplayAdaptive.getInstance().release();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
