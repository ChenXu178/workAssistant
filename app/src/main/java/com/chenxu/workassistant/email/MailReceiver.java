package com.chenxu.workassistant.email;


import android.annotation.SuppressLint;
import android.util.Log;

import com.chenxu.workassistant.utils.RelativeDateFormat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

public class MailReceiver implements Serializable {

    private static final long serialVersionUID = 1L;
    private MimeMessage mimeMessage = null;
    private StringBuffer mailContent = new StringBuffer();// 邮件内容
    private String dataFormat = "yyyy年MM月dd日 HH:mm:ss";
    private String charset;
    private boolean html;
    private ArrayList<String> attachments = new ArrayList<String>();
    private ArrayList<InputStream> attachmentsInputStreams = new ArrayList<InputStream>();

    public MailReceiver(MimeMessage mimeMessage) {
        this.mimeMessage = mimeMessage;
        try {
            charset = parseCharset(mimeMessage.getContentType());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public MimeMessage getMimeMessage(){
        return mimeMessage;
    }

    /**
     * 获得送信人的姓名和邮件地址
     *
     * @throws Exception
     */
    public String getFrom() throws Exception {
        InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
        String addr = address[0].getAddress();
        String name = address[0].getPersonal();
        if (addr == null) {
            addr = "";
        }
        if (name == null) {
            name = addr;
        } else if (charset == null) {
            name = TranCharset.TranEncodeTOUTF8(name);
        }
        String nameAddr = name + "<" + addr + ">";
        return name;
    }

    /**
     * 获得送信人的姓名和邮件地址
     *
     * @throws Exception
     */
    public String getFromAddress() throws Exception {
        InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
        String addr = address[0].getAddress();
        if (addr == null) {
            addr = "";
        }
        return addr;
    }

    /**
     * 根据类型，获取邮件地址 "TO"--收件人地址 "CC"--抄送人地址 "BCC"--密送人地址
     *
     * @throws Exception
     */
    public String getMailAddress(String type) throws Exception {
        String mailAddr = "";
        String addType = type.toUpperCase(Locale.CHINA);
        InternetAddress[] address = null;
        if (addType.equals("TO")) {
            address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);
        } else if (addType.equals("CC")) {
            address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);
        } else if (addType.equals("BCC")) {
            address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);
        } else {
            System.out.println("error type!");
            throw new Exception("Error emailaddr type!");
        }
        if (address != null) {
            for (int i = 0; i < address.length; i++) {
                String mailaddress = address[i].getAddress();
                if (mailaddress != null) {
                    mailaddress = MimeUtility.decodeText(mailaddress);
                } else {
                    mailaddress = "";
                }
                String name = address[i].getPersonal();
                if (name != null) {
                    name = MimeUtility.decodeText(name);
                } else {
                    name = "";
                }
                mailAddr = name + "<" + mailaddress + ">";
            }
        }
        return mailAddr;
    }

    /**
     * 取得邮件标题
     *
     * @return String
     */
    public String getSubject() {
        String subject = "";
        try {
            subject = mimeMessage.getSubject();
            if (subject.indexOf("=?gb18030?") != -1) {
                subject = subject.replace("gb18030", "gb2312");
            }
            subject = MimeUtility.decodeText(subject);
            if (charset == null) {
                subject = TranCharset.TranEncodeTOGB(subject);
            }
        } catch (Exception e) {
        }
        return subject;
    }

    /**
     * 取得邮件日期
     *
     * @throws MessagingException
     */
    public String getSentDate() throws MessagingException {
        Date sentdata = mimeMessage.getSentDate();
        if (sentdata != null) {
            SimpleDateFormat format = new SimpleDateFormat(dataFormat, Locale.CHINA);
            return format.format(sentdata);
        } else {
            return "未知时间";
        }
    }

    /**
     * 取得邮件日期
     *
     * @throws MessagingException
     */
    public String getSentRelativeDate() throws MessagingException {
        Date sentdata = mimeMessage.getSentDate();
        if (sentdata != null) {
            return RelativeDateFormat.format(sentdata);
        } else {
            return "未知时间";
        }
    }

    /**
     * 取得邮件内容
     *
     * @throws Exception
     */
    public String getMailContent() throws Exception {
        compileMailContent((Part) mimeMessage);
        String content = mailContent.toString();
        if (content.indexOf("<html>") != -1) {
            html = true;
        }
        mailContent.setLength(0);
        return content;
    }

    public void setMailContent(StringBuffer mailContent) {
        this.mailContent = mailContent;
    }

    /**
     * 是否有回执
     *
     * @throws MessagingException
     */
    public boolean getReplySign() throws MessagingException {
        boolean replySign = false;
        String needreply[] = mimeMessage.getHeader("Disposition-Notification-To");
        if (needreply != null) {
            replySign = true;
        }
        return replySign;
    }

    /**
     * 取得「message-ID」
     *
     * @throws MessagingException
     */
    public String getMessageID() throws MessagingException {
        return mimeMessage.getMessageID();
    }

    /**
     * 是否新邮件
     *
     * @throws MessagingException
     */
    public boolean isNew() throws MessagingException {
        boolean isnew = true;
        Flags flags = ((Message) mimeMessage).getFlags();
        Flags.Flag[] flag = flags.getSystemFlags();
        for (int i = 0; i < flag.length; i++) {
            if (flag[i] == Flags.Flag.SEEN) {
                isnew = false;
                break;
            }
        }
        return isnew;
    }

    public String getCharset() {
        return charset;
    }

    public ArrayList<String> getAttachments() {
        return attachments;
    }

    public boolean isHtml() {
        return html;
    }

    public ArrayList<InputStream> getAttachmentsInputStreams() {
        return attachmentsInputStreams;
    }

    /**
     * 解析邮件内容
     *
     * @param part
     * @throws Exception
     */
    private void compileMailContent(Part part) throws Exception {
        String contentType = part.getContentType();
        // Log.v("content type", "[" + contentType.replace("\n", "") + "]" + "["
        // + part.getContent() + "]");
        boolean connName = false;
        if (contentType.indexOf("name") != -1) {
            connName = true;
        }
        if (part.isMimeType("text/plain") && !connName) {
            String content = parseInputStream((InputStream) part.getContent());
            mailContent.append(content);
        } else if (part.isMimeType("text/html") && !connName) {
            html = true;
            String content = null;
            try{
                content = parseInputStream((InputStream) part.getContent());
            }catch (Exception e){
                content = parseString((String) part.getContent());
            }
            mailContent.append(content);
        } else if (part.isMimeType("multipart/*") || part.isMimeType("message/rfc822")) {
            if (part.getContent() instanceof Multipart) {
                Multipart multipart = (Multipart) part.getContent();
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    compileMailContent(multipart.getBodyPart(i));
                }
            } else {
                Multipart multipart = new MimeMultipart(new ByteArrayDataSource(part.getInputStream(), "multipart/*"));
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    compileMailContent(multipart.getBodyPart(i));
                }
            }
        } else if (part.getDisposition() != null && part.getDisposition().equals(Part.ATTACHMENT)) {
            // 获取附件
            String filename = part.getFileName();
            if (filename != null) {
                if (filename.indexOf("=?gb18030?") != -1) {
                    filename = filename.replace("gb18030", "gb2312");
                }
                filename = MimeUtility.decodeText(filename);
                attachments.add(filename);
                attachmentsInputStreams.add(part.getInputStream());
            }
            Log.e("content", "附件：" + filename);
        }
    }

    /**
     * 解析字符集编码
     *
     * @param contentType
     * @return
     */
    private String parseCharset(String contentType) {
        if (!contentType.contains("charset")) {
            return null;
        }
        if (contentType.contains("gbk")) {
            return "GBK";
        } else if (contentType.contains("GB2312") || contentType.contains("gb18030")) {
            return "gb2312";
        } else if (contentType.contains("utf-8") || contentType.contains("UTF-8")) {
            return "UTF-8";
        } else {
            String sub = contentType.substring(contentType.indexOf("charset") + 8).replace("\"", "");
            if (sub.contains(";")) {
                return sub.substring(0, sub.indexOf(";"));
            } else {
                return sub;
            }
        }
    }

    /**
     * 解析流格式
     *
     * @param is
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    private String parseInputStream(InputStream is) throws IOException, MessagingException {
        StringBuffer str = new StringBuffer();
        byte[] readByte = new byte[1024];
        int count;
        try {
            while ((count = is.read(readByte)) != -1) {
                if (charset == null) {
                    str.append(new String(readByte, 0, count, "UTF-8"));
                } else {
                    str.append(new String(readByte, 0, count, charset));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    /**
     * 解析字符串
     *
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    private String parseString(String string) throws IOException, MessagingException {
        if (charset != null) {
            string = new String(string.getBytes(charset),"utf-8");
        }
        return string;
    }

    /**
     * 判断此邮件是否包含附件
     */
    @SuppressLint("DefaultLocale")
    public boolean isContainAttach(Part part) throws Exception {
        boolean attachflag = false;
        // String contentType = part.getContentType();
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mpart = mp.getBodyPart(i);
                String disposition = mpart.getDisposition();
                if ((disposition != null)
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition
                        .equals(Part.INLINE))))
                    attachflag = true;
                else if (mpart.isMimeType("multipart/*")) {
                    attachflag = isContainAttach((Part) mpart);
                } else {
                    String contype = mpart.getContentType();
                    if (contype.toLowerCase().indexOf("application") != -1)
                        attachflag = true;
                    if (contype.toLowerCase().indexOf("name") != -1)
                        attachflag = true;
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            attachflag = isContainAttach((Part) part.getContent());
        }
        return attachflag;
    }

    /**
     * 【保存附件】
     */
    @SuppressLint("DefaultLocale")
    public void saveAttachMent(Part part) throws Exception {
        String fileName = "";
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mpart = mp.getBodyPart(i);//主体部分得到处理
                String disposition = mpart.getDisposition();
                if ((disposition != null)
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition
                        .equals(Part.INLINE)))) {//ATTACHMENT附件，INLINE嵌入
                    fileName = mpart.getFileName();
                    if (fileName.toLowerCase().indexOf("gb18030") != -1) {
                        fileName = MimeUtility.decodeText(fileName);
                    }
                    saveFile(fileName, mpart.getInputStream());
                } else if (mpart.isMimeType("multipart/*")) {
                    saveAttachMent(mpart);
                } else {
                    fileName = mpart.getFileName();
                    if ((fileName != null)
                            && (fileName.toLowerCase().indexOf("GB18030") != -1)) {
                        fileName = MimeUtility.decodeText(fileName);
                        saveFile(fileName, mpart.getInputStream());
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachMent((Part) part.getContent());
        }
    }

    /**
     * 【真正的保存附件到指定目录里】
     */
    @SuppressLint("DefaultLocale")
    private void saveFile(String fileName, InputStream in) throws Exception {
        String osName = System.getProperty("os.name");
        System.out.println("----fileName----" + fileName);
        // String storedir = getAttachPath();
//        String separator = "";
        if (osName == null)
            osName = "";
        File storefile = new File(File.separator + "mnt" + File.separator
                + "sdcard" + File.separator + fileName);

        storefile.createNewFile();
        System.out.println("storefile's path: " + storefile.toString());
//         for(int i=0;storefile.exists();i++){
//         storefile = new File(storedir+separator+fileName+i);
//         }

        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(storefile));
            bis = new BufferedInputStream(in);
            int c;
            while ((c = bis.read()) != -1) {
                bos.write(c);
                bos.flush();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("文件保存失败!");
        } finally {
            bos.close();
            bis.close();
        }
    }

}
