package com.zmovie.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.zmovie.app.R;
import com.zmovie.app.adapter.CommonRecyclerViewAdapter;
import com.zmovie.app.adapter.GridAdapter;
import com.zmovie.app.data.GlobalMsg;
import com.zmovie.app.display.DisplayAdaptive;
import com.zmovie.app.domain.BtInfo;
import com.zmovie.app.domain.RecentUpdate;
import com.zmovie.app.presenter.CenterPresenter;
import com.zmovie.app.presenter.GetRecpresenter;
import com.zmovie.app.presenter.iview.IMoview;
import com.zmovie.app.view.MovieDetailActivity;

import butterknife.BindView;

/**
 * creator huangyong
 * createTime 2019/1/2 下午11:08
 * path com.zmovie.app.fragment
 * description:
 */
public class MovListFragment extends BaseFragment implements IMoview {
    private RecentUpdate info;
    private int index;
    @BindView(R.id.mvlist)
    TvRecyclerView mRecyclerView;
    private  String pageType;
    private static MovListFragment btlistFragment;
    private CommonRecyclerViewAdapter mAdapter;
    private GetRecpresenter getRecpresenter;
    private CenterPresenter centerPresenter;

    public static MovListFragment newInstance(String type) {
        btlistFragment = new MovListFragment();
        btlistFragment.pageType = type;
        Bundle bundle = new Bundle();
        bundle.putString("Type", type);
        btlistFragment.setArguments(bundle);
        return btlistFragment;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        this.pageType = bundle.getString("Type");
        initData(pageType);
    }

    private void initData(String pageType) {
        getRecpresenter = new GetRecpresenter(getContext(), this);
        centerPresenter = new CenterPresenter(getContext(),this);
        index = 1;
        if (pageType.equals("recommend")){
            getRecpresenter.getRecentUpdate( index,18);
        }else {
            centerPresenter.getLibraryDdata(pageType,index,18);
        }
        mRecyclerView.setSpacingWithMargins(20, 30);
        mRecyclerView.setSelectedItemAtCentered(true);
        setListener();

    }

    private void setListener() {
        setScrollListener(mRecyclerView);

        mRecyclerView.setOnItemListener(listener);
        mRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mFocusBorder!=null){
                    mFocusBorder.setVisible(hasFocus);
                }

            }
        });

        mRecyclerView.setOnLoadMoreListener(new TvRecyclerView.OnLoadMoreListener() {
            @Override
            public boolean onLoadMore() {
                mRecyclerView.setLoadingMore(true); //正在加载数据

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (pageType.equals("recommend")){
                            getRecpresenter.getMoreData(++index,18);
                        }else {
                            centerPresenter.getLibraryMoreDdata(pageType, ++index, 18);
                        }

                    }
                },1000);
                return true; //是否还有更多数据
            }
        });
    }
    SimpleOnItemListener listener =   new SimpleOnItemListener() {

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
    };


    @Override
    int getLayoutId() {
        return R.layout.mv_list_fragment;
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
