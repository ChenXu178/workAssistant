package com.chenxu.workassistant.FileReader;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.databinding.ActivityFileOfficeReaderBinding;
import com.chenxu.workassistant.utils.StatusBarUtil;
import com.tencent.smtt.sdk.TbsReaderView;

public class OfficeFileReaderActivity extends BaseActivity<ActivityFileOfficeReaderBinding> implements TbsReaderView.ReaderCallback{

    TbsReaderView tbsReaderView;
    String filePath,cachePath;

    public static final String VIEW_DETAIL = "view:detail";
    public static final String FILE_ID = "file:office";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_file_office_reader;
    }

    @Override
    protected void initView() {
        StatusBarUtil.darkMode(this);
        tbsReaderView = new TbsReaderView(this,this);
        mBinding.rlMain.addView(tbsReaderView,new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        filePath = getIntent().getStringExtra(FILE_ID);
        cachePath = getExternalCacheDir().getPath();
        ViewCompat.setTransitionName(mBinding.rlMain,VIEW_DETAIL);

        new Thread(){
            @Override
            public void run() {
                super.run();
                boolean result = tbsReaderView.preOpen(getFileType(filePath),false);
                if (result){
                    handler.sendEmptyMessage(1);
                }else {

                }
            }
        }.start();
        openFile(filePath,cachePath);
    }

    @Override
    protected void bindEvent() {
        mBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                openFile(filePath,cachePath);
            }else {
                Toast.makeText(OfficeFileReaderActivity.this,R.string.file_reader_open_err,Toast.LENGTH_LONG).show();
            }
        }
    };

    private void openFile(String filePath,String tempPath){
        Bundle bundle = new Bundle();
        bundle.putString("filePath", filePath);
        bundle.putString("tempPath", tempPath);
        tbsReaderView.openFile(bundle);
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }
        str = paramString.substring(i + 1);
        return str;
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tbsReaderView.onStop();
    }
}
