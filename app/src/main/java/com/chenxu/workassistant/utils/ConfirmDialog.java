package com.chenxu.workassistant.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chenxu.workassistant.R;

public class ConfirmDialog {

    private Context context;
    private int hint;
    private View relyView;
    private PopupWindow window;

    private ConfirmListener.OnDialogConfirmClickListener confirmClickListener;
    private ConfirmListener.OnDialogCancelClickListener cancelClickListener;

    public interface ConfirmListener{

        interface OnDialogConfirmClickListener{
            void onConfirmButtonClick();
        }

        interface OnDialogCancelClickListener{
            void onCancelButtonClick();
        }

    }

    public ConfirmDialog(Context context){
        this.context = context;
    }

    public static ConfirmDialog with(Context context){
        return new ConfirmDialog(context);
    }


    public ConfirmDialog setHint(int hint) {
        this.hint = hint;
        return this;
    }

    public ConfirmDialog setRelyView(View relyView) {
        this.relyView = relyView;
        return this;
    }


    public ConfirmDialog setOnConfirmListener(ConfirmListener.OnDialogConfirmClickListener onDialogConfirmClickListener){
        this.confirmClickListener = onDialogConfirmClickListener;
        return this;
    }

    public ConfirmDialog setOnCancelListener(ConfirmListener.OnDialogCancelClickListener onDialogCancelClickListener){
        this.cancelClickListener = onDialogCancelClickListener;
        return this;
    }

    public ConfirmDialog build(){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm,null);
        TextView tvHint = view.findViewById(R.id.tv_confirm_hint);
        tvHint.setText(hint);
        ImageView ivClose = view.findViewById(R.id.iv_confirm_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (window!=null){
                    window.dismiss();
                }
                cancelClickListener.onCancelButtonClick();
            }
        });
        Button btnCancel = view.findViewById(R.id.btn_confirm_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (window!=null){
                    window.dismiss();
                }
                cancelClickListener.onCancelButtonClick();
            }
        });

        Button btnConfirm = view.findViewById(R.id.btn_confirm_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (window!=null){
                    window.dismiss();
                }
                confirmClickListener.onConfirmButtonClick();
            }
        });

        window = new PopupWindow(view);
        window.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        window.setTouchable(true);
        window.setAnimationStyle(R.style.fadeDialogAnim);
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                BackgroundUtil.setBackgroundAlpha(1f,context);
            }
        });
        return this;
    }

    public ConfirmDialog show(){
        BackgroundUtil.setBackgroundAlpha(0.8f,context);
        if (window!=null){
            window.showAtLocation(relyView, Gravity.CENTER,0,0);
        }
        return this;
    }

}
