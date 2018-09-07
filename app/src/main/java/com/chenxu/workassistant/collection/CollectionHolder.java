package com.chenxu.workassistant.collection;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Application;
import com.chenxu.workassistant.utils.FileUtil;

public class CollectionHolder extends RecyclerView.ViewHolder {
    ImageView ivCollectionIcon;
    TextView tvCollectionName;
    TextView tvCollectionTime;

    public CollectionHolder(View itemView) {
        super(itemView);
        ivCollectionIcon = itemView.findViewById(R.id.iv_collection_icon);
        tvCollectionName = itemView.findViewById(R.id.tv_collection_name);
        tvCollectionTime = itemView.findViewById(R.id.tv_collection_time);
    }

    public void bindData(FileBean fileBean){
        switch (FileUtil.fileType(fileBean.getFile())){//1文件夹、2音乐、3文件、4代码、5Excel、6图片、7PDF、8PPT、9TXT、10视频、11Word、12压缩
            case 1: ivCollectionIcon.setImageResource(R.drawable.file_folder); break;
            case 2: ivCollectionIcon.setImageResource(R.drawable.file_audio); break;
            case 3: ivCollectionIcon.setImageResource(R.drawable.file_blank); break;
            case 4: ivCollectionIcon.setImageResource(R.drawable.file_code); break;
            case 5: ivCollectionIcon.setImageResource(R.drawable.file_excel); break;
            case 6: Glide.with(Application.getInstance()).load(fileBean.getFile()).into(ivCollectionIcon); break;
            case 7: ivCollectionIcon.setImageResource(R.drawable.file_pdf); break;
            case 8: ivCollectionIcon.setImageResource(R.drawable.file_ppt); break;
            case 9: ivCollectionIcon.setImageResource(R.drawable.file_txt); break;
            case 10: ivCollectionIcon.setImageResource(R.drawable.file_video); break;
            case 11: ivCollectionIcon.setImageResource(R.drawable.file_word); break;
            case 12: ivCollectionIcon.setImageResource(R.drawable.file_zip); break;
        }
        tvCollectionName.setText(fileBean.getFile().getName());
        tvCollectionTime.setText(fileBean.getTime());
    }
}
