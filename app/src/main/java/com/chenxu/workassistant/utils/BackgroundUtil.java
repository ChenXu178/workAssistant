package com.chenxu.workassistant.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.WindowManager;


/**
 * Created by 陈旭 on 2017/1/27.
 */

public class BackgroundUtil {

    private static final String TAG = "BackgroundUtil";
    private static Bitmap bitmap;

    /**
     * 背景透明度
     * @param bgAlpha
     * @param context
     */
    public static void setBackgroundAlpha(float bgAlpha, Context context) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) context).getWindow().setAttributes(lp);
    }



}
