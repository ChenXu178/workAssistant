package com.chenxu.workassistant.home;

import com.chenxu.workassistant.dao.GreenDaoManager;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class HomeModel implements HomeContract.Model {

    private HomeContract.Presenter mPresenter;

    public HomeModel(HomeContract.Presenter presenter){
        this.mPresenter = presenter;
    }

    @Override
    public Observable<Integer> queryEnclosureCount() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                int count = GreenDaoManager.getEnclosureEntityDao().loadAll().size();
                e.onNext(count);
            }
        });
    }
}
