package com.zmovie.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.zmovie.app.R;
import com.zmovie.app.adapter.CommonRecyclerViewAdapter;
import com.zmovie.app.adapter.OnlineMvAdapter;
import com.zmovie.app.data.GlobalMsg;
import com.zmovie.app.display.DisplayAdaptive;
import com.zmovie.app.domain.OnlinePlayInfo;
import com.zmovie.app.presenter.GetOnlinePresenter;
import com.zmovie.app.presenter.iview.IOnlineView;
import com.zmovie.app.view.MovieDetailActivity;
import com.zmovie.app.view.OnlineMovDetailActivity;

import butterknife.BindView;

/**
 * creator huangyong
 * createTime 2019/1/2 下午11:08
 * path com.zmovie.app.fragment
 * description:
 */
public class OnlineSerisListFragment extends BaseFragment implements IOnlineView {
    private OnlinePlayInfo info;
    private int index;
    @BindView(R.id.mvlist)
    TvRecyclerView mRecyclerView;
    private  String pageType;
    private static OnlineSerisListFragment btlistFragment;
    private CommonRecyclerViewAdapter mAdapter;
    private GetOnlinePresenter getOnlinePresenter;

    public static OnlineSerisListFragment newInstance(String type) {
        btlistFragment = new OnlineSerisListFragment();
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
        mRecyclerView.setSpacingWithMargins(20, 30);
        mRecyclerView.setSelectedItemAtCentered(true);
        setListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initData(pageType);
    }

    private void initData(String pageType) {
        index = 1;
        getOnlinePresenter = new GetOnlinePresenter(getContext(),this);
        getOnlinePresenter.getOnlineSerisData(pageType,index,18);

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
                        getOnlinePresenter.getSerisMoreData(pageType,++index,18);

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
                    Intent intent = new Intent(getContext(), OnlineMovDetailActivity.class);
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
    public void loadData(OnlinePlayInfo info) {
        this.info = info;
        mAdapter = new OnlineMvAdapter(getContext());
        mAdapter.setDatas(info.getData());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void loadError(String msg) {

    }

    @Override
    public void loadMore(OnlinePlayInfo result) {
        mAdapter.appendDatas(result.getData()); //加载数据
        mRecyclerView.setLoadingMore(false); //加载数据完毕
    }

}
