package com.chenxu.workassistant.sendEmail;

import com.chenxu.workassistant.BasePresenter;
import com.chenxu.workassistant.BaseView;
import com.chenxu.workassistant.dao.EnclosureEntity;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public interface SendEmailContract {

    interface View extends BaseView{
        void setEnclosureDetail(int text);

        void setEnclosureDetail(String text);

        void setEnclosureData(List<File> files);

        void toggleEnclosureIcon();

        void toggleEnclosureIconAndShowEnclosure();

        void removeAdopterItem(int position);
    }

    interface Presenter extends BasePresenter{
        void formatEnclosureDetail(List<File> files);

        void enclosureTitleClick();

        void deleteEnclosure(int position);
    }

    interface Model {
        Observable<List<File>> getEnclosure();

        Observable<Long> getEnclosureSize(List<File> files);

        Observable<Boolean> deleteEnclosure(String path);
    }
}
