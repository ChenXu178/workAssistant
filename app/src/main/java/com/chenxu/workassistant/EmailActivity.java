package com.chenxu.workassistant;

import android.app.Activity;
import android.os.Bundle;

import com.chenxu.workassistant.databinding.ActivityEmailBinding;
import com.chenxu.workassistant.utils.ImageLoadUtil;
import com.chenxu.workassistant.utils.StatusBarUtil;

import java.io.IOException;

public class EmailActivity extends BaseActivity<ActivityEmailBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_email;
    }

    @Override
    protected void initView() {
        StatusBarUtil.darkMode(this);
    }

    @Override
    protected void bindEvent() {

    }

}
