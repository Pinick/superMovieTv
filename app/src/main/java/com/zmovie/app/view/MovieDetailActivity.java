package com.zmovie.app.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.zmovie.app.R;
import com.zmovie.app.adapter.DownItemAdapter;
import com.zmovie.app.data.GlobalMsg;
import com.zmovie.app.display.DisplayAdaptive;
import com.zmovie.app.domain.DetailInfo;
import com.zmovie.app.domain.DetailItemInfo;
import com.zmovie.app.focus.FocusBorder;

import java.util.ArrayList;


/**
 *  intent.putExtra(GlobalMsg.KEY_POST_IMG, finalImgUrl);
 intent.putExtra(GlobalMsg.KEY_DOWN_URL,datas.getData().get(position).getDownLoadUrl());
 intent.putExtra(GlobalMsg.KEY_MOVIE_TITLE,datas.getData().get(position).getDownLoadName());
 intent.putExtra(GlobalMsg.KEY_MOVIE_DETAIL,datas.getData().get(position).getMvdesc());
 */
public class MovieDetailActivity extends AppCompatActivity {

    private String title;
    private String downUrl;
    private String posterUrl;
    private String mvdescTx;
    private TextView sdesc;
    private TvRecyclerView recyclerView;
    private String posterImagUrl;
    private String imgScreenShot;
    private TextView titleView;
    private String downItemTitle;
    private String[] downItemList;
    private LinearLayoutManager layoutManager;
    private String[] items;
    private String playUrl;
    private String playTitle;
    private ImageView poster;
    private String descContent;
    private FocusBorder mFocusBorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_layout);

        initData();
        initView();

    }

    private void initData() {
        Intent intent = getIntent();
        posterUrl = intent.getStringExtra(GlobalMsg.KEY_POST_IMG);
        playUrl = intent.getStringExtra(GlobalMsg.KEY_PLAY_URL);
        playTitle = intent.getStringExtra(GlobalMsg.KEY_PLAY_TITLE);
        downItemTitle = intent.getStringExtra(GlobalMsg.KEY_MOVIE_DOWN_ITEM_TITLE);
        downItemList = downItemTitle.split(",");


        if (posterUrl.contains(",")){
           String[] imgArr =  posterUrl.split(",");
            imgScreenShot = imgArr[1];
        }

        posterImagUrl = posterUrl.split(",")[0];
        downUrl = intent.getStringExtra(GlobalMsg.KEY_DOWN_URL);
        title = intent.getStringExtra(GlobalMsg.KEY_MOVIE_TITLE);
        mvdescTx = intent.getStringExtra(GlobalMsg.KEY_MOVIE_DETAIL);
    }


    private void initView() {
        // 移动框
        if(null == mFocusBorder) {
//            mFocusBorder = new FocusBorder.Builder().asDrawable().borderResId(R.drawable.focus).build(this);
            mFocusBorder = new FocusBorder.Builder()
                    .asColor()
                    .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 2)
                    .borderColor(getResources().getColor(R.color.item_activated_color))
                    .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 18)
                    .borderRadius(0)
                    .build(this);
        }
        poster = findViewById(R.id.video_view);
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

            }
        });
        sdesc = findViewById(R.id.detail_desc);



        DetailInfo info = new DetailInfo();

        //海报右边的短简介
        sdesc.setText(mvdescTx);
        sdesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DescDialog descDialog = new DescDialog(MovieDetailActivity.this,1);
                descDialog.setDescData(mvdescTx,posterUrl.split(",")[1]);
                descDialog.show();
            }
        });
        sdesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    float radius = DisplayAdaptive.getInstance().toLocalPx(0);
                    onMoveFocusBorder(view, 1.0f, radius);
                }
            }
        });


        String[] downUrl = this.downUrl.split(",");
        ArrayList url = new ArrayList();
        for (int i = 0; i < downUrl.length; i++) {
            url.add(downUrl[i]);
        }
        ArrayList downTitle = new ArrayList();
        for (int i = 0; i < downItemList.length; i++) {
            downTitle.add(downItemList[i]);
        }
        //截屏
        info.setImgScreenShot(imgScreenShot);
        //下载地址
        info.setDownUrl(url);
        //
        info.setDownUrlName(downTitle);
        //下载页显示的海报
        info.setImgUrl(posterImagUrl);

        ArrayList inS = new ArrayList();
        for (int i = 0; i < downItemList.length; i++) {
            DetailItemInfo inList = new DetailItemInfo();
            inList.setMvTitle(downItemList[i]);
            inList.setMvUrl(downUrl[i]);
            inS.add(inList);
        }


        DownItemAdapter adapter = new DownItemAdapter(this);
        adapter.setDatas(inS);
        recyclerView.setAdapter(adapter);
       /* detailAdapter = new DetailAdapter(downItemList,list,this);
        DownListAdapter dialogAdapter = new DownListAdapter(downItemList,list,this);
        recyclerView.setAdapter(detailAdapter);
*/
        //添加点播按钮布局到底部
       /* *//**
         *  url = getIntent().getStringExtra(Params.PROXY_PALY_URL);
         title = getIntent().getStringExtra(Params.TASK_TITLE_KEY);
         urlMd5 = getIntent().getStringExtra(Params.URL_MD5_KEY);
         movieProgress = getIntent().getStringExtra(Params.MOVIE_PROGRESS);
         poster = getIntent().getStringExtra(Params.POST_IMG_KEY);
         *//*
        if (!TextUtils.isEmpty(playUrl)){
            View playView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.online_item,null);
            RecyclerView onlineBt = playView.findViewById(R.id.rvOnline);
            String[] playUri = playUrl.split(",");
            String[] playTitles = playTitle.split(",");
            ArrayList<BtPlayInfo> infos = new ArrayList<>();
            for (int i = 0; i < playUri.length; i++) {
                BtPlayInfo info1 = new BtPlayInfo();
                info1.setMovPoster(posterImagUrl);
                info1.setMovTitle(title);
                info1.setProgress("0");
                info1.setMovName(playTitles[i]);
                info1.setMovPlayUrl(playUri[i]);
                infos.add(info1);
            }
            OnlinePlayAdapter playAdapter = new OnlinePlayAdapter(infos);
            onlineBt.setLayoutManager(new GridLayoutManager(getApplicationContext(),4));
            onlineBt.setAdapter(playAdapter);
            mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(detailAdapter);
            mHeaderAndFooterWrapper.addHeaderView(playView);
            recyclerView.setAdapter(mHeaderAndFooterWrapper);
            mHeaderAndFooterWrapper.notifyDataSetChanged();
        }*/


        Glide.with(this).load(posterImagUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                poster.setImageBitmap(resource);
            }
        });

    }
    protected void onMoveFocusBorder(View focusedView, float scale, float roundRadius) {
        if(null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale, roundRadius));
        }
    }
    protected void onReMoveFocusBorder(View focusedView, float scale, float roundRadius) {
        if(null != mFocusBorder) {
            mFocusBorder.setVisible(false);
        }
    }




   /* @Override
    public void clicked(String url, String imgUrl) {
        Toast.makeText(this, "下载任务已添加", Toast.LENGTH_SHORT).show();
        Log.e("dowurllsit",url+"\n"+imgUrl);
        TaskLibHelper.addNewTask(url, Params.DEFAULT_PATH,imgUrl,getApplicationContext());
    }*/
}
