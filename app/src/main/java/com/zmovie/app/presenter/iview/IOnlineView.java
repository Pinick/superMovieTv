package com.zmovie.app.presenter.iview;


import com.zmovie.app.domain.OnlinePlayInfo;

public interface IOnlineView {
    void loadData(OnlinePlayInfo info);

    void loadError(String msg);

    void loadMore(OnlinePlayInfo result);
}
