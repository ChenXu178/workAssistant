package com.chenxu.workassistant.sendEmail;

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
    TextView tvFileDetail;
    TextView tvFileName;
    Button btnFileDelete;

    public EnclosureHolder(View itemView) {
        super(itemView);
        ivFileType = itemView.findViewById(R.id.iv_file_type);
        tvFileDetail = itemView.findViewById(R.id.tv_file_detail);
        tvFileName = itemView.findViewById(R.id.tv_file_name);
        btnFileDelete = itemView.findViewById(R.id.btn_file_delete);
    }

    public void bindData(File file){
        ivFileType.setImageResource(FileUtil.fileIconForType(file));
        tvFileName.setText(file.getName());
        tvFileDetail.setText(FileUtil.convertFileSize(file.length()));
    }
}
