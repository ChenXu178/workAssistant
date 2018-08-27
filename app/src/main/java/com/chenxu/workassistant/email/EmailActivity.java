package com.chenxu.workassistant.email;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Applacation;
import com.chenxu.workassistant.databinding.ActivityEmailBinding;
import com.chenxu.workassistant.login.LoginActivity;
import com.chenxu.workassistant.readEmail.ReadEmailActivity;
import com.chenxu.workassistant.sendEmail.SendEmailActivity;
import com.chenxu.workassistant.utils.ImageLoadUtil;
import com.chenxu.workassistant.utils.SizeUtils;
import com.chenxu.workassistant.utils.SnackBarUtils;
import com.chenxu.workassistant.utils.StatusBarUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class EmailActivity extends BaseActivity<ActivityEmailBinding> implements EmailContract.View,View.OnClickListener {

    private EmailContract.Presenter mPresenter;
    private EmailAdapter adapter;

    public static final String OPEN_TYPE = "EMAIL:OPEN"; //1首页 2登录
    public static final String VIEW_ANIM = "EMAIL:ANIM";
    public static final String EMAIL_DATA_EMAIL = "EMAIL:DATA_EMAIL";
    public static final String EMAIL_DATA_MAIL = "EMAIL:DATA_MAIL";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_email;
    }

    @Override
    protected void initView() {
        StatusBarUtil.darkMode(this);
        this.mPresenter = new EmailPresenter(this,this);
        if (getIntent().getIntExtra(OPEN_TYPE,2) == 1){
            ViewCompat.setTransitionName(mBinding.tvBarTitle,VIEW_ANIM);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.smrvEmail.setLayoutManager(linearLayoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.email_divider));
        mBinding.smrvEmail.addItemDecoration(divider);

        mBinding.smrvEmail.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                SwipeMenuItem collectionItem = new SwipeMenuItem(Applacation.getInstance());
                // 各种文字和图标属性设置。
                collectionItem.setHeight(MATCH_PARENT);
                collectionItem.setWidth(SizeUtils.dp2px(Applacation.getInstance(),80));
                collectionItem.setText(R.string.email_item_menu_delete);
                collectionItem.setTextSize(16);
                collectionItem.setBackgroundColor(EmailActivity.this.getResources().getColor(R.color.EmailRightItemDeleteBG));
                collectionItem.setTextColor(EmailActivity.this.getResources().getColor(R.color.white));
                swipeRightMenu.addMenuItem(collectionItem); // 在Item右侧添加一个菜单。
            }
        });

        mBinding.smrvEmail.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                menuBridge.closeMenu();
                int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
                int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
                mPresenter.deleteEmail(adapterPosition);
            }
        });

        mBinding.smrvEmail.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                mPresenter.openEmail(position);
            }
        });

        adapter = new EmailAdapter(new ArrayList<>(),this);
        mBinding.smrvEmail.setAdapter(adapter);
        mPresenter.start();
    }

    @Override
    protected void bindEvent() {
        mBinding.btnBack.setOnClickListener(this::onClick);
        mBinding.btnSend.setOnClickListener(this::onClick);
        mBinding.srlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.refreshEmail(refreshLayout);
            }
        });
    }

    @Override
    public void loginError() {
        Toast.makeText(this,R.string.email_login_err1,Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void initData(List<Email> list) {
        adapter.setData(list);
        mBinding.rlLoading.setVisibility(View.GONE);
        mBinding.srlRefresh.setVisibility(View.VISIBLE);
    }

    @Override
    public void deleteItem(int position) {
        adapter.deleteItem(position);
    }

    @Override
    public void onDataLoading() {
        SnackBarUtils.showSnackBarMSG(mBinding.rlBar,R.string.email_loading_error,R.color.white,R.color.mainBlue);
    }

    @Override
    public void setEmailRead(int position) {
        adapter.setItemRead(position);
    }

    @Override
    public void openEmail(long UID) {
        Intent intent = new Intent(this, ReadEmailActivity.class);
        intent.putExtra(ReadEmailActivity.EMAIL_UID,UID);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        mPresenter.closeInbox();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_send:
                startActivity(new Intent(this, SendEmailActivity.class));
                break;
        }
    }
}
