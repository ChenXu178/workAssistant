package com.chenxu.workassistant.readEmail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenxu.workassistant.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EnclosureAdapter extends RecyclerView.Adapter {

    private List<String> files;
    private Context mContext;
    private OnItemButtonClickListener onItemButtonClickListener;

    interface OnItemButtonClickListener{
        void onItemButtonClick(int position);
    }

    public EnclosureAdapter(List<String> data,Context context){
        this.files = data;
        this.mContext = context;
    }

    public void setData(List<String> data){
        this.files = data;
        this.notifyDataSetChanged();
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public void setOnItemButtonClickListener(OnItemButtonClickListener onItemButtonClickListener) {
        this.onItemButtonClickListener = onItemButtonClickListener;
    }

    public void removeItem(int position){
        //files.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(0,files.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_read_enclosure,null);
        return new EnclosureHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EnclosureHolder enclosureHolder = (EnclosureHolder) holder;
        enclosureHolder.bindData(files.get(position));
        if (onItemButtonClickListener != null){
            enclosureHolder.btnFileDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemButtonClickListener.onItemButtonClick(position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}
