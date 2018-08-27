package com.chenxu.workassistant.fileMenage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.chenxu.workassistant.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Android on 2018/3/26.
 */

public class FilesAdapter extends RecyclerView.Adapter {

    private ArrayList<FileBean> mFileBeans;
    private Context mContext;

    /**
     * 事件接口
     */
    interface OnItemClickListener {
        //      点击事件
        void onItemClick(View view, int position);
        //      长按事件
        void onItemLongClick(View view, int position);
    }


    public FilesAdapter(ArrayList<FileBean> fileBeans,Context context){
        mFileBeans = fileBeans;
        mContext = context;
    }

    /**
     * 更新全部数据
     * @param mFileBeans
     */
    public void setFileBeans(ArrayList<FileBean> mFileBeans) {
        this.mFileBeans = mFileBeans;
        this.notifyDataSetChanged();
    }

    /**
     * 显示选择框
     */
    public void showCheckbox(){
        for (FileBean fileBean:mFileBeans) {
            fileBean.setShowCB(true);
        }
        this.notifyDataSetChanged();
    }

    /**
     * 隐藏选择框
     */
    public void hideCheckbox(){
        for (FileBean fileBean:mFileBeans) {
            fileBean.setShowCB(false);
            fileBean.setChecked(false);
        }
        this.notifyDataSetChanged();
    }

    /**
     * 改变选择框状态
     * @param isChecked
     * @param position
     */
    public void setFileChecked(boolean isChecked,int position){
        mFileBeans.get(position).setChecked(isChecked);
        this.notifyItemChanged(position);
    }

    /**
     * 全选、取消全选
     * @param isChecked
     */
    public void setFileAllChecked(boolean isChecked){
        for (FileBean fileBean:mFileBeans) {
            fileBean.setChecked(isChecked);
        }
        this.notifyItemRangeChanged(0,mFileBeans.size());
    }

    /**
     * 删除一项
     * @param fileBean
     */
    public void deleteFile(FileBean fileBean){
        int position = mFileBeans.indexOf(fileBean);
        mFileBeans.remove(fileBean);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(0,mFileBeans.size());
    }

    /**
     * 添加多个
     * @param files
     */
    public void addFiles(ArrayList<FileBean> files){
        int positionStart = mFileBeans.size() - 1;
        for (FileBean fileBean:files){
            mFileBeans.add(fileBean);
        }
        this.notifyItemRangeInserted(positionStart,files.size());
    }

    public void addFile(FileBean fileBean){
        mFileBeans.add(fileBean);
        this.notifyItemInserted(mFileBeans.size());
    }

    /**
     * 更新一项
     * @param position
     * @param file
     */
    public void updateFile(int position,File file){
        mFileBeans.get(position).setFile(file);
        this.notifyItemChanged(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_file_menage_file,null);
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final FileHolder fileHolder = (FileHolder)holder;
        fileHolder.bindData(mFileBeans.get(position),mContext);

        fileHolder.cbSelect.setTag(position);
        fileHolder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = (int) buttonView.getTag();
                if (isChecked) {
                    mFileBeans.get(pos).setChecked(true);
                    //do something
                } else {
                    mFileBeans.get(pos).setChecked(false);
                    //do something else
                }
            }
        });

        fileHolder.cbSelect.setChecked(mFileBeans.get(position).isChecked());
    }

    @Override
    public int getItemCount() {
        return mFileBeans.size();
    }
}
