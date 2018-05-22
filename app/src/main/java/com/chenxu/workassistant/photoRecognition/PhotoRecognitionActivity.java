package com.chenxu.workassistant.photoRecognition;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Applacation;
import com.chenxu.workassistant.databinding.ActivityPhotoRecognitionBinding;
import com.chenxu.workassistant.utils.ClickUtil;
import com.chenxu.workassistant.utils.DialogUtil;
import com.chenxu.workassistant.utils.ImageLoadUtil;
import com.chenxu.workassistant.utils.StatusBarUtil;
import com.library.flowlayout.FlowLayoutManager;

import java.io.File;
import java.util.ArrayList;


public class PhotoRecognitionActivity extends BaseActivity<ActivityPhotoRecognitionBinding> implements PhotoRecognitionContract.View,View.OnClickListener {

    private static final String TAG = PhotoRecognitionActivity.class.getSimpleName();
    private File imageFile,photoFile;
    private Uri imageUri;
    private static final int CHOOSE_PICTURE = 1;
    private static final int CROP_PICTURE = 2;
    private static final int PICK_CAMERA = 3;
    private static final int VALUE_PICK_PICTURE = 4;
    private PhotoRecognitionContract.Presenter mPresenter;
    public static final String VIEW_ANIM = "VIEW_ANIM:PHOTO";
    public static final String OPEN_TYPE = "PHOTO:OPEN_TYPE"; //1首页 2文件浏览
    public static final String OPEN_FILE = "PHOTO:OPEN_FILE_PATH"; //文件浏览进入，文件地址
    private TextAdapter adapter;
    private PopupWindow loadDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photo_recognition;
    }

    @Override
    protected void initView() {
        StatusBarUtil.darkMode(this);
        mPresenter = new PhotoRecognitionPresenter(this,this);
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        mBinding.rvText.setLayoutManager(flowLayoutManager);
        adapter = new TextAdapter(new ArrayList<>(),this);
        mBinding.rvText.setAdapter(adapter);
        imageFile = new File(Environment.getExternalStorageDirectory().getPath() + "/WorkAssistantTemp.jpg");
        if (imageFile.exists()){
            imageFile.delete();
        }
        imageUri = Uri.fromFile(imageFile);
        if (getIntent().getIntExtra(OPEN_TYPE,1) == 1){
            ViewCompat.setTransitionName(mBinding.tvBarTitle,this.VIEW_ANIM);
        }else {
            ViewCompat.setTransitionName(mBinding.btnCopy,this.VIEW_ANIM);
            String imagePath = getIntent().getStringExtra(OPEN_FILE);
            Message message = new Message();
            message.what = 1;
            message.obj = new File(imagePath);
            handler.sendMessageDelayed(message,200);
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    startPhotoZoom(Uri.fromFile((File)msg.obj));
                    break;
            }
        }
    };

    @Override
    protected void bindEvent() {
        mBinding.btnBack.setOnClickListener(this);
        mBinding.btnReset.setOnClickListener(this);
        mBinding.btnRetry.setOnClickListener(this);
        mBinding.btnCopy.setOnClickListener(this);
        adapter.setOnItemClickListener(new TextAdapter.OnItemClickListener() {
            @Override
            public void OnCheckedChange(CompoundButton buttonView, boolean isChecked, int position) {
                mPresenter.setItemSelect(isChecked,position);
            }
        });

        new ClickUtil() {
            @Override
            public void method() {
                if (Applacation.getBaiduError() != 200){
                    mPresenter.onError(Applacation.getBaiduError());
                    return;
                }
                startImageSelect();
            }
        }.clickAntiShake(mBinding.btnSelect);

        new ClickUtil() {
            @Override
            public void method() {
                if (Applacation.getBaiduError() != 200){
                    mPresenter.onError(Applacation.getBaiduError());
                    return;
                }
                startCamera();
            }
        }.clickAntiShake(mBinding.btnPhoto);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_reset:
                resetView();
                break;
            case R.id.btn_retry:
                if(imageFile.exists()){
                    mPresenter.parseImg(imageFile);
                }else {
                    resetView();
                }
                break;
            case R.id.btn_copy:
                copyText();
                break;
        }
    }

    @Override
    public void startCamera() {
        photoFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), System.currentTimeMillis()+".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 下面这句指定调用相机拍照后的照片存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(intent, PICK_CAMERA);
    }

    @Override
    public void startImageSelect() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, CHOOSE_PICTURE);
    }

    @Override
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        //是否返回bitmap对象
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片的格式
        intent.putExtra("noFaceDetection", false); // 头像识别
        startActivityForResult(intent, CROP_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, resultCode + "");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData());
                    break;
                case CROP_PICTURE: // 取得裁剪后的图片
                    mBinding.ivImage.setImageBitmap(ImageLoadUtil.parseFile(imageFile.getAbsoluteFile()));
                    showLoading();
                    mPresenter.parseImg(imageFile);
                    break;
                case VALUE_PICK_PICTURE: // 如果是直接从相册获取
                    startPhotoZoom(data.getData());//拿到所选图片的Uri
                    break;
                case PICK_CAMERA:
                    showLoading();
                    startPhotoZoom(Uri.fromFile(photoFile));
                    break;
                default:

                    break;
            }
        }
    }

    @Override
    public void showToast(int textId) {
        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        loadDialog = DialogUtil.initLoadDialog(PhotoRecognitionActivity.this,R.string.photo_photo_loading);
        loadDialog.showAtLocation(mBinding.rlBar, Gravity.CENTER,0,0);
    }

    @Override
    public void hideLoading() {
        if (loadDialog != null)
            loadDialog.dismiss();
    }

    @Override
    public void setSelectView() {
        mBinding.llBtns.setVisibility(View.GONE);
        mBinding.rvText.setVisibility(View.VISIBLE);
        mBinding.ivIcon.setVisibility(View.GONE);
        mBinding.ivImage.setVisibility(View.VISIBLE);
        mBinding.rlRetry.setVisibility(View.GONE);
        mBinding.rlImage.setBackground(this.getDrawable(R.drawable.analysis_top_bg));
        mBinding.btnCopy.setVisibility(View.VISIBLE);
    }

    @Override
    public void resetView() {
        mPresenter.cleanData();
        mBinding.rvText.setVisibility(View.GONE);
        mBinding.llBtns.setVisibility(View.VISIBLE);
        mBinding.btnCopy.setVisibility(View.INVISIBLE);
        mBinding.ivImage.setVisibility(View.GONE);
        mBinding.ivIcon.setVisibility(View.VISIBLE);
        mBinding.rlRetry.setVisibility(View.GONE);
        mBinding.rlImage.setBackgroundColor(this.getResources().getColor(R.color.white));
        mBinding.btnCopy.setEnabled(false);
        if (imageFile.exists()){
            imageFile.delete();
        }
    }

    @Override
    public void onParseError() {
        mBinding.llBtns.setVisibility(View.GONE);
        mBinding.rvText.setVisibility(View.GONE);
        mBinding.ivIcon.setVisibility(View.GONE);
        mBinding.ivImage.setVisibility(View.VISIBLE);
        mBinding.rlRetry.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTextData(ArrayList<TextBean> data) {
        setSelectView();
        adapter.setData(data);
        hideLoading();
    }

    @Override
    public void setCopyBtnState(boolean state) {
        mBinding.btnCopy.setEnabled(state);
    }

    @Override
    public void copyText() {
        String text = mPresenter.getSelectText();
        Log.e("copyText",text);
        ClipboardManager mClipboardManager =(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label",text);
        mClipboardManager.setPrimaryClip(mClipData);
        Toast.makeText(this,R.string.photo_btn_copy_hint, Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoadUtil.close();
        if (imageFile.exists()){
            imageFile.delete();
        }
    }
}
