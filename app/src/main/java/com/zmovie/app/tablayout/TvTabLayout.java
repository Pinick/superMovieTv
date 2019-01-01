package com.zmovie.app.tablayout;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by owen on 16/8/30.
 */
public class TvTabLayout extends TabLayout {
    private static final String LOGTAG = TvTabLayout.class.getSimpleName();
    
    private float mScaleValue = 1f;

    public TvTabLayout(Context context) {
        this(context, null);
    }

    public TvTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScaleValue(@FloatRange(from = 1.0) float value) {
        mScaleValue = value;
    }

    @Override
    protected void onTabSelected(@NonNull Tab tab) {
        ViewPropertyAnimator animator = tab.getView().animate();
        if(mScaleValue > 1) {
            animator.scaleX(mScaleValue).scaleY(mScaleValue)
                    .setDuration(500)
                    .start();
            return;
        }
        animator.scaleX(1.2f).scaleY(1.2f)
                .translationY((getHeight() - tab.getView().getHeight()) / 2)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(700)
                .start();
    }

    @Override
    protected void onTabUnselected(@NonNull Tab tab) {
        ViewPropertyAnimator animator = tab.getView().animate();
        if(mScaleValue > 1) {
            animator.scaleX(1).scaleY(1)
                    .setDuration(500)
                    .start();
            return;
        }
        animator.scaleX(1f).scaleY(1f)
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(500)
                .start();
    }

}

