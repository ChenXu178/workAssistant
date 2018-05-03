package com.chenxu.workassistant.collection;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Applacation;

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
        if (file.isDirectory()) {
            mView.openFolder(file);
        } else {
            String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            switch (suffix) {
                case "mp3":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "wav":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "flac":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "ape":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "java":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "html":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "js":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "css":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "json":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "xml":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;

                case "xlsx":
                    mView.openOffice(file, view);
                    break;
                case "xls":
                    mView.openOffice(file, view);
                    break;
                case "png":
                    mView.openImage(file, view);
                    break;
                case "jpg":
                    mView.openImage(file, view);
                    break;
                case "gif":
                    mView.openImage(file, view);
                    break;
                case "bmp":
                    mView.openImage(file, view);
                    break;
                case "pdf":
                    mView.openOffice(file, view);
                    break;
                case "ppt":
                    mView.openOffice(file, view);
                    break;
                case "pptx":
                    mView.openOffice(file, view);
                    break;
                case "txt":
                    mView.openOffice(file, view);
                    break;
                case "mp4":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "flv":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "avi":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "3gp":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "mkv":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "rmvb":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "wmv":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "doc":
                    mView.openOffice(file, view);
                    break;
                case "docx":
                    mView.openOffice(file, view);
                    break;
                case "rar":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                case "zip":
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(Applacation.getInstance(), R.string.file_menage_open_err, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
