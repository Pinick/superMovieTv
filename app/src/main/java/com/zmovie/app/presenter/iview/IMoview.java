package com.zmovie.app.presenter.iview;


import com.zmovie.app.domain.BtInfo;
import com.zmovie.app.domain.RecentUpdate;

/**
 * Created by huangyong on 2018/1/26.
 */

public interface IMoview {
    void loadData(RecentUpdate info);
    void loadError(String msg);

    void loadMore(RecentUpdate result);
}
