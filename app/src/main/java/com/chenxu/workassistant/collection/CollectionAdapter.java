package com.chenxu.workassistant.collection;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenxu.workassistant.R;

import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter {
    private List<FileBean> list;
    private Context mContext;

    public CollectionAdapter(List<FileBean> data, Context context){
        this.list = data;
        this.mContext = context;
    }

    public void deleteByPosition(int position){
        //this.list.remove(position);
        this.notifyItemRemoved(position);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_collection,null);
        return new CollectionHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CollectionHolder collectionHolder = (CollectionHolder)holder;
        collectionHolder.bindData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
