package com.chenxu.workassistant.login;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Constant;
import com.chenxu.workassistant.databinding.ActivityLoginBinding;
import com.chenxu.workassistant.email.EmailActivity;
import com.chenxu.workassistant.utils.ClickUtil;
import com.chenxu.workassistant.utils.DialogUtil;
import com.chenxu.workassistant.utils.SizeUtils;
import com.chenxu.workassistant.utils.SnackBarUtils;
import com.chenxu.workassistant.utils.StatusBarUtil;
import com.chenxu.workassistant.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> implements LoginContract.View, View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private LoginContract.Presenter mPresenter;
    private ArrayAdapter emailTypeAdapter;
    private PopupWindow loadDialog;
    private boolean isSave = false;
    private int mX;
    private int mY;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        StatusBarUtil.immersive(this);
        mBinding.rlLoginContent.post(new Runnable() {
            @Override
            public void run() {
                mX = getIntent().getIntExtra("x", 0);
                mY = getIntent().getIntExtra("y", 0);
                Log.d(TAG, "searchContent.post: X = " + mX + " Y = " + mY);
                Animator animator = createRevealAnimator(false, mX, mY);
                animator.start();
            }
        });
        emailTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constant.EMAIL_SERVER_TITLE);
        emailTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spType.setAdapter(emailTypeAdapter);
        mBinding.etEmail.setText(Constant.spSetting.getString(Constant.EMAIL_ACCOUNT,""));
        mBinding.etPassword.setText(Constant.spSetting.getString(Constant.EMAIL_PASSWORD,""));
        this.mPresenter = new LoginPresenter(this, this);
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
                submit();
            }
        }.clickAntiShake(mBinding.btnLogin);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_save:
                if (isSave) {
                    mBinding.lavSave.setProgress(0f);
                    mBinding.lavSave.cancelAnimation();
                    isSave = false;
                } else {
                    mBinding.lavSave.resumeAnimation();
                    isSave = true;
                }
                break;
        }
    }

    @Override
    public void submit() {
        Utils.closeKeyboard(this);
        int emailType = mBinding.spType.getSelectedItemPosition();
        String account = mBinding.etEmail.getText().toString().trim();
        String password = mBinding.etPassword.getText().toString().trim();
        if (!Utils.emailFormat(account)){
            SnackBarUtils.showSnackBarMSG(mBinding.btnLogin,R.string.login_err1,R.color.white,R.color.mainBlue);
            return;
        }
        if ("".equals(password)){
            SnackBarUtils.showSnackBarMSG(mBinding.btnLogin,R.string.login_err2,R.color.white,R.color.mainBlue);
            return;
        }
        loadDialog = DialogUtil.initLoadDialog(this,R.string.login_loading,mBinding.vBg);
        loadDialog.showAtLocation(mBinding.btnLogin, Gravity.CENTER,0,0);
        mPresenter.emailLogin(isSave,emailType,account,password);
    }

    @Override
    public void onError() {
        if (loadDialog != null){
            loadDialog.dismiss();
        }
        SnackBarUtils.showSnackBarMSG(mBinding.btnLogin,R.string.login_err3,R.color.white,R.color.mainBlue);
    }

    @Override
    public void onSuccess() {
        if (loadDialog != null){
            loadDialog.dismiss();
        }
        Intent intent = new Intent();
        intent.setAction(Constant.BC_LOGIN);
        sendBroadcast(intent);
        startActivity(new Intent(this, EmailActivity.class));
        finish();
    }


    private Animator createRevealAnimator(boolean reversed, int x, int y) {
        float hypot = (float) Math.hypot(mBinding.rlLoginContent.getHeight(), mBinding.rlLoginContent.getWidth());
        float startRadius = reversed ? hypot : 0;
        float endRadius = reversed ? 0 : hypot;

        Animator animator = ViewAnimationUtils.createCircularReveal(
                mBinding.rlLoginContent, x, y,
                startRadius,
                endRadius);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        return animator;
    }
}
