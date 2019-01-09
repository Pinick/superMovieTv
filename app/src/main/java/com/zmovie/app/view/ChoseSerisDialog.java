package com.zmovie.app.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;

import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.zmovie.app.R;
import com.zmovie.app.adapter.PlayListWindowAdapter;

import java.util.ArrayList;

public class ChoseSerisDialog extends Dialog {


    private final TvRecyclerView seriList;
    private OnItemClicked listener;

    public ChoseSerisDialog(@NonNull Context context, int playListSize, OnItemClicked listener) {
        super(context, R.style.Dialog_Fullscreens);
        this.listener = listener;
        setContentView(R.layout.chose_seris_layout);

        seriList = findViewById(R.id.serilist);
        seriList.setSpacingWithMargins(20, 30);
        ArrayList<String> playM3u8 =new ArrayList<>();

        for (int i = 0; i < playListSize; i++) {
            playM3u8.add("第"+(i+1)+"集");
        }
        PlayListWindowAdapter playListAdapter = new PlayListWindowAdapter(context);
        playListAdapter.setDatas(playM3u8);

        seriList.setSelectedItemAtCentered(true);
        seriList.setAdapter(playListAdapter);
        setListener();
    }
    private void setListener() {


        seriList.setOnItemListener(ItemClicklistener);

    }
    SimpleOnItemListener ItemClicklistener =   new SimpleOnItemListener() {

        @Override
        public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
        }

        @Override
        public void onItemClick(TvRecyclerView parent, View itemView, int position) {
            if (listener!=null){
                listener.clicked(position);
            }
        }
    };

    interface OnItemClicked{
        void clicked(int postion);
    }
}
