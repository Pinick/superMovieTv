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
public class DownloadTabFragment extends BaseFragment {

    private static volatile DownloadTabFragment onlineTabFragment;


    @BindView(R.id.down_Tab_container)
    FrameLayout tabContainer;


    @Override
    int getLayoutId() {
        return R.layout.download_tab_fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public static DownloadTabFragment getInstance(){
        if (onlineTabFragment==null){
            synchronized (DownloadTabFragment.class){
                if (onlineTabFragment==null){
                    onlineTabFragment = new DownloadTabFragment();
                }
            }
        }
        return onlineTabFragment;
    }

}
