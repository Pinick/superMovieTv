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

package com.zmovie.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;

import com.owen.tvrecyclerview.utils.Loger;
import com.zmovie.app.focus.FocusBorder;
import com.zmovie.app.fragment.BaseFragment;
import com.zmovie.app.fragment.GridFragment;
import com.zmovie.app.fragment.HomeFragment;
import com.zmovie.app.fragment.ListFragment;
import com.zmovie.app.fragment.MetroFragment;
import com.zmovie.app.fragment.SpannableFragment;
import com.zmovie.app.fragment.StaggeredFragment;
import com.zmovie.app.fragment.UpdateDataFragment;
import com.zmovie.app.fragment.V7GridFragment;
import com.zmovie.app.tablayout.TabLayout;
import com.zmovie.app.tablayout.TvTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity implements BaseFragment.FocusBorderHelper{
    private final String LOGTAG = MainActivity.class.getSimpleName();

    @BindView(R.id.tab_layout)
    TvTabLayout mTabLayout;
    
    private FocusBorder mFocusBorder;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        
        Loger.isDebug = true; //是否打开TvRecyclerView的log打印
        
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
        
        mTabLayout.setScaleValue(1.1f);
        mTabLayout.addOnTabSelectedListener(new TabSelectedListener());
        mTabLayout.addTab(
                mTabLayout.newTab()
                        .setText("最近更新")
//                        .setIcon(R.drawable.ic_grid)
                , true);
        mTabLayout.addTab(
                mTabLayout.newTab()
                        .setText("在线观看")
//                        .setIcon(R.drawable.ic_staggered)
                );
        mTabLayout.addTab(
                mTabLayout.newTab()
                        .setText("BT影库")
//                        .setIcon(R.drawable.ic_staggered)
               );
        mTabLayout.addTab(
                mTabLayout.newTab()
                        .setText("经典港片")
//                        .setIcon(R.drawable.ic_list)
                );

        mTabLayout.addTab(
                mTabLayout.newTab()
                        .setText("国产剧")
//                        .setIcon(R.drawable.ic_grid)
        );
        mTabLayout.addTab(
                mTabLayout.newTab()
                        .setText("Staggered")
//                        .setIcon(R.drawable.ic_staggered)
        );
        mTabLayout.addTab(
                mTabLayout.newTab()
                        .setText("Spannable")
//                        .setIcon(R.drawable.selector_ic_spannable)
        );
        mTabLayout.addTab(
                mTabLayout.newTab()
                        .setText("UpdateData")
//                        .setIcon(R.drawable.ic_launcher)
        );

    }

    @Override
    public FocusBorder getFocusBorder() {
        return mFocusBorder;
    }

    public class TabSelectedListener implements TabLayout.OnTabSelectedListener {
        private Fragment mFragment;
        private int[] layoutIds = {
                R.layout.layout_grid,
                R.layout.home_layout,
                R.layout.layout_metro_grid,
                R.layout.layout_list,
                R.layout.layout_grid2,
                R.layout.layout_staggered_grid,
                R.layout.layout_spannable_grid,
                R.layout.layout_update_data_changed
        };

        public TabSelectedListener() {
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            final int position = tab.getPosition();
            mFragment = (Fragment) getSupportFragmentManager().findFragmentByTag(position + "");
            FragmentTransaction mFt = getSupportFragmentManager().beginTransaction();
            if (mFragment == null) {
                switch (layoutIds[position]) {

                    case R.layout.layout_grid:
                        mFragment = GridFragment.newInstance();
                        break;
                    case R.layout.home_layout:
                        mFragment = HomeFragment.newInstance();
                        break;
                    case R.layout.layout_metro_grid:
                        mFragment = MetroFragment.newInstance();
                        break;
                    case R.layout.layout_list:
                        mFragment = ListFragment.newInstance();
                        break;
                    case R.layout.layout_grid2:
                        mFragment = V7GridFragment.newInstance();
                        break;
                    case R.layout.layout_staggered_grid:
                        mFragment = StaggeredFragment.newInstance();
                        break;
                    case R.layout.layout_spannable_grid:
                        mFragment = SpannableFragment.newInstance();
                        break;
                    case R.layout.layout_update_data_changed:
                        mFragment = UpdateDataFragment.newInstance();
                        break;
                }
                mFt.add(R.id.content, mFragment, String.valueOf(position));
            } else {
                mFt.attach(mFragment);
            }
            mFt.commit();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            if (mFragment != null) {
                FragmentTransaction mFt = getSupportFragmentManager().beginTransaction();
                mFt.detach(mFragment);
                mFt.commit();
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }
}
