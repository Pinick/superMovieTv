package com.zmovie.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

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
 * description:
 */
public class OnlineTabFragment extends BaseFragment {

    private static volatile OnlineTabFragment onlineTabFragment;



    @BindView(R.id.online_tab_layout)
    TvTabLayout mTablayout;
    @BindView(R.id.mvpager)
    ViewPager mvpager;

    @Override
    int getLayoutId() {
        return R.layout.online_tab_fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        String[] arr = { "今日推荐","最新电影","经典高清","国配电影",
                "经典港片","国产剧","日韩剧",
                "美剧","综艺","动漫",
                "纪录片","4K高清区"};
        String[] type = {"recommend","latest","highdpi","cznmv",
                "hungkong","native","koria",
                "america","complex","curtoon",
                "document","k4mv"};
        ArrayList<Fragment> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            list.add(MovListFragment.newInstance(type[i]));
            mTablayout.addTab(mTablayout.newTab().setText(arr[i]),i==0);
        }
        PageAdapter adapter = new PageAdapter(getChildFragmentManager(),list,arr);
        mTablayout.setTabScrollMode(TabLayout.MODE_SCROLLABLE);
        mTablayout.setupWithViewPager(mvpager);
        mvpager.setOffscreenPageLimit(5);
        mvpager.setAdapter(adapter);

    }
    public static OnlineTabFragment getInstance(){
        if (onlineTabFragment==null){
            synchronized (OnlineTabFragment.class){
                if (onlineTabFragment==null){
                    onlineTabFragment = new OnlineTabFragment();
                }
            }
        }
        return onlineTabFragment;
    }

}
