package com.chenxu.workassistant.photoRecognition;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.chenxu.workassistant.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;

public class PhotoRecognitionPresenter implements PhotoRecognitionContract.Presenter {

    private PhotoRecognitionContract.View mView;
    private PhotoRecognitionContract.Model mModel;
    private Context mContext;
    private ArrayList<TextBean> data,selectData;

    public PhotoRecognitionPresenter(PhotoRecognitionContract.View view,Context context){
        this.mView = view;
        this.mModel = new LexerModel(this);
        this.mContext = context;
        data = new ArrayList<>();
        selectData = new ArrayList<>();
    };

    @Override
    public void start() {

    }


    @Override
    public void parseImg(File imgFile) {
        // 通用文字识别参数设置
        GeneralBasicParams param = new GeneralBasicParams();
        param.setDetectDirection(true);
        param.setImageFile(imgFile);
        // 调用通用文字识别服务
        OCR.getInstance().recognizeAccurateBasic(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                // 调用成功，返回GeneralResult对象
                StringBuffer sb = new StringBuffer();
                for (WordSimple wordSimple : result.getWordList()) {
                    // wordSimple不包含位置信息
                    sb.append(wordSimple.getWords());
                }
                Log.e("GeneralResult",sb.toString());
                //词法分析
                if ("".equals(sb.toString())){
                    Message message = new Message();
                    message.what = 1;
                    message.obj = R.string.error_parse_no_text;
                    handler.sendMessage(message);
                }else {
                    mModel.toLexer(sb.toString());
                }
            }
            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError对象
                Log.e("OCRError",""+error.getErrorCode());
                Message message = new Message();
                message.what = 1;
                message.obj = error.getErrorCode();
                handler.sendMessage(message);
            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1://OCR错误
                    onError((int)msg.obj);
                    break;
            }
        }
    };

    @Override
    public void dataHandle(JSONArray array) {
        try {
            for (int i = 0;i<array.length();i++){
                TextBean textBean = new TextBean(array.get(i).toString(),true);
                data.add(textBean);
                selectData.add(textBean);
            }
            mView.setTextData(data);
        } catch (JSONException e) {
            e.printStackTrace();
            Message message = new Message();
            message.what = 1;
            message.obj = R.string.error_parse;
            handler.sendMessage(message);
        }
    }

    @Override
    public void onError(int textId) {
        mView.hideLoading();
        mView.onParseError();
        switch (textId){
            case 1:mView.showToast(R.string.error_1);break;
            case 2:mView.showToast(R.string.error_2);break;
            case 4:mView.showToast(R.string.error_4);break;
            case 17:mView.showToast(R.string.error_17);break;
            case 18:mView.showToast(R.string.error_18);break;
            case 19:mView.showToast(R.string.error_19);break;
            case 100:mView.showToast(R.string.error_100);break;
            case 110:mView.showToast(R.string.error_110);break;
            case 111:mView.showToast(R.string.error_111);break;
            case 283504:mView.showToast(R.string.error_283504);break;
            case 283505:mView.showToast(R.string.error_283505);break;
            case 283506:mView.showToast(R.string.error_283506);break;
            case 283601:mView.showToast(R.string.error_283601);break;
            case 283602:mView.showToast(R.string.error_283602);break;
            case 283700:mView.showToast(R.string.error_283700);break;
            case 216200:mView.showToast(R.string.error_216200);break;
            case 216202:mView.showToast(R.string.error_216202);break;
            case 216401:mView.showToast(R.string.error_216401);break;
            case 216500:mView.showToast(R.string.error_216500);break;
            case 216632:mView.showToast(R.string.error_216632);break;
            case R.string.error_parse:mView.showToast(R.string.error_parse);break;
            case R.string.error_parse_no_text:mView.showToast(R.string.error_parse_no_text);break;
        }
    }

    @Override
    public void cleanData() {
        data.clear();
        selectData.clear();
    }

    @Override
    public void setItemSelect(boolean isChecked, int position) {
        TextBean bean = data.get(position);
        if (isChecked){
            if (selectData.size() != data.size()){
                selectData.add(bean);
            }
        }else {
            selectData.remove(bean);
        }
        if (selectData.size()>0){
            mView.setCopyBtnState(true);
        }else {
            mView.setCopyBtnState(false);
        }
    }

    @Override
    public String getSelectText() {
        StringBuffer sb = new StringBuffer();
        for (TextBean bean:selectData){
            sb.append(bean.getText());
        }
        return sb.toString();
    }


}
