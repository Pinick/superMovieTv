package com.zmovie.app.domain;

/**
 * creator huangyong
 * createTime 2019/1/5 下午2:52
 * path com.zmovie.app.domain
 * description:手动拼装的实体，播放列表
 */
public class PlayItemBean {

    private String playUrl;

    private String rootTitle;

    public String getPlayTitle() {
        return playTitle;
    }

    public void setPlayTitle(String playTitle) {
        this.playTitle = playTitle;
    }

    private String playTitle;

    private int type;

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getRootTitle() {
        return rootTitle;
    }

    public void setRootTitle(String rootTitle) {
        this.rootTitle = rootTitle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
