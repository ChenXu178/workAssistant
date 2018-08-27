package com.chenxu.workassistant.config;

import android.content.SharedPreferences;

/**
 * Created by Android on 2018/3/26.
 */

public class Constant {
    public static SharedPreferences spPermission = null;  //storage、camera
    public static SharedPreferences.Editor permissionEditor = null;
    public static SharedPreferences spSetting = null;
    public static SharedPreferences.Editor editorSetting = null;

    public static final String BC_LOGIN = "com.chenxu.workassistant.bclogin";
    public static final String BC_EXIT = "com.chenxu.workassistant.bcexit";

    public static final String EMAIL_SERVER_TYPE = "EMAIL:TYPE";
    public static final String EMAIL_SAVE_ACCOUNT = "EMAIL:SAVE";
    public static final String EMAIL_ACCOUNT = "EMAIL:ACCOUNT";
    public static final String EMAIL_PASSWORD = "EMAIL:PASSWORD";
    public static final String EMAIL_STATE = "EMAIL:STATE";
    public static final String EMAIL_REFRESH_TIME = "EMAIL:REFRESH_TIME";
    public static final String [] EMAIL_SERVER_IMAP = {"imap.qq.com","imap.163.com","imap.126.com","imap.sina.com","imap.sohu.com"};
    public static final String [] EMAIL_SERVER_SMTP = {"smtp.qq.com","smtp.163.com","smtp.126.com","smtp.sina.com","smtp.sohu.com"};
    public static final String [] EMAIL_SERVER_TITLE = {"QQ邮箱","网易163邮箱","网易126邮箱","新浪邮箱","搜狐邮箱"};
}
