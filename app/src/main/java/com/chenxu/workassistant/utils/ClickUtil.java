package com.chenxu.workassistant.utils;

import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Android on 2018/3/22.
 */

public abstract class ClickUtil {

    public ClickUtil clickAntiShake(View view){
        RxView.clicks(view).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Observer<Object>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object value) {
                method();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        return this;
    }

    public abstract void method();
}
