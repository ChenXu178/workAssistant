package com.chenxu.workassistant.login;

import com.chenxu.workassistant.BasePresenter;
import com.chenxu.workassistant.BaseView;

import io.reactivex.Observable;

public interface LoginContract {

    interface View extends BaseView{
        void submit();
        void onError();
        void onSuccess();
    }

    interface Presenter extends BasePresenter{
        void emailLogin(boolean isSave,int emailType,String account,String password);
    }

    interface Model {
        Observable<Boolean> login(int emailType,String account,String password);
    }
}
