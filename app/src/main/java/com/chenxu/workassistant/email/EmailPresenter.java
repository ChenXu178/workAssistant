package com.chenxu.workassistant.email;

import android.content.Context;
import android.util.Log;

import com.chenxu.workassistant.config.Applacation;
import com.chenxu.workassistant.config.Constant;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.chenxu.workassistant.config.Constant.EMAIL_STATE;
import static com.chenxu.workassistant.email.EmailActivity.EMAIL_DATA_EMAIL;
import static com.chenxu.workassistant.email.EmailActivity.EMAIL_DATA_MAIL;

public class EmailPresenter implements EmailContract.Presenter {

    private EmailContract.View mView;
    private EmailContract.Model mModel;
    private Context mContext;
    private ArrayList<MailReceiver> emailList;
    private ArrayList<Email> emails;


    public EmailPresenter(EmailContract.View view,Context context){
        this.mView = view;
        this.mContext = context;
        this.mModel = new EmailModel(this);
    }

    @Override
    public void start() {
        int emailType = Constant.spSetting.getInt(Constant.EMAIL_SERVER_TYPE,0);
        String account = Constant.spSetting.getString(Constant.EMAIL_ACCOUNT,"");
        String password = Constant.spSetting.getString(Constant.EMAIL_PASSWORD,"");
        emails = (ArrayList<Email>) Applacation.getACache().getAsObject(EMAIL_DATA_EMAIL);
        if (emails!=null){
            mView.initData(emails);
        }
        mModel.login(emailType,account,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean){
                            mModel.getMailReceivers()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<ArrayList<MailReceiver>>() {
                                        @Override
                                        public void accept(ArrayList<MailReceiver> mailReceivers) throws Exception {
                                            emailList = mailReceivers;
                                            getEmail(mailReceivers);
                                        }
                                    });
                        }else {
                            Constant.editorSetting.putBoolean(Constant.EMAIL_SAVE_ACCOUNT,false).putBoolean(EMAIL_STATE,false).commit();
                            mView.loginError();
                        }
                    }
                });
    }

    @Override
    public void getEmail(ArrayList<MailReceiver> list) {
        mModel.getEmailList(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Email>>() {
                    @Override
                    public void accept(ArrayList<Email> result) throws Exception {
                        emails = result;
                        mView.initData(result);
                        emailList = list;
                    }
                });
    }

    @Override
    public void refreshEmail(RefreshLayout refreshLayout) {
        mModel.getMailReceivers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<MailReceiver>>() {
                    @Override
                    public void accept(ArrayList<MailReceiver> mailReceivers) throws Exception {
                        emailList = mailReceivers;
                        mModel.getEmailList(mailReceivers)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<ArrayList<Email>>() {
                                    @Override
                                    public void accept(ArrayList<Email> result) throws Exception {
                                        emails = result;
                                        mView.initData(result);
                                        emailList = mailReceivers;
                                        refreshLayout.finishRefresh();
                                    }
                                });
                    }
                });
    }

    @Override
    public void deleteEmail(int position) {
        if (emailList == null || emailList.size() == 0 || emailList.size() != emails.size()){
            mView.onDataLoading();
            return;
        }
        MailReceiver mailReceiver = emailList.get(emailList.size()-position-1);
        mModel.deleteEmail(mailReceiver)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mView.deleteItem(position);
                        emailList.remove(mailReceiver);
                    }
                });
    }

    @Override
    public void openEmail(int position) {
        if (emailList == null || emailList.size() == 0 || emailList.size() != emails.size()){
            mView.onDataLoading();
            return;
        }
        MailReceiver mailReceiver = emailList.get(emailList.size()-position-1);
        if (emails.get(position).isNews()){
            mModel.setEmailRead(mailReceiver)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            emails.get(position).setNews(false);
                            mView.setEmailRead(position);
                        }
                    });
        }
        //打开邮件
        mModel.getEmailUID(mailReceiver)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long UID) throws Exception {
                        Log.e("getEmailUID",UID+"");
                        mView.openEmail(UID);
                    }
                });
    }

    @Override
    public void closeInbox() {
        Applacation.getACache().remove(EMAIL_DATA_EMAIL);
        Applacation.getACache().put(EMAIL_DATA_EMAIL,emails);
        mModel.exitStore()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {

                    }
                });
    }
}
