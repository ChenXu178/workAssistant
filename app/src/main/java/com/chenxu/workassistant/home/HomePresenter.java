package com.chenxu.workassistant.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Constant;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.weyye.hipermission.PermissionItem;

/**
 * Created by Android on 2018/3/26.
 */

public class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View mView;
    private HomeContract.Model mModel;
    private Context mContext;

    public HomePresenter(HomeContract.View view,Context context){
        this.mView = view;
        this.mModel = new HomeModel(this);
        this.mContext = context;
    }

    @Override
    public void start() {
        checkPermission();

        mModel.queryEnclosureCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mView.setEnclosureCount(integer);
                    }
                });
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
}
