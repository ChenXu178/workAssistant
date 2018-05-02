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
        void setEnclosureCount(int count);
    }

    interface Presenter extends BasePresenter{
        void checkPermission();
    };

    interface Model{
        Observable<Integer> queryEnclosureCount();
    }
}
