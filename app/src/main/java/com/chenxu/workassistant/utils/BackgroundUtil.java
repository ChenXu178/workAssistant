package com.chenxu.workassistant.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.WindowManager;


/**
 * Created by 陈旭 on 2017/1/27.
 */

public class BackgroundUtil {

    private static final String TAG = "BackgroundUtil";
    private static Bitmap bitmap;

    /**
     * 背景透明度
     * @param view
     * @param show
     */
    public static void setBackgroundAlpha(View view, boolean show) {
        if (view != null){
            view.setVisibility( show ? View.VISIBLE : View.GONE);
        }
    }

}
