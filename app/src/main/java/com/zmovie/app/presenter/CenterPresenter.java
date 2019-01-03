package com.zmovie.app.presenter;

import android.content.Context;

import com.zmovie.app.domain.RecentUpdate;
import com.zmovie.app.http.ApiManager;
import com.zmovie.app.presenter.iview.IAllView;
import com.zmovie.app.presenter.iview.IMoview;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CenterPresenter extends BasePresenter<IMoview> {


    public CenterPresenter(Context context, IMoview iview) {
        super(context, iview);

    }

    @Override
    public void release() {
        unSubcription();
    }


    public void getLibraryDdata(String type,int page,int pagesize){

        Subscription subscription = ApiManager
                .getRetrofitInstance()
                .getLibraryDatas(type,page,pagesize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RecentUpdate>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        iview.loadError("");
                    }
                    @Override
                    public void onNext(RecentUpdate result) {
                        if (result.getData().size()>0){
                            iview.loadData(result);
                        }else {
                            iview.loadError("");
                        }
                    }
                });
        addSubscription(subscription);
    }
    public void getLibraryMoreDdata(String type,int page,int pagesize){

        Subscription subscription = ApiManager
                .getRetrofitInstance()
                .getLibraryDatas(type,page,pagesize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RecentUpdate>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        iview.loadError("");
                    }
                    @Override
                    public void onNext(RecentUpdate result) {
                        if (result.getData().size()>0){
                            iview.loadMore(result);
                        }else {
                            iview.loadError("");
                        }
                    }
                });
        addSubscription(subscription);
    }

}
