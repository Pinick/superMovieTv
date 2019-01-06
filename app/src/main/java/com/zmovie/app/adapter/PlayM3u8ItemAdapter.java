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
import com.zmovie.app.domain.DetailItemInfo;
import com.zmovie.app.domain.PlayUrlBean;

import java.util.ArrayList;
import java.util.List;


public class PlayM3u8ItemAdapter extends CommonRecyclerViewAdapter<PlayUrlBean.M3u8Bean> {
    public PlayM3u8ItemAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.play_item;
    }

    @Override
    public void onBindItemHolder(CommonRecyclerViewHolder helper, PlayUrlBean.M3u8Bean item, int position) {
        helper.getHolder()
                .setImageResource(R.id.image,R.drawable.epg_login_bg)
                .setText(R.id.d_tv_changed,item.getTitle())
                .setText(R.id.title,item.getTitle())
        ;
    }
}
