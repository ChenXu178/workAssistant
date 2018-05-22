package com.chenxu.workassistant.fileMenage;

import com.chenxu.workassistant.dao.CollectionEntity;
import com.chenxu.workassistant.dao.CollectionEntityDao;
import com.chenxu.workassistant.dao.EnclosureEntity;
import com.chenxu.workassistant.dao.EnclosureEntityDao;
import com.chenxu.workassistant.dao.GreenDaoManager;
import com.chenxu.workassistant.dao.HistoryEntity;
import com.chenxu.workassistant.dao.HistoryEntityDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class FileManageModel implements FileMenageContract.Model {

    private FileMenageContract.Presenter mPresenter;

    public FileManageModel(FileMenageContract.Presenter presenter){
        this.mPresenter = presenter;
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
}
