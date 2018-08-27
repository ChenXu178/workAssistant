package com.chenxu.workassistant.photoRecognition;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.chenxu.workassistant.R;

public class TextHolder extends RecyclerView.ViewHolder {
    CheckBox cbText;

    public TextHolder(View itemView) {
        super(itemView);
        cbText = (CheckBox)itemView.findViewById(R.id.cb_text);
    }


}
