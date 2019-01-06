package com.zmovie.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.owen.tab.TvTabLayout;
import com.zmovie.app.R;
import com.zmovie.app.adapter.PageAdapter;
import com.zmovie.app.tablayout.TabLayout;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * creator huangyong
 * createTime 2019/1/2 下午9:50
 * path com.zmovie.app.fragment
 array(
 "native"=>"国产剧",
 "hongkong"=>"香港剧",
 "taiwan"=>"台湾剧",
 "japanise"=>"日本剧",
 "america"=>"欧美剧",
 "koria"=>"韩国剧",
 "curtoon"=>"动画片",
 "shortmv"=>"微电影",
 "ocean"=>"海外剧");
 */
public class OnlineSerisFragment extends BaseFragment {

    private static volatile OnlineSerisFragment onlineTabFragment;

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
    }

    private void initView() {
        String[] arr = { "国产剧","港剧","台湾剧","日剧",
                "美剧","韩剧","动漫",
                "微电影","海外剧"};
        String[] type = {"native","hongkong","taiwan","japanise",
                "america","koria","curtoon","shortmv","ocean"};
        ArrayList<Fragment> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            list.add(OnlineSerisListFragment.newInstance(type[i]));
            if (i==0){
                mTablayout.addTab(mTablayout.newTab().setText(arr[i]),true);
            }else {
                mTablayout.addTab(mTablayout.newTab().setText(arr[i]));
            }

        }
        PageAdapter adapter = new PageAdapter(getChildFragmentManager(),list,arr);
        mTablayout.setTabScrollMode(TabLayout.MODE_SCROLLABLE);
        mTablayout.addOnTabSelectedListener(new TvTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TvTabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TvTabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TvTabLayout.Tab tab) {

            }
        });
        mvpager.setAdapter(adapter);
        mvpager.setOffscreenPageLimit(5);
        mTablayout.setupWithViewPager(mvpager);


    }
    public static OnlineSerisFragment getInstance(){
        if (onlineTabFragment==null){
            synchronized (OnlineSerisFragment.class){
                if (onlineTabFragment==null){
                    onlineTabFragment = new OnlineSerisFragment();
                }
            }
        }
        return onlineTabFragment;
    }

}
