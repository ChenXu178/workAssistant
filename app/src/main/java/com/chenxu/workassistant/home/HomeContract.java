package com.chenxu.workassistant.home;

import com.chenxu.workassistant.BasePresenter;
import com.chenxu.workassistant.BaseView;

import java.util.List;

import io.reactivex.Observable;
import me.weyye.hipermission.PermissionItem;

/**
 * Created by Android on 2018/3/26.
 */

public interface HomeContract {

    interface View extends BaseView{
        void showJurisdictionDialog(List<PermissionItem> permissionItems);
        void setEmailCountVisibility(int count);
        void onEmailAutoLoginError();
        void onError(int str);
        void openOfficeFile(String filePath);
        void openImageFile(String filePath);
    }

    interface Presenter extends BasePresenter{
        void checkPermission();
        void startPollingEmail();
        void initTimerTask();
        void getUnReadEmailCount();
        void exitEmail();
        void openFile(String filePath);
    };

    interface Model{
        Observable<Boolean> loginEmail();
        Observable<Integer> getUnreadEmailCount();
    }
}
