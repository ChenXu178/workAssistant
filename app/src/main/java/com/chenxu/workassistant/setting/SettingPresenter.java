package com.chenxu.workassistant.setting;

import android.content.Context;

import com.chenxu.workassistant.config.Applacation;
import com.chenxu.workassistant.config.Constant;

import static com.chenxu.workassistant.setting.SettingActivity.SP_FILTER_FILE;
import static com.chenxu.workassistant.setting.SettingActivity.SP_SHOW_HIDE_FILE;

public class SettingPresenter implements SettingContract.Presenter {

    private SettingContract.View mView;
    private Context mContext;

    public SettingPresenter(SettingContract.View view,Context context){
        this.mContext = context;
        this.mView = view;
    }

    @Override
    public void start() {
        if (Constant.spSetting != null){
            mView.setShowHideFileState(Constant.spSetting.getBoolean(SP_SHOW_HIDE_FILE,false));
            mView.setFilterFileState(Constant.spSetting.getBoolean(SP_FILTER_FILE,false));
            mView.setExitButtonVisibility(Constant.spSetting.getBoolean(Constant.EMAIL_SAVE_ACCOUNT,false));
            mView.setCacheSize(Applacation.getACache().cacheSize());
        }
    }

    @Override
    public void setSPForHideFileState(boolean state) {
        Constant.editorSetting.putBoolean(SP_SHOW_HIDE_FILE,state).commit();
        mView.setShowHideFileState(state);
    }

    @Override
    public void setSPForFilterFileState(boolean state) {
        Constant.editorSetting.putBoolean(SP_FILTER_FILE,state).commit();
        mView.setFilterFileState(state);
    }

    @Override
    public void clearCache() {
        Applacation.getACache().clear();
        mView.setCacheSize(Applacation.getACache().cacheSize());
    }
}
