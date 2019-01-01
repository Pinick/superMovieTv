package com.zmovie.app.data;

/**
 * Created by owen on 2017/7/6.
 */

public class ItemBean {
    public int id;
    public String title;
    public String imgUrl;
    public String detailUrl;

    public String MovDesc;
    public String donwUrl;
    public String donwtitle;


    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getMovDesc() {
        return MovDesc;
    }

    public void setMovDesc(String movDesc) {
        MovDesc = movDesc;
    }

    public String getDonwUrl() {
        return donwUrl;
    }

    public void setDonwUrl(String donwUrl) {
        this.donwUrl = donwUrl;
    }

    public String getDonwtitle() {
        return donwtitle;
    }

    public void setDonwtitle(String donwtitle) {
        this.donwtitle = donwtitle;
    }

    public ItemBean(){}
    
    public ItemBean(int id, String imgUrl) {
        this(id, imgUrl, imgUrl.substring(imgUrl.lastIndexOf("/")));
    }
    
    public ItemBean(int id, String imgUrl, String title) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.title = title;
    }
}
