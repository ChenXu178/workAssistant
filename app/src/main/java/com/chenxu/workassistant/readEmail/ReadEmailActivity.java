package com.chenxu.workassistant.readEmail;

import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import com.tencent.smtt.sdk.WebSettings;
import android.widget.PopupWindow;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.databinding.ActivityReadEmailBinding;
import com.chenxu.workassistant.utils.DialogUtil;
import com.chenxu.workassistant.utils.SnackBarUtils;
import com.chenxu.workassistant.utils.StatusBarUtil;
import com.chenxu.workassistant.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ReadEmailActivity extends BaseActivity<ActivityReadEmailBinding> implements ReadEmailContract.View,View.OnClickListener{

    private ReadEmailContract.Presenter mPresenter;
    private long UID;
    private EnclosureAdapter adapter;
    private PopupWindow loadDialog;

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
        mBinding.rvEnclosure.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EnclosureAdapter(new ArrayList<>(),this);
        mBinding.rvEnclosure.setAdapter(adapter);
        mBinding.ivReadEnclosureState.setTag(R.drawable.send_email_down);
        mPresenter.initData(UID);
    }

    @Override
    protected void bindEvent() {
        mBinding.btnBack.setOnClickListener(this::onClick);
        mBinding.rlEnclosureTitle.setOnClickListener(this::onClick);
        adapter.setOnItemButtonClickListener(new EnclosureAdapter.OnItemButtonClickListener() {
            @Override
            public void onItemButtonClick(int position) {
                mPresenter.downloadEnclosure(position);
            }
        });
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
    public void existEnclosure(List<String> list) {
        mBinding.llEnclosure.setVisibility(View.VISIBLE);
        adapter.setData(list);
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
    public void showDownLoading() {
        loadDialog = DialogUtil.initLoadDialog(this,R.string.read_email_download_hint,mBinding.vBg);
        loadDialog.showAtLocation(mBinding.rlBar, Gravity.CENTER,0,0);
    }

    @Override
    public void cancelDownLoading() {
        if (loadDialog!= null && loadDialog.isShowing()){
            loadDialog.dismiss();
        }
    }

    @Override
    public void showErrorSnackBar(int text) {
        Utils.closeKeyboard(this, mBinding.wvContent);
        SnackBarUtils.showSnackBarMSG(mBinding.rlBar, text, R.color.white, R.color.red);
    }

    @Override
    public void showSnackBar(int text) {
        Utils.closeKeyboard(this, mBinding.wvContent);
        SnackBarUtils.showSnackBarMSG(mBinding.rlBar, text, R.color.white, R.color.mainBlue);
    }

    @Override
    protected void onDestroy() {
        mBinding.wvContent.destroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.rl_enclosure_title:
                toggleEnclosureState();
                break;
        }
    }

    private void toggleEnclosureState(){
        int state = (int) mBinding.ivReadEnclosureState.getTag();
        switch (state){
            case R.drawable.send_email_down:
                mBinding.rvEnclosure.setVisibility(View.VISIBLE);
                mBinding.ivReadEnclosureState.setImageResource(R.drawable.send_email_up);
                mBinding.ivReadEnclosureState.setTag(R.drawable.send_email_up);
                break;
            case R.drawable.send_email_up:
                mBinding.rvEnclosure.setVisibility(View.GONE);
                mBinding.ivReadEnclosureState.setImageResource(R.drawable.send_email_down);
                mBinding.ivReadEnclosureState.setTag(R.drawable.send_email_down);
                break;
        }
    }
}
