package com.chenxu.workassistant.fileSearch;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Applacation;
import com.chenxu.workassistant.dao.CollectionEntity;
import com.chenxu.workassistant.dao.EnclosureEntity;
import com.chenxu.workassistant.dao.SearchEntity;
import com.chenxu.workassistant.utils.FileUtil;

import java.io.File;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FileSearchPresenter implements FileSearchContract.Presenter {
    private FileSearchContract.View mView;
    private FileSearchContract.Model mModel;
    private Context mContext;
    private List<SearchBean> fileList;

    public FileSearchPresenter(FileSearchContract.View view,Context context){
        this.mView = view;
        this.mContext = context;
        this.mModel = new FileSearchModel(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void loadSearchHistory() {
        mModel.list()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SearchEntity>>() {
                    @Override
                    public void accept(List<SearchEntity> searchEntities) throws Exception {
                        mView.showSearchHistory(searchEntities);
                    }
                });
    }

    //文件搜索
    @Override
    public void onFileSearch(String text) {
        mView.showLoading();
        mModel.addSearchTextToHistory(text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e("insertSearchHistory",aBoolean+"");
                    }
                });
        mModel.searchFile(text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SearchBean>>() {
                    @Override
                    public void accept(List<SearchBean> files) throws Exception {
                        mView.hideLoading();
                        mView.setSearchResultData(files);
                        fileList = files;
                        if (files.size() == 0){
                            mView.noSearchFile();
                        }
                    }
                });
    }

    @Override
    public void swipeMenuItemClick(int adapterPosition, int menuPosition) {
        SearchBean searchBean = fileList.get(adapterPosition);
        if (menuPosition == 0){
            mView.openPath(searchBean);
        }
        if(menuPosition == 1){
            //附件
            if (searchBean.getFile().isDirectory()){
                mView.showSnackBar(R.string.file_right_menu_err1,R.color.SnackBarText,R.color.SnackBarBG);
            }else {
                insertEnclosure(searchBean.getFile());
            }
        }
        if (menuPosition == 2){
            //收藏
            insertCollection(searchBean.getFile());
        }
    }

    @Override
    public void insertCollection(File file) {
        CollectionEntity entity = new CollectionEntity(null,file.getPath(),System.currentTimeMillis());
        mModel.insertCollection(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean){
                            mView.showCollectionDialog();
                        }else {
                            mView.showSnackBar(R.string.file_right_menu_err2,R.color.SnackBarText,R.color.SnackBarBG);
                        }
                    }
                });
    }

    @Override
    public void insertEnclosure(File file) {
        EnclosureEntity entity = new EnclosureEntity(null,file.getPath(),System.currentTimeMillis());
        mModel.insertEnclosure(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean){
                            mView.showEnclosureDialog();
                        }else {
                            mView.showSnackBar(R.string.file_right_menu_err3,R.color.SnackBarText,R.color.SnackBarBG);
                        }
                    }
                });
    }

    @Override
    public void onFileItemClick(int position, View view) {
        File file = fileList.get(position).getFile();
        switch (FileUtil.fileType(file)){//1文件夹、2音乐、3文件、4代码、5Excel、6图片、7PDF、8PPT、9TXT、10视频、11Word、12压缩
            case 2: Toast.makeText(Applacation.getInstance(),R.string.file_menage_open_err,Toast.LENGTH_SHORT).show(); break;
            case 3: Toast.makeText(Applacation.getInstance(),R.string.file_menage_open_err,Toast.LENGTH_SHORT).show(); break;
            case 4: Toast.makeText(Applacation.getInstance(),R.string.file_menage_open_err,Toast.LENGTH_SHORT).show(); break;
            case 5: mView.openOfficeFile(file.getPath(),view); break;
            case 6: mView.openImageFile(file.toString(),view); break;
            case 7: mView.openOfficeFile(file.getPath(),view); break;
            case 8: mView.openOfficeFile(file.getPath(),view); break;
            case 9: mView.openOfficeFile(file.getPath(),view); break;
            case 10: Toast.makeText(Applacation.getInstance(),R.string.file_menage_open_err,Toast.LENGTH_SHORT).show(); break;
            case 11: mView.openOfficeFile(file.getPath(),view); break;
            case 12: Toast.makeText(Applacation.getInstance(),R.string.file_menage_open_err,Toast.LENGTH_SHORT).show(); break;
        }
    }

    /**
     * 快速搜索
     * @param type 搜索类型
     */
    @Override
    public void fastSearch(int type) {
        mView.showLoading();
        mModel.fastSearchFile(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SearchBean>>() {
                    @Override
                    public void accept(List<SearchBean> files) throws Exception {
                        mView.hideLoading();
                        mView.setSearchResultData(files);
                        fileList = files;
                        if (files.size() == 0){
                            mView.noSearchFile();
                        }
                        mView.showSearchClose();
                    }
                });
    }
}
