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
    public static String BASE_SERVER = "https://aip.baidubce.com";
}
