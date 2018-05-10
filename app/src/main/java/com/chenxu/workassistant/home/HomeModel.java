package com.chenxu.workassistant.home;

import com.chenxu.workassistant.config.Applacation;
import com.chenxu.workassistant.config.Constant;
import com.chenxu.workassistant.dao.GreenDaoManager;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Session;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class HomeModel implements HomeContract.Model {

    private HomeContract.Presenter mPresenter;
    private IMAPStore store;
    private IMAPFolder folder;

    public HomeModel(HomeContract.Presenter presenter){
        this.mPresenter = presenter;
    }


    @Override
    public Observable<Boolean> loginEmail() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                if (Applacation.getStore() == null){
                    int emailType = Constant.spSetting.getInt(Constant.EMAIL_SERVER_TYPE,0);
                    String account = Constant.spSetting.getString(Constant.EMAIL_ACCOUNT,"");
                    String password = Constant.spSetting.getString(Constant.EMAIL_PASSWORD,"");
                    String emailIMAP = Constant.EMAIL_SERVER_IMAP[emailType];
                    if ("".equals(account) || "".equals(password)){
                        emitter.onNext(false);
                    }else {
                        Properties prop = System.getProperties();
                        prop.put("mail.store.protocol", "imap");
                        prop.put("mail.imap.host",emailIMAP);
                        try{
                            Session session = Session.getInstance(prop);
                            store = (IMAPStore) session.getStore("imap");
                            store.connect(account,password);
                            Applacation.setStore(store);
                            folder = (IMAPFolder) store.getFolder("INBOX");
                            folder.open(Folder.READ_WRITE);
                            emitter.onNext(true);
                        }catch (Exception e){
                            e.printStackTrace();
                            emitter.onNext(false);
                        }
                    }
                }else {
                    store = Applacation.getStore();
                    folder = (IMAPFolder) store.getFolder("INBOX");
                    folder.open(Folder.READ_WRITE);
                    emitter.onNext(true);
                }

            }
        });
    }

    @Override
    public Observable<Integer> getUnreadEmailCount() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                if (folder != null){
                    e.onNext(folder.getUnreadMessageCount());
                }else {
                    e.onNext(0);
                }
            }
        });
    }
}
