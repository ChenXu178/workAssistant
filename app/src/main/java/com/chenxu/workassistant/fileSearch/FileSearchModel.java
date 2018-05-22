package com.chenxu.workassistant.fileSearch;

import android.os.Environment;

import com.chenxu.workassistant.config.Constant;
import com.chenxu.workassistant.dao.CollectionEntity;
import com.chenxu.workassistant.dao.CollectionEntityDao;
import com.chenxu.workassistant.dao.EnclosureEntity;
import com.chenxu.workassistant.dao.EnclosureEntityDao;
import com.chenxu.workassistant.dao.GreenDaoManager;
import com.chenxu.workassistant.dao.SearchEntity;
import com.chenxu.workassistant.dao.SearchEntityDao;
import com.chenxu.workassistant.setting.SettingActivity;
import com.chenxu.workassistant.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class FileSearchModel implements FileSearchContract.Model {

    private FileSearchContract.Presenter mPresenter;

    public FileSearchModel(FileSearchContract.Presenter presenter){
        this.mPresenter = presenter;
    }

    @Override
    public synchronized Observable<Boolean> addSearchTextToHistory(String text) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                List<SearchEntity> list = GreenDaoManager.getSearchEntityDao().queryBuilder().where(SearchEntityDao.Properties.Text.eq(text)).list();
                if (list.size() > 0){
                    for(SearchEntity entity :list){
                        GreenDaoManager.getSearchEntityDao().delete(entity);
                    }
                }
                List<SearchEntity> allEntity = GreenDaoManager.getSearchEntityDao().queryBuilder().orderAsc(SearchEntityDao.Properties.Time).list();
                if (allEntity.size() >= 10){
                    GreenDaoManager.getSearchEntityDao().delete(allEntity.get(0));
                }
                GreenDaoManager.getSearchEntityDao().insert(new SearchEntity(null,text,System.currentTimeMillis()));
                emitter.onNext(true);
            }
        });
    }

    @Override
    public Observable<List<SearchEntity>> list() {
        return Observable.create(new ObservableOnSubscribe<List<SearchEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SearchEntity>> emitter) throws Exception {
                emitter.onNext(GreenDaoManager.getSearchEntityDao().queryBuilder().orderDesc(SearchEntityDao.Properties.Time).list());
            }
        });
    }

    @Override
    public Observable<List<SearchBean>> searchFile(String text) {
        return Observable.create(new ObservableOnSubscribe<List<SearchBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SearchBean>> emitter) throws Exception {
                boolean filter = Constant.spSetting.getBoolean(SettingActivity.SP_FILTER_FILE,false);
                List<SearchBean> list = new ArrayList<>();
                FileUtil.findFiles(Environment.getExternalStorageDirectory().toString(),text,filter,list);
                emitter.onNext(list);
            }
        });
    }

    @Override
    public synchronized Observable<Boolean> insertCollection(CollectionEntity entity) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                CollectionEntity collectionEntity = GreenDaoManager.getCollectionEntityDao().queryBuilder().where(CollectionEntityDao.Properties.Path.eq(entity.getPath())).unique();
                if (collectionEntity != null){
                    e.onNext(false);
                }else {
                    GreenDaoManager.getCollectionEntityDao().insert(entity);
                    e.onNext(true);
                }
            }
        });
    }

    @Override
    public synchronized Observable<Boolean> insertEnclosure(EnclosureEntity entity){
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                EnclosureEntity enclosureEntity = GreenDaoManager.getEnclosureEntityDao().queryBuilder().where(EnclosureEntityDao.Properties.Path.eq(entity.getPath())).unique();
                if (enclosureEntity != null){
                    e.onNext(false);
                }else {
                    GreenDaoManager.getEnclosureEntityDao().insert(entity);
                    e.onNext(true);
                }
            }
        });
    }

    @Override
    public Observable<List<SearchBean>> fastSearchFile(int type) {
        return Observable.create(new ObservableOnSubscribe<List<SearchBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SearchBean>> emitter) throws Exception {
                boolean filter = Constant.spSetting.getBoolean(SettingActivity.SP_FILTER_FILE,false);
                List<SearchBean> list = new ArrayList<>();
                FileUtil.findFilesBySuffix(Environment.getExternalStorageDirectory().toString(),type,filter,list);
                emitter.onNext(list);
            }
        });
    }
}
