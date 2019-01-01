package com.zmovie.app.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by owen on 15/8/26.
 * 通用Adapter抽象类
 * 需要与CommonViewHolder配套使用
 */
public abstract class CommonBaseAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mDatas;

    public CommonBaseAdapter(Context context) {
        this.mContext = context;
    }

    public CommonBaseAdapter(Context context, List<T> mDatas) {
        this(context);
        this.mDatas = mDatas;
    }

    public void setDatas(List<T> mDatas){
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return null != this.mDatas ? this.mDatas.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return (null != mDatas && position < mDatas.size()) ? mDatas.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CommonViewHolder viewHolder = getViewHolder(position, convertView,
                parent, getItem(position));
        onBindItemHolder(viewHolder, getItem(position));
        return viewHolder.getConvertView();

    }

    private CommonViewHolder getViewHolder(int position, View convertView, ViewGroup parent, T item) {
        int mItemLayoutId = getItemLayoutId(position, convertView, item);
        return CommonViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                position);
    }

    public abstract int getItemLayoutId(int position, @Nullable View convertView, T item);

    public abstract void onBindItemHolder(CommonViewHolder helper, T item);

}
