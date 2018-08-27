package com.chenxu.workassistant.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Applacation;
import com.chenxu.workassistant.config.Constant;
import com.chenxu.workassistant.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.MessagingException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.weyye.hipermission.PermissionItem;

/**
 * Created by Android on 2018/3/26.
 */

public class HomePresenter implements HomeContract.Presenter {
    private static final String TAG = "HomePresenter";
    private HomeContract.View mView;
    private HomeContract.Model mModel;
    private Context mContext;
    private boolean emailLoginState = false;
    private Timer timer;
    private Task task;

    public HomePresenter(HomeContract.View view,Context context){
        this.mView = view;
        this.mModel = new HomeModel(this);
        this.mContext = context;
    }

    @Override
    public void start() {
        checkPermission();
    }

    @Override
    public void checkPermission() {
        List<PermissionItem> permissionItems = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 23){
            if (mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE,"存储", R.drawable.permission_ic_storage));
            }
            if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                permissionItems.add(new PermissionItem(Manifest.permission.CAMERA,"摄像头",R.drawable.permission_ic_camera));
            }
            if (permissionItems.size() > 0){
                mView.showJurisdictionDialog(permissionItems);
            }
        }else {
            Constant.permissionEditor.putBoolean("storage",true).putBoolean("camera",true).commit();
        }

    }

    @Override
    public void startPollingEmail() {
        mModel.loginEmail()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean){
                            initTimerTask();
                        }else {
                            Constant.editorSetting.putBoolean(Constant.EMAIL_SAVE_ACCOUNT,false).commit();
                            mView.onEmailAutoLoginError();
                        }
                    }
                });
    }

    @Override
    public void initTimerTask() {
        timer = new Timer();
        task = new Task();
        timer.schedule(task,1000,Constant.spSetting.getLong(Constant.EMAIL_REFRESH_TIME,10000));
    }

    @Override
    public void getUnReadEmailCount() {
        mModel.getUnreadEmailCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mView.setEmailCountVisibility(integer);
                    }
                });
    }

    @Override
    public void exitEmail() {
        task.cancel();
        timer.cancel();
        try {
            if (Applacation.getStore()!= null){
                Applacation.getStore().close();
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        Applacation.setStore(null);
    }

    /**
     * 第三方软件调用文件浏览
     * @param filePath 路径
     */
    @Override
    public void openFile(String filePath) {
        Log.e(TAG, "openFile: "+filePath);
        int fileType = FileUtil.fileType(filePath);
        switch (fileType){
            case 5:
            case 7:
            case 8:
            case 9:
            case 11:
                mView.openOfficeFile(filePath);
                break;
            case 6:
                mView.openImageFile(filePath);
                break;
            default:
                  mView.onError(R.string.open_file_error);
                break;
        }
    }

    public class Task extends TimerTask{

        @Override
        public void run() {
            getUnReadEmailCount();
        }
    }
}
