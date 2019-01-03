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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.zmovie.app.R;
import com.zmovie.app.adapter.CommonRecyclerViewAdapter;
import com.zmovie.app.adapter.GridAdapter;
import com.zmovie.app.data.GlobalMsg;
import com.zmovie.app.display.DisplayAdaptive;
import com.zmovie.app.domain.BtInfo;
import com.zmovie.app.domain.RecentUpdate;
import com.zmovie.app.presenter.GetRecpresenter;
import com.zmovie.app.presenter.iview.IMoview;
import com.zmovie.app.view.MovieDetailActivity;

import butterknife.BindView;

public class GridFragment extends BaseFragment implements IMoview {

    @BindView(R.id.list) TvRecyclerView mRecyclerView;
    
    private CommonRecyclerViewAdapter mAdapter;
    private GetRecpresenter getRecpresenter;
    private RecentUpdate info;

    public static GridFragment newInstance() {
        GridFragment fragment = new GridFragment();
        return fragment;
    }
    private int index;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        index = 1;
        getRecpresenter = new GetRecpresenter(getContext(), this);
        getRecpresenter.getRecentUpdate( index,18);
        getRecpresenter.getBtRecommend(1,10);


        setListener();
        // 通过Margins来设置布局的横纵间距(与addItemDecoration()方法可二选一)
        // 推荐使用此方法
        mRecyclerView.setSpacingWithMargins(20, 30);
        mRecyclerView.setSelectedItemAtCentered(true);


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
                if (info!=null&&info.getData().size()>0){
                    if (position<info.getData().size()){
                        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
                        String imgUrl = info.getData().get(position).getDownimgurl();
                        intent.putExtra(GlobalMsg.KEY_POST_IMG, imgUrl);
                        intent.putExtra(GlobalMsg.KEY_DOWN_URL,info.getData().get(position).getDownLoadUrl());
                        intent.putExtra(GlobalMsg.KEY_MOVIE_TITLE, info.getData().get(position).getDownLoadName());
                        intent.putExtra(GlobalMsg.KEY_MOVIE_DOWN_ITEM_TITLE, info.getData().get(position).getDowndtitle());
                        intent.putExtra(GlobalMsg.KEY_MOVIE_DETAIL,info.getData().get(position).getMvdesc());
                        getActivity().startActivity(intent);
//                        showToast("onItemClick::"+position);
                    }
                }


            }
        });

        mRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mFocusBorder.setVisible(hasFocus);
            }
        });

        mRecyclerView.setOnLoadMoreListener(new TvRecyclerView.OnLoadMoreListener() {
            @Override
            public boolean onLoadMore() {
                mRecyclerView.setLoadingMore(true); //正在加载数据
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getRecpresenter.getMoreData(++index,18);
                    }
                },1000);
                return true; //是否还有更多数据
            }
        });
    }

    public int getLayoutId() {
        return R.layout.layout_grid;
    }

    @Override
    public void loadData(RecentUpdate info) {
        this.info = info;
        mAdapter = new GridAdapter(getContext());
        mAdapter.setDatas(info.getData());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void loadError(String msg) {

    }

    @Override
    public void loadMore(RecentUpdate result) {
        mAdapter.appendDatas(result.getData()); //加载数据
        mRecyclerView.setLoadingMore(false); //加载数据完毕
    }

}
