package com.chenxu.workassistant.home;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.fileMenage.FileMenageActivity;
import com.chenxu.workassistant.LoginActivity;
import com.chenxu.workassistant.photoRecognition.PhotoRecognitionActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Constant;
import com.chenxu.workassistant.databinding.ActivityHomeBinding;
import com.chenxu.workassistant.setting.SettingActivity;
import com.chenxu.workassistant.utils.BackgroundUtil;
import com.chenxu.workassistant.utils.ClickUtil;
import com.chenxu.workassistant.utils.StatusBarUtil;

import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

public class HomeActivity extends BaseActivity<ActivityHomeBinding> implements HomeContract.View,View.OnClickListener{

    private HomeContract.Presenter mPresenter;
    private Animation ivSearchAnimation,ivSearchBgAnimation;

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.ivSearch.clearAnimation();
        mBinding.ivSearchBg.clearAnimation();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        mPresenter = new HomePresenter(this,this);
        StatusBarUtil.darkMode(this);
        mPresenter.start();
    }

    @Override
    protected void bindEvent() {
        mBinding.llFiles.setOnClickListener(this);
        mBinding.llEmail.setOnClickListener(this);
        mBinding.llCollection.setOnClickListener(this);
        mBinding.llHistory.setOnClickListener(this);
        mBinding.llPhoto.setOnClickListener(this);
        mBinding.llSetting.setOnClickListener(this);

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
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_files:
                if (Constant.spPermission.getBoolean("storage",true)){
                    Intent intent = new Intent(this, FileMenageActivity.class);
                    intent.putExtra(FileMenageActivity.OPEN_TYPE,1);
                    ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(mBinding.tvFile, FileMenageActivity.VIEW_ANIM));
                    ActivityCompat.startActivity(this, intent, compat.toBundle());
                }else {
                    mPresenter.checkPermission();
                }
                break;
            case R.id.ll_email:
                startActivity(new Intent(this,LoginActivity.class));
                break;
            case R.id.ll_photo:
                if (Constant.spPermission.getBoolean("camera",true)){
                    Intent intent = new Intent(this, PhotoRecognitionActivity.class);
                    intent.putExtra(PhotoRecognitionActivity.OPEN_TYPE,1);
                    ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(mBinding.tvPhoto, PhotoRecognitionActivity.VIEW_ANIM));
                    ActivityCompat.startActivity(this, intent, compat.toBundle());
                }else {
                    mPresenter.checkPermission();
                }
                break;
            case R.id.ll_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(mBinding.tvSetting, SettingActivity.VIEW_ANIM));
                ActivityCompat.startActivity(this, intent, compat.toBundle());
                break;
        }
    }
}
