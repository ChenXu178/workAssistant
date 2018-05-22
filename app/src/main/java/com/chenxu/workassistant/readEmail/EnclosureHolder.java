package com.chenxu.workassistant.readEmail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenxu.workassistant.R;
import com.chenxu.workassistant.utils.FileUtil;

import java.io.File;

public class EnclosureHolder extends RecyclerView.ViewHolder {

    ImageView ivFileType;
    TextView tvFileName;
    Button btnFileDownload;

    public EnclosureHolder(View itemView) {
        super(itemView);
        ivFileType = itemView.findViewById(R.id.iv_file_type);
        tvFileName = itemView.findViewById(R.id.tv_file_name);
        btnFileDownload = itemView.findViewById(R.id.btn_file_download);
    }

    public void bindData(String filename){
        ivFileType.setImageResource(FileUtil.fileIconForType(filename));
        tvFileName.setText(filename);
    }
}
