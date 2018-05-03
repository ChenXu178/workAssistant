package com.chenxu.workassistant.collection;

import android.util.Log;

import com.chenxu.workassistant.dao.CollectionEntity;
import com.chenxu.workassistant.dao.CollectionEntityDao;
import com.chenxu.workassistant.dao.GreenDaoManager;
import com.chenxu.workassistant.utils.RelativeDateFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class CollectionModel implements CollectionContract.Model {

    private CollectionContract.Presenter mPresenter;

    public CollectionModel(CollectionContract.Presenter presenter){
        this.mPresenter = presenter;
    }

    @Override
    public Observable<List<FileBean>> selectAllOrderByTime() {
        return Observable.create(new ObservableOnSubscribe<List<FileBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<FileBean>> e) throws Exception {
                List<CollectionEntity> list = GreenDaoManager.getCollectionEntityDao().queryBuilder().orderDesc(CollectionEntityDao.Properties.Time).list();
                List<FileBean> data = new ArrayList<>();
                for (CollectionEntity entity:list){
                    Log.e("CollectionEntity",entity.getPath());
                    FileBean bean = new FileBean(entity.getId(),new File(entity.getPath()), RelativeDateFormat.ordinaryFormat(entity.getTime()));
                    if (bean.getFile().exists()){
                        data.add(bean);
                    }else {
                        GreenDaoManager.getCollectionEntityDao().delete(entity);
                    }
                }
                Log.e("data",data.size()+"");
                e.onNext(data);
            }
        });
    }

    @Override
    public Observable deleteById(long id) {
        return Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                GreenDaoManager.getCollectionEntityDao().deleteByKey(id);
                e.onNext("");
            }
        });
    }
}
