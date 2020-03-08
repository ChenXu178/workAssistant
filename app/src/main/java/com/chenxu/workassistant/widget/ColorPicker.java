package com.chenxu.workassistant.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chenxu.workassistant.R;
import com.chenxu.workassistant.utils.BackgroundUtil;
import com.flask.colorpicker.ColorPickerView;

public class ColorPicker {
    private Context context;
    private int title,density = 12;
    private int startColor = Color.parseColor("#FFFFFF");
    private View relyView;
    private View bgView;

    private PopupWindow window;
    private View dialogView;

    private Listener.OnColorSelectedListener colorSelectedListener;
    private Listener.OnDialogCancelListener dialogCancelListener;

    public ColorPicker(Context context) {
        this.context = context;
    }

    public static ColorPicker with(Context context) {
        return new ColorPicker(context);
    }

    public ColorPicker setTitle(int title) {
        this.title = title;
        return this;
    }

    public ColorPicker setDensity(int density) {
        this.density = density;
        return this;
    }

    public ColorPicker setStartColor(int color) {
        this.startColor = color;
        return this;
    }

    public ColorPicker setRelyView(View view) {
        this.relyView = view;
        return this;
    }

    public ColorPicker setBgView(View view) {
        this.bgView = view;
        return this;
    }

    public ColorPicker setColorSelectedListener(Listener.OnColorSelectedListener onColorSelectedListener){
        this.colorSelectedListener = onColorSelectedListener;
        return this;
    }

    public ColorPicker setDialogCancelListener(Listener.OnDialogCancelListener onDialogCancelListener){
        this.dialogCancelListener = onDialogCancelListener;
        return  this;
    }


    public ColorPicker build() {
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_color_selector, null);
        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_color_title);
        tvTitle.setText(title);
        ColorPickerView pickerView = dialogView.findViewById(R.id.color_picker_view);
        pickerView.setInitialColor(startColor, true);
        pickerView.setDensity(density);
        Button btnConfirm = dialogView.findViewById(R.id.btn_color_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.dismiss();
                colorSelectedListener.onColorSelected(pickerView.getSelectedColor());
            }
        });
        ImageView ivColorClose = dialogView.findViewById(R.id.iv_color_close);
        ivColorClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.dismiss();
                dialogCancelListener.onDialogCancel();
            }
        });
        window  = new PopupWindow(dialogView);
        window.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        window.setTouchable(true);
        window.setAnimationStyle(R.style.fadeDialogAnim);
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (bgView != null){
                    BackgroundUtil.setBackgroundAlpha(bgView,false);
                }
            }
        });
        return this;
    }

    public ColorPicker show(){
        if (bgView != null){
            BackgroundUtil.setBackgroundAlpha(bgView,true);
        }
        window.showAtLocation(relyView, Gravity.CENTER,0,0);
        return this;
    }

    public interface Listener{
        interface OnColorSelectedListener{
            void onColorSelected(int selectColor);
        }

        interface OnDialogCancelListener{
            void onDialogCancel();
        }
    }

}
