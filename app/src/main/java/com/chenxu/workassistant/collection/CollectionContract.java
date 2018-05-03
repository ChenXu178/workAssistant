package com.chenxu.workassistant.collection;

import android.view.View;

import com.chenxu.workassistant.BasePresenter;
import com.chenxu.workassistant.BaseView;
import com.chenxu.workassistant.dao.CollectionEntity;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;

public interface CollectionContract {
    interface View extends BaseView{
        void initData(List<FileBean> list);
        void deleteByPosition(int Position);
        void openImage(File file, android.view.View view);
        void openOffice(File file, android.view.View view);
        void openFolder(File file);
    }

    interface Presenter extends BasePresenter{
        void deleteByPosition(int position);
        void openFile(int position, android.view.View view);
    }

    interface Model{
        Observable<List<FileBean>> selectAllOrderByTime();

        Observable deleteById(long id);
    }
}
