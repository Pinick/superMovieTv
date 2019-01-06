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

package com.zmovie.app.adapter;

import android.content.Context;

import com.zmovie.app.R;
import com.zmovie.app.domain.OnlinePlayInfo;
import com.zmovie.app.domain.RecentUpdate;


public class OnlineMvAdapter extends CommonRecyclerViewAdapter<OnlinePlayInfo.DataBean> {
    public OnlineMvAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item;
    }

    @Override
    public void onBindItemHolder(CommonRecyclerViewHolder helper, OnlinePlayInfo.DataBean item, int position) {
        helper.getHolder()
                .setText(R.id.title, item.getDownLoadName())
                .setText(R.id.tv_changed,item.getDownLoadName())
                .showImage(R.id.image, item.getDownimgurl());
    }
}
