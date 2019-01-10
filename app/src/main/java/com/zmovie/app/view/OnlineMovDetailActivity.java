package com.zmovie.app.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huangyong.playerlib.CustomIjkplayer;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.zmovie.app.R;
import com.zmovie.app.adapter.PlayListAdapter;
import com.zmovie.app.data.GlobalMsg;
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
    private CustomIjkplayer ijkplayer;
    private String descContent;
    private FocusBorder mFocusBorder;
    private PlayUrlBean playUrlBean;
//    private TvRecyclerView recyclerView2;
    private TextView shortDesc;
    private PlayerHelper playerHelper;
    private View fullScreen;
    private View descView;
    private ChoseSerisDialog serisDialog;

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
        titleView.setText(title);
        if (!TextUtils.isEmpty(descBean.getDesc())){
            shortDesc.setText("简介："+descBean.getDesc());
        }else {
            for (int i = 0; i < descBean.getHeader_key().size(); i++) {
                if (descBean.getHeader_key().get(i).contains("演员")){
                    shortDesc.setText("主演："+descBean.getHeader_value().get(i));
                }
            }

        }

        //海报右边的短简介
        final StringBuilder mDescHeader = new StringBuilder();
        for (int i = 0; i < descBean.getHeader_key().size(); i++) {
            if (TextUtils.isEmpty(descBean.getHeader_value().get(i).trim())){
                continue;
            }
            mDescHeader.append(descBean.getHeader_key().get(i)+descBean.getHeader_value().get(i)+"\n");
        }

        descView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DescMovieDialog descDialog = new DescMovieDialog(OnlineMovDetailActivity.this,1);
                descDialog.setDescData(mDescHeader.toString()+descBean.getDesc(),posterUrl);
                descDialog.show();
            }
        });
        tvDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescMovieDialog descDialog = new DescMovieDialog(OnlineMovDetailActivity.this,1);
                descDialog.setDescData(mDescHeader.toString()+descBean.getDesc(),posterUrl);
                descDialog.show();
            }
        });



        playUrlBean = gson.fromJson(downUrl, PlayUrlBean.class);
        ArrayList<String> playM3u8List = new ArrayList<>();
        ArrayList<String> playWebUrlList = new ArrayList<>();
        if (playUrlBean.getM3u8()!=null){
            playerHelper.startPlay(playUrlBean.getM3u8().get(0).getUrl(),playUrlBean.getM3u8().get(0).getTitle());
        }

        for (int i = 0; i < playUrlBean.getNormal().size(); i++) {
            playWebUrlList.add("第"+(i+1)+"集");
        }
        for (int i = 0; i < playUrlBean.getM3u8().size(); i++) {
            if (playUrlBean.getM3u8().size()==1){
                playM3u8List.add("在线观看");
            }else {
                if (playUrlBean.getM3u8().size()>10){
                    playM3u8List.add("第"+(i+1)+"集");
                    if (i==9){
                        break;
                    }
                }

            }

        }

        final PlayListAdapter mAdapter = new PlayListAdapter(OnlineMovDetailActivity.this, false);
        mAdapter.setDatas(playM3u8List);
//

        serisDialog = new ChoseSerisDialog(OnlineMovDetailActivity.this,playUrlBean.getM3u8().size(), new ChoseSerisDialog.OnItemClicked() {
            @Override
            public void clicked(int postion) {
                Log.e("playurllist",playUrlBean.getM3u8().get(postion).getUrl()+"");
               // playerHelper.startPlayFullScreen(playUrlBean.getM3u8().get(postion).getUrl(),playUrlBean.getM3u8().get(postion).getTitle());
                playerHelper.startPlayFullScreen(playUrlBean.getM3u8().get(postion).getUrl(),playUrlBean.getM3u8().get(postion).getTitle());
                serisDialog.dismiss();
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("qq", "onFocusChange1==> " +hasFocus);
                if(!hasFocus) {
                    mFocusBorder.setVisible(false);
                }
            }
        });
        //Glide.with(this).load(posterUrl).placeholder(R.drawable.mv_place_holder).into(ijkplayer);
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
        ijkplayer = findViewById(R.id.video_view);
        descView = findViewById(R.id.show_desc);
        //全屏按钮
        fullScreen = findViewById(R.id.fullscreen_view);
        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerHelper.makeFullscreen();
            }
        });
        fullScreen.requestFocus();
        fullScreen.setNextFocusLeftId(R.id.fullscreen_view);

        shortDesc = findViewById(R.id.desc_short);
        shortDesc.setNextFocusLeftId(R.id.desc_short);


        recyclerView = findViewById(R.id.downlist);
//        recyclerView2 = findViewById(R.id.downlist2);

        recyclerView.setNextFocusUpId(R.id.fullscreen_view);

        recyclerView.setSpacingWithMargins(12, 20);
//        recyclerView2.setSpacingWithMargins(12,20);

        //播放器
       playerHelper = PlayerHelper.getInstance();
        playerHelper.init(this,ijkplayer);


        titleView = findViewById(R.id.detai_title);

        recyclerView.setOnItemListener(new SimpleOnItemListener() {

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                itemView.setSelected(true);
            }
            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                if (position==9){
                    serisDialog.show();
                }else {
                   playerHelper.startPlay(playUrlBean.getM3u8().get(position).getUrl(),playUrlBean.getM3u8().get(position).getTitle());
                }
            }
        });
        tvDescription = findViewById(R.id.desc_short);

    }
    protected void onMoveFocusBorder(View focusedView, float scale, float roundRadius) {
        if(null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale, roundRadius));
        }
    }

    @Override
    public void onBackPressed() {

        if (ijkplayer.isFullScreen()){
            playerHelper.cancelFullscreen();
            fullScreen.requestFocus();
            return;
        }

        if (!ijkplayer.onBackPressed()) {
            super.onBackPressed();
        }
//        if ((System.currentTimeMillis() - mExitTime) > 2000) {
//            Toast.makeText(this, "再按一次退出噢", Toast.LENGTH_SHORT).show();
//            mExitTime = System.currentTimeMillis();
//        } else {
//            finish();
//            overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
//        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ijkplayer.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ijkplayer.pause();
    }


    @Override
    protected void onDestroy() {
        ijkplayer.release();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (ijkplayer.isFullScreen()){
            playerHelper.onKeyEvent(keyCode,event);
            return true;
        }


        return super.onKeyDown(keyCode, event);
    }
}
