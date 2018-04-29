package com.chenxu.workassistant.FileMenage;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Applacation;
import com.chenxu.workassistant.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Android on 2018/3/26.
 */

public class FileMenagePresenter implements FileMenageContract.Presenter {

    private FileMenageContract.View mView;
    private File currentFile;  //当前文件
    private File rootFile; //根目录
    private ArrayList<FileBean> fileList,selectedFiles;
    private ArrayList<CatalogBean> catalogList; //目录
    private int showIndex = 0; //浏览位置
    private boolean isRetainOldFile = false,isRunFileNumber = true; //是否保留老文件（移动）
    private int fileNumber,folderNumber;
    private static final int HANDLER_FILE_NUMBER = 1;

    public FileMenagePresenter(FileMenageContract.View view){
        mView = view;
        fileList = new ArrayList<>();
        selectedFiles = new ArrayList<>();
        catalogList = new ArrayList<>();
        rootFile = new File(Environment.getExternalStorageDirectory().toString());
    }

    @Override
    public void start() {
        currentFile = new File(Environment.getExternalStorageDirectory().toString());
        loadFiles(currentFile,1);
    }


    /**
     * 加载文件列表
     * @param selectFile 文件
     */
    @Override
    public void loadFiles(final File selectFile,final int type) {
        this.currentFile = selectFile;
        mView.loadFilesView(new ArrayList<FileBean>(),type);
        int fileNumber = selectFile.listFiles().length;
        boolean showLoad = fileNumber > 10 ? true : false;
        if (showLoad)
            mView.setLoadViewDisplay(true);
        Observable<ArrayList<FileBean>> observable = Observable.create(new ObservableOnSubscribe<ArrayList<FileBean>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<FileBean>> e) throws Exception {
                fileList.clear();
                File[] temp = selectFile.listFiles();
                for (File file:temp){
                    char isHide = file.getName().charAt(0);
                    if (isHide != '.'){
                        fileList.add(new FileBean(file,FileUtil.fileType(file),false,false));
                    }
                }
                fileList = FileUtil.FilesSort(fileList);
                e.onNext(fileList);
            }
        });

        Consumer<ArrayList<FileBean>> consumer = new Consumer<ArrayList<FileBean>>() {
            @Override
            public void accept(ArrayList<FileBean> fileBeans) throws Exception {
                mView.setLoadViewDisplay(false);
                mView.loadFilesView(fileBeans,type);
                if (showIndex > 0){
                    mView.moveToPosition(showIndex);
                }
            }
        };

        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);

    }

    /**
     * 进入下一层页面
     * @param position 选中的下标
     * @param showIndex 当前显示的第一项
     */
    @Override
    public void onFileItemClick(int position, int showIndex, View view) {
        FileBean fileBean = fileList.get(position);
        if (fileBean.getFile().isDirectory()) {
            //目录，进入下一层
            loadFiles(fileBean.getFile(),1);
            CatalogBean catalogBean = new CatalogBean(fileBean.getFile(), showIndex);
            catalogList.add(catalogBean);
            mView.addCatalog(catalogBean, catalogList.size());
        }else {
            //文件，打开
            switch (fileBean.getType()){//1文件夹、2音乐、3文件、4代码、5Excel、6图片、7PDF、8PPT、9TXT、10视频、11Word、12压缩
                case 2: Toast.makeText(Applacation.getInstance(),R.string.file_menage_open_err,Toast.LENGTH_SHORT).show(); break;
                case 3: Toast.makeText(Applacation.getInstance(),R.string.file_menage_open_err,Toast.LENGTH_SHORT).show(); break;
                case 4: Toast.makeText(Applacation.getInstance(),R.string.file_menage_open_err,Toast.LENGTH_SHORT).show(); break;
                case 5: mView.openOfficeFile(fileBean.getFile().getPath(),view); break;
                case 6: mView.openImageFile(fileBean.getFile().toString(),view); break;
                case 7: mView.openOfficeFile(fileBean.getFile().getPath(),view); break;
                case 8: mView.openOfficeFile(fileBean.getFile().getPath(),view); break;
                case 9: mView.openOfficeFile(fileBean.getFile().getPath(),view); break;
                case 10: Toast.makeText(Applacation.getInstance(),R.string.file_menage_open_err,Toast.LENGTH_SHORT).show(); break;
                case 11: mView.openOfficeFile(fileBean.getFile().getPath(),view); break;
                case 12: Toast.makeText(Applacation.getInstance(),R.string.file_menage_open_err,Toast.LENGTH_SHORT).show(); break;
            }
        }
    }

    /**
     * 返回上一层
     * @return 是否已经是首页
     */
    @Override
    public boolean onBack() {
        if (catalogList.size() > 0){
            CatalogBean catalogBean = catalogList.get(catalogList.size()-1);
            setShowIndex(catalogBean.getShowIndex());
            catalogList.remove(catalogList.size()-1);
            loadFiles(catalogBean.getFile().getParentFile(),2);
            mView.removeCatalog();
            return false;
        }else {
            return true;
        }
    }

    /**
     * 设置显示文件列表位置
     * @param index
     */
    @Override
    public void setShowIndex(int index) {
        this.showIndex = index;
    }

    /**
     * 回到首页
     */
    @Override
    public void toRootFiles() {
        if (catalogList.size() > 0){
            setShowIndex(catalogList.get(0).getShowIndex());
            catalogList.clear();
            loadFiles(rootFile,3);
            mView.clearCatalog();
        }else {
            mView.smoothMoveToPosition(0);
        }
    }


    /**
     * 返回指定的一层
     * @param position 下标
     */
    @Override
    public void backToPosition(int position) {
        if (catalogList.size()-1 == position){
            mView.smoothMoveToPosition(0);
        }else {
            setShowIndex(catalogList.get(position+1).getShowIndex());
            while (catalogList.size()-1>position){
                catalogList.remove(catalogList.size()-1);
            }
            loadFiles(catalogList.get(catalogList.size()-1).getFile(),3);
            mView.removeCatalogToPosition(position);
        }

    }

    /**
     * 获得文件选中状态
     * @param position 下标
     * @return
     */
    @Override
    public boolean getFileSelectState(int position) {
        return fileList.get(position).isChecked();
    }

    /**
     * 添加文件到已选择区
     * @param position
     */
    @Override
    public void addSelectedFile(int position) {
        selectedFiles.add(fileList.get(position));
        mView.setSelectFileNumber(selectedFiles.size());
    }

    /***
     * 从已选择区中删除该文件
     * @param position
     */
    @Override
    public void delSelectedFile(int position) {
        selectedFiles.remove(fileList.get(position));
        mView.setSelectFileNumber(selectedFiles.size());
    }

    /**
     * 全选、取消全选
     * @param isAll
     */
    @Override
    public void allSelectFile(boolean isAll) {
        if (isAll){
            for (FileBean fileBean:fileList) {
                selectedFiles.add(fileBean);
            }
        }else {
            selectedFiles.clear();
        }
        mView.setSelectFileNumber(selectedFiles.size());
    }

    @Override
    public int getSelectFileNumber() {
        return selectedFiles.size();
    }

    @Override
    public String getSelectFileName() {
        if (selectedFiles.size()>0){
            FileBean fileBean = selectedFiles.get(0);
            if (fileBean.getType() == 1){
                return fileBean.getFile().getName();
            }else {
                return fileBean.getFile().getName().substring(0,fileBean.getFile().getName().lastIndexOf("."));
            }
        }
        return null;
    }

    @Override
    public void deleteSelectFile() {
        mView.showLoadingDialog();
        Observable<ArrayList<FileBean>> observable = Observable.create(new ObservableOnSubscribe<ArrayList<FileBean>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<FileBean>> e) throws Exception {
                for(FileBean fileBean : selectedFiles){
                    FileUtil.deleteFile(fileBean.getFile());
                }
                e.onNext(selectedFiles);
            }
        });
        Consumer<ArrayList<FileBean>> consumer = new Consumer<ArrayList<FileBean>>() {
            @Override
            public void accept(ArrayList<FileBean> fileBeans) throws Exception {
                for (FileBean fileBean : fileBeans){
                    mView.deleteFileItem(fileBean);
                }
                selectedFiles.clear();
                mView.setFileSelectViewDisplay(false);
                mView.hideLoadingDialog();
            }
        };
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    }

    /**
     * 重命名文件
     * @param newFileName 文件名
     */
    @Override
    public void renameSelectFile(String newFileName) {
        if ("".equals(newFileName)){
            mView.showToast(R.string.file_dialog_err1);
            return;
        }
        FileBean fileBean = selectedFiles.get(0);
        File testFile;
        if (fileBean.getType() == 1){
            testFile = new File(fileBean.getFile().getParent()+"/"+newFileName);
            if (testFile.exists()){
                mView.showToast(R.string.file_dialog_err2);
                return;
            }else {
                if (!fileBean.getFile().renameTo(testFile)){
                    mView.showToast(R.string.file_dialog_err3);
                    return;
                }
            }
        }else {
            String name = fileBean.getFile().getName();
            String type = name.substring(name.lastIndexOf("."),name.length());
            testFile = new File(fileBean.getFile().getParent()+"/"+newFileName+type);
            if (testFile.exists()){
                mView.showToast(R.string.file_dialog_err2);
                return;
            }else {
                if (!fileBean.getFile().renameTo(testFile)){
                    mView.showToast(R.string.file_dialog_err3);
                    return;
                }
            }
        }
        //成功重命名，更新文件数据
        int index = fileList.indexOf(fileBean);
        fileList.get(index).setFile(testFile);
        selectedFiles.clear();
        mView.updateFile(index,testFile);
    }


    @Override
    public void setRetainOldFile(boolean retainOldFile) {
        isRetainOldFile = retainOldFile;
    }

    @Override
    public boolean checkFilePathIsCorrect() {
        Log.e("selectedFiles",selectedFiles.get(0).getFile().getParent());
        Log.e("currentFile",currentFile.getPath());
        if (selectedFiles.size() <= 0){
            return false;
        }
        if (currentFile.getPath().equals(selectedFiles.get(0).getFile().getParent())){
            return false;
        }
        for (FileBean fileBean : selectedFiles){
            if (fileBean.getFile().isDirectory()){
                if (fileBean.getFile().getPath().length()<= currentFile.getPath().length()){
                    if (currentFile.getPath().startsWith(fileBean.getFile().getPath())){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void moveFiles() {
        final ArrayList<FileBean> temp = new ArrayList<>();
        Observable<ArrayList<FileBean>> observable = Observable.create(new ObservableOnSubscribe<ArrayList<FileBean>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<FileBean>> e) throws Exception {
                for (FileBean fileBean: selectedFiles){
                   File file = FileUtil.copyFile(fileBean.getFile(),currentFile);
                   temp.add(new FileBean(file,FileUtil.fileType(file),false,false));
                   if (!isRetainOldFile){
                       FileUtil.deleteFile(fileBean.getFile());
                   }
                }
                selectedFiles.clear();
                e.onNext(temp);
            }
        });
        Consumer<ArrayList<FileBean>> consumer = new Consumer<ArrayList<FileBean>>() {
            @Override
            public void accept(ArrayList<FileBean> fileBeans) throws Exception {
                mView.addFiles(fileBeans);
                mView.hideLoadingDialog();
                mView.setFileSelectViewDisplay(false);
            }
        };
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    }

    @Override
    public void addFolder(String name) {
        if ("".equals(name)){
            mView.showToast(R.string.file_dialog_err1);
            return;
        }
        String path = currentFile.getAbsolutePath();
        File testFile = new File(path+"/"+name);
        if (testFile.exists()){
            mView.showToast(R.string.file_dialog_err2);
            return;
        }else {
            if (!testFile.mkdir()){
                mView.showToast(R.string.file_dialog_err4);
                return;
            }
        }
        FileBean fileBean = new FileBean(testFile,1,false,false);
        mView.addFile(fileBean);
    }

    @Override
    public void swipeMenuItemClick(int adapterPosition, int menuPosition) {
        File file = fileList.get(adapterPosition).getFile();
        if(menuPosition == 0){
            //附件
            if (file.isDirectory()){
                mView.showSnackBar(R.string.file_right_menu_err1,R.color.SnackBarText,R.color.SnackBarBG);
            }else {

            }
        }
        if (menuPosition == 1){
            //收藏
            mView.showCollectionDialog();
        }
    }

    @Override
    public void getFileDetail() {
        File file = selectedFiles.get(0).getFile();
        if (file.isDirectory()){
            //是目录，遍历文件数
            mView.showDetailDialog(2,file.getName(),FileUtil.convertFileTime(file.lastModified()),"",file.getPath());
            fileNumber = 0;
            folderNumber = 0;
            isRunFileNumber = true;
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    if (recursionFileNumber(file)){
                        handler.sendEmptyMessage(HANDLER_FILE_NUMBER);
                    }
                }
            }.start();
        }else {
            //是文件
            mView.showDetailDialog(1,file.getName(),FileUtil.convertFileTime(file.lastModified()),FileUtil.convertFileSize(file.length()),file.getPath());
        }
    }

    @Override
    public void setRunFileNumber(boolean isRun) {
        this.isRunFileNumber = isRun;
    }

    private boolean recursionFileNumber(File file){
        File[] files = file.listFiles();
        for (File item : files){
            if (isRunFileNumber){
                if (item.isDirectory()){
                    folderNumber ++;
                    recursionFileNumber(item);
                }else {
                    fileNumber ++;
                }
            }else {
                return false;
            }
        }
        files = null;
        return true;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_FILE_NUMBER:
                    mView.setDialogDetailFileNumber(fileNumber,folderNumber);
                    break;
            }
        }
    };


}
