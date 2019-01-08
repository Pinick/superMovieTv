package com.zmovie.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;

import com.huangyong.playerlib.Params;
import com.owen.tab.TvTabLayout;
import com.zmovie.app.R;
import com.zmovie.app.adapter.PageAdapter;
import com.zmovie.app.focus.FocusBorder;
import com.zmovie.app.tablayout.TabLayout;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * creator huangyong
 * createTime 2019/1/2 下午9:50
 * path com.zmovie.app.fragment
 * "science"=>"科幻片",
 * description:"comedy"=>"喜剧片",
 * "love"=>"爱情片",
 * "war"=>"战争片",
 * "document"=>"记录片",
 * "story"=>"剧情片",
 * "terror"=>"恐怖片",
 * "show"=>"综艺片");
 */
public class OnlineMovieFragment extends BaseFragment {

    private static volatile OnlineMovieFragment onlineTabFragment;

    @BindView(R.id.online_tab_layout)
    TvTabLayout mTablayout;
    @BindView(R.id.mvpager)
    ViewPager mvpager;

    @Override
    int getLayoutId() {
        return R.layout.online_mv_tab_fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Params.ACTION_RESET_POSITION);
        getContext().registerReceiver(receiver,filter);
    }

    private void initView() {

        String[] arr = { "科幻","喜剧","爱情","战争","剧情","恐怖",
                "综艺"};
        String[] type = {"science","comedy","love","war","story","terror","show"};
        ArrayList<Fragment> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            list.add(OnlineMovListFragment.newInstance(type[i]));
            if (i==0){
                mTablayout.addTab(mTablayout.newTab().setText(arr[i]),true);
            }else {
                mTablayout.addTab(mTablayout.newTab().setText(arr[i]));
            }

        }
        PageAdapter adapter = new PageAdapter(getChildFragmentManager(),list,arr);
        mTablayout.setTabScrollMode(TabLayout.MODE_SCROLLABLE);

        mvpager.setAdapter(adapter);
        mvpager.setOffscreenPageLimit(5);
        mTablayout.setupWithViewPager(mvpager);


    }
    public static OnlineMovieFragment getInstance(){
        if (onlineTabFragment==null){
            synchronized (OnlineMovieFragment.class){
                if (onlineTabFragment==null){
                    onlineTabFragment = new OnlineMovieFragment();
                }
            }
        }
        return onlineTabFragment;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Params.ACTION_RESET_POSITION)) {
              if (mTablayout!=null){
                  mTablayout.getSelectedTab().getTabView().requestFocus();
                  onMoveFocusBorder(mTablayout.getSelectedTab().getTabView(),1.2f,0);
              }
            }
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        getContext().unregisterReceiver(receiver);
    }
}
