package com.chenxu.workassistant.FileMenage;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chenxu.workassistant.R;

public class CatalogHolder extends RecyclerView.ViewHolder {

    TextView tvCatalogName;

    public CatalogHolder(View itemView) {
        super(itemView);
        tvCatalogName = (TextView)itemView.findViewById(R.id.tv_catalog_name);
    }

    public void bindData(CatalogBean catalogBean){
        tvCatalogName.setText(catalogBean.getFile().getName());
    }
}
