package com.chenxu.workassistant;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;


/**页面描述：activity基类
 * Created by Android on 2018/3/21.
 */

public abstract class BaseActivity<VB extends ViewDataBinding> extends Activity {

    protected VB mBinding;
    protected View rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(this.getLayoutId(), null, false);
        mBinding = DataBindingUtil.bind(rootView);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        fullScreen();
        super.setContentView(rootView);
        initView();
        bindEvent();
    }

    protected void fullScreen(){}

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void bindEvent();
}
