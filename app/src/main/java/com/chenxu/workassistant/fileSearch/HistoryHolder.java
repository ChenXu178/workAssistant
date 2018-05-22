package com.chenxu.workassistant.fileSearch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chenxu.workassistant.R;
import com.chenxu.workassistant.dao.SearchEntity;

public class HistoryHolder extends RecyclerView.ViewHolder {
    TextView tvSearchHistory;

    public HistoryHolder(View itemView) {
        super(itemView);
        tvSearchHistory = itemView.findViewById(R.id.tv_search_history);
    }

    public void bindData(SearchEntity entity){
        tvSearchHistory.setText(entity.getText());
    }
}
