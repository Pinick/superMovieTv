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
import android.util.Log;
import android.view.View;

import com.owen.tvrecyclerview.widget.MetroGridLayoutManager;
import com.owen.tvrecyclerview.widget.MetroTitleItemDecoration;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.zmovie.app.R;
import com.zmovie.app.adapter.CommonRecyclerViewAdapter;
import com.zmovie.app.adapter.CommonRecyclerViewHolder;
import com.zmovie.app.adapter.MetroAdapter;
import com.zmovie.app.data.ItemDatas;
import com.zmovie.app.display.DisplayAdaptive;

import java.util.List;

import butterknife.BindView;

public class MetroFragment extends BaseFragment {
    private static final String LOGTAG = MetroFragment.class.getSimpleName();
    
    @BindView(R.id.list) TvRecyclerView mRecyclerView;
    
    @BindView(R.id.list_menu) TvRecyclerView mMenuView;
    
    private MetroAdapter mAdapter;
    private MenuAdapter mMenuAdapter;
    private int mCurSelectedMenuPosition = 0;

    public static MetroFragment newInstance() {
        MetroFragment fragment = new MetroFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListener();
        
        mMenuView.setSpacingWithMargins(14, 0);
        mMenuAdapter = new MenuAdapter(getContext(), ItemDatas.getDatas(3));
        mMenuView.setAdapter(mMenuAdapter);
        
        mAdapter = new MetroAdapter(getContext());
        mAdapter.setDatas(ItemDatas.getDatas(60));
        
//        MetroGridLayoutManager layoutManager = new MetroGridLayoutManager
//        (TwoWayLayoutManager.Orientation.VERTICAL, 24, 60, 10);
//        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new MetroTitleItemDecoration(mAdapter));//设置title
        mRecyclerView.setSpacingWithMargins(10, 10);//设置行列间距
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setListener() {
        setScrollListener(mRecyclerView);
        
        mMenuView.setOnItemListener(new SimpleOnItemListener() {
            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                if(mCurSelectedMenuPosition != position) {
                    mCurSelectedMenuPosition = position;
                    switch (mCurSelectedMenuPosition) {
                        case 0:
                            mRecyclerView.smoothScrollToPosition(0);
                            break;
                        case 1:
                            mRecyclerView.smoothScrollToPosition(7);
                            break;
                        case 2:
                            mRecyclerView.smoothScrollToPosition(19);
                            break;
                    }
                }
                onMoveFocusBorder(itemView, 1.1f, 0);
            }
        });
        
        mRecyclerView.setOnItemListener(new SimpleOnItemListener() {

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                float radius = DisplayAdaptive.getInstance().toLocalPx(10);
                onMoveFocusBorder(itemView, 1.1f, radius);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                showToast("onItemClick::"+position);
            }
        });

        mRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(mMenuView.hasFocus() && !hasFocus)
                    return;
                mFocusBorder.setVisible(hasFocus);
            }
        });
        
        mMenuView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(mRecyclerView.hasFocus() && !hasFocus)
                    return;
                mFocusBorder.setVisible(hasFocus);
            }
        });

        //设置选中段落的监听
        final MetroGridLayoutManager layoutManager = (MetroGridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.addOnSectionSelectedListener(new MetroGridLayoutManager.OnSectionSelectedListener() {
            @Override
            public void onSectionSelected(int sectionIndex) {
                Log.i("@$@$", "onSectionSelected...sectionIndex="+sectionIndex);
                if(!mMenuView.hasFocus() && mCurSelectedMenuPosition != sectionIndex) {
                    mCurSelectedMenuPosition = sectionIndex;
                    mMenuView.setItemActivated(sectionIndex);
                }
            }
        });
    }

    public int getLayoutId() {
        return R.layout.layout_metro_grid;
    }

    private class MenuAdapter extends CommonRecyclerViewAdapter {

        public MenuAdapter(Context context, List datas) {
            super(context, datas);
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_list_menu;
        }

        @Override
        public void onBindItemHolder(CommonRecyclerViewHolder helper, Object item, int position) {
            helper.getHolder().setText(R.id.title, "菜单 "+position);
        }
    }
}
