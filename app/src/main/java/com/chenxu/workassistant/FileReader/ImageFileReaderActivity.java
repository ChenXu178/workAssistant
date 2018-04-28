package com.chenxu.workassistant.FileReader;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.bumptech.glide.Glide;
import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.PhotoRecognition.PhotoRecognitionActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.databinding.ActivityFileImageReaderBinding;
import com.chenxu.workassistant.utils.StatusBarUtil;

import java.io.File;

public class ImageFileReaderActivity extends BaseActivity<ActivityFileImageReaderBinding> implements View.OnClickListener {

    public static final String VIEW_DETAIL = "view:detail";
    public static final String FILE_ID = "file:id";
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
        }
    }
}
