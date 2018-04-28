package com.chenxu.workassistant.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Applacation;

public class DialogUtil {

    public static PopupWindow initDialog(View dialogView){
        PopupWindow window = new PopupWindow(dialogView);
        window.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setOutsideTouchable(true);
        window.setFocusable(true);
        window.setTouchable(true);
        window.setBackgroundDrawable(Applacation.getInstance().getDrawable(R.drawable.base_dialog_bg));
        window.setAnimationStyle(R.style.baseDialogAnim);
        return window;
    }

    public static PopupWindow initLoadDialog(final Activity activity){
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_file_loading,null);
        PopupWindow window = new PopupWindow(view);
        window.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        window.setTouchable(true);
        window.setAnimationStyle(R.style.fadeDialogAnim);
        BackgroundUtil.setBackgroundAlpha(0.8f,activity);
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                BackgroundUtil.setBackgroundAlpha(1f,activity);
            }
        });
        return window;
    }

    public static PopupWindow initLoadDialog(final Activity activity,int title){
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_loading,null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        PopupWindow window = new PopupWindow(view);
        window.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        window.setTouchable(true);
        window.setAnimationStyle(R.style.fadeDialogAnim);
        BackgroundUtil.setBackgroundAlpha(0.8f,activity);
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                BackgroundUtil.setBackgroundAlpha(1f,activity);
            }
        });
        return window;
    }
}
