package com.chenxu.workassistant.utils;

import com.chenxu.workassistant.config.Application;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.chenxu.workassistant.R;

public class SnackBarUtils {

    /**
     *Snackbar显示提示信息
     * @param view 当前页面的一个控件
     * @param msg 提示信息，在strings.xml
     * @param messageColor 文本颜色
     * @param backgroundColor 背景色
     */
    public static void showSnackBarMSG(View view,int msg, int messageColor, int backgroundColor) {
        Snackbar snackbar = Snackbar.make(view,msg, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();//获取SnackBar的view
        if(snackbarView!=null){
            snackbarView.setBackgroundColor(Application.getInstance().getResources().getColor(backgroundColor));//修改view的背景色
            ((TextView) snackbarView.findViewById(R.id.snackbar_text)).setTextColor(Application.getInstance().getResources().getColor(messageColor));//获取Snackbar的message控件，修改字体颜色
        }
        snackbar.show();
    }


    /**
     *Snackbar显示提示信息
     * @param view 当前页面的一个控件
     * @param msg 提示信息，在strings.xml
     * @param messageColor 文本颜色
     * @param backgroundColor 背景色
     */
    public static void showSnackBarMSG(View view,String msg, int messageColor, int backgroundColor) {
        Snackbar snackbar = Snackbar.make(view,msg, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();//获取SnackBar的view
        if(snackbarView!=null){
            snackbarView.setBackgroundColor(Application.getInstance().getResources().getColor(backgroundColor));//修改view的背景色
            ((TextView) snackbarView.findViewById(R.id.snackbar_text)).setTextColor(Application.getInstance().getResources().getColor(messageColor));//获取Snackbar的message控件，修改字体颜色
        }
        snackbar.show();
    }
}
