package com.chenxu.workassistant.login;

import com.chenxu.workassistant.config.Applacation;
import com.chenxu.workassistant.config.Constant;
import com.sun.mail.imap.IMAPStore;

import java.util.Properties;

import javax.mail.Session;
import javax.mail.Store;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


public class LoginModel implements LoginContract.Model {

    private LoginContract.Presenter mPresenter;

    public LoginModel(LoginContract.Presenter presenter){
        this.mPresenter = presenter;
    }

    @Override
    public Observable<Boolean> login(int emailType, String account, String password) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                String emailIMAP = Constant.EMAIL_SERVER_IMAP[emailType];
                Properties prop = System.getProperties();
                prop.put("mail.store.protocol", "imap");
                prop.put("mail.imap.host",emailIMAP);
                try{
                    Session session = Session.getInstance(prop);
                    IMAPStore store = (IMAPStore) session.getStore("imap");
                    store.connect(account,password);
                    Applacation.setStore(store);
                    emitter.onNext(true);
                }catch (Exception e){
                    e.printStackTrace();
                    emitter.onNext(false);
                }
            }
        });
    }
}
