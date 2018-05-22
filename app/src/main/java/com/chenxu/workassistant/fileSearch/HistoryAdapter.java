package com.chenxu.workassistant.fileSearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenxu.workassistant.R;
import com.chenxu.workassistant.dao.SearchEntity;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter {

    private List<SearchEntity> list;
    private Context context;

    private OnClickListener onClickListener;

    public interface OnClickListener{
        void onClick(String text);
    }


    public HistoryAdapter(List<SearchEntity> list,Context context){
        this.list = list;
        this.context = context;
    }

    public void setList(List<SearchEntity> list){
        this.list = list;
        this.notifyDataSetChanged();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_text,null);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HistoryHolder historyHolder = (HistoryHolder)holder;
        historyHolder.bindData(list.get(position));
        if (onClickListener != null){
            historyHolder.tvSearchHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(list.get(position).getText());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
