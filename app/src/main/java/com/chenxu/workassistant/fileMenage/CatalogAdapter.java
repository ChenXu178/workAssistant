package com.chenxu.workassistant.fileMenage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenxu.workassistant.R;

import java.util.ArrayList;

public class CatalogAdapter extends RecyclerView.Adapter {

    private ArrayList<CatalogBean> data;
    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener OnItemClickListener) {
        this.mOnItemClickListener = OnItemClickListener;
    }

    public CatalogAdapter(ArrayList<CatalogBean> data){
        this.data = data;
    }

    public void addItem(CatalogBean catalogBean){
        this.data.add(catalogBean);
        this.notifyItemInserted(this.data.size()-1);
    }

    public void removeItem(){
        int index = this.data.size()-1;
        this.data.remove(this.data.size()-1);
        this.notifyItemRemoved(index);
    }

    public void cleaItem(){
        this.data.clear();
        this.notifyDataSetChanged();
    }

    public void removeItemToPosition(int position){
        while (this.data.size()-1 > position){
            this.data.remove(this.data.size()-1);
        }
        this.notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_catalog,null);
        return new CatalogHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CatalogHolder catalogHolder = (CatalogHolder)holder;

        catalogHolder.bindData(data.get(position));

        if (mOnItemClickListener != null){
            catalogHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(catalogHolder.itemView,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
