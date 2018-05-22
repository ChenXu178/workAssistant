package com.chenxu.workassistant.readEmail;

import com.chenxu.workassistant.BasePresenter;
import com.chenxu.workassistant.BaseView;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public interface ReadEmailContract {
    interface View extends BaseView{
        void setTitle(String title);

        void setSendTime(String time);

        void setSender(String sender);

        void existEnclosure(List<String> list);

        void setHTMLContent(String content,String encoding);

        void showDownLoading();

        void cancelDownLoading();

        void showErrorSnackBar(int text);

        void showSnackBar(int text);
    }

    interface Presenter extends BasePresenter{
        void initData(long UID);

        void getTitle();

        void getSendTime();

        void getSender();

        void getEnclosure();

        void getContent();

        void downloadEnclosure(int position);

    }

    interface Model {
        Observable<Object> getEmail(long UID);

        Observable<String> getTitle();

        Observable<String> getSendTime();

        Observable<String> getSender();

        Observable<String> getContent();

        Observable<String> getEncoding();

        Observable<List<String>> getEnclosure();

        Observable<Boolean> downEnclosureByPosition(int position);
    }

}
