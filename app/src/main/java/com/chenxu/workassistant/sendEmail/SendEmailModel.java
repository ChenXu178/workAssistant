package com.chenxu.workassistant.sendEmail;

public class SendEmailModel implements SendEmailContract.Model {

    private SendEmailContract.Presenter mPresenter;

    public SendEmailModel(SendEmailContract.Presenter presenter){
        this.mPresenter = presenter;
    }
}
