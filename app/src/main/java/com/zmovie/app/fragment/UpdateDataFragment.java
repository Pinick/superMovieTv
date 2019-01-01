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

package com.zmovie.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.zmovie.app.R;
import com.zmovie.app.adapter.CommonRecyclerViewAdapter;
import com.zmovie.app.adapter.CommonRecyclerViewHolder;
import com.zmovie.app.data.ItemBean;
import com.zmovie.app.data.ItemDatas;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class UpdateDataFragment extends BaseFragment {

    @BindView(R.id.rv_v7grid) TvRecyclerView mV7GridView;
    @BindView(R.id.rv_grid) TvRecyclerView mGridView;
    
    private CommonRecyclerViewAdapter mV7GridAdapter;
    private CommonRecyclerViewAdapter mGridAdapter;
    
    private int MODE_SIZE = 6;
    private int mGridViewUpdateMode = 0;
    private int mV7GridViewUpdateMode = 0;

    public static UpdateDataFragment newInstance() {
        UpdateDataFragment fragment = new UpdateDataFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListener();

        mV7GridView.setSpacingWithMargins(10, 10);
        mGridView.setSpacingWithMargins(10, 10);

        mV7GridAdapter = new UpdateDataAdapter(getContext());
        mV7GridAdapter.setDatas(ItemDatas.getDatas(50));
        mGridAdapter = new UpdateDataAdapter(getContext());
        mGridAdapter.setDatas(ItemDatas.getDatas(50));

        mV7GridView.setAdapter(mV7GridAdapter);
        mGridView.setAdapter(mGridAdapter);
    }
    
    private void setListener() {
        setScrollListener(mGridView);
        
        mGridView.setOnItemListener(new SimpleOnItemListener() {
            @Override
            public void onItemClick(TvRecyclerView parent, final View itemView, int position) {
                handleMode(mGridViewUpdateMode, false, false, mGridAdapter, position);
                
//                testGirdRemoveItem(mGridAdapter, position);
//                testGirdAppendDatas(mGridAdapter);
//                testGridResetDatas(mGridAdapter);
//                testGridResetAdapter(false);
//                testGridChangedItem(mGridAdapter, position);
//                testGridMovedItem(mGridAdapter, position, position + 2);
            }

        });

        mV7GridView.setOnItemListener(new SimpleOnItemListener() {
            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                handleMode(mV7GridViewUpdateMode, false, true, mV7GridAdapter, position);
                
//                testGirdRemoveItem(mV7GridAdapter, position);
//                testGirdAppendDatas(mV7GridAdapter);
//                testGridResetDatas(mV7GridAdapter);
//                testGridResetAdapter(true);
//                testGridChangedItem(mV7GridAdapter, position);
//                testGridMovedItem(mV7GridAdapter, position, position + 2);
            }
        });
        
        mGridView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    setScrollListener(mGridView);
                }
            }
        });
        
        mV7GridView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    setScrollListener(mV7GridView);
                }
            }
        });
    }
    
    @OnClick(R.id.btn1) void onBtn1Click() {
        mV7GridViewUpdateMode = (mV7GridViewUpdateMode + 1) % MODE_SIZE;
        showMode(mV7GridViewUpdateMode);
    }
    
    @OnClick(R.id.btn2) void onBtn2Click() {
        mGridViewUpdateMode = (mGridViewUpdateMode + 1) % MODE_SIZE;
        showMode(mGridViewUpdateMode);
    }
    
    private void showMode(int mode) {
        handleMode(mode, true, false, null, 0);
    }
    
    private void handleMode(int mode, boolean isBtnClick, boolean isV7, CommonRecyclerViewAdapter adapter, int position) {
        switch (mode) {
            case 0:
                if(isBtnClick) {
                    showToast("test Gird Remove Item");
                } else {
                    testGirdRemoveItem(adapter, position);
                }
                break;
            case 1:
                if(isBtnClick) {
                    showToast("test Gird Append Datas");
                } else {
                    testGirdAppendDatas(adapter);
                }
                break;
            case 2:
                if(isBtnClick) {
                    showToast("test Grid Reset Datas");
                } else {
                    testGridResetDatas(adapter);
                }
                break;
            case 3:
                if(isBtnClick) {
                    showToast("test Grid Reset Adapter");
                } else {
                    testGridResetAdapter(isV7);
                }
                break;
            case 4:
                if(isBtnClick) {
                    showToast("test Grid Changed Item");
                } else {
                    testGridChangedItem(adapter, position);
                }
                break;
            case 5:
                if(isBtnClick) {
                    showToast("test Grid Moved Item");
                } else {
                    testGridMovedItem(adapter, position, position + 2);
                }
                break;
            default:
                showToast("模式计算出错!!!!!");
                break;
        }
    }

    private void testGridMovedItem(CommonRecyclerViewAdapter adapter, int form, int to) {
        adapter.movedItem(form, to);
    }

    private void testGridChangedItem(CommonRecyclerViewAdapter adapter, int position) {
        adapter.notifyItemChanged(position);
    }

    private void testGridResetAdapter(boolean isV7) {
        showToast("grid reset adapter");
        if(isV7) {
            mV7GridAdapter = new UpdateDataAdapter(getContext());
            mV7GridAdapter.setDatas(ItemDatas.getDatas(30));
//        mV7GridView.setAdapter(mGridAdapter);
            mV7GridView.swapAdapter(mV7GridAdapter, false);
        } else {
            mGridAdapter = new UpdateDataAdapter(getContext());
            mGridAdapter.setDatas(ItemDatas.getDatas(30));
//        mGridView.setAdapter(mGridAdapter);
            mGridView.swapAdapter(mGridAdapter, false);
        }
    }

    private void testGridResetDatas(CommonRecyclerViewAdapter adapter) {
        showToast("grid reset datas");
        adapter.setDatas(ItemDatas.getDatas(63));
        adapter.notifyDataSetChanged();
    }

    private void testGirdAppendDatas(CommonRecyclerViewAdapter adapter) {
        showToast("grid append 10 item");
        adapter.appendDatas(ItemDatas.getDatas(10));
    }


    private void testGirdRemoveItem(CommonRecyclerViewAdapter adapter, int position) {
        showToast("remove grid item "+position);
        adapter.removeItem(position);
    }

    public int getLayoutId() {
        return R.layout.layout_update_data_changed;
    }

    private class UpdateDataAdapter extends CommonRecyclerViewAdapter<ItemBean> {
        private DateFormat mFormat;

        public UpdateDataAdapter(Context context) {
            super(context);
            mFormat = SimpleDateFormat.getTimeInstance();
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_has_bg_selector;
        }

        @Override
        public void onBindItemHolder(CommonRecyclerViewHolder helper, ItemBean item, int position) {
            helper.getHolder()
                    .setText(R.id.title, String.valueOf(position))
                    .setText(R.id.tv_changed, mFormat.format(new Date()));
        }
    }
}
