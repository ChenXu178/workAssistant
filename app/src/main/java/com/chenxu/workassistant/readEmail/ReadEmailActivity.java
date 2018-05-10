package com.chenxu.workassistant.readEmail;

import android.view.View;
import android.webkit.WebSettings;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.databinding.ActivityReadEmailBinding;
import com.chenxu.workassistant.utils.StatusBarUtil;

public class ReadEmailActivity extends BaseActivity<ActivityReadEmailBinding> implements ReadEmailContract.View {

    private ReadEmailContract.Presenter mPresenter;
    private long UID;

    public static final String EMAIL_UID = "EMAIL:UID";


    @Override
    protected int getLayoutId() {
        return R.layout.activity_read_email;
    }

    @Override
    protected void initView() {
        StatusBarUtil.darkMode(this);
        mPresenter = new ReadEmailPresenter(this, this);
        UID = getIntent().getLongExtra(EMAIL_UID, 0);
        mPresenter.initData(UID);
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    public void setTitle(String title) {
        mBinding.tvEmailTitle.setText(title);
    }

    @Override
    public void setSendTime(String time) {
        mBinding.tvEmailTime.setText(time);
    }

    @Override
    public void setSender(String sender) {
        mBinding.tvEmailSender.setVisibility(View.VISIBLE);
        mBinding.tvEmailSender.setText(sender);
    }

    @Override
    public void setHTMLContent(String content, String encoding) {
        mBinding.rlLoading.setVisibility(View.GONE);
        //支持javascript
        mBinding.wvContent.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        mBinding.wvContent.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        mBinding.wvContent.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        mBinding.wvContent.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        mBinding.wvContent.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mBinding.wvContent.getSettings().setLoadWithOverviewMode(true);
        mBinding.wvContent.loadDataWithBaseURL(null, content, "text/html", encoding, null);
    }

    @Override
    protected void onDestroy() {
        mBinding.wvContent.destroy();
        super.onDestroy();
    }
}
