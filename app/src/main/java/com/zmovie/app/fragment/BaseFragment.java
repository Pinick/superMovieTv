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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.zmovie.app.R;
import com.zmovie.app.focus.FocusBorder;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public abstract class BaseFragment extends Fragment {
    private TextView mPositionText;
    private TextView mCountText;
    private TextView mStateText;
    protected Toast mToast;
    protected FocusBorder mFocusBorder;
    private Unbinder unbinder;

    private RecyclerView mRecyclerView;
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
            updateState(scrollState);
        }

        @Override
        public void onScrolled(RecyclerView rv, int i, int i2) {
            updatePosition(rv);
        }

    };
    
    abstract int getLayoutId();
    
    protected void onMoveFocusBorder(View focusedView, float scale) {
        if(null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale));
        }
    }

    protected void onMoveFocusBorder(View focusedView, float scale, float roundRadius) {
        if(null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale, roundRadius));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() instanceof FocusBorderHelper) {
            mFocusBorder = ((FocusBorderHelper)getActivity()).getFocusBorder();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Activity activity = getActivity();

        mToast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        
        mPositionText = (TextView) view.getRootView().findViewById(R.id.position);
        mCountText = (TextView) view.getRootView().findViewById(R.id.count);
        mStateText = (TextView) view.getRootView().findViewById(R.id.state);
        updateState(SCROLL_STATE_IDLE);
    }
    
    public void showToast(String str) {
        mToast.setText(str);
        mToast.show();
    }
    
    public void showToast(int resId) {
        mToast.setText(resId);
        mToast.show();
    }
    
    protected void setScrollListener(RecyclerView recyclerView) {
        if(mRecyclerView != recyclerView) {
            if(null != mRecyclerView) {
                mRecyclerView.removeOnScrollListener(mOnScrollListener);
            }
            recyclerView.addOnScrollListener(mOnScrollListener);
            mRecyclerView = recyclerView;
        }
    }

    private void updatePosition(RecyclerView rv) {
        if(null != mPositionText && null != mCountText) {
            final int count = rv.getChildCount();
            final int first = count == 0 ? 0 : rv.getChildAdapterPosition(rv.getChildAt(0));
            mPositionText.setText("First: " + first);
            mCountText.setText("Count: " + count);
        }
    }

    private void updateState(int scrollState) {
        if(null != mStateText) {
            String stateName = "Undefined";
            switch (scrollState) {
                case SCROLL_STATE_IDLE:
                    stateName = "Idle";
                    break;

                case SCROLL_STATE_DRAGGING:
                    stateName = "Dragging";
                    break;

                case SCROLL_STATE_SETTLING:
                    stateName = "Flinging";
                    break;
            }

            mStateText.setText(stateName);
        }
    }

    public interface FocusBorderHelper {
        FocusBorder getFocusBorder();
    }
}
