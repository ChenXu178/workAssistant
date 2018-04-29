package com.chenxu.workassistant.FileMenage;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.FileReader.ImageFileReaderActivity;
import com.chenxu.workassistant.FileReader.OfficeFileReaderActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Applacation;
import com.chenxu.workassistant.databinding.ActivityFileMenageBinding;
import com.chenxu.workassistant.utils.BackgroundUtil;
import com.chenxu.workassistant.utils.DialogUtil;
import com.chenxu.workassistant.utils.SizeUtils;
import com.chenxu.workassistant.utils.SnackBarUtils;
import com.chenxu.workassistant.utils.StatusBarUtil;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeItemLongClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;

import java.io.File;
import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class FileMenageActivity extends BaseActivity<ActivityFileMenageBinding> implements FileMenageContract.View,View.OnClickListener{

    public static final String OPEN_TYPE = "OPEN_TYPE:FILE_MENAGE";//打开方式 1首页打开 2收藏
    public static final String VIEW_ANIM = "VIEW_ANIM:FILE_MENAGE";


    private FileMenageContract.Presenter mPresenter;
    private FilesAdapter filesAdapter;
    private CatalogAdapter catalogAdapter;
    private LinearLayoutManager catalogManager; //列表布局
    private ScrollSpeedLinearLayoutManger fileManager; //列表布局
    private LayoutAnimationController fileInAnim,fileOutAnim,fileFadeAnim; //列表加载动画
    private PopupWindow dialogNewFile,dialogRename,dialogDelete,dialogLoading,dialogCollection,dialogDetail; //弹出窗口
    private boolean isFileSelect = false;  //是否选择文件模式
    private boolean isLoad = false;  //是否正在加载

    private DialogViewClickListener dialogViewClickListener; //弹窗点击事件


    @Override
    protected int getLayoutId() {
        return R.layout.activity_file_menage;
    }

    @Override
    protected void initView() {
        mPresenter = new FileMenagePresenter(this);
        StatusBarUtil.darkMode(this);

        fileManager = new ScrollSpeedLinearLayoutManger(this);
        fileManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvFiles.setLayoutManager(fileManager);
        addSideslipForFiles(this);//添加侧滑菜单
        filesAdapter = new FilesAdapter(new ArrayList<FileBean>(),this);
        mBinding.rvFiles.setAdapter(filesAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        decoration.setDrawable(this.getResources().getDrawable(R.drawable.file_divider));
        mBinding.rvFiles.addItemDecoration(decoration);

        catalogManager = new LinearLayoutManager(this);
        catalogManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.rvFileCatalog.setLayoutManager(catalogManager);
        catalogAdapter = new CatalogAdapter(new ArrayList<CatalogBean>());
        mBinding.rvFileCatalog.setAdapter(catalogAdapter);

        fileInAnim = AnimationUtils.loadLayoutAnimation(this,R.anim.layout_animation_file_in);
        fileOutAnim = AnimationUtils.loadLayoutAnimation(this,R.anim.layout_animation_file_out);
        fileFadeAnim = AnimationUtils.loadLayoutAnimation(this,R.anim.layout_animation_file_fade);

        dialogViewClickListener = new DialogViewClickListener();

        int openType = getIntent().getIntExtra(this.OPEN_TYPE,1);
        if (openType == 1){ //判断进入方式
            ViewCompat.setTransitionName(mBinding.tvBarTitle,this.VIEW_ANIM);
            new Handler().postDelayed(()->{ mPresenter.start(); },300);
        }
    }



    @Override
    protected void bindEvent(){
        mBinding.btnBack.setOnClickListener(this);
        mBinding.btnAdd.setOnClickListener(this);
        mBinding.rlRootCatalog.setOnClickListener(this);
        mBinding.tvCancelSelect.setOnClickListener(this);
        mBinding.llSelectAll.setOnClickListener(this);

        mBinding.llCopy.setOnClickListener(this);
        mBinding.llMove.setOnClickListener(this);
        mBinding.llDelete.setOnClickListener(this);
        mBinding.llRename.setOnClickListener(this);
        mBinding.llDetail.setOnClickListener(this);

        mBinding.btnInquiryConfirm.setOnClickListener(this);
        mBinding.btnInquiryCancel.setOnClickListener(this);

        catalogAdapter.setOnItemClickListener(new CatalogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isLoad)
                    return;
                mPresenter.backToPosition(position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:  //返回
                onBackPressed();
                break;
            case R.id.btn_add:  //新建文件
                if (isLoad)
                    return;
                showDialogNewFolder();
                break;
            case R.id.rl_root_catalog: //根目录
                if (isLoad)
                    return;
                mPresenter.toRootFiles();
                break;
            case R.id.tv_cancel_select:
                setFileSelectViewDisplay(false);
                break;
            case R.id.ll_select_all:
                if (mBinding.cbSelectAll.isChecked()){
                    mBinding.cbSelectAll.setChecked(false);
                    filesAdapter.setFileAllChecked(false);
                    mPresenter.allSelectFile(false);
                }else {
                    mBinding.cbSelectAll.setChecked(true);
                    filesAdapter.setFileAllChecked(true);
                    mPresenter.allSelectFile(true);
                }
                break;
            case R.id.ll_copy: //复制
                showSelectFolderBtn(true);
                break;
            case R.id.ll_move: //移动
                showSelectFolderBtn(false);
                break;
            case R.id.ll_delete: //删除
                showDeleteDialog(mPresenter.getSelectFileNumber());
                break;
            case R.id.ll_rename: // 重命名
                showRenameDialog(mPresenter.getSelectFileName());
                break;
            case R.id.ll_detail: // 详情
                mPresenter.getFileDetail();
                break;
            case R.id.btn_inquiry_confirm: // 选择目录确定
                showLoadingDialog();
                mPresenter.moveFiles();
                break;
            case R.id.btn_inquiry_cancel: // 选择目录取消
                setFileSelectViewDisplay(false);
                break;
        }
    }

    /**
     * 返回按钮
     */
    @Override
    public void onBackPressed() {
        if (isLoad){ //如果正在加载
            return;
        }
        if (isFileSelect){ //如果是选择文件状态
            setFileSelectViewDisplay(false);
            return;
        }
        if (mPresenter.onBack())
        super.onBackPressed();
    }

    /**
     * 加载文件列表
     * @param fileList
     */
    @Override
    public void loadFilesView(ArrayList<FileBean> fileList,int type) {
        filesAdapter.setFileBeans(fileList);
        switch (type){
            case 1: mBinding.rvFiles.setLayoutAnimation(fileInAnim); break;
            case 2: mBinding.rvFiles.setLayoutAnimation(fileOutAnim); break;
            case 3: mBinding.rvFiles.setLayoutAnimation(fileFadeAnim); break;
        }
        mBinding.rvFiles.startLayoutAnimation();
        if (mBinding.llInquiryBtn.getVisibility() == View.VISIBLE){ //如果是选择目录状态，就判断路径是否合法
            if (mPresenter.checkFilePathIsCorrect()){
                mBinding.btnInquiryConfirm.setEnabled(true);
            }else {
                mBinding.btnInquiryConfirm.setEnabled(false);
            }
        }
    }



    /**
     * 添加一个目录
     * @param catalogBean
     * @param listSize
     */
    @Override
    public void addCatalog(CatalogBean catalogBean,int listSize) {
        catalogAdapter.addItem(catalogBean);
        mBinding.rvFileCatalog.smoothScrollToPosition(listSize - 1);
        mBinding.rvFiles.scrollToPosition(0);
    }

    /**
     * 删除目录
     */
    @Override
    public void removeCatalog() {
        catalogAdapter.removeItem();
    }

    /**
     * 清除目录（退回首页）
     */
    @Override
    public void clearCatalog() {
        catalogAdapter.cleaItem();
    }

    /**
     * 清除目录到指定位置
     * @param position
     */
    @Override
    public void removeCatalogToPosition(int position) {
        catalogAdapter.removeItemToPosition(position);
    }


    /**
     * 显示新建文件夹
     */
    @Override
    public void showDialogNewFolder() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_file_new,null);
        EditText etName = (EditText)view.findViewById(R.id.et_new_name);
        etName.setLongClickable(false);
        BackgroundUtil.setBackgroundAlpha(0.5f,this);
        dialogNewFile = DialogUtil.initDialog(view);
        dialogNewFile.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                BackgroundUtil.setBackgroundAlpha(1f,FileMenageActivity.this);
            }
        });
        ImageView ivCancel = (ImageView) view.findViewById(R.id.iv_new_close);
        Button btnConfirm = (Button) view.findViewById(R.id.btn_new_confirm);
        ivCancel.setOnClickListener(dialogViewClickListener);
        btnConfirm.setOnClickListener(dialogViewClickListener);
        dialogNewFile.showAtLocation(mBinding.rvFiles, Gravity.CENTER,0,0);
    }

    /**
     * 打开office文件
     * @param filePath 路径
     * @param view 视图
     */
    @Override
    public void openOfficeFile(String filePath,View view) {
        Intent intent = new Intent(this, OfficeFileReaderActivity.class);
        intent.putExtra(OfficeFileReaderActivity.FILE_ID,filePath);
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(view, OfficeFileReaderActivity.VIEW_DETAIL));
        ActivityCompat.startActivity(this, intent, compat.toBundle());
    }

    /**
     * 打开图片
     * @param filePath 图片路径
     * @param view 选中的view
     */
    @Override
    public void openImageFile(String filePath, View view) {
        Intent intent = new Intent(this, ImageFileReaderActivity.class);
        intent.putExtra(ImageFileReaderActivity.FILE_ID,filePath);
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(view.findViewById(R.id.iv_file_type), ImageFileReaderActivity.VIEW_DETAIL));
        ActivityCompat.startActivity(this, intent, compat.toBundle());
    }

    /**
     * 是否显示文件加载界面
     * @param display
     */
    @Override
    public void setLoadViewDisplay(boolean display) {
        this.isLoad = display;
        if (display){
            mBinding.rlLoading.setVisibility(View.VISIBLE);
        }else {
            mBinding.rlLoading.setVisibility(View.GONE);
        }
    }

    /**
     * 是否显示多选文件操作界面（初始化）
     * @param display
     */
    @Override
    public void setFileSelectViewDisplay(boolean display) {
        if (display){
            filesAdapter.showCheckbox();
            mBinding.btnBack.setVisibility(View.GONE);
            mBinding.btnAdd.setVisibility(View.GONE);
            mBinding.tvBarTitle.setVisibility(View.GONE);

            mBinding.tvCancelSelect.setVisibility(View.VISIBLE);
            mBinding.llSelectAll.setVisibility(View.VISIBLE);
            mBinding.tvBarFileNumber.setVisibility(View.VISIBLE);
            mBinding.llBottom.setVisibility(View.VISIBLE);
            setSelectFileNumber(mPresenter.getSelectFileNumber());

            filesAdapter.showCheckbox();

            mBinding.cbSelectAll.setChecked(false);

            isFileSelect = true;
        }else {
            filesAdapter.hideCheckbox();
            mBinding.btnBack.setVisibility(View.VISIBLE);
            mBinding.btnAdd.setVisibility(View.VISIBLE);
            mBinding.tvBarTitle.setVisibility(View.VISIBLE);

            mBinding.tvCancelSelect.setVisibility(View.GONE);
            mBinding.llSelectAll.setVisibility(View.GONE);
            mBinding.tvBarFileNumber.setVisibility(View.GONE);
            mBinding.llBottom.setVisibility(View.GONE);

            mBinding.llInquiryBtn.setVisibility(View.GONE);
            mBinding.llBtns.setVisibility(View.VISIBLE);

            filesAdapter.hideCheckbox();
            mPresenter.allSelectFile(false);
            isFileSelect = false;
        }
    }

    /**
     * 设置选择文件数量，更新操作按钮
     * @param number
     */
    @Override
    public void setSelectFileNumber(int number) {
        mBinding.tvBarFileNumber.setText(this.getResources().getString(R.string.file_bar_btn_select_number1)+number+this.getResources().getString(R.string.file_bar_btn_select_number2));
        checkFileBtns(number);
    }

    /**
     * 启用所有操作按钮
     */
    @Override
    public void enableFileOperationBtn() {
        mBinding.llCopy.setEnabled(true);
        mBinding.llMove.setEnabled(true);
        mBinding.llRename.setEnabled(true);
        mBinding.llDelete.setEnabled(true);
        mBinding.llDetail.setEnabled(true);

        mBinding.ivCopy.setSelected(true);
        mBinding.ivMove.setSelected(true);
        mBinding.ivRename.setSelected(true);
        mBinding.ivDelete.setSelected(true);
        mBinding.ivDetail.setSelected(true);

        mBinding.tvCopy.setSelected(true);
        mBinding.tvMove.setSelected(true);
        mBinding.tvRename.setSelected(true);
        mBinding.tvDelete.setSelected(true);
        mBinding.tvDetail.setSelected(true);
    }

    /**
     * 禁用所有操作按钮
     */
    @Override
    public void disableFileOperationBtn() {
        mBinding.llCopy.setEnabled(false);
        mBinding.llMove.setEnabled(false);
        mBinding.llRename.setEnabled(false);
        mBinding.llDelete.setEnabled(false);
        mBinding.llDetail.setEnabled(false);

        mBinding.ivCopy.setSelected(false);
        mBinding.ivMove.setSelected(false);
        mBinding.ivRename.setSelected(false);
        mBinding.ivDelete.setSelected(false);
        mBinding.ivDetail.setSelected(false);

        mBinding.tvCopy.setSelected(false);
        mBinding.tvMove.setSelected(false);
        mBinding.tvRename.setSelected(false);
        mBinding.tvDelete.setSelected(false);
        mBinding.tvDetail.setSelected(false);
    }

    /**
     * 禁用重命名按钮
     */
    @Override
    public void disableFileRenameBtn() {
        mBinding.llCopy.setEnabled(true);
        mBinding.llMove.setEnabled(true);
        mBinding.llRename.setEnabled(false);
        mBinding.llDelete.setEnabled(true);
        mBinding.llDetail.setEnabled(false);

        mBinding.ivCopy.setSelected(true);
        mBinding.ivMove.setSelected(true);
        mBinding.ivRename.setSelected(false);
        mBinding.ivDelete.setSelected(true);
        mBinding.ivDetail.setSelected(false);

        mBinding.tvCopy.setSelected(true);
        mBinding.tvMove.setSelected(true);
        mBinding.tvRename.setSelected(false);
        mBinding.tvDelete.setSelected(true);
        mBinding.tvDetail.setSelected(false);
    }

    /**
     * 检查文件选择数量，显示相应的按钮
     * @param number
     */
    @Override
    public void checkFileBtns(int number) {
        if (number == 0){
            disableFileOperationBtn();
        }else if (number == 1){
            enableFileOperationBtn();
        }else {
            disableFileRenameBtn();
        }
    }

    /**
     * 显示删除菜单
     * @param number
     */
    @Override
    public void showDeleteDialog(int number) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_file_delete,null);
        RelativeLayout rlDeleteConfirm = (RelativeLayout)view.findViewById(R.id.rl_delete_confirm);
        RelativeLayout rlDeleteCancel = (RelativeLayout)view.findViewById(R.id.rl_delete_cancel);
        rlDeleteConfirm.setOnClickListener(dialogViewClickListener);
        rlDeleteCancel.setOnClickListener(dialogViewClickListener);
        TextView tvDeleteNumber = view.findViewById(R.id.tv_delete_number);
        tvDeleteNumber.setText(this.getResources().getString(R.string.file_menage_delete_text1)+number+this.getResources().getString(R.string.file_menage_delete_text2));
        dialogDelete = new PopupWindow(view);
        dialogDelete.setWidth(MATCH_PARENT);
        dialogDelete.setHeight(WRAP_CONTENT);
        dialogDelete.setOutsideTouchable(true);
        dialogDelete.setFocusable(true);
        dialogDelete.setTouchable(true);
        dialogDelete.setBackgroundDrawable(getDrawable(R.color.white));
        dialogDelete.setAnimationStyle(R.style.deleteDialogAnim);
        BackgroundUtil.setBackgroundAlpha(0.5f,this);
        dialogDelete.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                BackgroundUtil.setBackgroundAlpha(1f,FileMenageActivity.this);
            }
        });
        dialogDelete.showAtLocation(mBinding.rvFiles,Gravity.BOTTOM,0,0);
    }

    @Override
    public void showRenameDialog(String fileName) {
        if (fileName == null){
            Toast.makeText(this,R.string.file_select_err,Toast.LENGTH_SHORT).show();
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_file_rename,null);
        EditText etName = (EditText)view.findViewById(R.id.et_rename_name);
        Button btnConfirm = (Button)view.findViewById(R.id.btn_rename_confirm);
        ImageView ivCancel = (ImageView)view.findViewById(R.id.iv_rename_close);
        etName.setText(fileName);
        etName.setLongClickable(false);
        btnConfirm.setOnClickListener(dialogViewClickListener);
        ivCancel.setOnClickListener(dialogViewClickListener);
        BackgroundUtil.setBackgroundAlpha(0.5f,this);
        dialogRename = DialogUtil.initDialog(view);
        dialogRename.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                BackgroundUtil.setBackgroundAlpha(1f,FileMenageActivity.this);
            }
        });
        dialogRename.showAtLocation(mBinding.rvFiles,Gravity.CENTER,0,0);
    }

    /**
     * 显示加载框
     */
    @Override
    public void showLoadingDialog() {
        dialogLoading = DialogUtil.initLoadDialog(this);
        dialogLoading.showAtLocation(mBinding.rvFiles,Gravity.CENTER,0,0);
    }

    /**
     * 隐藏加载框
     */
    @Override
    public void hideLoadingDialog() {
        dialogLoading.dismiss();
    }

    /**
     * 删除文件
     * @param fileBean
     */
    @Override
    public void deleteFileItem(FileBean fileBean) {
        filesAdapter.deleteFile(fileBean);
    }

    /**
     * 显示目录选择按钮
     * @param isRetainOldFile 是否保留原文件
     */
    @Override
    public void showSelectFolderBtn(boolean isRetainOldFile) {
        mPresenter.setRetainOldFile(isRetainOldFile);
        if (isRetainOldFile){
            mBinding.btnInquiryConfirm.setText(R.string.file_menage_inquiry_copy);
        }else {
            mBinding.btnInquiryConfirm.setText(R.string.file_menage_inquiry_move);
        }
        mBinding.llBtns.setVisibility(View.GONE);
        mBinding.llInquiryBtn.setVisibility(View.VISIBLE);

        filesAdapter.hideCheckbox();
        mBinding.btnBack.setVisibility(View.VISIBLE);
        mBinding.btnAdd.setVisibility(View.VISIBLE);
        mBinding.tvBarTitle.setVisibility(View.VISIBLE);

        mBinding.tvCancelSelect.setVisibility(View.GONE);
        mBinding.llSelectAll.setVisibility(View.GONE);
        mBinding.tvBarFileNumber.setVisibility(View.GONE);

        isFileSelect = false;
    }

    /**
     * 添加多个文件到列表
     * @param files 文件
     */
    @Override
    public void addFiles(ArrayList<FileBean> files) {
        filesAdapter.addFiles(files);
    }

    @Override
    public void showToast(int string) {
        Toast.makeText(this,string,Toast.LENGTH_SHORT).show();
    }

    /**
     * 重命名更新文件
     * @param index
     * @param newFile
     */
    @Override
    public void updateFile(int index, File newFile) {
        filesAdapter.updateFile(index,newFile);
        setFileSelectViewDisplay(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogRename.dismiss();
            }
        }, 300);
    }

    /**
     * 添加一项
     * @param fileBean
     */
    @Override
    public void addFile(FileBean fileBean) {
        filesAdapter.addFile(fileBean);
        dialogNewFile.dismiss();
    }

    @Override
    public void addSideslipForFiles(Context context) {
        mBinding.rvFiles.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {

                SwipeMenuItem enclosureItem = new SwipeMenuItem(context);
                // 各种文字和图标属性设置。
                enclosureItem.setHeight(MATCH_PARENT);
                enclosureItem.setWidth(SizeUtils.dp2px(Applacation.getInstance(),80));
                enclosureItem.setText(R.string.file_right_enclosure_item_name);
                enclosureItem.setTextSize(16);
                enclosureItem.setBackgroundColor(context.getResources().getColor(R.color.fileRightItemEnclosureBG));
                enclosureItem.setTextColor(context.getResources().getColor(R.color.white));
                swipeRightMenu.addMenuItem(enclosureItem);

                SwipeMenuItem collectionItem = new SwipeMenuItem(Applacation.getInstance());
                // 各种文字和图标属性设置。
                collectionItem.setHeight(MATCH_PARENT);
                collectionItem.setWidth(SizeUtils.dp2px(Applacation.getInstance(),80));
                collectionItem.setText(R.string.file_right_collection_item_name);
                collectionItem.setTextSize(16);
                collectionItem.setBackgroundColor(context.getResources().getColor(R.color.fileRightItemCollectionBG));
                collectionItem.setTextColor(context.getResources().getColor(R.color.white));
                swipeRightMenu.addMenuItem(collectionItem); // 在Item右侧添加一个菜单。
            }
        });

        mBinding.rvFiles.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                menuBridge.closeMenu();
                int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
                int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
                mPresenter.swipeMenuItemClick(adapterPosition,menuPosition);
            }
        });


        mBinding.rvFiles.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (isFileSelect){
                    if (mPresenter.getFileSelectState(position)){
                        //已经是选中
                        mPresenter.delSelectedFile(position);
                        filesAdapter.setFileChecked(false,position);
                    }else {
                        //未选中
                        mPresenter.addSelectedFile(position);
                        filesAdapter.setFileChecked(true,position);
                    }
                }else {
                    mPresenter.onFileItemClick(position,fileManager.findFirstVisibleItemPosition(),itemView);
                }
            }
        });

        mBinding.rvFiles.setSwipeItemLongClickListener(new SwipeItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                if (!isFileSelect){
                    setFileSelectViewDisplay(true);
                    //
                    mPresenter.addSelectedFile(position);
                    filesAdapter.setFileChecked(true,position);
                }
            }
        });
    }

    @Override
    public void showSnackBar(int msg, int snackBarMSGColor, int snackBarBGColor) {
        SnackBarUtils.showSnackBarMSG(mBinding.rlBar,msg,snackBarMSGColor,snackBarBGColor);
    }

    /**
     * 显示收藏成功窗口
     */
    @Override
    public void showCollectionDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_file_collection,null);
        dialogCollection = DialogUtil.initDialog(view);
        BackgroundUtil.setBackgroundAlpha(0.7f,this);
        dialogCollection.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                BackgroundUtil.setBackgroundAlpha(1f,FileMenageActivity.this);
            }
        });
        dialogCollection.showAtLocation(mBinding.rlBar,Gravity.CENTER,0,0);
        new Handler().postDelayed(()->{
            dialogCollection.dismiss();
        },1500);
    }

    /**
     * 显示文件详情窗口
     * @param type 文件类型 1文件 2文件夹
     * @param name 文件名
     * @param time 修改时间
     * @param size 大小 目录时不显示
     * @param path 路径
     */
    @Override
    public void showDetailDialog(int type, String name,String time, String size, String path) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_file_detail,null);
        ImageView ivClose = view.findViewById(R.id.iv_detail_close);
        TextView tvName = view.findViewById(R.id.tv_detail_name);
        TextView tvTime = view.findViewById(R.id.tv_detail_time);
        TextView tvSize = view.findViewById(R.id.tv_detail_size);
        TextView tvPath = view.findViewById(R.id.tv_detail_path);
        if (type == 1){
            LinearLayout llNumber = view.findViewById(R.id.ll_detail_number);
            llNumber.setVisibility(View.GONE);
        }if (type == 2){
            LinearLayout llSize = view.findViewById(R.id.ll_detail_size);
            llSize.setVisibility(View.GONE);
        }
        tvName.setText(name);
        tvTime.setText(time);
        tvSize.setText(size);
        tvPath.setText(path);
        Button btnConfirm = view.findViewById(R.id.btn_detail_confirm);
        btnConfirm.setOnClickListener(dialogViewClickListener);
        ivClose.setOnClickListener(dialogViewClickListener);
        dialogDetail = DialogUtil.initDialog(view);
        dialogDetail.setOnDismissListener(()->{
            BackgroundUtil.setBackgroundAlpha(1f,FileMenageActivity.this);
            mPresenter.setRunFileNumber(false);
        });
        BackgroundUtil.setBackgroundAlpha(0.5f,this);
        dialogDetail.showAtLocation(mBinding.rlBar,Gravity.CENTER,0,0);
    }

    @Override
    public void setDialogDetailFileNumber(int fileNumber, int folderNumber) {
        if (dialogDetail!=null && dialogDetail.isShowing()){
            TextView tvNumber = dialogDetail.getContentView().findViewById(R.id.tv_detail_number);
            tvNumber.setText(fileNumber+this.getResources().getString(R.string.dialog_file_detail_number_text1)+folderNumber+this.getResources().getString(R.string.dialog_file_detail_number_text2));
        }
    }

    /**
     * 弹出窗口点击事件
     */
    class DialogViewClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_new_close:
                    dialogNewFile.dismiss();
                    break;
                case R.id.btn_new_confirm:
                    EditText etName = dialogNewFile.getContentView().findViewById(R.id.et_new_name);
                    mPresenter.addFolder(etName.getText().toString().trim());
                    break;
                case R.id.rl_delete_confirm:
                    dialogDelete.dismiss();
                    mPresenter.deleteSelectFile();
                    break;
                case R.id.rl_delete_cancel:
                    dialogDelete.dismiss();
                    break;
                case R.id.iv_rename_close:
                    dialogRename.dismiss();
                    break;
                case R.id.btn_rename_confirm:
                    EditText etFileName = (EditText)dialogRename.getContentView().findViewById(R.id.et_rename_name);
                    String fileName = etFileName.getText().toString().trim();
                    mPresenter.renameSelectFile(fileName);
                    break;
                case R.id.btn_detail_confirm:
                    dialogDetail.dismiss();
                    break;
                case R.id.iv_detail_close:
                    dialogDetail.dismiss();
                    break;
            }
        }
    }

    /**
     * 移动文件列表
     * @param index
     */
    @Override
    public void moveToPosition(int index) {
        fileManager.scrollToPositionWithOffset(index,0);
        mPresenter.setShowIndex(0);
    }

    /**
     * 平滑移动
     * @param index
     */
    @Override
    public void smoothMoveToPosition(int index) {
        mBinding.rvFiles.smoothScrollToPosition(index);
        mPresenter.setShowIndex(0);
    }


}
