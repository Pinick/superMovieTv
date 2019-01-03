package com.zmovie.app.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.zmovie.app.R;

import butterknife.BindView;

/**
 * creator huangyong
 * createTime 2019/1/2 下午9:50
 * path com.zmovie.app.fragment
 * description:
 */
public class BtTabFragment extends BaseFragment {

    private static volatile BtTabFragment onlineTabFragment;


    @BindView(R.id.bt_tab_content)
    FrameLayout tabContainer;


    @Override
    int getLayoutId() {
        return R.layout.bt_tab_fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public static BtTabFragment getInstance(){
        if (onlineTabFragment==null){
            synchronized (BtTabFragment.class){
                if (onlineTabFragment==null){
                    onlineTabFragment = new BtTabFragment();
                }
            }
        }
        return onlineTabFragment;
    }

}
