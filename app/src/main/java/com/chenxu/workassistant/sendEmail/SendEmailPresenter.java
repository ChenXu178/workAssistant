package com.chenxu.workassistant.sendEmail;

import android.content.Context;

public class SendEmailPresenter implements SendEmailContract.Presenter {

    private SendEmailContract.View mView;
    private SendEmailContract.Model mModel;
    private Context mContext;

    public SendEmailPresenter(SendEmailContract.View view,Context context){
        this.mView = view;
        this.mModel = new SendEmailModel(this);
        this.mContext = context;
    }

    @Override
    public void start() {

    }
}
