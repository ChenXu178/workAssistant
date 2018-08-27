package com.chenxu.workassistant.photoRecognition;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.chenxu.workassistant.R;

import java.util.ArrayList;

public class TextAdapter extends RecyclerView.Adapter {

    private ArrayList<TextBean> data;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public void setData(ArrayList<TextBean> list){
        this.data = list;
        this.notifyDataSetChanged();
    }

    /**
     * 事件接口
     */
    interface OnItemClickListener {
        //      点击事件
        void OnCheckedChange(CompoundButton buttonView, boolean isChecked, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public TextAdapter(ArrayList<TextBean> list, Context mContext) {
        this.data = list;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_photo_text,null);
        return new TextHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextHolder textHolder = (TextHolder)holder;
        textHolder.cbText.setText(data.get(position).getText());
        textHolder.cbText.setTag(position);
        textHolder.cbText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = (int) buttonView.getTag();
                if (isChecked) {
                    data.get(pos).setSelect(true);
                    //do something
                } else {
                    data.get(pos).setSelect(false);
                    //do something else
                }
                if (onItemClickListener != null){
                    onItemClickListener.OnCheckedChange(buttonView,isChecked,position);
                }
            }
        });
        textHolder.cbText.setChecked(data.get(position).isSelect());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
