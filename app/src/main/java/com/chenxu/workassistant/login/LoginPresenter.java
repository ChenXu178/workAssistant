package com.chenxu.workassistant.login;

import android.content.Context;

import com.chenxu.workassistant.config.Constant;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.chenxu.workassistant.config.Constant.EMAIL_STATE;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View mView;
    private LoginContract.Model mModel;
    private Context mContext;

    public LoginPresenter(LoginContract.View view,Context context){
        this.mView = view;
        this.mContext = context;
        this.mModel = new LoginModel(this);
    }


    @Override
    public void start() {

    }

    @Override
    public void emailLogin(boolean isSave,int emailType, String account, String password) {
        mModel.login(emailType, account, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean){
                            if (isSave){
                                Constant.editorSetting.putBoolean(Constant.EMAIL_SAVE_ACCOUNT,isSave).putInt(Constant.EMAIL_SERVER_TYPE,emailType).putString(Constant.EMAIL_ACCOUNT,account).putString(Constant.EMAIL_PASSWORD,password).putBoolean(EMAIL_STATE,true).commit();
                            }else {
                                Constant.editorSetting.putBoolean(Constant.EMAIL_SAVE_ACCOUNT,isSave).putInt(Constant.EMAIL_SERVER_TYPE,emailType).putString(Constant.EMAIL_ACCOUNT,account).putString(Constant.EMAIL_PASSWORD,"").putBoolean(EMAIL_STATE,true).commit();
                            }
                            mView.onSuccess();
                        }else {
                            mView.onError();
                        }
                    }
                });
    }
}
