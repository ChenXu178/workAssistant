package com.chenxu.workassistant.home;

import com.chenxu.workassistant.BasePresenter;
import com.chenxu.workassistant.BaseView;

import java.util.List;

import me.weyye.hipermission.PermissionItem;

/**
 * Created by Android on 2018/3/26.
 */

public interface HomeContract {

    interface View extends BaseView{
        void showJurisdictionDialog(List<PermissionItem> permissionItems);
    }

    interface Presenter extends BasePresenter{
        void checkPermission();
    };
}
