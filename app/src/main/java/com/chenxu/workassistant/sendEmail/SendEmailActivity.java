package com.chenxu.workassistant.sendEmail;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.databinding.ActivitySendEmailBinding;
import com.chenxu.workassistant.utils.StatusBarUtil;

public class SendEmailActivity extends BaseActivity<ActivitySendEmailBinding> implements SendEmailContract.View{

    private SendEmailContract.Presenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_send_email;
    }

    @Override
    protected void initView() {
        StatusBarUtil.darkMode(this);
        this.mPresenter = new SendEmailPresenter(this,this);
    }

    @Override
    protected void bindEvent() {

    }
}
