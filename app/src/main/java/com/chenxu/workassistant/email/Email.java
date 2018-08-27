package com.chenxu.workassistant.email;

import java.io.Serializable;
import java.util.ArrayList;

public class Email implements Serializable {
    private String messageID;
    private String from;
    private String subject;
    private String sentdate;
    private boolean news;

    public Email() {
    }

    public Email(String messageID, String from, String subject, String sentdate, boolean news) {
        this.messageID = messageID;
        this.from = from;
        this.subject = subject;
        this.sentdate = sentdate;
        this.news = news;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSentdate() {
        return sentdate;
    }

    public void setSentdate(String sentdate) {
        this.sentdate = sentdate;
    }

    public boolean isNews() {
        return news;
    }

    public void setNews(boolean news) {
        this.news = news;
    }

    @Override
    public String toString() {
        return "Email{" +
                "messageID='" + messageID + '\'' +
                ", from='" + from + '\'' +
                ", subject='" + subject + '\'' +
                ", sentdate='" + sentdate + '\'' +
                ", news=" + news +
                '}';
    }
}
