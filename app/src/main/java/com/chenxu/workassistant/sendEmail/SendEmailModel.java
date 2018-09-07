package com.chenxu.workassistant.sendEmail;

import com.chenxu.workassistant.config.Constant;
import com.chenxu.workassistant.dao.EnclosureEntity;
import com.chenxu.workassistant.dao.EnclosureEntityDao;
import com.chenxu.workassistant.dao.GreenDaoManager;
import com.chenxu.workassistant.utils.SendEmailUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class SendEmailModel implements SendEmailContract.Model {

    private SendEmailContract.Presenter mPresenter;
    public static final String MAP_ENCLOSURE_COUNT = "map:enclosure_count";
    public static final String MAP_ENCLOSURE_SIZE = "map:enclosure_size";
    public static final String MAP_PARAM_READER = "map:param_reader";
    public static final String MAP_PARAM_TITLE = "map:param_title";
    public static final String MAP_PARAM_CONTENT = "map:param_content";
    public static final String MAP_PARAM_ENCLOSURE = "map:param_enclosure";

    public SendEmailModel(SendEmailContract.Presenter presenter){
        this.mPresenter = presenter;
    }

    @Override
    public Observable<List<File>> getEnclosure() {
        return Observable.create(new ObservableOnSubscribe<List<File>>() {
            @Override
            public void subscribe(ObservableEmitter<List<File>> e) throws Exception {
                List<EnclosureEntity> list = GreenDaoManager.getEnclosureEntityDao().queryBuilder().list();
                List<File> data = new ArrayList<>();
                for(EnclosureEntity entity : list){
                    File file = new File(entity.getPath());
                    if (file.exists()){
                        data.add(file) ;
                    }else {
                        GreenDaoManager.getEnclosureEntityDao().delete(entity);
                    }
                }
                e.onNext(data);
            }
        });
    }

    @Override
    public Observable<Long> getEnclosureSize(List<File> files) {
        return Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> e) throws Exception {
                long size = 0;
                for (File file : files){
                    size += file.length();
                }
                e.onNext(size);
            }
        });
    }

    @Override
    public Observable<Boolean> deleteEnclosure(String path) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                List<EnclosureEntity> list = GreenDaoManager.getEnclosureEntityDao().queryBuilder().where(EnclosureEntityDao.Properties.Path.eq(path)).list();
                for (EnclosureEntity enclosureEntity : list){
                    GreenDaoManager.getEnclosureEntityDao().delete(enclosureEntity);
                }
                e.onNext(true);
            }
        });
    }

    @Override
    public Observable<Boolean> sendEmail(Map<String, Object> param) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                String reader = (String) param.get(MAP_PARAM_READER);
                String title = (String) param.get(MAP_PARAM_TITLE);
                String content = (String) param.get(MAP_PARAM_CONTENT);
                List<File> files = (List<File>) param.get(MAP_PARAM_ENCLOSURE);
                String smtp = Constant.EMAIL_SERVER_SMTP[Constant.spSetting.getInt(Constant.EMAIL_SERVER_TYPE,0)];
                String account = Constant.spSetting.getString(Constant.EMAIL_ACCOUNT,"");
                String password = Constant.spSetting.getString(Constant.EMAIL_PASSWORD,"");
                try{
                    SendEmailUtil util = new SendEmailUtil(smtp,account,password,reader,title,content,files);
                    util.send();
                    emitter.onNext(true);
                }catch (Exception e){
                    e.printStackTrace();
                    emitter.onNext(false);
                }
            }
        });
    }
}
