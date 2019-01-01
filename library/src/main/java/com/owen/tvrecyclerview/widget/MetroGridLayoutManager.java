package com.owen.tvrecyclerview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.owen.tvrecyclerview.Lanes;
import com.owen.tvrecyclerview.R;
import com.owen.tvrecyclerview.utils.Loger;
import com.owen.tvrecyclerview.utils.MathUtil;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Created by owen on 2017/6/22.
 */

public class MetroGridLayoutManager extends GridLayoutManager {
    private static final int[] DEFAULT_LANE_COUNTS = new int[]{2};;
    
    private int[] mLaneCounts;
    private boolean mMeasuring;
    private int mSelectedSectionIndex = 0;
    private boolean mIsIntelligentScroll = true;
    private List<OnSectionSelectedListener> mSectionSelectedListeners;
    
    public MetroGridLayoutManager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MetroGridLayoutManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.TvRecyclerView, defStyle, 0);
        String laneCountsStr = a.getString(R.styleable.TvRecyclerView_tv_laneCountsStr);
        mIsIntelligentScroll = a.getBoolean(R.styleable.TvRecyclerView_tv_isIntelligentScroll, true);
        a.recycle();

        final int[] laneCounts;
        if(!TextUtils.isEmpty(laneCountsStr)) {
            final String[] lanes = laneCountsStr.split(",");
            final int count = lanes.length;
            laneCounts = new int[count];
            for (int i = 0; i < count; i++) {
                laneCounts[i] = Integer.parseInt(lanes[i]);
            }
        } else {
            laneCounts = DEFAULT_LANE_COUNTS;
        }
        initLaneCounts(laneCounts);
    }

    public MetroGridLayoutManager(Orientation orientation, int... laneCounts) {
        super(orientation);
        initLaneCounts(laneCounts);
    }
    
    private void initLaneCounts(int... laneCounts) {
        mLaneCounts = laneCounts;
        final int multiple = MathUtil.commonMultiple(laneCounts);
        setNumColumns(multiple);
        setNumRows(multiple);
        Loger.i("multiple="+multiple);
    }

    public void setIntelligentScroll(boolean intelligentScroll) {
        mIsIntelligentScroll = intelligentScroll;
    }

    public boolean isIntelligentScroll() {
        return mIsIntelligentScroll;
    }

    private int getLaneSpan(LayoutParams lp, boolean isVertical) {
        return (isVertical ? lp.getColSpan() : lp.getRowSpan());
    }

    private int getLaneSpan(MetroItemEntry entry, boolean isVertical) {
        return (isVertical ? entry.getColSpan() : entry.getRowSpan());
    }

    @Override
    public boolean canScrollHorizontally() {
        return super.canScrollHorizontally() && !mMeasuring;
    }

    @Override
    public boolean canScrollVertically() {
        return super.canScrollVertically() && !mMeasuring;
    }

    @Override
    public int getLaneSpanForChild(View child) {
        return getLaneSpan((LayoutParams) child.getLayoutParams(), isVertical());
    }

    @Override
    public int getLaneSpanForPosition(int position) {
        final MetroItemEntry entry = (MetroItemEntry) getItemEntryForPosition(position);
        if (entry == null) {
            // add by zhousuqiang
            View view = getChildAt(position - getFirstVisiblePosition());
            if(null != view) {
                return getLaneSpanForChild(view);
            }
            throw new IllegalStateException("Could not find span for position " + position);
        }

        return getLaneSpan(entry, isVertical());
    }

    @Override
    public void getLaneForPosition(Lanes.LaneInfo outInfo, int position, Direction direction) {
        final MetroItemEntry entry = (MetroItemEntry) getItemEntryForPosition(position);
        if (entry != null) {
            outInfo.set(entry.startLane, entry.anchorLane);
            return;
        }

        outInfo.setUndefined();
    }

    @Override
    protected void getLaneForChild(Lanes.LaneInfo outInfo, View child, Direction direction) {
        super.getLaneForChild(outInfo, child, direction);
        if (outInfo.isUndefined()) {
            getLanes().findLane(outInfo, getLaneSpanForChild(child), direction);
        }
    }

    private int getChildWidth(int colSpan) {
        return (int)(getLanes().getLaneSize() * colSpan);
    }

    private int getChildHeight(int rowSpan) {
        return (int)(getLanes().getLaneSize() * rowSpan);
    }

    private int getWidthUsed(View child) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return getWidth() - getPaddingLeft() - getPaddingRight() - getChildWidth(lp.getColSpan());
    }

    private int getHeightUsed(View child) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return getHeight() - getPaddingTop() - getPaddingBottom() - getChildHeight(lp.getRowSpan());
    }

    @Override
    protected void measureChildWithMargins(View child) {
        mMeasuring = true;
        measureChildWithMargins(child, getWidthUsed(child), getHeightUsed(child));
        mMeasuring = false;
    }

    @Override
    protected void layoutChild(View child, Direction direction) {
        
        super.layoutChild(child, direction);
    }
    
    @Override
    protected void moveLayoutToPosition(int position, int offset, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final boolean isVertical = isVertical();
        final Lanes lanes = getLanes();

        lanes.reset(0);

        for (int i = 0; i <= position; i++) {
            MetroItemEntry entry = (MetroItemEntry) getItemEntryForPosition(i);
            if (entry == null) {
                final View child = recycler.getViewForPosition(i);
                entry = (MetroItemEntry) cacheChildLaneAndSpan(child, Direction.END);
            }

            mTempLaneInfo.set(entry.startLane, entry.anchorLane);

            // The lanes might have been invalidated because an added or
            // removed item. See BaseLayoutManager.invalidateItemLanes().
            if (mTempLaneInfo.isUndefined()) {
                lanes.findLane(mTempLaneInfo, getLaneSpanForPosition(i), Direction.END);
                entry.setLane(mTempLaneInfo);
            }

            lanes.getChildFrame(mTempRect, getChildWidth(entry.getColSpan()),
                    getChildHeight(entry.getRowSpan()), mTempLaneInfo, Direction.END);

            if (i != position) {
                pushChildFrame(entry, mTempRect, entry.startLane, getLaneSpan(entry, isVertical),
                        Direction.END);
            }
        }

        lanes.getLane(mTempLaneInfo.startLane, mTempRect);
        lanes.reset(Direction.END);
        lanes.offset(offset - (isVertical ? mTempRect.bottom : mTempRect.right));
    }

    @Override
    protected ItemEntry cacheChildLaneAndSpan(View child, Direction direction) {
        final int position = getPosition(child);
        
        mTempLaneInfo.setUndefined();

        MetroItemEntry entry = (MetroItemEntry) getItemEntryForPosition(position);
        if (entry != null) {
            mTempLaneInfo.set(entry.startLane, entry.anchorLane);
        }

        if (mTempLaneInfo.isUndefined()) {
            getLaneForChild(mTempLaneInfo, child, direction);
        }

        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        
        if (entry == null) {
            Loger.i("cacheChildLaneAndSpan position="+position + " direction="+direction);
            int itemSpanUsed = 0;
            for(int i = getFirstVisiblePosition(); i < position; i++) {
                entry = (MetroItemEntry) getItemEntryForPosition(i);
                if(null!= entry && entry.sectionIndex == lp.sectionIndex) {
                    itemSpanUsed += getLaneSpan(entry, isVertical());
                    if (itemSpanUsed > getLaneCount()) {
                        break;
                    }
                }
            }
            
            if((itemSpanUsed + getLaneSpan(lp, isVertical())) <= getLaneCount()) {
                lp.isSectionStart = true;
            } else {
                lp.isSectionStart = false;
            }
            
            entry = new MetroItemEntry(mTempLaneInfo.startLane, mTempLaneInfo.anchorLane,
                    lp.colSpan, lp.rowSpan, lp.scale, lp.sectionIndex, lp.isSectionStart);
            setItemEntryForPosition(position, entry);
        } else {
            entry.setLane(mTempLaneInfo);
            lp.isSectionStart = entry.isSectionStart;
        }
        
        Loger.i("cacheChildLaneAndSpan position="+position + " lp.isSectionStart="+lp.isSectionStart + " TopDecorationHeight="+getTopDecorationHeight(child));

        return entry;
    }

    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        boolean resutl = false;
        if(mSelectedSectionIndex != lp.sectionIndex) {
            final Direction direction = mSelectedSectionIndex < lp.sectionIndex ? Direction.END : Direction.START;
            final boolean suportIntelligentScroll = direction == Direction.END ? lp.isSuportIntelligentScrollEnd : lp.isSuportIntelligentScrollStart;
            if (mIsIntelligentScroll && suportIntelligentScroll && parent instanceof TvRecyclerView) {
                getDecoratedBoundsWithMargins(child, mTempRect);
                final TvRecyclerView recyclerView = (TvRecyclerView) parent;
                final int offset = isVertical() ? 
                        (direction == Direction.END ?
                        mTempRect.top - getPaddingTop() - recyclerView.getSelectedItemOffsetStart() :
                        -(recyclerView.getHeight() - mTempRect.bottom - getPaddingBottom() - recyclerView.getSelectedItemOffsetEnd()))
                        : (direction == Direction.END ?
                        mTempRect.left - getPaddingLeft() - recyclerView.getSelectedItemOffsetStart() :
                        -(recyclerView.getWidth() - mTempRect.right - getPaddingRight() - recyclerView.getSelectedItemOffsetEnd()));
                final int dx = !isVertical() && ViewCompat.canScrollHorizontally(parent, offset) ? offset : 0;
                final int dy = isVertical() && ViewCompat.canScrollVertically(parent, offset) ? offset : 0;
                
                Loger.d("dx=" + dx + " dy=" + dy);
                recyclerView.smoothScrollBy(dx, dy);
                resutl = true;
            }
            mSelectedSectionIndex = lp.sectionIndex;
            notifySectionSelectedChanged();
        }

        return resutl || super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
    }

    @Override
    public void onScrollStateChanged(int state) {
        if(state == RecyclerView.SCROLL_STATE_IDLE) {
            View child = findViewByPosition(smoothTargetPosition);
            if(null != child && !isSmoothScrolling() && !hasFocus()) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if(mSelectedSectionIndex != lp.sectionIndex) {
                    mSelectedSectionIndex = lp.sectionIndex;
                    notifySectionSelectedChanged();
                }
            }
            smoothTargetPosition = NO_POSITION;
        }
    }

    public int getSelectedSectionIndex() {
        return mSelectedSectionIndex;
    }

    private int smoothTargetPosition = NO_POSITION;
    @Override
    public void startSmoothScroll(RecyclerView.SmoothScroller smoothScroller) {
        smoothTargetPosition = smoothScroller.getTargetPosition();
        super.startSmoothScroll(smoothScroller);
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        super.checkLayoutParams(lp);

        if (lp instanceof LayoutParams) {
            final LayoutParams metroLp = (LayoutParams) lp;
            metroLp.scale = getLaneCount() / mLaneCounts[metroLp.sectionIndex];
            if (isVertical()) {
                metroLp.height = getChildHeight(metroLp.getRowSpan()) - metroLp.topMargin - metroLp.bottomMargin;
                metroLp.width = LayoutParams.MATCH_PARENT;
                return (metroLp.rowSpan >= 1 && metroLp.colSpan >= 1 &&
                        metroLp.colSpan <= getLaneCount()) && 
                        metroLp.sectionIndex >= 0 && metroLp.sectionIndex < mLaneCounts.length;
            } else {
                metroLp.height = LayoutParams.MATCH_PARENT;
                metroLp.width = getChildHeight(metroLp.getColSpan()) - metroLp.leftMargin - metroLp.rightMargin;
                return (metroLp.colSpan >= 1 && metroLp.rowSpan >= 1 &&
                        metroLp.rowSpan <= getLaneCount()) &&
                        metroLp.sectionIndex >= 0 && metroLp.sectionIndex < mLaneCounts.length;
            }
        }
        
        return false;
    }

    @Override
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        final LayoutParams metroLp = new LayoutParams((ViewGroup.MarginLayoutParams) lp);

        if (lp instanceof LayoutParams) {
            final LayoutParams other = (LayoutParams) lp;
            metroLp.sectionIndex = Math.max(0, Math.min(other.sectionIndex, mLaneCounts.length - 1));
            metroLp.scale = getLaneCount() / mLaneCounts[metroLp.sectionIndex];
            if (isVertical()) {
                metroLp.colSpan = Math.max(1, Math.min(other.colSpan, mLaneCounts[other.sectionIndex]));
                metroLp.rowSpan = Math.max(1, other.rowSpan);
                metroLp.height = getChildHeight(metroLp.getRowSpan()) - metroLp.topMargin - metroLp.bottomMargin;
                metroLp.width = LayoutParams.MATCH_PARENT;
            } else {
                metroLp.colSpan = Math.max(1, other.colSpan);
                metroLp.rowSpan = Math.max(1, Math.min(other.rowSpan, mLaneCounts[other.sectionIndex]));
                metroLp.height = LayoutParams.MATCH_PARENT;
                metroLp.width = getChildHeight(metroLp.getColSpan()) - metroLp.leftMargin - metroLp.rightMargin;
            }
        }
        
        return metroLp;
    }

    @Override
    public LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
        return new LayoutParams(c, attrs);
    }
    
    public void addOnSectionSelectedListener(OnSectionSelectedListener listener) {
        if(null == mSectionSelectedListeners) {
            mSectionSelectedListeners = new ArrayList<>();
        }
        mSectionSelectedListeners.add(listener);
    }
    
    public void removeOnSectionSelectedListener(OnSectionSelectedListener listener) {
        if(null != mSectionSelectedListeners) {
            mSectionSelectedListeners.remove(listener);
        }
    }
    
    private void notifySectionSelectedChanged() {
        if(null != mSectionSelectedListeners) {
            for (OnSectionSelectedListener listener : mSectionSelectedListeners) {
                listener.onSectionSelected(mSelectedSectionIndex);
            }
        }
    }

    public static class LayoutParams extends RecyclerView.LayoutParams {
        private static final int DEFAULT_INDEX = 0;
        private static final int DEFAULT_SCALE = 1;
        private static final int DEFAULT_SPAN = 1;

        public int rowSpan;
        public int colSpan;
        private int scale;
        public int sectionIndex;
        public boolean isSectionStart;
        public boolean isSuportIntelligentScrollStart;
        public boolean isSuportIntelligentScrollEnd;

        public LayoutParams(int width, int height) {
            super(width, height);
            init(null);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.TvRecyclerView_SpannableGridViewChild);
            colSpan = Math.max(
                    DEFAULT_SPAN, a.getInt(R.styleable.TvRecyclerView_SpannableGridViewChild_tv_colSpan, -1));
            rowSpan = Math.max(
                    DEFAULT_SPAN, a.getInt(R.styleable.TvRecyclerView_SpannableGridViewChild_tv_rowSpan, -1));
            a.recycle();
            
            init(null);
        }

        public LayoutParams(ViewGroup.LayoutParams other) {
            super(other);
            init(other);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams other) {
            super(other);
            init(other);
        }

        private void init(ViewGroup.LayoutParams other) {
            if (null != other && other instanceof LayoutParams) {
                final LayoutParams lp = (LayoutParams) other;
                this.scale = lp.scale;
                this.sectionIndex = lp.sectionIndex;
                this.isSectionStart = lp.isSectionStart;
                this.isSuportIntelligentScrollStart = lp.isSuportIntelligentScrollStart;
                this.isSuportIntelligentScrollEnd = lp.isSuportIntelligentScrollEnd;
            } else {
                rowSpan = DEFAULT_SPAN;
                colSpan = DEFAULT_SPAN;
                scale = DEFAULT_SCALE;
                sectionIndex = DEFAULT_INDEX;
                isSectionStart = false;
                isSuportIntelligentScrollStart = true;
                isSuportIntelligentScrollEnd = true;
            }
        }
        
        public int getRowSpan() {
            return rowSpan * scale;
        }
        
        public int getColSpan() {
            return colSpan * scale;
        }

        @Override
        public String toString() {
            return "[rowSpan="+rowSpan+" colSpan="+colSpan+ " sectionIndex="+sectionIndex+" scale="+scale+"]";
        }
    }

    public static class MetroItemEntry extends ItemEntry {
        private final int colSpan;
        private final int rowSpan;
        private final int scale;
        private final int sectionIndex;
        private final boolean isSectionStart;
        
        public MetroItemEntry(int startLane, int anchorLane, int colSpan, int rowSpan, int scale, int sectionIndex, boolean isSectionStart) {
            super(startLane, anchorLane);
            this.colSpan = colSpan;
            this.rowSpan = rowSpan;
            this.scale = scale;
            this.sectionIndex = sectionIndex;
            this.isSectionStart = isSectionStart;
        }

        public MetroItemEntry(Parcel in) {
            super(in);
            this.colSpan = in.readInt();
            this.rowSpan = in.readInt();
            this.scale = in.readInt();
            this.sectionIndex = in.readInt();
            boolean[] val = new boolean[1];
            in.readBooleanArray(val);
            this.isSectionStart = val[0];
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(colSpan);
            out.writeInt(rowSpan);
            out.writeInt(scale);
            out.writeInt(sectionIndex);
            boolean[] val = new boolean[1];
            val[0] = isSectionStart;
            out.writeBooleanArray(val);
            
        }

        public static final Parcelable.Creator<MetroItemEntry> CREATOR
                = new Parcelable.Creator<MetroItemEntry>() {
            @Override
            public MetroItemEntry createFromParcel(Parcel in) {
                return new MetroItemEntry(in);
            }

            @Override
            public MetroItemEntry[] newArray(int size) {
                return new MetroItemEntry[size];
            }
        };

        public int getRowSpan() {
            return rowSpan * scale;
        }

        public int getColSpan() {
            return colSpan * scale;
        }
    }
    
    public interface OnSectionSelectedListener {
        void onSectionSelected(int sectionIndex);
    }
}
