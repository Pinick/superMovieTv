package com.zmovie.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.FrameLayout;

import com.owen.tab.TvTabLayout;
import com.zmovie.app.focus.FocusBorder;
import com.zmovie.app.fragment.BaseFragment;
import com.zmovie.app.fragment.BtTabFragment;
import com.zmovie.app.fragment.DownloadTabFragment;
import com.zmovie.app.fragment.OnlineTabFragment;
import com.zmovie.app.tablayout.TabLayout;

public class HomeRootActivity extends FragmentActivity  implements BaseFragment.FocusBorderHelper{

    private TvTabLayout mTabLayout;
    private FrameLayout contents;
    private FocusBorder mFocusBorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_root);


        mTabLayout = findViewById(R.id.home_tab_layout);
        contents= findViewById(R.id.home_tab_container);


        // 移动框
        if(null == mFocusBorder) {
//            mFocusBorder = new FocusBorder.Builder().asDrawable().borderResId(R.drawable.focus).build(this);
            mFocusBorder = new FocusBorder.Builder()
                    .asColor()
                    .borderRadius(0)
                    .borderColor(getResources().getColor(R.color.item_default_color))
                    .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 3)
                    .shadowColor(getResources().getColor(R.color.item_default_color))
                    .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 18)
                    .build(this);
        }
        mTabLayout.addOnTabSelectedListener(new HomeTabSelectedListener());

        mTabLayout.addTab(
                mTabLayout.newTab()
                        .setText("在线影院")
//                        .setIcon(R.drawable.ic_staggered)
        ,true);
        mTabLayout.addTab(
                mTabLayout.newTab()
                        .setText("BT影库")
//                        .setIcon(R.drawable.ic_staggered)
        );
        mTabLayout.addTab(
                mTabLayout.newTab()
                        .setText("下载中心")
//                        .setIcon(R.drawable.ic_grid)
                );

    }

    @Override
    public FocusBorder getFocusBorder() {
        return mFocusBorder;
    }

    public class HomeTabSelectedListener implements TvTabLayout.OnTabSelectedListener{



        public HomeTabSelectedListener() {
        }

        private Fragment mFragment;
        private int[] layoutIds = {
                R.layout.online_tab_fragment,
                R.layout.bt_tab_fragment,
                R.layout.download_tab_fragment
        };

        @Override
        public void onTabSelected(TvTabLayout.Tab tab) {
            final int position = tab.getPosition();
            mFragment = (Fragment) getSupportFragmentManager().findFragmentByTag(position + "");
            FragmentTransaction mFt = getSupportFragmentManager().beginTransaction();

            if (mFragment == null) {
                switch (layoutIds[position]) {

                    case R.layout.online_tab_fragment:
                        mFragment = OnlineTabFragment.getInstance();
                        break;
                    case R.layout.bt_tab_fragment:
                        mFragment = BtTabFragment.getInstance();
                        break;
                    case R.layout.download_tab_fragment:
                        mFragment = DownloadTabFragment.getInstance();
                        break;

                }
                mFt.add(R.id.home_tab_container, mFragment, String.valueOf(position));
            } else {
                mFt.attach(mFragment);
            }
            mFt.commit();



        }

        @Override
        public void onTabUnselected(TvTabLayout.Tab tab) {
            if (mFragment != null) {
                FragmentTransaction mFt = getSupportFragmentManager().beginTransaction();
                mFt.detach(mFragment);
                mFt.commit();
            }
        }

        @Override
        public void onTabReselected(TvTabLayout.Tab tab) {

        }
    }
}
