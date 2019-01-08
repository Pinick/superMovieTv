package com.zmovie.app.view;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;

import com.dueeeke.videoplayer.player.IjkPlayer;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.huangyong.playerlib.CustomControler;
import com.huangyong.playerlib.CustomIjkplayer;
import com.huangyong.playerlib.PlayerActivity;
import com.zmovie.app.R;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * creator huangyong
 * createTime 2019/1/8 上午9:32
 * path com.zmovie.app.view
 * description:
 */
public class PlayerHelper {

    private CustomIjkplayer ijkVideoView;
    private CustomControler controller;

    public void init(Context context,String title, CustomIjkplayer ijkVideoView ){
        this.ijkVideoView = ijkVideoView;

        controller = new CustomControler(context);
        controller.getThumb().setImageResource(R.drawable.share_loadingview_bg);
        controller.setFocusable(false);
        controller.setOnCheckListener(listener );
        ijkVideoView.setVideoController(controller);
        ijkVideoView.setFocusable(false);
        IjkPlayer ijkPlayer = new IjkPlayer(context) {
            @Override
            public void setEnableMediaCodec(boolean isEnable) {
                int value = isEnable ? 1 : 0;
                mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", value);
            }

            @Override
            public void setOptions() {
                super.setOptions();
                //设置ijkplayer支持concat协议，以播放分段视频
                mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "rtmp,concat,ffconcat,file,subfile,http,https,tls,rtp,tcp,udp,crypto");
            }
        };
        PlayerConfig playerConfig = new PlayerConfig.Builder()
                //启用边播边缓存功能
                // .autoRotate() //启用重力感应自动进入/退出全屏功能
                .enableMediaCodec()//启动硬解码，启用后可能导致视频黑屏，音画不同步
                .usingSurfaceView() //启用SurfaceView显示视频，不调用默认使用TextureView
                .savingProgress() //保存播放进度
                .disableAudioFocus() //关闭AudioFocusChange监听
                .setLooping() //循环播放当前正在播放的视频
                .setCustomMediaPlayer(ijkPlayer)
                .build();
        ijkVideoView.setPlayerConfig(playerConfig);

    }



    PlayerActivity.OncheckListener listener = new  PlayerActivity.OncheckListener() {
        @Override
        public void onChecked(int index) {

            if (ijkVideoView!=null&&controller!=null){

                switch (index){
                    case 0:
                        ijkVideoView.setSpeed(1.0f);
                        controller.setCheckUpdate("正常");
                        break;
                    case 1:
                        ijkVideoView.setSpeed(1.25f);
                        controller.setCheckUpdate("1.25x");
                        break;
                    case 2:
                        ijkVideoView.setSpeed(1.5f);
                        controller.setCheckUpdate("1.5x");
                        break;
                    case 3:
                        ijkVideoView.setSpeed(1.75f);
                        controller.setCheckUpdate("1.75x");
                        break;
                    case 4:
                        ijkVideoView.setSpeed(2.0f);
                        controller.setCheckUpdate("2.0x");
                        break;
                    default:
                        ijkVideoView.setSpeed(1.0f);
                        controller.setCheckUpdate("1.0x");
                        break;
                }

            }
        }
    };

    public void startPlay(String url, String title) {
        if (ijkVideoView!=null&&ijkVideoView.isPlaying()){
            ijkVideoView.stopPlayback();
        }
        if (ijkVideoView!=null){
            ijkVideoView.setUrl(url);
            ijkVideoView.setTitle(title);
            ijkVideoView.start();
        }
    }

    public void makeFullscreen(){
        if (ijkVideoView!=null&&!ijkVideoView.isFullScreen()){
            ijkVideoView.startFullScreen();
            ijkVideoView.setFocusable(true);
            ijkVideoView.requestFocus();
        }
    }

    public void cancelFullscreen(){
        if (ijkVideoView!=null&&ijkVideoView.isFullScreen()){
            ijkVideoView.stopFullScreen();
            ijkVideoView.setFocusable(false);
        }
    }



    public void onKeyEvent(int keyCode, KeyEvent event) {
        if (controller==null){
            return;
        }
        controller.onKeyDown(keyCode,event);
    }
}
