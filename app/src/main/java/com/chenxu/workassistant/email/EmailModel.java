package com.chenxu.workassistant.email;

import android.util.Log;

import com.chenxu.workassistant.config.Applacation;
import com.chenxu.workassistant.config.Constant;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


public class EmailModel implements EmailContract.Model {

    private EmailContract.Presenter mPresenter;
    private IMAPStore store;
    private IMAPFolder folder;
    private boolean isReading = true;

    public EmailModel(EmailContract.Presenter presenter){
        this.mPresenter = presenter;
    }

    @Override
    public Observable<Boolean> login(int emailType, String account, String password) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                if (Applacation.getStore() == null){
                    String emailIMAP = Constant.EMAIL_SERVER_IMAP[emailType];
                    Properties prop = System.getProperties();
                    prop.put("mail.store.protocol", "imap");
                    prop.put("mail.imap.host",emailIMAP);
                    try{
                        Session session = Session.getInstance(prop);
                        store = (IMAPStore) session.getStore("imap");
                        store.connect(account,password);
                        Applacation.setStore(store);
                        emitter.onNext(true);
                    }catch (Exception e){
                        e.printStackTrace();
                        emitter.onNext(false);
                    }
                }else {
                    store = Applacation.getStore();
                    emitter.onNext(true);
                }
            }
        });
    }

    @Override
    public Observable<ArrayList<MailReceiver>> getMailReceivers() {
        return Observable.create(new ObservableOnSubscribe<ArrayList<MailReceiver>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<MailReceiver>> e) throws Exception {
                folder = (IMAPFolder) store.getFolder("INBOX");
                folder.open(Folder.READ_WRITE);
                Message[] messages =  folder.getMessages();
                ArrayList<MailReceiver> list = new ArrayList<>();
                for (Message message : messages){
                    MailReceiver mail = new MailReceiver((MimeMessage) message);
                    list.add(mail);
                }
                e.onNext(list);
            }
        });
    }

    @Override
    public synchronized Observable<ArrayList<Email>> getEmailList(List<MailReceiver> list) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<Email>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Email>> e) throws Exception {
                ArrayList<Email> emailList = new ArrayList<>();
                Log.e("listsize",list.size()+"");
                isReading = true;
                for (int i = 0; i < list.size(); i++) {
                    MailReceiver mail = list.get(list.size()-i-1);
                    Email email = new Email(mail.getMessageID(),mail.getFrom(),mail.getSubject(),mail.getSentRelativeDate(),mail.isNew());
                    Log.e("email",email.toString());
                    emailList.add(email);
                    Log.e("emailsize",emailList.size()+"");
                }
                Log.e("emailsize",emailList.size()+"");
                isReading = false;
                e.onNext(emailList);
            }
        });
    }

    @Override
    public Observable<Boolean> deleteEmail(MailReceiver mailReceiver) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                mailReceiver.getMimeMessage().setFlag(Flags.Flag.DELETED, true);
                e.onNext(true);
            }
        });
    }

    @Override
    public Observable<Boolean> setEmailRead(MailReceiver mailReceiver) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                mailReceiver.getMimeMessage().setFlag(Flags.Flag.SEEN,true);
                e.onNext(true);
            }
        });
    }

    @Override
    public Observable<Boolean> exitStore() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                while (isReading){
                    Thread.sleep(500);
                }
                folder.close(true);
            }
        });
    }

    @Override
    public Observable<Long> getEmailUID(MailReceiver mailReceiver) {
        return Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> e) throws Exception {
                long uid = folder.getUID(mailReceiver.getMimeMessage());
                e.onNext(uid);
            }
        });
    }

}
