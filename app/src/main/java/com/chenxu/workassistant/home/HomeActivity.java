package com.chenxu.workassistant.home;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.collection.CollectionActivity;
import com.chenxu.workassistant.email.EmailActivity;
import com.chenxu.workassistant.fileMenage.FileMenageActivity;
import com.chenxu.workassistant.fileSearch.FileSearchActivity;
import com.chenxu.workassistant.login.LoginActivity;
import com.chenxu.workassistant.photoRecognition.PhotoRecognitionActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Constant;
import com.chenxu.workassistant.databinding.ActivityHomeBinding;
import com.chenxu.workassistant.setting.SettingActivity;
import com.chenxu.workassistant.utils.BackgroundUtil;
import com.chenxu.workassistant.utils.ClickUtil;
import com.chenxu.workassistant.utils.SnackBarUtils;
import com.chenxu.workassistant.utils.StatusBarUtil;

import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

public class HomeActivity extends BaseActivity<ActivityHomeBinding> implements HomeContract.View,View.OnClickListener{

    private HomeContract.Presenter mPresenter;
    private Animation ivSearchAnimation,ivSearchBgAnimation;
    private EmailLoginBroadcastReceiver emailLoginBroadcastReceiver;
    private EmailExitBroadcastReceiver emailExitBroadcastReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.ivSearch.clearAnimation();
        mBinding.ivSearchBg.clearAnimation();
        mPresenter.start();
    }

    @Override
    protected void onStart() {
        IntentFilter loginFilter = new IntentFilter(Constant.BC_LOGIN);
        emailLoginBroadcastReceiver = new EmailLoginBroadcastReceiver();
        registerReceiver(emailLoginBroadcastReceiver,loginFilter);

        IntentFilter exitFilter = new IntentFilter(Constant.BC_EXIT);
        emailExitBroadcastReceiver = new EmailExitBroadcastReceiver();
        registerReceiver(emailExitBroadcastReceiver,exitFilter);

        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(emailLoginBroadcastReceiver);
        unregisterReceiver(emailExitBroadcastReceiver);

        super.onStop();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        mPresenter = new HomePresenter(this,this);
        StatusBarUtil.darkMode(this);


        if (Constant.spSetting.getBoolean(Constant.EMAIL_SAVE_ACCOUNT,false)){
            mPresenter.startPollingEmail();
        }
    }

    @Override
    protected void bindEvent() {
        mBinding.llFiles.setOnClickListener(this::onClick);
        mBinding.llEmail.setOnClickListener(this::onClick);
        mBinding.llCollection.setOnClickListener(this::onClick);
        mBinding.llPhoto.setOnClickListener(this::onClick);
        mBinding.llSetting.setOnClickListener(this::onClick);

        new ClickUtil() {
            @Override
            public void method() {
                ivSearchAnimation = AnimationUtils.loadAnimation(HomeActivity.this,R.anim.home_search_anim);
                mBinding.ivSearch.startAnimation(ivSearchAnimation);
                ivSearchBgAnimation = AnimationUtils.loadAnimation(HomeActivity.this,R.anim.home_search_bg_anim);
                mBinding.ivSearchBg.startAnimation(ivSearchBgAnimation);

                ivSearchAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        startActivity(new Intent(HomeActivity.this,FileSearchActivity.class));
                        overridePendingTransition(R.anim.fade_in_anim,R.anim.fade_out_anim);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }.clickAntiShake(mBinding.ivSearchBg);

    }


    @Override
    public void showJurisdictionDialog(List<PermissionItem> permissionItems) {
        BackgroundUtil.setBackgroundAlpha(0.5f,this);
        HiPermission.create(this).permissions(permissionItems).title(this.getResources().getString(R.string.dialog_permission_title)).msg(this.getResources().getString(R.string.dialog_permission_msg)).style(R.style.PermissionBlueStyle).filterColor(R.color.mainBlue).animStyle(R.style.PermissionAnimModal).checkMutiPermission(new PermissionCallback() {
            @Override
            public void onClose() {
                Constant.permissionEditor.putBoolean("storage",false).putBoolean("camera",false).commit();
                BackgroundUtil.setBackgroundAlpha(1f,HomeActivity.this);
            }

            @Override
            public void onFinish() {
                Constant.permissionEditor.putBoolean("storage",true).putBoolean("camera",true).commit();
                BackgroundUtil.setBackgroundAlpha(1f,HomeActivity.this);
            }

            @Override
            public void onDeny(String permission, int position) {
                BackgroundUtil.setBackgroundAlpha(1f,HomeActivity.this);
                if (permission == Manifest.permission.WRITE_EXTERNAL_STORAGE){
                    Constant.permissionEditor.putBoolean("storage",false).commit();
                }
                if (permission == Manifest.permission.WRITE_EXTERNAL_STORAGE){
                    Constant.permissionEditor.putBoolean("camera",false).commit();
                }
                Toast.makeText(HomeActivity.this,R.string.dialog_permission_err,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGuarantee(String permission, int position) {
                BackgroundUtil.setBackgroundAlpha(1f,HomeActivity.this);
            }
        });
    }

    @Override
    public void setEmailCountVisibility(int count) {
        if (count >= 1){
            mBinding.tvEmailCount.setVisibility(View.VISIBLE);
            mBinding.tvEmailCount.setText(count+"");
        }else {
            mBinding.tvEmailCount.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEmailAutoLoginError() {
        SnackBarUtils.showSnackBarMSG(mBinding.rlHome,R.string.home_email_login_err,R.color.white,R.color.red);
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        ActivityOptionsCompat compat = null;
        switch (v.getId()){
            case R.id.ll_files:
                if (Constant.spPermission.getBoolean("storage",true)){
                    intent = new Intent(this, FileMenageActivity.class);
                    intent.putExtra(FileMenageActivity.OPEN_TYPE,1);
                    compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(mBinding.tvFile, FileMenageActivity.VIEW_ANIM));
                    ActivityCompat.startActivity(this, intent, compat.toBundle());
                }else {
                    mPresenter.checkPermission();
                }
                break;
            case R.id.ll_email:
                boolean isSave = Constant.spSetting.getBoolean(Constant.EMAIL_SAVE_ACCOUNT,false);
                if (isSave){
                    intent = new Intent(this, EmailActivity.class);
                    intent.putExtra(EmailActivity.OPEN_TYPE,1);
                    compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(mBinding.tvEmail, EmailActivity.VIEW_ANIM));
                    ActivityCompat.startActivity(this, intent, compat.toBundle());
                }else {
                    startActivity(new Intent(this,LoginActivity.class));
                }
                break;
            case R.id.ll_collection:
                intent = new Intent(this, CollectionActivity.class);
                compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(mBinding.tvCollection, CollectionActivity.VIEW_ANIM));
                ActivityCompat.startActivity(this, intent, compat.toBundle());
                break;
            case R.id.ll_photo:
                if (Constant.spPermission.getBoolean("camera",true)){
                    intent = new Intent(this, PhotoRecognitionActivity.class);
                    intent.putExtra(PhotoRecognitionActivity.OPEN_TYPE,1);
                    compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(mBinding.tvPhoto, PhotoRecognitionActivity.VIEW_ANIM));
                    ActivityCompat.startActivity(this, intent, compat.toBundle());
                }else {
                    mPresenter.checkPermission();
                }
                break;
            case R.id.ll_setting:
                intent = new Intent(this, SettingActivity.class);
                compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(mBinding.tvSetting, SettingActivity.VIEW_ANIM));
                ActivityCompat.startActivity(this, intent, compat.toBundle());
                break;
        }
    }

    /**
     * 登录邮箱广播
     */
    public class EmailLoginBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //登录邮箱，开始轮询未读邮件
            Log.e("EmailLoginReceiver","接收登录邮箱广播");
            mPresenter.startPollingEmail();
        }
    }

    /**
     * 退出邮箱广播
     */
    public class EmailExitBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("EmailExitReceiver","接收退出邮箱广播");
            mPresenter.exitEmail();
            setEmailCountVisibility(0);
        }
    }
}
