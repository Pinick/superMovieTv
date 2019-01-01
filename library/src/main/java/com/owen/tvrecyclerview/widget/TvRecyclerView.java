/*
 * Copyright (C) 2014 Lucas Rocha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.owen.tvrecyclerview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.owen.tvrecyclerview.R;
import com.owen.tvrecyclerview.TwoWayLayoutManager;
import com.owen.tvrecyclerview.utils.Loger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TvRecyclerView extends RecyclerView implements View.OnClickListener, View.OnFocusChangeListener{
    private static final int DEFAULT_LOAD_MORE_BEFOREHAND_COUNT = 4;
    private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE =
            new Class[]{Context.class, AttributeSet.class, int.class};

    public int mVerticalSpacingWithMargins = 0;
    public int mHorizontalSpacingWithMargins = 0;
    private int mOldVerticalSpacingWithMargins = 0;
    private int mOldHorizontalSpacingWithMargins = 0;
    
    private int mSelectedItemOffsetStart;
    private int mSelectedItemOffsetEnd;
    private boolean mSelectedItemCentered;
    
    private boolean mIsSelectFirstVisiblePosition;
    private boolean mIsMenu;

    private boolean mHasMoreData = true;
    private boolean mLoadingMore = false;
    private int mLoadMoreBeforehandCount;
    
    private int mSelectedPosition = 0;
    private boolean mHasFocusWithPrevious = false;

    private OnItemListener mOnItemListener;
    private OnInBorderKeyEventListener mOnInBorderKeyEventListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    private final Rect mTempRect = new Rect();
    private final IRecyclerViewDataObserver mDataObserver = new IRecyclerViewDataObserver();
    private boolean mShouldReverseLayout = true;
    private boolean mOptimizeLayout;
    
    protected int mScrollX, mScrollY;

    public TvRecyclerView(Context context) {
        this(context, null);
    }

    public TvRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        init(context);
        
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TvRecyclerView, defStyle, 0);

        final String name = a.getString(R.styleable.TvRecyclerView_tv_layoutManager);
        if (!TextUtils.isEmpty(name)) {
            createLayoutManager(context, name, attrs, defStyle);
        }
        mSelectedItemCentered = a.getBoolean(R.styleable.TvRecyclerView_tv_selectedItemIsCentered, false);
        mIsMenu = a.getBoolean(R.styleable.TvRecyclerView_tv_isMenu, false);
        mIsSelectFirstVisiblePosition = a.getBoolean(R.styleable.TvRecyclerView_tv_isSelectFirstVisiblePosition, false);
        mLoadMoreBeforehandCount = a.getInt(R.styleable.TvRecyclerView_tv_loadMoreBeforehandCount, DEFAULT_LOAD_MORE_BEFOREHAND_COUNT);
        mSelectedItemOffsetStart = a.getDimensionPixelOffset(R.styleable.TvRecyclerView_tv_selectedItemOffsetStart, 0);
        mSelectedItemOffsetEnd = a.getDimensionPixelOffset(R.styleable.TvRecyclerView_tv_selectedItemOffsetEnd, 0);
        mOptimizeLayout = a.getBoolean(R.styleable.TvRecyclerView_tv_optimizeLayout, false);
        
        a.recycle();
    }

    private void init(Context context){
        setChildrenDrawingOrderEnabled(true);
        setWillNotDraw(true); // 自身不作onDraw处理
        setHasFixedSize(true);
        setOverScrollMode(View.OVER_SCROLL_NEVER);

        setClipChildren(false);
        setClipToPadding(false);

        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        
        //修复adapter.notifyItemChanged时焦点闪烁的问题
        ((SimpleItemAnimator)getItemAnimator()).setSupportsChangeAnimations(false);
    }
    
    /**
     * Instantiate and set a LayoutManager, if specified in the attributes.
     */
    private void createLayoutManager(Context context, String className, AttributeSet attrs,
                                     int defStyleAttr) {
        if (className != null) {
            className = className.trim();
            if (className.length() != 0) {  // Can't use isEmpty since it was added in API 9.
                className = getFullClassName(context, className);
                try {
                    ClassLoader classLoader;
                    if (isInEditMode()) {
                        // Stupid layoutlib cannot handle simple class loaders.
                        classLoader = this.getClass().getClassLoader();
                    } else {
                        classLoader = context.getClassLoader();
                    }
                    Class<? extends LayoutManager> layoutManagerClass =
                            classLoader.loadClass(className).asSubclass(LayoutManager.class);
                    Constructor<? extends LayoutManager> constructor;
                    Object[] constructorArgs = null;
                    try {
                        constructor = layoutManagerClass
                                .getConstructor(LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE);
                        constructorArgs = new Object[]{context, attrs, defStyleAttr};
                    } catch (NoSuchMethodException e) {
                        try {
                            constructor = layoutManagerClass.getConstructor();
                        } catch (NoSuchMethodException e1) {
                            e1.initCause(e);
                            throw new IllegalStateException(attrs.getPositionDescription() +
                                    ": Error creating LayoutManager " + className, e1);
                        }
                    }
                    constructor.setAccessible(true);
                    setLayoutManager(constructor.newInstance(constructorArgs));
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Unable to find LayoutManager " + className, e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Could not instantiate the LayoutManager: " + className, e);
                } catch (InstantiationException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Could not instantiate the LayoutManager: " + className, e);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Cannot access non-public constructor " + className, e);
                } catch (ClassCastException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Class is not a LayoutManager " + className, e);
                }
            }
        }
    }

    private String getFullClassName(Context context, String className) {
        if (className.charAt(0) == '.') {
            return context.getPackageName() + className;
        }
        if (className.contains(".")) {
            return className;
        }
        return TvRecyclerView.class.getPackage().getName() + '.' + className;
    }


    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectFirstVisiblePosition(boolean selectFirstVisiblePosition) {
        mIsSelectFirstVisiblePosition = selectFirstVisiblePosition;
    }

    public boolean isSelectFirstVisiblePosition() {
        return mIsSelectFirstVisiblePosition;
    }

    public void setMenu(boolean menu) {
        mIsMenu = menu;
    }

    public boolean isMenu() {
        return mIsMenu;
    }

    public void setLoadMoreBeforehandCount(int loadMoreBeforehandCount) {
        mLoadMoreBeforehandCount = loadMoreBeforehandCount;
    }

    public int getLoadMoreBeforehandCount() {
        return mLoadMoreBeforehandCount;
    }

    public boolean isHasMoreData() {
        return mHasMoreData;
    }

    public void setHasMoreData(boolean hasMoreData) {
        mHasMoreData = hasMoreData;
    }

    /**
     * 设置选中的Item距离开始或结束的偏移量；
     * 与滚动方向有关；
     * 与setSelectedItemAtCentered()方法二选一
     * @param offsetStart
     * @param offsetEnd
     */
    public void setSelectedItemOffset(int offsetStart, int offsetEnd) {
        this.mSelectedItemOffsetStart = offsetStart;
        this.mSelectedItemOffsetEnd = offsetEnd;
    }

    public int getSelectedItemOffsetStart() {
        return mSelectedItemOffsetStart;
    }

    public int getSelectedItemOffsetEnd() {
        return mSelectedItemOffsetEnd;
    }

    /**
     * 设置选中的Item居中；
     * 与setSelectedItemOffset()方法二选一
     * @param isCentered
     */
    public void setSelectedItemAtCentered(boolean isCentered) {
        this.mSelectedItemCentered = isCentered;
    }

    public boolean isSelectedItemCentered() {
        return mSelectedItemCentered;
    }

    public void setLoadingMore(boolean loadingMore) {
        mLoadingMore = loadingMore;
    }

    public boolean isLoadingMore() {
        return mLoadingMore;
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final long startMillis = System.currentTimeMillis();
        mHasFocusWithPrevious = mHasFocusWithPrevious || hasFocus();
        Loger.i("onLayout...start hasFocus()="+mHasFocusWithPrevious + " changed="+changed + " ,mShouldReverseLayout="+ mShouldReverseLayout);

        final boolean requestLayout = !mOptimizeLayout || (changed || mShouldReverseLayout);
        final boolean layoutAfterFocus;
        if(requestLayout) {
            super.onLayout(changed, l, t, r, b);
            mShouldReverseLayout = false;
        
            layoutAfterFocus = hasFocus();
            if(!layoutAfterFocus) {
                if(mSelectedPosition < 0) {
                    mSelectedPosition = getFirstVisiblePosition();
                } else if(mSelectedPosition >= getItemCount()) {
                    mSelectedPosition = getLastVisiblePosition();
                }
                if(mHasFocusWithPrevious && getPreserveFocusAfterLayout()) {
                    requestDefaultFocus();
                } else {
                    setItemActivated(mSelectedPosition);
                }
            }
        } else {
            layoutAfterFocus = hasFocus();
        }
        
        mHasFocusWithPrevious = false;
        Loger.i("onLayout...end layoutAfterFocus="+layoutAfterFocus+". used time " + (System.currentTimeMillis() - startMillis) / 1000f + "s");
    }
    
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        long startMillis = System.currentTimeMillis();
        Loger.i("onMeasure...start");
        super.onMeasure(widthSpec, heightSpec);
        Loger.i("onMeasure...end. used time " + (System.currentTimeMillis() - startMillis) / 1000f + "s");
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    public int getItemCount() {
        if(null != getAdapter()) {
            return getAdapter().getItemCount();
        }
        return 0;
    }

    @Override
    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        if(null == adapter) return;
        resetAdapter(adapter);
        super.swapAdapter(adapter, removeAndRecycleExistingViews);
    }

    @Override
    public void setAdapter(final Adapter adapter) {
        if(null == adapter) return;
        resetAdapter(adapter);
        super.setAdapter(adapter);
    }
    
    private void resetAdapter(Adapter newAdapter) {
        final Adapter oldAdapter = getAdapter();
        if(null != oldAdapter) {
            oldAdapter.unregisterAdapterDataObserver(mDataObserver);
            mShouldReverseLayout = true;
        }
        newAdapter.registerAdapterDataObserver(mDataObserver);
        
        //修复重新setAdapter后第一条被遮挡的问题
        View view = getChildAt(0);
        if(null != view && null != oldAdapter) {
            mHasFocusWithPrevious = hasFocus();
            int start = getLayoutManager().canScrollVertically() ? getLayoutManager().getDecoratedTop(view) : getLayoutManager().getDecoratedLeft(view);
            start -= getLayoutManager().canScrollVertically() ? getPaddingTop() : getPaddingLeft();
            scrollBy(start, start);
        } else {
            mSelectedPosition = 0;
        }
    }
    
    @Override
    public void onClick(View itemView) {
        if(null != mOnItemListener && this != itemView) {
            mOnItemListener.onItemClick(TvRecyclerView.this, itemView, getChildAdapterPosition(itemView));
        }
    }

    @Override
    public void onFocusChange(final View itemView, boolean hasFocus) {
        if(null != itemView && itemView != this) {
            final int position = getChildAdapterPosition(itemView);
            itemView.setSelected(hasFocus);
            if (hasFocus) {
                mSelectedPosition = position;
                if(mIsMenu && itemView.isActivated()) {
                    itemView.setActivated(false);
                }
                if(null != mOnItemListener)
                    mOnItemListener.onItemSelected(TvRecyclerView.this, itemView, position);
            } else {
                itemView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!hasFocus()) {
                            if(mIsMenu) {
                                // 解决选中后无状态表达的问题，selector中使用activated代表选中后焦点移走
                                itemView.setActivated(true);
                            }
                            //模拟TvRecyclerView失去焦点
                            onFocusChanged(false, FOCUS_DOWN, null);
                        }
                    }
                }, 6);
                if(null != mOnItemListener)
                    mOnItemListener.onItemPreSelected(TvRecyclerView.this, itemView, position);
            }
        }
    }

    /*@Override
    public void requestChildFocus(View child, View focused) {
        Loger.i("requestChildFocus... hasFocus="+hasFocus());
        
        if(getFocusedChild() == null) {
            //模拟TvRecyclerView获取焦点
            onFocusChanged(true, FOCUS_DOWN, null);
        }
        super.requestChildFocus(child, focused);
    }*/

    /*@Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        Loger.i("addFocusables3... direction="+direction+" focusableMode="+focusableMode+" hasFocus="+hasFocus());
        
        if(getChildCount() > 0 && getFocusedChild() == null) {
            final View view;
            if(mIsMenu || !mIsSelectFirstVisiblePosition) {
                view = getChildAt(mSelectedPosition - getFirstVisiblePosition());
            } else {
                view = getChildAt(0);
            }
            if(null != view) {
                views.add(view);
                return;
            }
        }
        super.addFocusables(views, direction, focusableMode);
    }*/

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        Loger.i("direction..."+direction);
        if(null == getFocusedChild()) {
            //请求默认焦点
            requestDefaultFocus();
        }
        return false;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        Loger.i("gainFocus="+gainFocus + " hasFocus="+hasFocus()+" direction="+direction);
        if(gainFocus) {
            setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        } else {
            setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    public void requestDefaultFocus() {
        if(mIsMenu || !mIsSelectFirstVisiblePosition) {
            setSelection(mSelectedPosition);
        } else {
            setSelection(getFirstVisiblePosition());
        }
    }
    
    public void setSelection(int position) {
        if(null == getAdapter() || position < 0 || position >= getItemCount()) {
            return;
        }
        
        View view = getChildAt(position - getFirstVisiblePosition());
        if(null != view) {
            if(!hasFocus()) {
                //模拟TvRecyclerView获取焦点
                onFocusChanged(true, FOCUS_DOWN, null);
            }
            view.requestFocus();
        }
        else {
            TvSmoothScroller scroller = new TvSmoothScroller(getContext(), true, true, mSelectedItemOffsetStart);
            scroller.setTargetPosition(position);
            getLayoutManager().startSmoothScroll(scroller);
        }
    }

    private int getFreeHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }
    
    private int getFreeWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    public void onScrollStateChanged(int state) {
        if(state == SCROLL_STATE_IDLE) {
            setScrollValue(0, 0);

            // 加载更多回调
            if(null != mOnLoadMoreListener && !mLoadingMore && mHasMoreData) {
                if(getLastVisiblePosition() >= getAdapter().getItemCount() - (1 + mLoadMoreBeforehandCount)) {
                    mHasMoreData = mOnLoadMoreListener.onLoadMore();
                }
            }
        }
        super.onScrollStateChanged(state);
    }

    private Point mScrollPoint = new Point();
    void setScrollValue(int x, int y) {
        if(x != 0 || y != 0) {
            mScrollPoint.set(x, y);
            setTag(mScrollPoint);
        } else {
            setTag(null);
        }
    }
    
    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        if(null == child)
            return false;
        
        if(mSelectedItemCentered) {
            getDecoratedBoundsWithMargins(child, mTempRect);
            mSelectedItemOffsetStart = (getLayoutManager().canScrollHorizontally() ? (getFreeWidth() - mTempRect.width()) 
                    : (getFreeHeight() - mTempRect.height())) / 2;
            mSelectedItemOffsetEnd = mSelectedItemOffsetStart;
        }

        int[] scrollAmount = getChildRectangleOnScreenScrollAmount2(child, rect, mSelectedItemOffsetStart, mSelectedItemOffsetEnd);
        int dx = scrollAmount[0];
        int dy = scrollAmount[1];
        Loger.i("dx="+dx+" dy="+dy);

        smoothScrollBy(dx, dy);

        if (dx != 0 || dy != 0) {
            return true;
        }

        // 重绘是为了选中item置顶，具体请参考getChildDrawingOrder方法
        postInvalidate();

        return false;
    }

    @Override
    public void smoothScrollBy(int dx, int dy, Interpolator interpolator) {
        setScrollValue(dx, dy);
        super.smoothScrollBy(dx, dy, interpolator);
    }

    private boolean isFocusedChildVisibleAfterScrolling(RecyclerView parent, int dx, int dy) {
        final View focusedChild = parent.getFocusedChild();
        if (focusedChild == null) {
            return false;
        }
        final int parentLeft = getPaddingLeft() + mHorizontalSpacingWithMargins / 2;
        final int parentTop = getPaddingTop() + mVerticalSpacingWithMargins / 2;
        final int parentRight = getWidth() - (getPaddingRight() + mHorizontalSpacingWithMargins / 2);
        final int parentBottom = getHeight() - (getPaddingBottom() + + mVerticalSpacingWithMargins / 2);
        final Rect bounds = mTempRect;
        getDecoratedBoundsWithMargins(focusedChild, bounds);

        if (bounds.left - dx >= parentRight || bounds.right - dx <= parentLeft
                || bounds.top - dy >= parentBottom || bounds.bottom - dy <= parentTop) {
            return false;
        }
        return true;
    }

    private int[] getChildRectangleOnScreenScrollAmount1(View child, Rect rect, int offsetStart, int offsetEnd) {
        int dx = 0;
        int dy = 0;

        if(getLayoutManager().canScrollHorizontally()) {
            final int parentLeft = getPaddingLeft() + mHorizontalSpacingWithMargins / 2;
            final int parentRight = getWidth() - (getPaddingRight() + mHorizontalSpacingWithMargins / 2);
            final int childLeft = child.getLeft() + rect.left - child.getScrollX();
            final int childRight = childLeft + rect.width();
            final int offScreenLeft = Math.min(0, childLeft - parentLeft - offsetStart);
            final int offScreenRight = Math.max(0, childRight - parentRight + offsetEnd);
            // Favor the "start" layout direction over the end when bringing one side or the other
            // of a large rect into view. If we decide to bring in end because start is already
            // visible, limit the scroll such that start won't go out of bounds.
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                dx = offScreenRight != 0 ? offScreenRight
                        : Math.max(offScreenLeft, childRight - parentRight);
            } else {
                dx = offScreenLeft != 0 ? offScreenLeft
                        : Math.min(childLeft - parentLeft, offScreenRight);
            }
            if(!ViewCompat.canScrollHorizontally(this, dx)) {
                Loger.i("cannotScrollHorizontally...");
                dx = 0;
            }
        }

        if(getLayoutManager().canScrollVertically()) {
            final int parentTop = getPaddingTop() + mVerticalSpacingWithMargins / 2;
            final int parentBottom = getHeight() - (getPaddingBottom() + mVerticalSpacingWithMargins / 2);
            final int childTop = child.getTop() + rect.top - child.getScrollY();
            final int childBottom = childTop + rect.height();
            final int offScreenTop = Math.min(0, childTop - parentTop - offsetStart);
            final int offScreenBottom = Math.max(0, childBottom - parentBottom + offsetEnd);
            // Favor bringing the top into view over the bottom. If top is already visible and
            // we should scroll to make bottom visible, make sure top does not go out of bounds.
            dy = offScreenTop != 0 ? offScreenTop : Math.min(childTop - parentTop, offScreenBottom);
            Loger.i("dy="+dy);
            if(!ViewCompat.canScrollVertically(this, dy)) {
                Loger.i("cannotScrollVertically...");
                dy = 0;
            }
        }
        
        return new int[]{dx, dy};
    }

    /**
     * 判断当前是否还可以向前或后滚动
     */
    private boolean cannotScrollForwardOrBackward(View child, int dx, int dy) {
        if(dy != 0) {
            if(getFirstVisiblePosition() == 0 && dy < 0) {
                getDecoratedBoundsWithMargins(getChildAt(0), mTempRect);
                final int top = mTempRect.top - getPaddingTop();
                if (top == 0 || top == 1) {
                    return true;
                }
            }
            if((getLastVisiblePosition() + 1 == getAdapter().getItemCount()) && dy > 0) {
                getDecoratedBoundsWithMargins(getChildAt(getChildCount() - 1), mTempRect);
                final int bottom = mTempRect.bottom + getPaddingBottom() - getHeight();
                if (bottom == 0 || bottom == 1) {
                    return true;
                }
            }
        }
        
        if(dx != 0) {
            if(getFirstVisiblePosition() == 0 && dx < 0){
                getDecoratedBoundsWithMargins(getChildAt(0), mTempRect);
                final int left = mTempRect.left - getPaddingLeft();
                if (left == 0 || left == 1) {
                    return true;
                }
            }
            if((getLastVisiblePosition() + 1 == getAdapter().getItemCount()) && dx > 0) {
                getDecoratedBoundsWithMargins(getChildAt(getChildCount() - 1), mTempRect);
                final int right = mTempRect.right + getPaddingRight() - getWidth();
                if (right == 0 || right == 1) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private int[] getChildRectangleOnScreenScrollAmount2(View focusView, Rect rect, int offsetStart, int offsetEnd) {
                    //横向滚动
        int dx = 0;
        int dy = 0;

        getDecoratedBoundsWithMargins(focusView, mTempRect);
        
        if(getLayoutManager().canScrollHorizontally()) {
            final int right =
                    mTempRect.right
                    + getPaddingRight()
                    - getWidth();
            final int left =
                    mTempRect.left
                    - getPaddingLeft();

            dx = computeScrollOffset(left, right, offsetStart, offsetEnd);
        }

        //竖向滚动
        if(getLayoutManager().canScrollVertically()) {
            final int bottom =
                    mTempRect.bottom
                    + getPaddingBottom()
                    - getHeight();
            final int top =
                    mTempRect.top
                    - getPaddingTop();

            dy = computeScrollOffset(top, bottom, offsetStart, offsetEnd);
        }

        return new int[]{dx, dy};
    }
    
    private int computeScrollOffset(int start, int end, int offsetStart, int offsetEnd) {
        Loger.i("start="+start+" end="+end+" offsetStart="+offsetStart+" offsetEnd="+offsetEnd);
        
        // focusView超出下/右边界
        if (end > 0) {
            if(getLastVisiblePosition() != (getItemCount() - 1)) {
                return end + offsetEnd;
            } else {
                return end;
            }
        }
        // focusView超出上/左边界
        else if (start < 0) {
            if(getFirstVisiblePosition() != 0) {
                return start - offsetStart;
            } else {
                return start;
            }
        }
        // focusView未超出上/左边界，但边距小于指定offset
        else if(start > 0 && start < offsetStart
                && (ViewCompat.canScrollHorizontally(this, -1) || ViewCompat.canScrollVertically(this, -1))) {
            return start - offsetStart;
        }
        // focusView未超出下/右边界，但边距小于指定offset
        else if(Math.abs(end) > 0 && Math.abs(end) < offsetEnd
                && (ViewCompat.canScrollHorizontally(this, 1) || ViewCompat.canScrollVertically(this, 1))) {
            return offsetEnd - Math.abs(end);
        }
        
        return 0;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if(null != getLayoutManager() && getLayoutManager() instanceof  TwoWayLayoutManager) {
            final TwoWayLayoutManager lm = (TwoWayLayoutManager) getLayoutManager();
            return lm.canScrollHorizontally() && (direction > 0 ? !lm.cannotScrollBackward(direction) : !lm.cannotScrollForward(direction));
        }
        return super.canScrollHorizontally(direction);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        if(null != getLayoutManager() && getLayoutManager() instanceof  TwoWayLayoutManager) {
            final TwoWayLayoutManager lm = (TwoWayLayoutManager) getLayoutManager();
            return lm.canScrollVertically() && (direction > 0 ? !lm.cannotScrollBackward(direction) : !lm.cannotScrollForward(direction));
        }
        return super.canScrollVertically(direction);
    }

    /**
     * 通过Margins来设置布局的横纵间距；
     * (与addItemDecoration()方法可二选一)
     * @param verticalSpacing
     * @param horizontalSpacing
     */
    public void setSpacingWithMargins(int verticalSpacing, int horizontalSpacing) {
        if(this.mVerticalSpacingWithMargins != verticalSpacing || this.mHorizontalSpacingWithMargins != horizontalSpacing) {
            this.mOldVerticalSpacingWithMargins = this.mVerticalSpacingWithMargins;
            this.mOldHorizontalSpacingWithMargins = this.mHorizontalSpacingWithMargins;
            this.mVerticalSpacingWithMargins = verticalSpacing;
            this.mHorizontalSpacingWithMargins = horizontalSpacing;
            adjustPadding();
        }
    }

    /**
     * 根据Margins调整Padding值
     */
    private void adjustPadding() {
        if((mVerticalSpacingWithMargins >= 0 || mHorizontalSpacingWithMargins >= 0)) {
            final int verticalSpacingHalf = mVerticalSpacingWithMargins / 2;
            final int horizontalSpacingHalf = mHorizontalSpacingWithMargins / 2;
            final int oldVerticalSpacingHalf = mOldVerticalSpacingWithMargins / 2;
            final int oldHorizontalSpacingHalf = mOldHorizontalSpacingWithMargins / 2;
            final int l = getPaddingLeft() + oldHorizontalSpacingHalf - horizontalSpacingHalf;
            final int t = getPaddingTop() + oldVerticalSpacingHalf - verticalSpacingHalf;
            final int r = getPaddingRight() +  oldHorizontalSpacingHalf - horizontalSpacingHalf;
            final int b = getPaddingBottom() + oldVerticalSpacingHalf - verticalSpacingHalf;
            setPadding(l, t, r, b);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        boolean result = super.checkLayoutParams(p);
        if(result && (mVerticalSpacingWithMargins >= 0 || mHorizontalSpacingWithMargins >= 0)) {
            final int verticalSpacingHalf = mVerticalSpacingWithMargins / 2;
            final int horizontalSpacingHalf = mHorizontalSpacingWithMargins / 2;
            final LayoutParams lp = (LayoutParams) p;
            lp.setMargins(horizontalSpacingHalf, verticalSpacingHalf, horizontalSpacingHalf, verticalSpacingHalf);
        }
        return result;
    }

    public int getFirstVisiblePosition() {
        if(getChildCount() == 0)
            return 0;
        else
            return getChildAdapterPosition(getChildAt(0));
    }

    public int getLastVisiblePosition() {
        final int childCount = getChildCount();
        if(childCount == 0)
            return 0;
        else
            return getChildAdapterPosition(getChildAt(childCount - 1));
    }

    /**
     * @deprecated Use {@link #scrollToPosition(int)} and
     *             {@link #scrollToPositionWithOffset(int, int)}
     * @param position
     */
    @Deprecated
    public void scrollToPositionWithOffsetStart(int position) {
        scrollToPositionWithOffset(position, mSelectedItemOffsetStart, false);
    }
    
    public void scrollToPositionWithOffset(int position, int offset) {
        scrollToPositionWithOffset(position, offset, false);
    }
    
    public void scrollToPositionWithOffset(int position, int offset, boolean isRequestFocus) {

        /*mSelectedPosition = position;
        mShouldReverseLayout = true;
        final LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof android.support.v7.widget.LinearLayoutManager) {
            ((LinearLayoutManager)layoutManager).scrollToPositionWithOffset(position, offset);
            return;
        } else if (layoutManager instanceof android.support.v7.widget.StaggeredGridLayoutManager) {
            ((android.support.v7.widget.StaggeredGridLayoutManager)layoutManager).scrollToPositionWithOffset(position, offset);
            return;
        } */
        
        scrollToPosition(position, isRequestFocus, false, offset);
    }

    @Override
    public void scrollToPosition(int position) {
        scrollToPosition(position, false);
    }
    
    public void scrollToPosition(int position, boolean isRequestFocus) {
        scrollToPosition(position, isRequestFocus, false, mSelectedItemOffsetStart);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        smoothScrollToPosition(position, false);
    }
    
    public void smoothScrollToPosition(int position, boolean isRequestFocus) {
        scrollToPosition(position, isRequestFocus, true, mSelectedItemOffsetStart);
    }
    
    private void scrollToPosition(int position, boolean isRequestFocus, boolean isSmooth, int offset) {
        mSelectedPosition = position;
        TvSmoothScroller smoothScroller = new TvSmoothScroller(getContext(), isRequestFocus, isSmooth, offset);
        smoothScroller.setTargetPosition(position);
        getLayoutManager().startSmoothScroll(smoothScroller);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View view = getFocusedChild();
        if(null != view) {
            int tempPosition = getChildAdapterPosition(view) - getFirstVisiblePosition();
            if (tempPosition < 0) {
                return i;
            } else {
                if (i == childCount - 1) {//这是最后一个需要刷新的item
                    if (tempPosition > i) {
                        tempPosition = i;
                    }
                    return tempPosition;
                }
                if (i == tempPosition) {//这是原本要在最后一个刷新的item
                    return childCount - 1;
                }
            }
        }
        return i;
    }

    public boolean isScrolling() {
        return getScrollState() != SCROLL_STATE_IDLE;
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean result = super.dispatchKeyEvent(event);
        if(!result) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    result = null != mOnInBorderKeyEventListener && handleKeyDown(event.getKeyCode(), event);
                    break;
                case KeyEvent.ACTION_UP:
                    result = onKeyUp(event.getKeyCode(), event);
                    break;
            }
        }
        return result;
    }

    /**
     * 处理onKeyDown等事件
     * @param keyCode
     * @param event
     * @return
     */
    private boolean handleKeyDown(int keyCode, KeyEvent event) {
        int direction = keyCode2Direction(keyCode);

        if(direction == -1 || null == mOnInBorderKeyEventListener) {
            return false;
        } 

        final View nextFocusedView = findNextFocus(direction);
        if(hasInBorder(direction, nextFocusedView)) {
            return mOnInBorderKeyEventListener.onInBorderKeyEvent(direction, keyCode, event);
        }
        if (null != nextFocusedView) {
            nextFocusedView.requestFocus();
        }
        return true;
    }

    /**
     * 查找下个可获取焦点的view
     * @param direction
     * @return
     */
    private View findNextFocus(int direction) {
        return FocusFinder.getInstance().findNextFocus(this, getFocusedChild(), direction);
    }

    /**
     * keycode值转成Direction值
     * @param keyCode
     * @return
     */
    private int keyCode2Direction(int keyCode) {
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return FOCUS_DOWN;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return FOCUS_RIGHT;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                return FOCUS_LEFT;

            case KeyEvent.KEYCODE_DPAD_UP:
                return FOCUS_UP;

            default:
                return -1;
        }
    }

    @Override
    public View focusSearch(View focused, int direction) {
        final View nextFocusedView = findNextFocus(direction);
        if(hasInBorder(direction, nextFocusedView)) {
            return super.focusSearch(focused, direction);
        } else {
            return nextFocusedView;
        }
    }

    /**
     * 判断选中的item是否到达边界
     */
    private boolean hasInBorder(int direction, View nextFocusedView) {
        if(null != nextFocusedView)
            return false;
        Loger.i("hasInBorder...direction="+direction);
        switch (direction) {
            case FOCUS_DOWN:
                return !ViewCompat.canScrollVertically(this, 1);
                
            case FOCUS_UP:
                return !ViewCompat.canScrollVertically(this, -1);
            
            case FOCUS_LEFT:
                return !ViewCompat.canScrollHorizontally(this, -1);
            
            case FOCUS_RIGHT:
                
                return !ViewCompat.canScrollHorizontally(this, 1);
            
            default:
                return false;
        }
    }
    
    @Override
    public void onChildAttachedToWindow(View child) {
        if(child.isClickable() && !ViewCompat.hasOnClickListeners(child)) {
            child.setOnClickListener(this);
        }
        if(child.isFocusable() && null == child.getOnFocusChangeListener()) {
            child.setOnFocusChangeListener(this);
        }
    }

    public void setItemActivated(int position) {
        if(mIsMenu) {
            ViewHolder holder;
            if(position != mSelectedPosition) {
                holder = findViewHolderForLayoutPosition(mSelectedPosition);
                if(null != holder && holder.itemView.isActivated()) {
                    holder.itemView.setActivated(false);
                }
                mSelectedPosition = position;
            }
            holder = findViewHolderForLayoutPosition(position);
            if(null != holder && !holder.itemView.isActivated()) {
                holder.itemView.setActivated(true);
            }
        }
    }
    
    public void setOnItemListener(OnItemListener onItemListener) {
        mOnItemListener = onItemListener;
    }

    public void setOnInBorderKeyEventListener(OnInBorderKeyEventListener onInBorderKeyEventListener) {
        mOnInBorderKeyEventListener = onInBorderKeyEventListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        boolean onLoadMore();
    }

    public interface OnInBorderKeyEventListener {
        boolean onInBorderKeyEvent(int direction, int keyCode, KeyEvent event);
    }

    public interface OnItemListener {
        void onItemPreSelected(TvRecyclerView parent, View itemView, int position);

        void onItemSelected(TvRecyclerView parent, View itemView, int position);

        void onItemClick(TvRecyclerView parent, View itemView, int position);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        RecyclerView.SavedState superSavedState = (RecyclerView.SavedState) super.onSaveInstanceState();
        ISavedState savedState = new ISavedState(superSavedState.getSuperState());
        savedState.mISuperState = superSavedState;
        savedState.mSelectedPosition = mSelectedPosition;
        savedState.mVerticalSpacingWithMargins = mVerticalSpacingWithMargins;
        savedState.mHorizontalSpacingWithMargins = mHorizontalSpacingWithMargins;
        savedState.mOldVerticalSpacingWithMargins = mOldVerticalSpacingWithMargins;
        savedState.mOldHorizontalSpacingWithMargins = mOldHorizontalSpacingWithMargins;
        savedState.mSelectedItemOffsetStart = mSelectedItemOffsetStart;
        savedState.mSelectedItemOffsetEnd = mSelectedItemOffsetEnd;
        savedState.mSelectedItemCentered = mSelectedItemCentered;
        savedState.mIsMenu = mIsMenu;
        savedState.mHasMoreData = mHasMoreData;
        savedState.mIsSelectFirstVisiblePosition = mIsSelectFirstVisiblePosition;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(null != state) {
            if(state instanceof ISavedState) {
                ISavedState savedState = (ISavedState) state;
                mSelectedPosition = savedState.mSelectedPosition;
                mVerticalSpacingWithMargins = savedState.mVerticalSpacingWithMargins;
                mHorizontalSpacingWithMargins = savedState.mHorizontalSpacingWithMargins;
                mOldVerticalSpacingWithMargins = savedState.mOldVerticalSpacingWithMargins;
                mOldHorizontalSpacingWithMargins = savedState.mOldHorizontalSpacingWithMargins;
                mSelectedItemOffsetStart = savedState.mSelectedItemOffsetStart;
                mSelectedItemOffsetEnd = savedState.mSelectedItemOffsetEnd;
                mSelectedItemCentered = savedState.mSelectedItemCentered;
                mIsMenu = savedState.mIsMenu;
                mHasMoreData = savedState.mHasMoreData;
                mIsSelectFirstVisiblePosition = savedState.mIsSelectFirstVisiblePosition;
                try {
                    super.onRestoreInstanceState(savedState.mISuperState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                super.onRestoreInstanceState(state);
            }
        }
    }

    protected static class ISavedState extends android.view.View.BaseSavedState {
        private int mSelectedPosition;
        private int mVerticalSpacingWithMargins;
        private int mOldVerticalSpacingWithMargins;
        private int mHorizontalSpacingWithMargins;
        private int mOldHorizontalSpacingWithMargins;
        private int mSelectedItemOffsetStart;
        private int mSelectedItemOffsetEnd;
        private boolean mSelectedItemCentered;
        private boolean mIsMenu;
        private boolean mHasMoreData;
        private boolean mIsSelectFirstVisiblePosition;
        private Parcelable mISuperState;

        protected ISavedState(Parcelable superState) {
            super(superState);
        }

        protected ISavedState(Parcel in) {
            super(in);
            mISuperState = in.readParcelable(RecyclerView.class.getClassLoader());
            mSelectedPosition = in.readInt();
            mVerticalSpacingWithMargins = in.readInt();
            mHorizontalSpacingWithMargins = in.readInt();
            mOldVerticalSpacingWithMargins = in.readInt();
            mOldHorizontalSpacingWithMargins = in.readInt();
            mSelectedItemOffsetStart = in.readInt();
            mSelectedItemOffsetEnd = in.readInt();
            boolean[] booleens = new boolean[4];
            in.readBooleanArray(booleens);
            mSelectedItemCentered = booleens[0];
            mIsMenu = booleens[1];
            mHasMoreData = booleens[2];
            mIsSelectFirstVisiblePosition = booleens[3];
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(mISuperState, 0);
            out.writeInt(mSelectedPosition);
            out.writeInt(mVerticalSpacingWithMargins);
            out.writeInt(mHorizontalSpacingWithMargins);
            out.writeInt(mOldVerticalSpacingWithMargins);
            out.writeInt(mOldHorizontalSpacingWithMargins);
            out.writeInt(mSelectedItemOffsetStart);
            out.writeInt(mSelectedItemOffsetEnd);
            boolean[] booleens = {mSelectedItemCentered, mIsMenu, mHasMoreData, mIsSelectFirstVisiblePosition};
            out.writeBooleanArray(booleens);
        }

        public static final Creator<ISavedState> CREATOR
                = new Creator<ISavedState>() {
            @Override
            public ISavedState createFromParcel(Parcel in) {
                return new ISavedState(in);
            }

            @Override
            public ISavedState[] newArray(int size) {
                return new ISavedState[size];
            }
        };
    }

    private class IRecyclerViewDataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            Loger.i("RecyclerView Data Changed!!!");
            mShouldReverseLayout = true;
        }
    }
    
    private class TvSmoothScroller extends LinearSmoothScroller {
        private boolean mRequestFocus;
        private boolean mIsSmooth;
        private int mOffset;

        public TvSmoothScroller(Context context, boolean isRequestFocus, boolean isSmooth, int offset) {
            super(context);
            mRequestFocus = isRequestFocus;
            mIsSmooth = isSmooth;
            mOffset = offset;
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            return mIsSmooth ? super.calculateTimeForScrolling(dx) : 
                    ((int) Math.ceil(Math.abs(dx) * (11f / getContext().getResources().getDisplayMetrics().densityDpi)));
        }

        @Override
        protected void onTargetFound(View targetView, State state, Action action) {
            if(mSelectedItemCentered && null != getLayoutManager()) {
                getDecoratedBoundsWithMargins(targetView, mTempRect);
                mOffset = (getLayoutManager().canScrollHorizontally() ? (getFreeWidth() - mTempRect.width())
                        : (getFreeHeight() - mTempRect.height())) / 2;
            }
            super.onTargetFound(targetView, state, action);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return boxStart - viewStart + mOffset;
        }

        @Override
        protected void onStop() {
            super.onStop();
            if(mRequestFocus) {
                final View itemView = findViewByPosition(getTargetPosition());
                if (null != itemView) {
                    Loger.i("SmoothScroller Top Request Child Focus");
                    itemView.post(new Runnable() {
                        @Override
                        public void run() {
                            if(!hasFocus()) {
                                onFocusChanged(true, FOCUS_DOWN, null);
                            }
                            itemView.requestFocus();
                        }
                    });
                }
            }
        }
    }
}
