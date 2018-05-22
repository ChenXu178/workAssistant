package com.chenxu.workassistant.fileSearch;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Applacation;
import com.chenxu.workassistant.dao.SearchEntity;
import com.chenxu.workassistant.databinding.ActivityFileSearchBinding;
import com.chenxu.workassistant.fileMenage.FileMenageActivity;
import com.chenxu.workassistant.fileReader.ImageFileReaderActivity;
import com.chenxu.workassistant.fileReader.OfficeFileReaderActivity;
import com.chenxu.workassistant.utils.BackgroundUtil;
import com.chenxu.workassistant.utils.DialogUtil;
import com.chenxu.workassistant.utils.SizeUtils;
import com.chenxu.workassistant.utils.SnackBarUtils;
import com.chenxu.workassistant.utils.StatusBarUtil;
import com.chenxu.workassistant.utils.Utils;
import com.chenxu.workassistant.widget.KeyboardChangeListener;
import com.library.flowlayout.FlowLayoutManager;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeItemLongClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class FileSearchActivity extends BaseActivity<ActivityFileSearchBinding> implements FileSearchContract.View,View.OnClickListener{

    private FileSearchContract.Presenter mPresenter;
    private HistoryAdapter historyAdapter;
    private SearchAdapter searchAdapter;
    private PopupWindow searchDialog,dialogCollection,dialogEnclosure;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_file_search;
    }

    @Override
    protected void initView() {
        StatusBarUtil.darkMode(this);
        mPresenter = new FileSearchPresenter(this,this);

        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        mBinding.rvSearchHistory.setLayoutManager(flowLayoutManager);
        historyAdapter = new HistoryAdapter(new ArrayList<>(),this);
        mBinding.rvSearchHistory.setAdapter(historyAdapter);
        addSidesLipForFiles(this);
        mBinding.smrvFiles.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchAdapter(new ArrayList<>(),this);
        mBinding.smrvFiles.setAdapter(searchAdapter);

        Glide.with(this).load(R.drawable.search_no_file).into(mBinding.ivNoFile);
    }

    @Override
    protected void bindEvent() {
        mBinding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                //以下方法防止两次发送请求
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_UP:
                            //发送请求
                            Utils.closeKeyboard(FileSearchActivity.this);
                            mPresenter.onFileSearch(mBinding.etSearch.getText().toString().trim());
                            return true;
                        default:
                            return true;
                    }
                }
                return false;
            }
        });

        mBinding.ivSearchClose.setOnClickListener(this::onClick);

        mBinding.llSearchDoc.setOnClickListener(this::onClick);
        mBinding.llSearchXls.setOnClickListener(this::onClick);
        mBinding.llSearchPpt.setOnClickListener(this::onClick);

        rootView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(FileSearchActivity.this.getCurrentFocus() != null){
                    Log.e("hide","hideSearchHistory");
                 hideSearchHistory();
                 Utils.closeKeyboard(FileSearchActivity.this);
                 if (searchAdapter.getItemCount() > 0){
                     hideSearchDetail();
                 }
                }
                return false;
            }
        });

        new KeyboardChangeListener(this).setKeyBoardListener(new KeyboardChangeListener.KeyBoardListener() {
            @Override
            public void onKeyboardChange(boolean isShow, int keyboardHeight) {
                if (isShow && mBinding.etSearch.isFocused()){
                    mPresenter.loadSearchHistory();
                }
            }
        });

        mBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ("".equals(mBinding.etSearch.getText().toString().trim())){
                    hideSearchClose();
                    mPresenter.loadSearchHistory();
                }else {
                    showSearchClose();
                    hideSearchHistory();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        historyAdapter.setOnClickListener(new HistoryAdapter.OnClickListener() {
            @Override
            public void onClick(String text) {
                Utils.closeKeyboard(FileSearchActivity.this);
                hideSearchDetail();
                mBinding.etSearch.setText(text);
                mBinding.etSearch.setSelection(text.length());
                mPresenter.onFileSearch(text);
            }
        });
    }

    @Override
    public void setSearchResultData(List<SearchBean> list) {
        searchAdapter.setList(list);
        hideSearchDetail();
    }

    @Override
    public void showSearchHistory(List<SearchEntity> list) {
        mBinding.rvSearchHistory.setVisibility(View.VISIBLE);
        mBinding.flSearchDetail.setVisibility(View.VISIBLE);
        mBinding.smrvFiles.setVisibility(View.VISIBLE);
        mBinding.llNoFile.setVisibility(View.GONE);
        historyAdapter.setList(list);
    }

    @Override
    public void hideSearchHistory() {
        mBinding.rvSearchHistory.setVisibility(View.GONE);
    }

    @Override
    public void hideSearchDetail() {
        mBinding.flSearchDetail.setVisibility(View.GONE);
    }

    @Override
    public void showSearchDetail() {
        mBinding.flSearchDetail.setVisibility(View.VISIBLE);
        mBinding.smrvFiles.setVisibility(View.VISIBLE);
        mBinding.llNoFile.setVisibility(View.GONE);
    }

    @Override
    public void showSearchClose() {
        mBinding.ivSearchClose.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSearchClose() {
        mBinding.ivSearchClose.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showLoading() {
        searchDialog = DialogUtil.initLoadDialog(this,R.string.file_search_loading);
        searchDialog.showAtLocation(mBinding.etSearch, Gravity.CENTER,0,0);
    }

    @Override
    public void hideLoading() {
        if (searchDialog != null && searchDialog.isShowing()){
            searchDialog.dismiss();
        }
    }

    @Override
    public void showSnackBar(int msg, int snackBarMSGColor, int snackBarBGColor) {
        SnackBarUtils.showSnackBarMSG(mBinding.etSearch,msg,snackBarMSGColor,snackBarBGColor);
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
                BackgroundUtil.setBackgroundAlpha(1f,FileSearchActivity.this);
            }
        });
        dialogCollection.showAtLocation(mBinding.etSearch,Gravity.CENTER,0,0);
        new Handler().postDelayed(()->{
            dialogCollection.dismiss();
        },1500);
    }

    /**
     * 显示添加附件成功
     */
    @Override
    public void showEnclosureDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_file_enclosure,null);
        dialogEnclosure = DialogUtil.initDialog(view);
        BackgroundUtil.setBackgroundAlpha(0.7f,this);
        dialogEnclosure.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                BackgroundUtil.setBackgroundAlpha(1f,FileSearchActivity.this);
            }
        });
        dialogEnclosure.showAtLocation(mBinding.etSearch,Gravity.CENTER,0,0);
        new Handler().postDelayed(()->{
            dialogEnclosure.dismiss();
        },1500);
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

    @Override
    public void noSearchFile() {
        mBinding.smrvFiles.setVisibility(View.GONE);
        mBinding.llNoFile.setVisibility(View.VISIBLE);
    }

    @Override
    public void openPath(SearchBean searchBean) {
        Intent intent = new Intent(this, FileMenageActivity.class);
        intent.putExtra(FileMenageActivity.OPEN_TYPE,3);
        intent.putExtra(FileMenageActivity.FILE_PATH,searchBean.getFile().getParent());
        intent.putExtra(FileMenageActivity.FILE_INDEX,searchBean.getIndex());
        startActivity(intent);
    }


    public void addSidesLipForFiles(Context context) {
        mBinding.smrvFiles.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {

                SwipeMenuItem listItem = new SwipeMenuItem(context);
                // 各种文字和图标属性设置。
                listItem.setHeight(MATCH_PARENT);
                listItem.setWidth(SizeUtils.dp2px(Applacation.getInstance(),80));
                listItem.setText(R.string.file_right_list_item_name);
                listItem.setTextSize(16);
                listItem.setBackgroundColor(context.getResources().getColor(R.color.fileRightItemListBG));
                listItem.setTextColor(context.getResources().getColor(R.color.white));
                swipeRightMenu.addMenuItem(listItem);

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

        mBinding.smrvFiles.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                menuBridge.closeMenu();
                int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
                int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
                mPresenter.swipeMenuItemClick(adapterPosition,menuPosition);
            }
        });


        mBinding.smrvFiles.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                mPresenter.onFileItemClick(position,itemView);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_search_close:
                mBinding.etSearch.setText("");
                showSearchDetail();
                searchAdapter.setList(new ArrayList<>());
                break;
            case R.id.ll_search_doc:
                mPresenter.fastSearch(1);
                break;
            case R.id.ll_search_xls:
                mPresenter.fastSearch(2);
                break;
            case R.id.ll_search_ppt:
                mPresenter.fastSearch(3);
                break;
        }
    }
}
