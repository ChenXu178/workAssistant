package com.chenxu.workassistant.readEmail;

import android.content.Context;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ReadEmailPresenter implements ReadEmailContract.Presenter {

    private ReadEmailContract.View mView;
    private ReadEmailContract.Model mModel;
    private Context mContext;

    public ReadEmailPresenter(ReadEmailContract.View view, Context context){
        this.mView = view;
        this.mContext = context;
        this.mModel = new ReadEmailModel(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void initData(long UID) {
        mModel.getEmail(UID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        getTitle();
                        getSendTime();
                        getSender();
                        getContent();
                    }
                });
    }

    @Override
    public void getTitle() {
        mModel.getTitle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String title) throws Exception {
                        mView.setTitle(title);
                    }
                });
    }

    @Override
    public void getSendTime() {
        mModel.getSendTime()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String time) throws Exception {
                        mView.setSendTime(time);
                    }
                });
    }

    @Override
    public void getSender() {
        mModel.getSender()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String sender) throws Exception {
                        mView.setSender(sender);
                    }
                });
    }

    @Override
    public void getContent() {
        mModel.getContent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String content) throws Exception {
                        mModel.getEncoding()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String encoding) throws Exception {
                                        mView.setHTMLContent(content,encoding);
                                    }
                                });
                    }
                });
    }
}
