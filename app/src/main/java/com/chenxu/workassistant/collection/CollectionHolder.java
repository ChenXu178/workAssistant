package com.chenxu.workassistant.collection;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenxu.workassistant.R;
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
        ivCollectionIcon.setImageResource(FileUtil.fileIconForType(fileBean.getFile()));
        tvCollectionName.setText(fileBean.getFile().getName());
        tvCollectionTime.setText(fileBean.getTime());
    }
}
