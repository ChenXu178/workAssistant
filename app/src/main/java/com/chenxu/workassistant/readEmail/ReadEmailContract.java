package com.chenxu.workassistant.readEmail;

import com.chenxu.workassistant.BasePresenter;
import com.chenxu.workassistant.BaseView;

import io.reactivex.Observable;

public interface ReadEmailContract {
    interface View extends BaseView{
        void setTitle(String title);

        void setSendTime(String time);

        void setSender(String sender);

        void setHTMLContent(String content,String encoding);
    }

    interface Presenter extends BasePresenter{
        void initData(long UID);

        void getTitle();

        void getSendTime();

        void getSender();

        void getContent();

    }

    interface Model {
        Observable<Object> getEmail(long UID);

        Observable<String> getTitle();

        Observable<String> getSendTime();

        Observable<String> getSender();

        Observable<String> getContent();

        Observable<String> getEncoding();
    }

}
