package com.zmovie.app.display;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by owen on 2017/8/9.
 * 适配原理：
 * 系统进行长度计算的收口为TypedValue中的applyDimension函数，传入单位与value将其计算为对应的px数值。
 * px,dp与sp都是平时常用的单位，而pt,in与mm几乎没有看见过，从这些不常见的单位下手正好可以不影响其他常用的单位。
 * 
 * 适配的目标：
 * 完全按照设计图上标注的尺寸来编写页面，所编写的页面在所有大小与分辨率的屏幕上都表现一致，
 * 即控件在所有屏幕上相对于整个屏幕的相对大小都一致（看起来只是将设计图缩放至屏幕大小）。
 *
 * 核心：使用冷门的pt作为长度单位。
 *
 * 绘制：编写xml时完全对照设计稿上的尺寸来编写，只不过单位换为pt。
 *
 * 动态：需要在代码中动态转换成px时使用 {@link DisplayAdaptive#toLocalPx} --> (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, value, metrics))。
 *
 * 预览：实时预览时绘制页面是很重要的一个环节。
 * 以1280x720的设计图为例，为了实现于正常绘制时一样的预览功能，创建一个长为1280，宽为720的设备作为预览，
 * 经换算约为20.39英寸{@link DisplayAdaptive#screenSize ((sqrt(1280^2+720^2))/72)}。预览时选择这个设备即可。
 */

public class DisplayAdaptive {
    
    private static class DisplayAdaptiveHolder {
        private static final DisplayAdaptive INSTANCE = new DisplayAdaptive();
    }
    
    private DisplayAdaptive() {}
    
    public static DisplayAdaptive getInstance() {
        return DisplayAdaptiveHolder.INSTANCE;
    }
    
    private float mBaseWidth;
    private Application mApplication;
    private IComponentCallbacks mIComponentCallbacks;
    
    public void init(float baseWidth, Application app) {
        mBaseWidth = baseWidth;
        mApplication = app;
        mIComponentCallbacks = new IComponentCallbacks(this);
        mApplication.registerComponentCallbacks(mIComponentCallbacks);
        resetDensity();
    }
    
    public void release() {
        mApplication.unregisterComponentCallbacks(mIComponentCallbacks);
        mIComponentCallbacks = null;
        mApplication = null;
    }
    
    public float toLocalPx(float pt) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, pt, mApplication.getResources().getDisplayMetrics());
    }

    private void resetDensity(){
        Point size = new Point();
        ((WindowManager)mApplication.getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getSize(size);

        mApplication.getResources().getDisplayMetrics().xdpi = size.x / mBaseWidth * 72f;
    }
    
    public static double screenSize(int w, int h) {
        return (Math.sqrt(w * w + h * h)) / 72d;
    }
    
    
    private static class IComponentCallbacks implements ComponentCallbacks {
        private WeakReference<DisplayAdaptive> mDisplayUtilWeakReference;
        
        public IComponentCallbacks(DisplayAdaptive displayAdaptive) {
            mDisplayUtilWeakReference = new WeakReference<>(displayAdaptive);
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            if(null != mDisplayUtilWeakReference && null != mDisplayUtilWeakReference.get()) {
                mDisplayUtilWeakReference.get().resetDensity();
            }
        }

        @Override
        public void onLowMemory() {

        }
    }
}
