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
import com.zmovie.app.adapter.CommonRecyclerViewAdapter;
import com.zmovie.app.adapter.SpannableAdapter;
import com.zmovie.app.data.ItemDatas;
import com.zmovie.app.display.DisplayAdaptive;

import butterknife.BindView;

public class SpannableFragment extends BaseFragment {

    @BindView(R.id.list) TvRecyclerView mRecyclerView;
    
    private CommonRecyclerViewAdapter mAdapter;

    public static SpannableFragment newInstance() {
        SpannableFragment fragment = new SpannableFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListener();

//      final Drawable divider = getResources().getDrawable(R.drawable.divider);
//      mRecyclerView.addItemDecoration(new DividerItemDecoration(divider));
//      mRecyclerView.addItemDecoration(new SpacingItemDecoration(20, 20));
        // 通过Margins来设置布局的横纵间距(与addItemDecoration()方法可二选一)
        // 推荐使用此方法
        mRecyclerView.setSpacingWithMargins(10, 10);

        mAdapter = new SpannableAdapter(getContext(), mRecyclerView);
        mAdapter.setDatas(ItemDatas.getDatas(60));
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
                mFocusBorder.setVisible(hasFocus);
            }
        });

//        mRecyclerView.setOnInBorderKeyEventListener(new TvRecyclerView.OnInBorderKeyEventListener() {
//            @Override
//            public boolean onInBorderKeyEvent(int direction, int keyCode, KeyEvent event) {
//                Log.i("zzzz", "onInBorderKeyEvent: ");
//                return false;
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
        return R.layout.layout_spannable_grid;
    }
    
}
