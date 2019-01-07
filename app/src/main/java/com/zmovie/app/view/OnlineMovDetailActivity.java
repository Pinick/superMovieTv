package com.zmovie.app.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bftv.myapplication.config.KeyParam;
import com.bftv.myapplication.view.LineWebview;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.zmovie.app.R;
import com.zmovie.app.adapter.CommonRecyclerViewAdapter;
import com.zmovie.app.adapter.CommonRecyclerViewHolder;
import com.zmovie.app.adapter.ListAdapter;
import com.zmovie.app.adapter.PlayListAdapter;
import com.zmovie.app.adapter.PlayM3u8ItemAdapter;
import com.zmovie.app.data.GlobalMsg;
import com.zmovie.app.display.DisplayAdaptive;
import com.zmovie.app.domain.DescBean;
import com.zmovie.app.domain.PlayUrlBean;
import com.zmovie.app.focus.FocusBorder;

import java.util.ArrayList;


/**
 *  intent.putExtra(GlobalMsg.KEY_POST_IMG, finalImgUrl);
 intent.putExtra(GlobalMsg.KEY_DOWN_URL,datas.getData().get(position).getDownLoadUrl());
 intent.putExtra(GlobalMsg.KEY_MOVIE_TITLE,datas.getData().get(position).getDownLoadName());
 intent.putExtra(GlobalMsg.KEY_MOVIE_DETAIL,datas.getData().get(position).getMvdesc());
 */
public class OnlineMovDetailActivity extends Activity {

    private String title;
    private String downUrl;
    private String posterUrl;
    private String movDescription;
    private TextView tvDescription;
    private TvRecyclerView recyclerView;
    private String imgScreenShot;
    private TextView titleView;
    private String downItemTitle;
    private String[] downItemList;
    private String[] items;
    private String playUrl;
    private String playTitle;
    private ImageView poster;
    private String descContent;
    private FocusBorder mFocusBorder;
    private PlayUrlBean playUrlBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_mov_detail_layout);
        initView();
        initData();


    }

    private void initData() {
        Intent intent = getIntent();
        posterUrl = intent.getStringExtra(GlobalMsg.KEY_POST_IMG);
        playUrl = intent.getStringExtra(GlobalMsg.KEY_PLAY_URL);
        playTitle = intent.getStringExtra(GlobalMsg.KEY_PLAY_TITLE);
        downItemTitle = intent.getStringExtra(GlobalMsg.KEY_MOVIE_DOWN_ITEM_TITLE);
        downItemList = downItemTitle.split(",");

        downUrl = intent.getStringExtra(GlobalMsg.KEY_DOWN_URL);
        title = intent.getStringExtra(GlobalMsg.KEY_MOVIE_TITLE);
        movDescription = intent.getStringExtra(GlobalMsg.KEY_MOVIE_DETAIL);

        Gson gson = new Gson();
        final DescBean descBean = gson.fromJson(movDescription, DescBean.class);

        //海报右边的短简介
        final StringBuilder mDescHeader = new StringBuilder();
        for (int i = 0; i < descBean.getHeader_key().size(); i++) {
            if (TextUtils.isEmpty(descBean.getHeader_value().get(i).trim())){
                continue;
            }
            mDescHeader.append(descBean.getHeader_key().get(i)+descBean.getHeader_value().get(i)+"\n");
        }

        tvDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DescMovieDialog descDialog = new DescMovieDialog(OnlineMovDetailActivity.this,1);
                descDialog.setDescData(mDescHeader.toString()+descBean.getDesc(),posterUrl);
                descDialog.show();
            }
        });



        playUrlBean = gson.fromJson(downUrl, PlayUrlBean.class);
        ArrayList<String> playM3u8List = new ArrayList<>();

        for (int i = 0; i < playUrlBean.getM3u8().size(); i++) {
            playM3u8List.add("第"+i+"集");
        }

        final PlayListAdapter mAdapter = new PlayListAdapter(OnlineMovDetailActivity.this, false);
        mAdapter.setDatas(playM3u8List);
        //播放列表
//        PlayM3u8ItemAdapter adapter = new PlayM3u8ItemAdapter(this);
//        if (playUrlBean !=null){
//            adapter.setDatas(playUrlBean.getM3u8());
//            recyclerView.setAdapter(adapter);
//        }

        recyclerView.setAdapter(new CommonRecyclerViewAdapter(OnlineMovDetailActivity.this) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_nested_recyclerview;
            }

            @Override
            public int getItemCount() {
                return 2;
            }

            @Override
            public void onBindItemHolder(CommonRecyclerViewHolder helper, Object item, int position) {
                TvRecyclerView recyclerView = helper.getHolder().getView(R.id.nestlist);
                recyclerView.setSpacingWithMargins(10, 10);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setSelectedItemAtCentered(true);

                recyclerView.setOnItemListener(new TvRecyclerView.OnItemListener() {
                    @Override
                    public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {

                    }

                    @Override
                    public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                        onMoveFocusBorder(itemView, 1.1f, 0);
                    }

                    @Override
                    public void onItemClick(TvRecyclerView parent, View itemView, int position) {

                    }
                });

            }

        });

        //Glide.with(this).load(posterUrl).placeholder(R.drawable.mv_place_holder).into(poster);
    }


    private void initView() {
        // 移动框
        if(null == mFocusBorder) {
            mFocusBorder = new FocusBorder.Builder()
                    .asColor()
                    .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 2)
                    .borderColor(getResources().getColor(R.color.item_activated_color))
                    .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 18)
                    .borderRadius(0)
                    .build(this);
        }
        poster = findViewById(R.id.detail_poster);
        recyclerView = findViewById(R.id.downlist);
        recyclerView.setSpacingWithMargins(12, 20);
        titleView = findViewById(R.id.detai_title);
        titleView.setText(title);
        recyclerView.setOnItemListener(new SimpleOnItemListener() {

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                float radius = DisplayAdaptive.getInstance().toLocalPx(0);
                onMoveFocusBorder(itemView, 1.1f, radius);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                Intent intent = new Intent(OnlineMovDetailActivity.this, LineWebview.class);
                intent.putExtra(KeyParam.PLAYURL,playUrlBean.getNormal().get(position).getUrl());
                startActivity(intent);

            }
        });
        tvDescription = findViewById(R.id.detail_desc);

    }
    protected void onMoveFocusBorder(View focusedView, float scale, float roundRadius) {
        if(null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale, roundRadius));
        }
    }

}
