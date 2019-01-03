package com.zmovie.app.presenter.iview;


import com.zmovie.app.domain.RecentUpdate;

public interface IAllView {

    void loadSuccess(RecentUpdate movieBean);

    void loadMore(RecentUpdate movieBean);

    void loadFail();
}
