package com.chenxu.workassistant.readEmail;

import android.os.Environment;

import com.chenxu.workassistant.config.Applacation;
import com.chenxu.workassistant.email.MailReceiver;
import com.chenxu.workassistant.utils.FileUtil;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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

    @Override
    public Observable<List<String>> getEnclosure() {
        return Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> emitter) throws Exception {
                emitter.onNext(mailReceiver.getAttachments());
            }
        });
    }

    @Override
    public Observable<Boolean> downEnclosureByPosition(int position) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                String filename = mailReceiver.getAttachments().get(position);
                File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + filename);
                if (file.exists()){
                    file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + FileUtil.repeatFileName(filename));
                }
                file.createNewFile();
                try{
                    InputStream is = mailReceiver.getAttachmentsInputStreams().get(position);
                    OutputStream os = new FileOutputStream(file);
                    byte bt[] = new byte[1024];
                    int c;
                    while ((c = is.read(bt)) > 0) {
                        os.write(bt, 0, c);
                    }
                    os.flush();
                    is.close();
                    os.close();
                    emitter.onNext(true);
                }catch (Exception e){
                    e.printStackTrace();
                    emitter.onNext(false);
                }
            }
        });
    }

}
