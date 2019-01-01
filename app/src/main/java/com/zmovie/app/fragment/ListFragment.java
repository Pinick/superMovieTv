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

import android.os.Bundle;
import android.view.View;

import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.zmovie.app.R;
import com.zmovie.app.adapter.ListAdapter;
import com.zmovie.app.data.ItemDatas;

import butterknife.BindView;
import butterknife.OnClick;

public class ListFragment extends BaseFragment {

    @BindView(R.id.list) TvRecyclerView mRecyclerView;
    
    @BindView(R.id.list_v7) TvRecyclerView mRecyclerViewV7;
    
    private ListAdapter mAdapter;
    private ListAdapter mV7Adapter;

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListener();

        // 设置布局的横纵间距
        mRecyclerView.setSpacingWithMargins(0, 10);
        mRecyclerViewV7.setSpacingWithMargins(10, 0);

        mAdapter = new ListAdapter(getContext(), false);
        mAdapter.setDatas(ItemDatas.getDatas(64));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setSelectedItemAtCentered(true);
//        mRecyclerView.setSelectedItemOffset(30, 30);

        mV7Adapter = new ListAdapter(getContext(), true);
        mV7Adapter.setDatas(ItemDatas.getDatas(20));
        mRecyclerViewV7.setAdapter(mV7Adapter);

    }
    
    @OnClick(R.id.btn2) void btn2() {
//        mRecyclerView.scrollToPositionWithOffset(30, 80);
//        mRecyclerView.smoothScrollToPosition(40);
//        mRecyclerView.scrollToPosition(13, true);
        mRecyclerView.scrollToPosition(63);
    }
    
    @OnClick(R.id.btn3) void btn3() {
        mRecyclerView.scrollToPosition(0);
    }
    
    private void setListener() {
        setScrollListener(mRecyclerView);

        mRecyclerViewV7.setOnItemListener(new SimpleOnItemListener() {

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                onMoveFocusBorder(itemView, 1.1f, 0);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                showToast("onItemClick::"+position);
            }
        });
        
        mRecyclerView.setOnItemListener(new SimpleOnItemListener() {

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                onMoveFocusBorder(itemView, 1.1f, 0);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                showToast("onItemClick::"+position);
            }
        });
        
        mRecyclerViewV7.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(mRecyclerView.hasFocus() && !hasFocus)
                    return;
                mFocusBorder.setVisible(hasFocus);
            }
        });

        mRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(mRecyclerViewV7.hasFocus() && !hasFocus)
                    return;
                mFocusBorder.setVisible(hasFocus);
            }
        });

        //边界监听
//        mRecyclerView.setOnInBorderKeyEventListener(new TvRecyclerView.OnInBorderKeyEventListener() {
//            @Override
//            public boolean onInBorderKeyEvent(int direction, int keyCode, KeyEvent event) {
//                Log.i("zzzz", "onInBorderKeyEvent: ");
//                return false;//需要拦截返回true,否则返回false
//            }
//        });
        
        /*mRecyclerView.setOnLoadMoreListener(new TvRecyclerView.OnLoadMoreListener() {
            @Override
            public boolean onLoadMore() {
                Log.i("@@@@", "onLoadMore: ");
                mRecyclerView.setLoadingMore(true); //正在加载数据
                mLayoutAdapter.appendDatas(); //加载数据
                mRecyclerView.setLoadingMore(false); //加载数据完毕
                return false; //是否还有更多数据
            }
        });*/
    }

    public int getLayoutId() {
        return R.layout.layout_list;
    }
    
}
