package com.zmovie.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.zmovie.app.focus.FocusBorder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by owen on 15/7/28.
 */
public abstract class CommonRecyclerViewAdapter<T> extends RecyclerView.Adapter<CommonRecyclerViewHolder>{
    protected Context mContext;
    private LayoutInflater mInflater;
    private List<T> mDatas = new ArrayList<>();

    protected FocusBorder mFocusBorder;

    public CommonRecyclerViewAdapter(Context context){
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public CommonRecyclerViewAdapter(Context context, List<T> datas){
        this(context);
        setDatas(datas);
    }

    public void clearDatas(){
        this.mDatas.clear();
    }
    
    public void setDatas(List<T> datas){
        this.mDatas = datas;
    }
    
    public void appendDatas(List<T> datas) {
        if(null == datas) return;
        int size = mDatas.size();
        this.mDatas.addAll(datas);
        notifyItemRangeInserted(size, datas.size());
    }
    
    public void removeItem(int postion) {
        if(null != mDatas && postion < mDatas.size()) {
            mDatas.remove(postion);
            notifyItemRemoved(postion);
        }
    }

    public void movedItem(int form, int to) {
        if(null != mDatas && !mDatas.isEmpty()) {
            if (form < 0 || form >= getItemCount() || to < 0 || to >= getItemCount()) {
                return;
            }
            T item = mDatas.get(form);
            mDatas.remove(form);
            mDatas.add(to-1, item);
            notifyItemMoved(form, to);
        }
    }

    @Override
    public CommonRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return CommonRecyclerViewHolder.get(this.mContext, viewGroup, getItemLayoutId(viewType));
    }

    @Override
    public void onBindViewHolder(final CommonRecyclerViewHolder holder, int position) {
        onBindItemHolder(holder, getItem(position), position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return null != this.mDatas ? this.mDatas.size() : 0;
    }

    public T getItem(int position) {
        return (null != mDatas && position < mDatas.size()) ? mDatas.get(position) : null;
    }

    public abstract int getItemLayoutId(int viewType);

    public abstract void onBindItemHolder(final CommonRecyclerViewHolder helper, T item, int position);
}
