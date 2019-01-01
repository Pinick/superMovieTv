package com.owen.tvrecyclerview.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.owen.tvrecyclerview.utils.Loger;

/**
 * Created by owen on 2017/6/16.
 */

public class MetroTitleItemDecoration extends RecyclerView.ItemDecoration {
    private final SparseArray<View> mTitleViews = new SparseArray<>();
    private final Rect mTempRect = new Rect();
    private Adapter mAdapter;
    private MetroGridLayoutManager.LayoutParams mTempItemLp;
    private View mTempTitleView;
    private int mTempSectionIndex = -1;
    
    public MetroTitleItemDecoration(Adapter adapter) {
        this.mAdapter = adapter;
    }
    
    private void createAndMeasureTitleView(RecyclerView parent) {
        if(mAdapter == null)
            return;
        
        mTempTitleView = mAdapter.getTitleView(mTempItemLp.sectionIndex, parent);
        if(null != mTempTitleView) {
            ViewGroup.LayoutParams lp = mTempTitleView.getLayoutParams();
            if (null == lp) {
                lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mTempTitleView.setLayoutParams(lp);
            }

            final int left = parent.getPaddingLeft() + mTempItemLp.leftMargin;
            final int right = parent.getPaddingRight() + mTempItemLp.rightMargin;
            final int top = parent.getPaddingTop() + mTempItemLp.topMargin;
            final int bottom = parent.getPaddingBottom() + mTempItemLp.bottomMargin;
            final int width = lp.width < 0 ? parent.getMeasuredWidth() - left - right : lp.width;
            final int height = lp.height < 0 ? parent.getMeasuredHeight() - top - bottom : lp.height;

            final int widthSpec = View.MeasureSpec.makeMeasureSpec(width,
                    lp.width == ViewGroup.LayoutParams.WRAP_CONTENT ? View.MeasureSpec.AT_MOST : View.MeasureSpec.EXACTLY);
            final int heightSpec = View.MeasureSpec.makeMeasureSpec(height,
                    lp.width == ViewGroup.LayoutParams.WRAP_CONTENT ? View.MeasureSpec.AT_MOST : View.MeasureSpec.EXACTLY);

        /*//测量处理
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
         int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(),View.MeasureSpec.UNSPECIFIED);
        //计算宽高
        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft() + parent.getPaddingRight(),lp.width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec, parent.getPaddingTop() + parent.getPaddingBottom(), lp.height);*/

            //设置宽高
            mTempTitleView.measure(widthSpec, heightSpec);
            mTitleViews.put(mTempItemLp.sectionIndex, mTempTitleView);
        }
    }
    
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        mTempItemLp = (MetroGridLayoutManager.LayoutParams) view.getLayoutParams();
        Loger.i("ViewAdapterPosition="+mTempItemLp.getViewAdapterPosition()+" isSectionStart="+mTempItemLp.isSectionStart 
                + " Decorated Top="+parent.getLayoutManager().getTopDecorationHeight(view));
        if(mTempItemLp.isSectionStart) {
            mTempTitleView = mTitleViews.get(mTempItemLp.sectionIndex);
            if(null == mTempTitleView) {
                createAndMeasureTitleView(parent);
            }
            if(null != mTempTitleView) {
                final boolean isVertical = ((MetroGridLayoutManager) parent.getLayoutManager()).isVertical();
                outRect.set(isVertical ? 0 : mTempTitleView.getMeasuredWidth(), isVertical ? mTempTitleView.getMeasuredHeight() : 0, 0, 0);
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int count = parent.getChildCount();
        for(int i = 0; i < count; i++) {
            mTempItemLp = (MetroGridLayoutManager.LayoutParams) parent.getChildAt(i).getLayoutParams();
            if(mTempSectionIndex != mTempItemLp.sectionIndex && mTempItemLp.isSectionStart) {
                mTempTitleView = mTitleViews.get(mTempItemLp.sectionIndex);
                if(null != mTempTitleView) {
                    final View itemView = parent.getChildAt(i);
                    parent.getLayoutManager().getDecoratedBoundsWithMargins(itemView, mTempRect);
                    final int left = itemView.getLeft();
                    final int top = mTempRect.top;
                    final int right = left + mTempTitleView.getMeasuredWidth();
                    final int bottom = top + mTempTitleView.getMeasuredHeight();
                    c.save();
                    mTempTitleView.layout(left, top, right, bottom);
                    c.translate(left, top);
                    mTempTitleView.draw(c);
                    c.restore();
                    Loger.i("mTitleView.draw ... sectionIndex="+mTempItemLp.sectionIndex);
                }
                mTempSectionIndex = mTempItemLp.sectionIndex;
            }
        }
        mTempSectionIndex = -1;
        mTempItemLp = null;
        mTempTitleView = null;
    }
    
    public static interface Adapter {
        View getTitleView(int index, RecyclerView parent);
    }
}
