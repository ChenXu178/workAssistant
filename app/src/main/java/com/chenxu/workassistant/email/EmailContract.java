package com.chenxu.workassistant.email;

import com.chenxu.workassistant.BasePresenter;
import com.chenxu.workassistant.BaseView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public interface EmailContract {

    interface View extends BaseView{
        void loginError();
        void initData(List<Email> list);

        void deleteItem(int position);

        void onDataLoading();

        void setEmailRead(int position);

        void openEmail(long UID);
    }

    interface Presenter extends BasePresenter{
        void getEmail(ArrayList<MailReceiver> list);

        void refreshEmail(RefreshLayout refreshLayout);

        void deleteEmail(int position);

        void openEmail(int position);

        void closeInbox();
    }

    interface Model{

        Observable<Boolean> login(int emailType, String account, String password);

        Observable<ArrayList<MailReceiver>> getMailReceivers();

        Observable<ArrayList<Email>> getEmailList(List<MailReceiver> list);

        Observable<Boolean> deleteEmail(MailReceiver mailReceiver);

        Observable<Boolean> setEmailRead(MailReceiver mailReceiver);

        Observable<Boolean> exitStore();

        Observable<Long> getEmailUID(MailReceiver mailReceiver);
    }

}
