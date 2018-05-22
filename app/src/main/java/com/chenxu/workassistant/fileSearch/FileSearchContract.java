package com.chenxu.workassistant.fileSearch;

import android.view.View;

import com.chenxu.workassistant.BasePresenter;
import com.chenxu.workassistant.BaseView;
import com.chenxu.workassistant.dao.CollectionEntity;
import com.chenxu.workassistant.dao.EnclosureEntity;
import com.chenxu.workassistant.dao.SearchEntity;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;


public interface FileSearchContract {

    interface View extends BaseView{

        void setSearchResultData(List<SearchBean> list);

        void showSearchHistory(List<SearchEntity> list);

        void hideSearchHistory();

        void hideSearchDetail();

        void showSearchDetail();

        void showSearchClose();

        void hideSearchClose();

        void showLoading();

        void hideLoading();

        void showSnackBar(int msg, int snackBarMSGColor, int snackBarBGColor);

        void showCollectionDialog(); //显示收藏成功窗口

        void showEnclosureDialog(); //显示收藏成功窗口

        void openOfficeFile(String filePath, android.view.View view);

        void openImageFile(String filePath, android.view.View view);

        void noSearchFile();

        void openPath(SearchBean searchBean);
    }

    interface Presenter extends BasePresenter{

        void loadSearchHistory();

        void onFileSearch(String text);

        void swipeMenuItemClick(int adapterPosition,int menuPosition);//列表侧滑菜单点击

        void insertCollection(File file);//添加收藏

        void insertEnclosure(File file);

        void onFileItemClick(int position, android.view.View view); //点击一项，进入下一层

        void fastSearch(int type); //type 1 doc  2 xls  3 ppt
    }

    interface Model {

        Observable<Boolean> addSearchTextToHistory(String text);

        Observable<List<SearchEntity>> list();

        Observable<List<SearchBean>> searchFile(String text);

        Observable<Boolean> insertCollection(CollectionEntity entity);

        Observable<Boolean> insertEnclosure(EnclosureEntity entity);

        Observable<List<SearchBean>> fastSearchFile(int type);
    }
}
