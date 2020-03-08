package com.chenxu.workassistant.home;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Toast;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.collection.CollectionActivity;
import com.chenxu.workassistant.email.EmailActivity;
import com.chenxu.workassistant.fileMenage.FileMenageActivity;
import com.chenxu.workassistant.fileReader.ImageFileReaderActivity;
import com.chenxu.workassistant.fileReader.OfficeFileReaderActivity;
import com.chenxu.workassistant.fileSearch.FileSearchActivity;
import com.chenxu.workassistant.login.LoginActivity;
import com.chenxu.workassistant.photoRecognition.PhotoRecognitionActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Constant;
import com.chenxu.workassistant.databinding.ActivityHomeBinding;
import com.chenxu.workassistant.setting.SettingActivity;
import com.chenxu.workassistant.utils.BackgroundUtil;
import com.chenxu.workassistant.utils.ClickUtil;
import com.chenxu.workassistant.utils.FilePathFormat;
import com.chenxu.workassistant.utils.FileUtil;
import com.chenxu.workassistant.utils.SnackBarUtils;
import com.chenxu.workassistant.utils.StatusBarUtil;

import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

public class HomeActivity extends BaseActivity<ActivityHomeBinding> implements HomeContract.View,View.OnClickListener{

    private HomeContract.Presenter mPresenter;
    private EmailLoginBroadcastReceiver emailLoginBroadcastReceiver;
    private EmailExitBroadcastReceiver emailExitBroadcastReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.vBg.setVisibility(View.GONE);
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

        String action = getIntent().getAction();
        if (Intent.ACTION_VIEW.equals(action)){
            String str = getIntent().getDataString();
            Log.e("Uri",str);
            if (str != null){
                Uri uri = Uri.parse(str);
                String filePath = FilePathFormat.getRealPathFromURI(this,uri);
                if (filePath != null){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPresenter.openFile(filePath);
                        }
                    }, 1000);
                }else {
                    onError(R.string.open_file_error);
                }
            }else {
                onError(R.string.open_file_error);
            }
        }
    }

    @Override
    protected void bindEvent() {
        mBinding.llFiles.setOnClickListener(this::onClick);
        mBinding.llCollection.setOnClickListener(this::onClick);
        mBinding.llPhoto.setOnClickListener(this::onClick);
        mBinding.llSetting.setOnClickListener(this::onClick);

        mBinding.llEmail.setOnTouchListener(new View.OnTouchListener() {
            private long touchTime = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        touchTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - touchTime < 500){
                            boolean isSave = Constant.spSetting.getBoolean(Constant.EMAIL_SAVE_ACCOUNT,false);
                            Intent intent;
                            if (isSave){
                                intent = new Intent(HomeActivity.this, EmailActivity.class);
                                intent.putExtra(EmailActivity.OPEN_TYPE,1);
                                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this,new Pair<View, String>(mBinding.tvEmail, EmailActivity.VIEW_ANIM));
                                ActivityCompat.startActivity(HomeActivity.this, intent, compat.toBundle());
                            }else {
                                mBinding.llEmail.playSoundEffect(SoundEffectConstants.CLICK);
                                int [] location = new int[2];
                                mBinding.llEmail.getLocationOnScreen(location);
                                intent = new Intent(HomeActivity.this, LoginActivity.class);
                                intent.putExtra("x", location[0] + (int)event.getX());
                                intent.putExtra("y", location[1] + (int)event.getY());
                                startActivity(intent);
                            }
                        }
                        break;
                }
                return false;
            }
        });
        new ClickUtil() {
            @Override
            public void method() {
                int[] xy = new int[2];
                mBinding.rlSearch.getLocationInWindow(xy);
                Intent i = new Intent(HomeActivity.this, FileSearchActivity.class);
                i.putExtra("x", xy[0] + (mBinding.rlSearch.getWidth()/2));
                i.putExtra("y", xy[1] );
                startActivity(i);
            }
        }.clickAntiShake(mBinding.rlSearch);

    }


    @Override
    public void showJurisdictionDialog(List<PermissionItem> permissionItems) {
        BackgroundUtil.setBackgroundAlpha(mBinding.vBg,true);
        HiPermission.create(this).permissions(permissionItems).title(this.getResources().getString(R.string.dialog_permission_title)).msg(this.getResources().getString(R.string.dialog_permission_msg)).style(R.style.PermissionBlueStyle).filterColor(R.color.mainBlue).animStyle(R.style.PermissionAnimModal).checkMutiPermission(new PermissionCallback() {
            @Override
            public void onClose() {
                Constant.permissionEditor.putBoolean("phone",false).putBoolean("storage",false).putBoolean("camera",false).commit();
                BackgroundUtil.setBackgroundAlpha(mBinding.vBg,false);
            }

            @Override
            public void onFinish() {
                Constant.permissionEditor.putBoolean("phone",true).putBoolean("storage",true).putBoolean("camera",true).commit();
                BackgroundUtil.setBackgroundAlpha(mBinding.vBg,false);
            }

            @Override
            public void onDeny(String permission, int position) {
                BackgroundUtil.setBackgroundAlpha(mBinding.vBg,false);
                if (permission == Manifest.permission.READ_PHONE_STATE){
                    Constant.permissionEditor.putBoolean("phone",false).commit();
                }
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
                BackgroundUtil.setBackgroundAlpha(mBinding.vBg,false);
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
    public void onError(int str) {
        SnackBarUtils.showSnackBarMSG(mBinding.rlHome,str,R.color.white,R.color.red);
    }

    @Override
    public void openOfficeFile(String filePath) {
        Intent intent = new Intent(this, OfficeFileReaderActivity.class);
        intent.putExtra(OfficeFileReaderActivity.FILE_ID,filePath);
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(mBinding.rlBar, OfficeFileReaderActivity.VIEW_DETAIL));
        ActivityCompat.startActivity(this, intent, compat.toBundle());
    }

    @Override
    public void openImageFile(String filePath) {
        Intent intent = new Intent(this, ImageFileReaderActivity.class);
        intent.putExtra(ImageFileReaderActivity.FILE_ID,filePath);
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(mBinding.rlBar, ImageFileReaderActivity.VIEW_DETAIL));
        ActivityCompat.startActivity(this, intent, compat.toBundle());
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        ActivityOptionsCompat compat = null;
        switch (v.getId()){
            case R.id.ll_files:
                intent = new Intent(this, FileMenageActivity.class);
                intent.putExtra(FileMenageActivity.OPEN_TYPE,1);
                compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(mBinding.tvFile, FileMenageActivity.VIEW_ANIM));
                ActivityCompat.startActivity(this, intent, compat.toBundle());
                break;
            case R.id.ll_collection:
                intent = new Intent(this, CollectionActivity.class);
                compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(mBinding.tvCollection, CollectionActivity.VIEW_ANIM));
                ActivityCompat.startActivity(this, intent, compat.toBundle());
                break;
            case R.id.ll_photo:
                intent = new Intent(this, PhotoRecognitionActivity.class);
                intent.putExtra(PhotoRecognitionActivity.OPEN_TYPE,1);
                compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(mBinding.tvPhoto, PhotoRecognitionActivity.VIEW_ANIM));
                ActivityCompat.startActivity(this, intent, compat.toBundle());
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
