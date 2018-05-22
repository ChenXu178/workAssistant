package com.chenxu.workassistant.utils;


import com.chenxu.workassistant.config.Applacation;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * Created by 陈旭 on 2017/1/19.
 */

public class SendEmailUtil {
    /**
     * 发邮件
     */
    String host;
    String username;
    String password;
    String send_addr;
    String send_title;
    String send_content;
    List<File> files;
    String comeFrom = "<br><br><br>——来自 Office办公助手";

    public SendEmailUtil(String host, String username, String password, String send_addr, String send_title, String send_content, List<File> files) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.send_addr = send_addr;
        this.send_title = send_title;
        this.send_content = send_content;
        this.files = files;
        this.send_content = this.send_content.concat(comeFrom);
    }


    public void send() throws Exception {
        Properties prop = new Properties();
        prop.setProperty("mail.host", host);
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.starttls.enable", "true");

        //创建认证器
        Authenticator auth = new Authenticator(){
            public PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(username,password);
            }
        };
        //获取session对象
        Session session = Session.getInstance(prop, auth);
        session.setDebug(true);

        //创建MimeMessage
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(send_addr));
        msg.setSubject(send_title);
        if (files != null && files.size()>0){
            MimeMultipart multi = new MimeMultipart();
            BodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setContent(send_content,"text/html;charset=utf-8");
            multi.addBodyPart(textBodyPart);
            for (int i=0;i<files.size();i++){
                // 附件
                File f = files.get(i);
                FileDataSource fds = new FileDataSource(f);
                BodyPart fileBodyPart = new MimeBodyPart();
                fileBodyPart.setDataHandler(new DataHandler(fds));
                fileBodyPart.setFileName(MimeUtility.encodeText(f.getName()));//如果附件有中文通过转换没有问题了
                multi.addBodyPart(fileBodyPart);
            }
            msg.setContent(multi);
        }else {
            msg.setContent(send_content,"text/html;charset=utf-8");
        }
        //发送邮件
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
        Transport.send(msg);
    }
}
