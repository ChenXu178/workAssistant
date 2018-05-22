package com.chenxu.workassistant.fileSearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenxu.workassistant.R;

import java.io.File;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter {
    private List<SearchBean> list;
    private Context context;

    public SearchAdapter(List<SearchBean> list,Context context){
        this.list = list;
        this.context = context;
    }

    public void setList(List<SearchBean> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file_search,null);
        return new SearchHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SearchHolder searchHolder = (SearchHolder)holder;
        searchHolder.bindData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
