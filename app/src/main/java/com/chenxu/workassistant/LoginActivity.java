package com.chenxu.workassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.chenxu.workassistant.databinding.ActivityLoginBinding;
import com.chenxu.workassistant.utils.ClickUtil;
import com.chenxu.workassistant.utils.StatusBarUtil;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> implements View.OnClickListener {
    private boolean isSave = false;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        StatusBarUtil.immersive(this);
    }

    @Override
    protected void bindEvent() {
        mBinding.llSave.setOnClickListener(this::onClick);
        new ClickUtil() {
            @Override
            public void method() {
                onBackPressed();
            }
        }.clickAntiShake(mBinding.btnCancel);
        new ClickUtil() {
            @Override
            public void method() {

            }
        }.clickAntiShake(mBinding.btnLogin);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_save:
                if (isSave){
                    mBinding.lavSave.setProgress(0f);
                    mBinding.lavSave.cancelAnimation();
                    isSave = false;
                }else {
                    mBinding.lavSave.resumeAnimation();
                    isSave = true;
                }
                break;
        }
    }
}
