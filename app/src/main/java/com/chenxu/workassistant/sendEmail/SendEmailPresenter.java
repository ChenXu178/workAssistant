package com.chenxu.workassistant.sendEmail;

import android.content.Context;

import com.chenxu.workassistant.R;
import com.chenxu.workassistant.dao.EnclosureEntity;
import com.chenxu.workassistant.utils.FileUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SendEmailPresenter implements SendEmailContract.Presenter {

    private SendEmailContract.View mView;
    private SendEmailContract.Model mModel;
    private Context mContext;

    private List<File> fileList;
    private long allEnclosureSize = 0;

    public SendEmailPresenter(SendEmailContract.View view,Context context){
        this.mView = view;
        this.mModel = new SendEmailModel(this);
        this.mContext = context;
    }

    @Override
    public void start() {
        mModel.getEnclosure()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        if (files.size() > 0){
                            mView.setEnclosureData(files);
                            fileList = files;
                            formatEnclosureDetail(files);
                        }else {
                            mView.setEnclosureDetail(R.string.send_email_enclosure_detail_no);
                        }
                    }
                });

    }

    @Override
    public void formatEnclosureDetail(List<File> files) {
        mModel.getEnclosureSize(files)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mView.setEnclosureDetail(files.size()+mContext.getResources().getString(R.string.send_email_enclosure_detail)+ FileUtil.convertFileSize(aLong));
                        allEnclosureSize = aLong;
                    }
                });
    }

    @Override
    public void enclosureTitleClick() {
        if (fileList != null && fileList.size() > 0){
            mView.toggleEnclosureIconAndShowEnclosure();
        }else {
            mView.toggleEnclosureIcon();
        }
    }

    @Override
    public void deleteEnclosure(int position) {
        mModel.deleteEnclosure(fileList.get(position).getPath())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        fileList.remove(position);
                        mView.removeAdopterItem(position);
                        formatEnclosureDetail(fileList);
                    }
                });
    }
}
