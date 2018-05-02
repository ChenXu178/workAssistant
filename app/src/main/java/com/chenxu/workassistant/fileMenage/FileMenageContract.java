package com.chenxu.workassistant.fileMenage;

import android.content.Context;

import com.chenxu.workassistant.BasePresenter;
import com.chenxu.workassistant.BaseView;
import com.chenxu.workassistant.dao.CollectionEntity;
import com.chenxu.workassistant.dao.EnclosureEntity;
import com.chenxu.workassistant.dao.HistoryEntity;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by Android on 2018/3/26.
 */

public interface FileMenageContract {

    interface View extends BaseView{

        void loadFilesView(ArrayList<FileBean> fileList,int type);  //载入文件列表type:1进入 2返回 3跳转

        void addCatalog(CatalogBean catalogBean,int listSize); //设置文件路径

        void removeCatalog();

        void moveToPosition(int index);  //滚动文件列表到指定位置

        void smoothMoveToPosition(int index);  //平滑滚动文件列表到指定位置

        void clearCatalog();  //清除文件目录

        void removeCatalogToPosition(int position); //删除目录到指定位置

        void showDialogNewFolder(); //显示新建文件夹弹窗

        void openOfficeFile(String filePath, android.view.View view); //打开office文件

        void openImageFile(String filePath, android.view.View view); //打开image文件

        void setLoadViewDisplay(boolean display); //设置加载视图显示

        void setFileSelectViewDisplay(boolean display); //设置选择文件界面是否显示

        void setSelectFileNumber(int number); //设置已选择文件数量

        void enableFileOperationBtn();  //启用所有文件操作按钮

        void disableFileOperationBtn(); //禁用文件操作按钮

        void disableFileRenameBtn(); //禁用重命名按钮

        void checkFileBtns(int number); //检查按钮状态

        void showDeleteDialog(int number);  //显示删除弹窗

        void showRenameDialog(String fileName);  //显示重命名弹窗

        void showLoadingDialog();  //显示加载弹窗

        void hideLoadingDialog();  //隐藏加载弹窗

        void deleteFileItem(FileBean fileBean); //从列表删除一项

        void showSelectFolderBtn(boolean isRetainOldFile); //显示选择目录界面（复制、移动）

        void addFiles(ArrayList<FileBean> files); //添加多个文件到列表

        void showToast(int string); //显示toast

        void updateFile(int index,File newFile); //更新一项文件

        void addFile(FileBean fileBean); //添加一项文件

        void addSideslipForFiles(Context context);

        void showSnackBar(int msg, int snackBarMSGColor, int snackBarBGColor);

        void showCollectionDialog(); //显示收藏成功窗口

        void showEnclosureDialog(); //显示收藏成功窗口

        void showDetailDialog(int type,String name,String time,String size,String path); //显示文件详情窗口 type 1文件 2文件夹

        void setDialogDetailFileNumber(int fileNumber,int folderNumber); //设置详情窗口文件数量
    }

    interface Presenter extends BasePresenter{

        void loadFiles(File file,int type);  //加载文件列表 type:1进入 2返回 3跳转

        void onFileItemClick(int position, int showIndex, android.view.View view); //点击一项，进入下一层

        boolean onBack(); //返回上一层

        void setShowIndex(int index); //设置显示文件列表位置

        void toRootFiles();  //回到首页

        void backToPosition(int position);

        boolean getFileSelectState(int position);

        void addSelectedFile(int position); //添加选择文件

        void delSelectedFile(int position); //删除选择文件

        void allSelectFile(boolean isAll); //是否全选

        int getSelectFileNumber(); //获得选择文件数量

        String getSelectFileName(); //获得选中文件名

        void deleteSelectFile(); //删除选择的文件

        void renameSelectFile(String newFileName); //删除选择的文件

        void setRetainOldFile(boolean retainOldFile); //设置状态

        boolean checkFilePathIsCorrect();

        void moveFiles(); //移动文件

        void addFolder(String name); //新建文件夹

        void swipeMenuItemClick(int adapterPosition,int menuPosition);//列表侧滑菜单点击

        void getFileDetail(); //获得文件详情

        void setRunFileNumber(boolean isRun);

        void insertHistory(File file);//添加历史记录

        void insertCollection(File file);//添加收藏

        void insertEnclosure(File file);

    }

    interface Model{

        Observable insertHistory(HistoryEntity entity);

        Observable<Boolean> insertCollection(CollectionEntity entity);

        Observable<Boolean> insertEnclosure(EnclosureEntity entity);
    }
}
