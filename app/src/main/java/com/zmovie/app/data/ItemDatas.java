package com.zmovie.app.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by owen on 2017/7/6.
 */

public class ItemDatas {
    
    public static List<ItemBean> getDatas(int count) {
        return getMovietDatas(count);
//        List<ItemBean> mItems = new ArrayList<>();
//        ItemBean itemBean;
//        for(int i=0; i<count; i++) {
//            itemBean = new ItemBean(i, ImgDatas.getUrl());
//            mItems.add(itemBean);
//        }
//        return mItems;
    }

    public static List<ItemBean> getMovietDatas(int count) {
        List<ItemBean> mItems = new ArrayList<>();
        ItemBean itemBean;
        for(int i=0; i<count; i++) {
            itemBean = new ItemBean(i, ImgDatas.getMovieUrl());
            mItems.add(itemBean);
        }
        return mItems;
    }
}
