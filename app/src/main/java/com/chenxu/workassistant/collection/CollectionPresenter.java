package com.chenxu.workassistant.collection;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Application;
import com.chenxu.workassistant.utils.FileUtil;

import java.io.File;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CollectionPresenter implements CollectionContract.Presenter {

    private CollectionContract.View mView;
    private CollectionContract.Model mModel;
    private Context mContext;
    private List<FileBean> list;

    public CollectionPresenter(CollectionContract.View view, Context context) {
        this.mView = view;
        this.mModel = new CollectionModel(this);
        this.mContext = context;
    }

    @Override
    public void start() {
        mModel.selectAllOrderByTime()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FileBean>>() {
                    @Override
                    public void accept(List<FileBean> fileBeans) throws Exception {
                        list = fileBeans;
                        mView.initData(list);
                    }
                });
    }

    @Override
    public void deleteByPosition(int Position) {
        mModel.deleteById(list.get(Position).getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        list.remove(Position);
                        mView.deleteByPosition(Position);
                    }
                });
    }

    @Override
    public void openFile(int position, View view) {
        File file = list.get(position).getFile();
        int fileType = FileUtil.fileType(file);
        switch (fileType){//1文件夹、2音乐、3文件、4代码、5Excel、6图片、7PDF、8PPT、9TXT、10视频、11Word、12压缩
            case 1: mView.openFolder(file); break;
            case 2: FileUtil.openFiles(Application.getInstance(),file.getPath()); break;
            case 3: Toast.makeText(Application.getInstance(),R.string.file_menage_open_err,Toast.LENGTH_SHORT).show(); break;
            case 4: FileUtil.openFiles(Application.getInstance(),file.getPath()); break;
            case 5: mView.openOffice(file,view); break;
            case 6: mView.openImage(file,view); break;
            case 7: mView.openOffice(file,view); break;
            case 8: mView.openOffice(file,view); break;
            case 9: mView.openOffice(file,view); break;
            case 10: FileUtil.openFiles(Application.getInstance(),file.getPath()); break;
            case 11: mView.openOffice(file,view); break;
            case 12: FileUtil.openFiles(Application.getInstance(),file.getPath()); break;
        }
    }

    @Override
    public void swipeMenuItemClick(int adapterPosition, int menuPosition) {
        if (menuPosition == 0){
            mView.openPath(list.get(adapterPosition).getFile());
        }
        if (menuPosition == 1){
            deleteByPosition(adapterPosition);
        }
    }
    
}
