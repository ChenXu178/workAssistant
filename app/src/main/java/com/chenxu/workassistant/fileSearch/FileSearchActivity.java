package com.chenxu.workassistant.fileSearch;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.utils.StatusBarUtil;

public class FileSearchActivity extends BaseActivity implements FileSearchContract.View{

    private FileSearchContract.Presenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_file_search;
    }

    @Override
    protected void initView() {
        StatusBarUtil.darkMode(this);
        mPresenter = new FileSearchPresenter(this,this);
    }

    @Override
    protected void bindEvent() {

    }
}
