package com.chenxu.workassistant.fileSearch;

import android.content.Context;

public class FileSearchPresenter implements FileSearchContract.Presenter {
    private FileSearchContract.View mView;
    private Context mContext;

    public FileSearchPresenter(FileSearchContract.View view,Context context){
        this.mView = view;
        this.mContext = context;
    }

    @Override
    public void start() {

    }
}
