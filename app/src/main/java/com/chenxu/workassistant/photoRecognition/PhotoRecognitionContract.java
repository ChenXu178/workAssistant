package com.chenxu.workassistant.photoRecognition;

import android.net.Uri;

import com.chenxu.workassistant.BasePresenter;
import com.chenxu.workassistant.BaseView;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;

public interface PhotoRecognitionContract {

    interface View extends BaseView {
        void startCamera(); //打开相机
        void startImageSelect(); //图片选择
        void startPhotoZoom(Uri uri); //裁剪图片
        void showToast(int textId); //显示提示
        void showToast(String text); //显示提示
        void showLoading(); //显示加载框
        void hideLoading(); //隐藏加载框
        void setSelectView(); //设置为选择文本视图
        void resetView(); //复位视图
        void setTextData(ArrayList<TextBean> data);  //设置文本内容
        void setCopyBtnState(boolean state);  //设置复制按钮状态
        void copyText(); //复制文本
        void onParseError(); //解析错误视图
    }

    interface Presenter extends BasePresenter {
        void parseImg(File imgFile);  //解析图片
        void dataHandle(JSONArray array); //解析json
        void onError(int textId); //错误
        void cleanData(); //清空数据
        void setItemSelect(boolean isChecked, int position); //选择文本
        String getSelectText(); //获得已选择的文本
    }

    interface Model{
        void toLexer(String text); //解析文本
    }
}
