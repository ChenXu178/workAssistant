package com.chenxu.workassistant.collection;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Application;
import com.chenxu.workassistant.databinding.ActivityCollectionBinding;
import com.chenxu.workassistant.fileMenage.FileMenageActivity;
import com.chenxu.workassistant.fileReader.ImageFileReaderActivity;
import com.chenxu.workassistant.fileReader.OfficeFileReaderActivity;
import com.chenxu.workassistant.utils.SizeUtils;
import com.chenxu.workassistant.utils.StatusBarUtil;
import com.chenxu.workassistant.utils.Utils;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;

import java.io.File;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class CollectionActivity extends BaseActivity<ActivityCollectionBinding> implements CollectionContract.View,View.OnClickListener{

    private CollectionContract.Presenter mPresenter;
    public static final String VIEW_ANIM = "COLLECTION:ANIM";
    private CollectionAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collection;
    }

    @Override
    protected void initView() {
        ViewCompat.setTransitionName(mBinding.tvBarTitle,VIEW_ANIM);
        StatusBarUtil.darkMode(this);
        this.mPresenter = new CollectionPresenter(this,this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        decoration.setDrawable(this.getResources().getDrawable(R.drawable.file_divider));
        mBinding.smrvMain.addItemDecoration(decoration);
        mBinding.smrvMain.setLayoutManager(new LinearLayoutManager(this));

        mBinding.smrvMain.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                SwipeMenuItem listItem = new SwipeMenuItem(Application.getInstance());
                // 各种文字和图标属性设置。
                listItem.setHeight(MATCH_PARENT);
                listItem.setWidth(SizeUtils.dp2px(Application.getInstance(),80));
                listItem.setText(R.string.file_right_list_item_name);
                listItem.setTextSize(16);
                listItem.setBackgroundColor(Application.getInstance().getResources().getColor(R.color.fileRightItemListBG));
                listItem.setTextColor(Application.getInstance().getResources().getColor(R.color.white));
                swipeRightMenu.addMenuItem(listItem);

                SwipeMenuItem collectionItem = new SwipeMenuItem(Application.getInstance());
                // 各种文字和图标属性设置。
                collectionItem.setHeight(MATCH_PARENT);
                collectionItem.setWidth(SizeUtils.dp2px(Application.getInstance(),90));
                collectionItem.setText(R.string.item_collection_btn);
                collectionItem.setTextSize(16);
                collectionItem.setBackgroundColor(CollectionActivity.this.getResources().getColor(R.color.CollectionRightItemCancelBG));
                collectionItem.setTextColor(CollectionActivity.this.getResources().getColor(R.color.white));
                swipeRightMenu.addMenuItem(collectionItem); // 在Item右侧添加一个菜单。
            }
        });

        mBinding.smrvMain.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                menuBridge.closeMenu();
                int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
                int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
                mPresenter.swipeMenuItemClick(adapterPosition,menuPosition);
            }
        });

        mBinding.smrvMain.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (Utils.checkClickTime()){
                    return;
                }
                mPresenter.openFile(position,itemView);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.start();
            }
        },500);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void bindEvent() {
        mBinding.btnBack.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void initData(List<FileBean> list) {
        adapter = new CollectionAdapter(list,this);
        mBinding.smrvMain.setAdapter(adapter);
    }

    @Override
    public void deleteByPosition(int position) {
        adapter.deleteByPosition(position);
    }

    @Override
    public void openImage(File file,View view) {
        Intent intent = new Intent(this, ImageFileReaderActivity.class);
        intent.putExtra(ImageFileReaderActivity.FILE_ID,file.getPath());
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(view.findViewById(R.id.iv_collection_icon), ImageFileReaderActivity.VIEW_DETAIL));
        ActivityCompat.startActivity(this, intent, compat.toBundle());
    }

    @Override
    public void openOffice(File file, View view) {
        Intent intent = new Intent(this, OfficeFileReaderActivity.class);
        intent.putExtra(OfficeFileReaderActivity.FILE_ID,file.getPath());
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(view, OfficeFileReaderActivity.VIEW_DETAIL));
        ActivityCompat.startActivity(this, intent, compat.toBundle());
    }

    @Override
    public void openFolder(File file) {
        Intent intent = new Intent(this, FileMenageActivity.class);
        intent.putExtra(FileMenageActivity.OPEN_TYPE,2);
        intent.putExtra(FileMenageActivity.FILE_PATH,file.getPath());
        startActivity(intent);
    }

    @Override
    public void openPath(File file) {
        Intent intent = new Intent(this, FileMenageActivity.class);
        intent.putExtra(FileMenageActivity.OPEN_TYPE,3);
        intent.putExtra(FileMenageActivity.FILE_PATH,file.getParent());
        intent.putExtra(FileMenageActivity.FILE_INDEX,0);
        startActivity(intent);
    }
}
