package com.chenxu.workassistant.readEmail;

import com.chenxu.workassistant.config.Applacation;
import com.chenxu.workassistant.email.MailReceiver;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import javax.mail.Folder;
import javax.mail.internet.MimeMessage;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class ReadEmailModel implements ReadEmailContract.Model {

    private ReadEmailContract.Presenter mPresenter;
    private IMAPStore store;
    private IMAPFolder folder;
    private MailReceiver mailReceiver;

    public ReadEmailModel(ReadEmailContract.Presenter presenter){
        this.mPresenter = presenter;
    }

    @Override
    public Observable<Object> getEmail(long UID) {
        return Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                store = Applacation.getStore();
                folder = (IMAPFolder) store.getFolder("INBOX");
                folder.open(Folder.READ_WRITE);
                mailReceiver = new MailReceiver((MimeMessage) folder.getMessageByUID(UID));
                e.onNext("");
            }
        });
    }

    @Override
    public Observable<String> getTitle() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext(mailReceiver.getSubject());
            }
        });
    }

    @Override
    public Observable<String> getSendTime() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext(mailReceiver.getSentDate());
            }
        });
    }

    @Override
    public Observable<String> getSender() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext(mailReceiver.getFrom());
            }
        });
    }

    @Override
    public Observable<String> getContent() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext(mailReceiver.getMailContent());
            }
        });
    }

    @Override
    public Observable<String> getEncoding() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String encoding = mailReceiver.getCharset();
                if (encoding == null || "".equals(encoding)){
                    e.onNext("UTF-8");
                }else {
                    e.onNext(encoding);
                }
            }
        });
    }

}
