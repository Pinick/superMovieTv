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
import android.util.Log;
import android.view.View;

import com.owen.tvrecyclerview.widget.MetroGridLayoutManager;
import com.owen.tvrecyclerview.widget.MetroTitleItemDecoration;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.zmovie.app.R;
import com.zmovie.app.adapter.HomeAdapter;
import com.zmovie.app.data.ItemDatas;
import com.zmovie.app.display.DisplayAdaptive;

import butterknife.BindView;

public class HomeFragment extends BaseFragment {
    private static final String LOGTAG = HomeFragment.class.getSimpleName();
    
    @BindView(R.id.home_list) TvRecyclerView mRecyclerView;
    
    private HomeAdapter mAdapter;
    private int mCurSelectedMenuPosition = 0;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListener();
        

        mAdapter = new HomeAdapter(getContext());
        mAdapter.setDatas(ItemDatas.getDatas(60));

//        MetroGridLayoutManager layoutManager = new MetroGridLayoutManager
//        (TwoWayLayoutManager.Orientation.VERTICAL, 24, 60, 10);
//        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new MetroTitleItemDecoration(mAdapter));//设置title
        mRecyclerView.setSpacingWithMargins(20,20);//设置行列间距
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setListener() {
        setScrollListener(mRecyclerView);
        

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
                if (mFocusBorder!=null){
                    mFocusBorder.setVisible(hasFocus);
                }
            }
        });
        

        //设置选中段落的监听
        final MetroGridLayoutManager layoutManager = (MetroGridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.addOnSectionSelectedListener(new MetroGridLayoutManager.OnSectionSelectedListener() {
            @Override
            public void onSectionSelected(int sectionIndex) {
                Log.i("@$@$", "onSectionSelected...sectionIndex="+sectionIndex);
                if(mCurSelectedMenuPosition != sectionIndex) {
                    mCurSelectedMenuPosition = sectionIndex;
                }
            }
        });
    }

    public int getLayoutId() {
        return R.layout.home_layout;
    }
}
