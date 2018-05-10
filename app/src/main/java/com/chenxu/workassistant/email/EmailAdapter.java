package com.chenxu.workassistant.email;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenxu.workassistant.R;

import java.util.List;

public class EmailAdapter extends RecyclerView.Adapter {

    private List<Email> list;
    private Context context;

    public EmailAdapter(List<Email> list,Context context){
        this.list = list;
        this.context = context;
    }

    public void setData(List<Email> data){
        this.list = data;
        this.notifyDataSetChanged();
    }

    public void deleteItem(int position){
        this.list.remove(position);
        this.notifyItemRemoved(position);
    }

    public void setItemRead(int position){
        this.list.get(position).setNews(false);
        this.notifyItemChanged(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_email,null);
        return new EmailHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EmailHolder emailHolder = (EmailHolder)holder;
        emailHolder.bindData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
