package com.chenxu.workassistant.about;

import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.databinding.ActivityAboutBinding;
import com.chenxu.workassistant.utils.StatusBarUtil;
import com.chenxu.workassistant.utils.Utils;

public class AboutActivity extends BaseActivity<ActivityAboutBinding> implements View.OnClickListener{
    public static final String VIEW_ANIM = "ABOUT:ANIM";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        StatusBarUtil.darkMode(this);
        ViewCompat.setTransitionName(mBinding.tvBarTitle,VIEW_ANIM);
        mBinding.tvVersion.setText(Utils.getVerName(this));
    }

    @Override
    protected void bindEvent() {
        mBinding.btnBack.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }
}
