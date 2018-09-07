package com.chenxu.workassistant.fileSearch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Application;
import com.chenxu.workassistant.utils.FileUtil;

public class SearchHolder extends RecyclerView.ViewHolder {
    ImageView ivFileType;
    TextView tvFileDetail;
    TextView tvFileName;

    public SearchHolder(View itemView) {
        super(itemView);
        ivFileType = (ImageView) itemView.findViewById(R.id.iv_file_type);
        tvFileDetail = (TextView) itemView.findViewById(R.id.tv_file_detail);
        tvFileName = (TextView) itemView.findViewById(R.id.tv_file_name);
    }

    public void bindData(SearchBean searchBean){
        switch (FileUtil.fileType(searchBean.getFile())){//1文件夹、2音乐、3文件、4代码、5Excel、6图片、7PDF、8PPT、9TXT、10视频、11Word、12压缩
            case 1: ivFileType.setImageResource(R.drawable.file_folder); break;
            case 2: ivFileType.setImageResource(R.drawable.file_audio); break;
            case 3: ivFileType.setImageResource(R.drawable.file_blank); break;
            case 4: ivFileType.setImageResource(R.drawable.file_code); break;
            case 5: ivFileType.setImageResource(R.drawable.file_excel); break;
            case 6: Glide.with(Application.getInstance()).load(searchBean.getFile()).into(ivFileType); break;
            case 7: ivFileType.setImageResource(R.drawable.file_pdf); break;
            case 8: ivFileType.setImageResource(R.drawable.file_ppt); break;
            case 9: ivFileType.setImageResource(R.drawable.file_txt); break;
            case 10: ivFileType.setImageResource(R.drawable.file_video); break;
            case 11: ivFileType.setImageResource(R.drawable.file_word); break;
            case 12: ivFileType.setImageResource(R.drawable.file_zip); break;
        }
        tvFileName.setText(searchBean.getFile().getName());
        tvFileDetail.setText(FileUtil.convertFileSize(searchBean.getFile().length()));
    }
}
