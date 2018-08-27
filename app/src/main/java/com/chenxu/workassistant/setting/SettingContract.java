package com.chenxu.workassistant.setting;

import com.chenxu.workassistant.BasePresenter;
import com.chenxu.workassistant.BaseView;

public interface SettingContract {
    interface View extends BaseView{
        void setShowHideFileState(boolean checked);
        void setFilterFileState(boolean checked);
        void setExitButtonVisibility(boolean visibility);
        void setCacheSize(String text);
    }

    interface Presenter extends BasePresenter{
        void setSPForHideFileState(boolean state);
        void setSPForFilterFileState(boolean state);
        void clearCache();
    }
}
