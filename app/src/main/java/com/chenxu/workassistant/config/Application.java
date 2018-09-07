package com.chenxu.workassistant.config;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.chenxu.workassistant.dao.GreenDaoManager;
import com.chenxu.workassistant.utils.ACache;
import com.sun.mail.imap.IMAPStore;


/**
 * Created by Android on 2018/3/20.
 */

public class Application extends android.app.Application {

    private static Context instance;
    private static String baiduToken;
    private static int baiduError = 200;
    public static ACache mACache;
    private static IMAPStore store;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Constant.spPermission = getSharedPreferences("permission",MODE_PRIVATE);
        Constant.permissionEditor = Constant.spPermission.edit();
        Constant.spSetting = getSharedPreferences("setting",MODE_PRIVATE);
        Constant.editorSetting = Constant.spSetting.edit();

        mACache = ACache.get(this);

        GreenDaoManager.getInstance();

        initOCRAccessToken();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    public static IMAPStore getStore() {
        return store;
    }

    public static void setStore(IMAPStore store) {
        Application.store = store;
    }

    public static ACache getACache() {
        return mACache;
    }

    public static Context getInstance() {
        return instance;
    }

    private void initOCRAccessToken(){
        OCR.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象
                baiduError = 200;
                baiduToken = result.getAccessToken();
                Log.e("baiduToken",baiduToken);
            }
            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError子类SDKError对象
                baiduError = error.getErrorCode();
                Log.e("OCRError",""+error.getErrorCode());
                handler.sendEmptyMessageDelayed(1,1000);
            }
        }, getApplicationContext());
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1: initOCRAccessToken(); break;
            }
        }
    };

    public static String getBaiduToken() {
        return baiduToken;
    }

    public static int getBaiduError() {
        return baiduError;
    }
}
