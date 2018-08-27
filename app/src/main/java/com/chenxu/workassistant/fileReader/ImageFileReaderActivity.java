package com.chenxu.workassistant.fileReader;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.bumptech.glide.Glide;
import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.photoRecognition.PhotoRecognitionActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.databinding.ActivityFileImageReaderBinding;
import com.chenxu.workassistant.utils.StatusBarUtil;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class ImageFileReaderActivity extends BaseActivity<ActivityFileImageReaderBinding> implements View.OnClickListener {

    public static final String VIEW_DETAIL = "view:detail";
    public static final String FILE_ID = "file:id";
    private static final int showTime = 1500;
    private String imagePath;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_file_image_reader;
    }

    @Override
    protected void initView() {
        StatusBarUtil.darkMode(this);
        imagePath = getIntent().getStringExtra(FILE_ID);
        ViewCompat.setTransitionName(mBinding.ivImage,VIEW_DETAIL);
        Glide.with(this).load(new File(imagePath)).into(mBinding.ivImage);
        handler.sendEmptyMessageDelayed(1,showTime);
    }

    @Override
    protected void bindEvent() {
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBinding.btnAnalysis.setOnClickListener(this);
        mBinding.ivImage.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_analysis:
                Intent intent = new Intent(this, PhotoRecognitionActivity.class);
                intent.putExtra(PhotoRecognitionActivity.OPEN_TYPE,2);
                intent.putExtra(PhotoRecognitionActivity.OPEN_FILE,imagePath);
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(mBinding.btnAnalysis, PhotoRecognitionActivity.VIEW_ANIM));
                ActivityCompat.startActivity(this, intent, compat.toBundle());
                break;
            case R.id.iv_image:
                if (mBinding.rlBar.getVisibility() == View.GONE){
                    setShowBar(true);
                }
                break;
        }
    }

    private void setShowBar(boolean isShow){
        handler.removeMessages(0);
        if (!isShow){
            mBinding.rlBar.setVisibility(View.GONE);
            mBinding.ivLine.setVisibility(View.GONE);
            mBinding.btnAnalysis.setVisibility(View.GONE);
        }else {
            mBinding.rlBar.setVisibility(View.VISIBLE);
            mBinding.ivLine.setVisibility(View.VISIBLE);
            mBinding.btnAnalysis.setVisibility(View.VISIBLE);
            handler.sendEmptyMessageDelayed(0,showTime);
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setShowBar(false);
        }
    };
}
