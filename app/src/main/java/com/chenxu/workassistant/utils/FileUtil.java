package com.chenxu.workassistant.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import com.chenxu.workassistant.R;
import com.chenxu.workassistant.config.Constant;
import com.chenxu.workassistant.fileMenage.FileBean;
import com.chenxu.workassistant.fileSearch.SearchBean;
import com.chenxu.workassistant.setting.SettingActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Android on 2018/3/26.
 */

public class FileUtil {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 格式化文件大小
     *
     * @param size 文件大小
     * @return
     */
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    public static String convertFileTime(long time){
        return dateFormat.format(new Date(time));
    }

    /**
     * 文件排序
     *
     * @param fileArrayList 文件集
     * @return
     */
    public static ArrayList<FileBean> FilesSort(ArrayList<FileBean> fileArrayList) {
        //排序文件夹
        for (int i = 0; i < fileArrayList.size() - 1; i++) {
            for (int j = i + 1; j < fileArrayList.size(); j++) {
                char char1 = fileArrayList.get(i).getFile().getName().charAt(0);
                char char2 = fileArrayList.get(j).getFile().getName().charAt(0);
                if (char1 > char2 && fileArrayList.get(i).getFile().isDirectory() && fileArrayList.get(j).getFile().isDirectory()) {
                    FileBean temp = fileArrayList.get(i);
                    fileArrayList.set(i, fileArrayList.get(j));
                    fileArrayList.set(j, temp);
                }
            }
        }
        //文件夹在前，文件在后
        for (int i = 0; i < fileArrayList.size() - 1; i++) {
            for (int j = i + 1; j < fileArrayList.size(); j++) {
                if (fileArrayList.get(i).getFile().isFile() && fileArrayList.get(j).getFile().isDirectory()) {
                    FileBean temp = fileArrayList.get(i);
                    fileArrayList.set(i, fileArrayList.get(j));
                    fileArrayList.set(j, temp);
                }
            }
        }

        for (int i = 0; i < fileArrayList.size() - 1; i++) {
            for (int j = i + 1; j < fileArrayList.size(); j++) {
                char char1 = fileArrayList.get(i).getFile().getName().charAt(0);
                char char2 = fileArrayList.get(j).getFile().getName().charAt(0);
                if (char1 > char2 && fileArrayList.get(i).getFile().isFile() && fileArrayList.get(j).getFile().isFile()) {
                    FileBean temp = fileArrayList.get(i);
                    fileArrayList.set(i, fileArrayList.get(j));
                    fileArrayList.set(j, temp);
                }
            }
        }
        return fileArrayList;
    }

    /**
     * 1文件夹、2音乐、3文件、4代码、5Excel、6图片、7PDF、8PPT、9TXT、10视频、11Word、12压缩
     *
     * @param file
     */
    public static int fileType(File file) {
        if (file.isDirectory()) {
            return 1;
        } else {
            String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            int type;
            switch (suffix) {
                case "mp3":
                    type = 2;
                    break;
                case "wav":
                    type = 2;
                    break;
                case "flac":
                    type = 2;
                    break;
                case "ape":
                    type = 2;
                    break;

                case "java":
                    type = 4;
                    break;
                case "html":
                    type = 4;
                    break;
                case "js":
                    type = 4;
                    break;
                case "css":
                    type = 4;
                    break;
                case "json":
                    type = 4;
                    break;
                case "xml":
                    type = 4;
                    break;

                case "xlsx":
                    type = 5;
                    break;
                case "xls":
                    type = 5;
                    break;

                case "png":
                    type = 6;
                    break;
                case "jpg":
                    type = 6;
                    break;
                case "gif":
                    type = 6;
                    break;
                case "bmp":
                    type = 6;
                    break;

                case "pdf":
                    type = 7;
                    break;

                case "ppt":
                    type = 8;
                    break;
                case "pptx":
                    type = 8;
                    break;

                case "txt":
                    type = 9;
                    break;

                case "mp4":
                    type = 10;
                    break;
                case "flv":
                    type = 10;
                    break;
                case "avi":
                    type = 10;
                    break;
                case "3gp":
                    type = 10;
                    break;
                case "mkv":
                    type = 10;
                    break;
                case "rmvb":
                    type = 10;
                    break;
                case "wmv":
                    type = 10;
                    break;

                case "doc":
                    type = 11;
                    break;
                case "docx":
                    type = 11;
                    break;

                case "rar":
                    type = 12;
                    break;
                case "zip":
                    type = 12;
                    break;

                default:
                    type = 3;
                    break;
            }
            return type;
        }
    }

    /**
     * 1文件夹、2音乐、3文件、4代码、5Excel、6图片、7PDF、8PPT、9TXT、10视频、11Word、12压缩
     *
     * @param filePath
     */
    public static int fileType(String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            return 1;
        } else {
            String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            int type;
            switch (suffix) {
                case "mp3":
                    type = 2;
                    break;
                case "wav":
                    type = 2;
                    break;
                case "flac":
                    type = 2;
                    break;
                case "ape":
                    type = 2;
                    break;

                case "java":
                    type = 4;
                    break;
                case "html":
                    type = 4;
                    break;
                case "js":
                    type = 4;
                    break;
                case "css":
                    type = 4;
                    break;
                case "json":
                    type = 4;
                    break;
                case "xml":
                    type = 4;
                    break;

                case "xlsx":
                    type = 5;
                    break;
                case "xls":
                    type = 5;
                    break;

                case "png":
                    type = 6;
                    break;
                case "jpg":
                    type = 6;
                    break;
                case "gif":
                    type = 6;
                    break;
                case "bmp":
                    type = 6;
                    break;

                case "pdf":
                    type = 7;
                    break;

                case "ppt":
                    type = 8;
                    break;
                case "pptx":
                    type = 8;
                    break;

                case "txt":
                    type = 9;
                    break;

                case "mp4":
                    type = 10;
                    break;
                case "flv":
                    type = 10;
                    break;
                case "avi":
                    type = 10;
                    break;
                case "3gp":
                    type = 10;
                    break;
                case "mkv":
                    type = 10;
                    break;
                case "rmvb":
                    type = 10;
                    break;
                case "wmv":
                    type = 10;
                    break;

                case "doc":
                    type = 11;
                    break;
                case "docx":
                    type = 11;
                    break;

                case "rar":
                    type = 12;
                    break;
                case "zip":
                    type = 12;
                    break;

                default:
                    type = 3;
                    break;
            }
            return type;
        }
    }

    public static int fileIconForType(File file) {
        if (file.isDirectory()) {
            return R.drawable.file_folder;
        } else {
            String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            int type;
            switch (suffix) {
                case "mp3":
                    type = R.drawable.file_audio;
                    break;
                case "wav":
                    type = R.drawable.file_audio;
                    break;
                case "flac":
                    type = R.drawable.file_audio;
                    break;
                case "ape":
                    type = R.drawable.file_audio;
                    break;

                case "java":
                    type = R.drawable.file_code;
                    break;
                case "html":
                    type = R.drawable.file_code;
                    break;
                case "js":
                    type = R.drawable.file_code;
                    break;
                case "css":
                    type = R.drawable.file_code;
                    break;
                case "json":
                    type = R.drawable.file_code;
                    break;
                case "xml":
                    type = R.drawable.file_code;
                    break;

                case "xlsx":
                    type = R.drawable.file_excel;
                    break;
                case "xls":
                    type = R.drawable.file_excel;
                    break;

                case "png":
                    type = R.drawable.file_img;
                    break;
                case "jpg":
                    type = R.drawable.file_img;
                    break;
                case "gif":
                    type = R.drawable.file_img;
                    break;
                case "bmp":
                    type = R.drawable.file_img;
                    break;

                case "pdf":
                    type = R.drawable.file_pdf;
                    break;

                case "ppt":
                    type = R.drawable.file_ppt;
                    break;
                case "pptx":
                    type = R.drawable.file_ppt;
                    break;

                case "txt":
                    type = R.drawable.file_txt;
                    break;

                case "mp4":
                    type = R.drawable.file_video;
                    break;
                case "flv":
                    type = R.drawable.file_video;
                    break;
                case "avi":
                    type = R.drawable.file_video;
                    break;
                case "3gp":
                    type = R.drawable.file_video;
                    break;
                case "mkv":
                    type = R.drawable.file_video;
                    break;
                case "rmvb":
                    type = R.drawable.file_video;
                    break;
                case "wmv":
                    type = R.drawable.file_video;
                    break;

                case "doc":
                    type = R.drawable.file_word;
                    break;
                case "docx":
                    type = R.drawable.file_word;
                    break;

                case "rar":
                    type = R.drawable.file_zip;
                    break;
                case "zip":
                    type = R.drawable.file_zip;
                    break;

                default:
                    type = R.drawable.file_blank;
                    break;
            }
            return type;
        }
    }

    public static String repeatFileName(String filename){
        String name = filename.substring(0,filename.lastIndexOf("."));
        String suffix = filename.substring(filename.lastIndexOf("."));
        return name + "(1)" + suffix;
    }

    public static int fileIconForType(String filename) {
        if (filename.indexOf(".") == -1) {
            return R.drawable.file_blank;
        } else {
            String suffix = filename.substring(filename.lastIndexOf(".") + 1);
            int type;
            switch (suffix) {
                case "mp3":
                    type = R.drawable.file_audio;
                    break;
                case "wav":
                    type = R.drawable.file_audio;
                    break;
                case "flac":
                    type = R.drawable.file_audio;
                    break;
                case "ape":
                    type = R.drawable.file_audio;
                    break;

                case "java":
                    type = R.drawable.file_code;
                    break;
                case "html":
                    type = R.drawable.file_code;
                    break;
                case "js":
                    type = R.drawable.file_code;
                    break;
                case "css":
                    type = R.drawable.file_code;
                    break;
                case "json":
                    type = R.drawable.file_code;
                    break;
                case "xml":
                    type = R.drawable.file_code;
                    break;

                case "xlsx":
                    type = R.drawable.file_excel;
                    break;
                case "xls":
                    type = R.drawable.file_excel;
                    break;

                case "png":
                    type = R.drawable.file_img;
                    break;
                case "jpg":
                    type = R.drawable.file_img;
                    break;
                case "gif":
                    type = R.drawable.file_img;
                    break;
                case "bmp":
                    type = R.drawable.file_img;
                    break;

                case "pdf":
                    type = R.drawable.file_pdf;
                    break;

                case "ppt":
                    type = R.drawable.file_ppt;
                    break;
                case "pptx":
                    type = R.drawable.file_ppt;
                    break;

                case "txt":
                    type = R.drawable.file_txt;
                    break;

                case "mp4":
                    type = R.drawable.file_video;
                    break;
                case "flv":
                    type = R.drawable.file_video;
                    break;
                case "avi":
                    type = R.drawable.file_video;
                    break;
                case "3gp":
                    type = R.drawable.file_video;
                    break;
                case "mkv":
                    type = R.drawable.file_video;
                    break;
                case "rmvb":
                    type = R.drawable.file_video;
                    break;
                case "wmv":
                    type = R.drawable.file_video;
                    break;

                case "doc":
                    type = R.drawable.file_word;
                    break;
                case "docx":
                    type = R.drawable.file_word;
                    break;

                case "rar":
                    type = R.drawable.file_zip;
                    break;
                case "zip":
                    type = R.drawable.file_zip;
                    break;

                default:
                    type = R.drawable.file_blank;
                    break;
            }
            return type;
        }
    }


    /**
     * 删除文件
     *
     * @param file 文件
     */
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }


    public static File copyFile(File oldFile,File newPath){
        if (oldFile.isDirectory()){//是目录
            File file = new File(newPath.getAbsolutePath()+"/"+oldFile.getName());
            file.mkdirs();
            File[] files = oldFile.listFiles();
            for (File item: files){
                copyFile(item,file);
            }
            return file;
        }else { //是文件
            return copy(oldFile.getAbsolutePath(),newPath.getAbsolutePath()+"/"+oldFile.getName());
        }
    }

    public static File copy (String start,String end){
        File staFile = new File(start);
        File endFile = new File(end);
        try {
            InputStream fosfrom = new FileInputStream(staFile);
            OutputStream fosto = new FileOutputStream(endFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosto.flush();
            fosfrom.close();
            fosto.close();
        }catch (Exception e){

        }
        return endFile;
    }


    /**
     * 递归查找文件
     * @param baseDirName  查找的文件夹路径
     * @param targetFileName  需要查找的文件名
     * @param fileList  查找到的文件集合
     */
    public synchronized static void findFiles(String baseDirName, String targetFileName,boolean filter, List<SearchBean> fileList) {

        File baseDir = new File(baseDirName);       // 创建一个File对象
        String tempName = null;
        //判断目录是否存在
        File tempFile;
        File[] files = baseDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            tempFile = files[i];
            tempName = tempFile.getName();
            if(tempFile.isDirectory()){
                if (tempName.startsWith(".")){
                    continue;
                }
                if (filter){
                    if (tempName.indexOf("Android") != -1 ||tempName.indexOf("cache") != -1||tempName.indexOf("Cache") != -1||tempName.indexOf("temp") != -1||tempName.indexOf("Temp") != -1||tempName.indexOf("tencent") != -1||tempName.indexOf("Tencent") != -1){
                        continue;
                    }else {
                        findFiles(tempFile.getAbsolutePath(), targetFileName,filter, fileList);
                    }
                }else {
                    findFiles(tempFile.getAbsolutePath(), targetFileName,filter, fileList);
                }
            }else if(tempFile.isFile()){
                if(tempName.indexOf(targetFileName) != -1){
                    // 匹配成功，将文件名添加到结果集
                    fileList.add(new SearchBean(tempFile,i));
                    Log.e("findFile:",tempName);
                }
            }
        }
    }

    /**
     * 递归查找文件
     * @param baseDirName  查找的文件夹路径
     * @param type  需要查找的文件类型
     * @param fileList  查找到的文件集合
     */
    public synchronized static void findFilesBySuffix(String baseDirName,int type,boolean filter, List<SearchBean> fileList) {

        File baseDir = new File(baseDirName);       // 创建一个File对象
        String tempName = null;
        //判断目录是否存在
        File tempFile;
        File[] files = baseDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            tempFile = files[i];
            tempName = tempFile.getName();
            if(tempFile.isDirectory()){
                if (tempName.startsWith(".")){
                    continue;
                }
                if (filter){
                    if (tempName.indexOf("Android") != -1 ||tempName.indexOf("cache") != -1||tempName.indexOf("Cache") != -1||tempName.indexOf("temp") != -1||tempName.indexOf("Temp") != -1||tempName.indexOf("tencent") != -1||tempName.indexOf("Tencent") != -1){
                        continue;
                    }else {
                        findFilesBySuffix(tempFile.getAbsolutePath(), type,filter, fileList);
                    }
                }else {
                    findFilesBySuffix(tempFile.getAbsolutePath(), type,filter, fileList);
                }
            }else if(tempFile.isFile()){
                switch (type){
                    case 1:
                        if(tempName.endsWith(".doc")||tempName.endsWith(".docx")){
                            // 匹配成功，将文件名添加到结果集
                            fileList.add(new SearchBean(tempFile,i));
                        }
                        break;
                    case 2:
                        if(tempName.endsWith(".xls")||tempName.endsWith(".xlsx")){
                            // 匹配成功，将文件名添加到结果集
                            fileList.add(new SearchBean(tempFile,i));
                        }
                        break;
                    case 3:
                        if(tempName.endsWith(".ppt")||tempName.endsWith(".pptx")){
                            // 匹配成功，将文件名添加到结果集
                            fileList.add(new SearchBean(tempFile,i));
                        }
                        break;
                }
            }
        }
    }

    // 根据文件后缀名获得对应的MIME类型。
    public static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

//    /**
//     * 复制整个文件夹内容
//     *
//     * @param oldPath String 原文件路径 如：c:/fqf
//     * @param newPath String 复制后路径 如：f:/fqf/ff
//     * @return boolean
//     */
//    public void copyFolder(File oldFile, String newPath) {
//
//        try {
//            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
//            String[] file = oldFile.list();
//            File temp = null;
//            for (int i = 0; i < file.length; i++) {
//                if (oldFile.endsWith(File.separator)) {
//                    temp = new File(oldPath + file[i]);
//                } else {
//                    temp = new File(oldPath + File.separator + file[i]);
//                }
//
//                if (temp.isFile()) {
//                    FileInputStream input = new FileInputStream(temp);
//                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
//                    byte[] b = new byte[1024 * 5];
//                    int len;
//                    while ((len = input.read(b)) != -1) {
//                        output.write(b, 0, len);
//                    }
//                    output.flush();
//                    output.close();
//                    input.close();
//                }
//                if (temp.isDirectory()) {//如果是子文件夹
//                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
//                }
//            }
//        } catch (Exception e) {
//            Log.e("move", "复制整个文件夹内容操作出错");
//            e.printStackTrace();
//
//        }

    //}

    /**
     * 3.质量压缩
     * 设置bitmap options属性，降低图片的质量，像素不会减少
     * 第一个参数为需要压缩的bitmap图片对象，第二个参数为压缩后图片保存的位置
     * 设置options 属性0-100，来实现压缩
     *
     * @param bmp
     * @param file
     */
    public static void qualityCompress(Bitmap bmp, File file) {
        // 0-100 100为不压缩
        int quality = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openFiles(Context context,String filesPath) {
        Uri uri = Uri.parse("file://" + filesPath);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        String type = getMIMEType(filesPath);
        intent.setDataAndType(uri, type);
        if (!type.equals("*/*")) {
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                context.startActivity(showOpenTypeDialog(filesPath));
            }
        } else {
            context.startActivity(showOpenTypeDialog(filesPath));
        }
    }

    //显示打开方式
    public static void show(Context context,String filesPath){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        context.startActivity(showOpenTypeDialog(filesPath));
    }

    public static Intent showOpenTypeDialog(String param) {
        Log.e("ViChildError", "showOpenTypeDialog");
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    /**
     * --获取文件类型 --
     */
    public static String getMIMEType(String filePath) {
        String type = "*/*";
        String fName = filePath;

        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }

        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") {
            return type;
        }

        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0])) {
                type = MIME_MapTable[i][1];
            }
        }
        return type;
    }

    /**
     * -- MIME 列表 --
     */
    public static final String[][] MIME_MapTable =
            {
                    // --{后缀名， MIME类型}   --
                    {".3gp", "video/3gpp"},
                    {".3gpp", "video/3gpp"},
                    {".aac", "audio/x-mpeg"},
                    {".amr", "audio/x-mpeg"},
                    {".apk", "application/vnd.android.package-archive"},
                    {".avi", "video/x-msvideo"},
                    {".aab", "application/x-authoware-bin"},
                    {".aam", "application/x-authoware-map"},
                    {".aas", "application/x-authoware-seg"},
                    {".ai", "application/postscript"},
                    {".aif", "audio/x-aiff"},
                    {".aifc", "audio/x-aiff"},
                    {".aiff", "audio/x-aiff"},
                    {".als", "audio/x-alpha5"},
                    {".amc", "application/x-mpeg"},
                    {".ani", "application/octet-stream"},
                    {".asc", "text/plain"},
                    {".asd", "application/astound"},
                    {".asf", "video/x-ms-asf"},
                    {".asn", "application/astound"},
                    {".asp", "application/x-asap"},
                    {".asx", " video/x-ms-asf"},
                    {".au", "audio/basic"},
                    {".avb", "application/octet-stream"},
                    {".awb", "audio/amr-wb"},
                    {".bcpio", "application/x-bcpio"},
                    {".bld", "application/bld"},
                    {".bld2", "application/bld2"},
                    {".bpk", "application/octet-stream"},
                    {".bz2", "application/x-bzip2"},
                    {".bin", "application/octet-stream"},
                    {".bmp", "image/bmp"},
                    {".c", "text/plain"},
                    {".class", "application/octet-stream"},
                    {".conf", "text/plain"},
                    {".cpp", "text/plain"},
                    {".cal", "image/x-cals"},
                    {".ccn", "application/x-cnc"},
                    {".cco", "application/x-cocoa"},
                    {".cdf", "application/x-netcdf"},
                    {".cgi", "magnus-internal/cgi"},
                    {".chat", "application/x-chat"},
                    {".clp", "application/x-msclip"},
                    {".cmx", "application/x-cmx"},
                    {".co", "application/x-cult3d-object"},
                    {".cod", "image/cis-cod"},
                    {".cpio", "application/x-cpio"},
                    {".cpt", "application/mac-compactpro"},
                    {".crd", "application/x-mscardfile"},
                    {".csh", "application/x-csh"},
                    {".csm", "chemical/x-csml"},
                    {".csml", "chemical/x-csml"},
                    {".css", "text/css"},
                    {".cur", "application/octet-stream"},
                    {".doc", "application/msword"},
                    {".dcm", "x-lml/x-evm"},
                    {".dcr", "application/x-director"},
                    {".dcx", "image/x-dcx"},
                    {".dhtml", "text/html"},
                    {".dir", "application/x-director"},
                    {".dll", "application/octet-stream"},
                    {".dmg", "application/octet-stream"},
                    {".dms", "application/octet-stream"},
                    {".dot", "application/x-dot"},
                    {".dvi", "application/x-dvi"},
                    {".dwf", "drawing/x-dwf"},
                    {".dwg", "application/x-autocad"},
                    {".dxf", "application/x-autocad"},
                    {".dxr", "application/x-director"},
                    {".ebk", "application/x-expandedbook"},
                    {".emb", "chemical/x-embl-dl-nucleotide"},
                    {".embl", "chemical/x-embl-dl-nucleotide"},
                    {".eps", "application/postscript"},
                    {".epub", "application/epub+zip"},
                    {".eri", "image/x-eri"},
                    {".es", "audio/echospeech"},
                    {".esl", "audio/echospeech"},
                    {".etc", "application/x-earthtime"},
                    {".etx", "text/x-setext"},
                    {".evm", "x-lml/x-evm"},
                    {".evy", "application/x-envoy"},
                    {".exe", "application/octet-stream"},
                    {".fh4", "image/x-freehand"},
                    {".fh5", "image/x-freehand"},
                    {".fhc", "image/x-freehand"},
                    {".fif", "image/fif"},
                    {".fm", "application/x-maker"},
                    {".fpx", "image/x-fpx"},
                    {".fvi", "video/isivideo"},
                    {".flv", "video/x-msvideo"},
                    {".gau", "chemical/x-gaussian-input"},
                    {".gca", "application/x-gca-compressed"},
                    {".gdb", "x-lml/x-gdb"},
                    {".gif", "image/gif"},
                    {".gps", "application/x-gps"},
                    {".gtar", "application/x-gtar"},
                    {".gz", "application/x-gzip"},
                    {".gif", "image/gif"},
                    {".gtar", "application/x-gtar"},
                    {".gz", "application/x-gzip"},
                    {".h", "text/plain"},
                    {".hdf", "application/x-hdf"},
                    {".hdm", "text/x-hdml"},
                    {".hdml", "text/x-hdml"},
                    {".htm", "text/html"},
                    {".html", "text/html"},
                    {".hlp", "application/winhlp"},
                    {".hqx", "application/mac-binhex40"},
                    {".hts", "text/html"},
                    {".ice", "x-conference/x-cooltalk"},
                    {".ico", "application/octet-stream"},
                    {".ief", "image/ief"},
                    {".ifm", "image/gif"},
                    {".ifs", "image/ifs"},
                    {".imy", "audio/melody"},
                    {".ins", "application/x-net-install"},
                    {".ips", "application/x-ipscript"},
                    {".ipx", "application/x-ipix"},
                    {".it", "audio/x-mod"},
                    {".itz", "audio/x-mod"},
                    {".ivr", "i-world/i-vrml"},
                    {".j2k", "image/j2k"},
                    {".jad", "text/vnd.sun.j2me.app-descriptor"},
                    {".jam", "application/x-jam"},
                    {".jnlp", "application/x-java-jnlp-file"},
                    {".jpe", "image/jpeg"},
                    {".jpz", "image/jpeg"},
                    {".jwc", "application/jwc"},
                    {".jar", "application/java-archive"},
                    {".java", "text/plain"},
                    {".jpeg", "image/jpeg"},
                    {".jpg", "image/jpeg"},
                    {".js", "application/x-javascript"},
                    {".kjx", "application/x-kjx"},
                    {".lak", "x-lml/x-lak"},
                    {".latex", "application/x-latex"},
                    {".lcc", "application/fastman"},
                    {".lcl", "application/x-digitalloca"},
                    {".lcr", "application/x-digitalloca"},
                    {".lgh", "application/lgh"},
                    {".lha", "application/octet-stream"},
                    {".lml", "x-lml/x-lml"},
                    {".lmlpack", "x-lml/x-lmlpack"},
                    {".log", "text/plain"},
                    {".lsf", "video/x-ms-asf"},
                    {".lsx", "video/x-ms-asf"},
                    {".lzh", "application/x-lzh "},
                    {".m13", "application/x-msmediaview"},
                    {".m14", "application/x-msmediaview"},
                    {".m15", "audio/x-mod"},
                    {".m3u", "audio/x-mpegurl"},
                    {".m3url", "audio/x-mpegurl"},
                    {".ma1", "audio/ma1"},
                    {".ma2", "audio/ma2"},
                    {".ma3", "audio/ma3"},
                    {".ma5", "audio/ma5"},
                    {".man", "application/x-troff-man"},
                    {".map", "magnus-internal/imagemap"},
                    {".mbd", "application/mbedlet"},
                    {".mct", "application/x-mascot"},
                    {".mdb", "application/x-msaccess"},
                    {".mdz", "audio/x-mod"},
                    {".me", "application/x-troff-me"},
                    {".mel", "text/x-vmel"},
                    {".mi", "application/x-mif"},
                    {".mid", "audio/midi"},
                    {".midi", "audio/midi"},
                    {".m4a", "audio/mp4a-latm"},
                    {".m4b", "audio/mp4a-latm"},
                    {".m4p", "audio/mp4a-latm"},
                    {".m4u", "video/vnd.mpegurl"},
                    {".m4v", "video/x-m4v"},
                    {".mov", "video/quicktime"},
                    {".mp2", "audio/x-mpeg"},
                    {".mp3", "audio/x-mpeg"},
                    {".mp4", "video/mp4"},
                    {".mpc", "application/vnd.mpohun.certificate"},
                    {".mpe", "video/mpeg"},
                    {".mpeg", "video/mpeg"},
                    {".mpg", "video/mpeg"},
                    {".mpg4", "video/mp4"},
                    {".mpga", "audio/mpeg"},
                    {".msg", "application/vnd.ms-outlook"},
                    {".mif", "application/x-mif"},
                    {".mil", "image/x-cals"},
                    {".mio", "audio/x-mio"},
                    {".mmf", "application/x-skt-lbs"},
                    {".mng", "video/x-mng"},
                    {".mny", "application/x-msmoney"},
                    {".moc", "application/x-mocha"},
                    {".mocha", "application/x-mocha"},
                    {".mod", "audio/x-mod"},
                    {".mof", "application/x-yumekara"},
                    {".mol", "chemical/x-mdl-molfile"},
                    {".mop", "chemical/x-mopac-input"},
                    {".movie", "video/x-sgi-movie"},
                    {".mpn", "application/vnd.mophun.application"},
                    {".mpp", "application/vnd.ms-project"},
                    {".mps", "application/x-mapserver"},
                    {".mrl", "text/x-mrml"},
                    {".mrm", "application/x-mrm"},
                    {".ms", "application/x-troff-ms"},
                    {".mts", "application/metastream"},
                    {".mtx", "application/metastream"},
                    {".mtz", "application/metastream"},
                    {".mzv", "application/metastream"},
                    {".nar", "application/zip"},
                    {".nbmp", "image/nbmp"},
                    {".nc", "application/x-netcdf"},
                    {".ndb", "x-lml/x-ndb"},
                    {".ndwn", "application/ndwn"},
                    {".nif", "application/x-nif"},
                    {".nmz", "application/x-scream"},
                    {".nokia-op-logo", "image/vnd.nok-oplogo-color"},
                    {".npx", "application/x-netfpx"},
                    {".nsnd", "audio/nsnd"},
                    {".nva", "application/x-neva1"},
                    {".oda", "application/oda"},
                    {".oom", "application/x-atlasMate-plugin"},
                    {".ogg", "audio/ogg"},
                    {".pac", "audio/x-pac"},
                    {".pae", "audio/x-epac"},
                    {".pan", "application/x-pan"},
                    {".pbm", "image/x-portable-bitmap"},
                    {".pcx", "image/x-pcx"},
                    {".pda", "image/x-pda"},
                    {".pdb", "chemical/x-pdb"},
                    {".pdf", "application/pdf"},
                    {".pfr", "application/font-tdpfr"},
                    {".pgm", "image/x-portable-graymap"},
                    {".pict", "image/x-pict"},
                    {".pm", "application/x-perl"},
                    {".pmd", "application/x-pmd"},
                    {".png", "image/png"},
                    {".pnm", "image/x-portable-anymap"},
                    {".pnz", "image/png"},
                    {".pot", "application/vnd.ms-powerpoint"},
                    {".ppm", "image/x-portable-pixmap"},
                    {".pps", "application/vnd.ms-powerpoint"},
                    {".ppt", "application/vnd.ms-powerpoint"},
                    {".pqf", "application/x-cprplayer"},
                    {".pqi", "application/cprplayer"},
                    {".prc", "application/x-prc"},
                    {".proxy", "application/x-ns-proxy-autoconfig"},
                    {".prop", "text/plain"},
                    {".ps", "application/postscript"},
                    {".ptlk", "application/listenup"},
                    {".pub", "application/x-mspublisher"},
                    {".pvx", "video/x-pv-pvx"},
                    {".qcp", "audio/vnd.qcelp"},
                    {".qt", "video/quicktime"},
                    {".qti", "image/x-quicktime"},
                    {".qtif", "image/x-quicktime"},
                    {".r3t", "text/vnd.rn-realtext3d"},
                    {".ra", "audio/x-pn-realaudio"},
                    {".ram", "audio/x-pn-realaudio"},
                    {".ras", "image/x-cmu-raster"},
                    {".rdf", "application/rdf+xml"},
                    {".rf", "image/vnd.rn-realflash"},
                    {".rgb", "image/x-rgb"},
                    {".rlf", "application/x-richlink"},
                    {".rm", "audio/x-pn-realaudio"},
                    {".rmf", "audio/x-rmf"},
                    {".rmm", "audio/x-pn-realaudio"},
                    {".rnx", "application/vnd.rn-realplayer"},
                    {".roff", "application/x-troff"},
                    {".rp", "image/vnd.rn-realpix"},
                    {".rpm", "audio/x-pn-realaudio-plugin"},
                    {".rt", "text/vnd.rn-realtext"},
                    {".rte", "x-lml/x-gps"},
                    {".rtf", "application/rtf"},
                    {".rtg", "application/metastream"},
                    {".rtx", "text/richtext"},
                    {".rv", "video/vnd.rn-realvideo"},
                    {".rwc", "application/x-rogerwilco"},
                    {".rar", "application/x-rar-compressed"},
                    {".rc", "text/plain"},
                    {".rmvb", "audio/x-pn-realaudio"},
                    {".s3m", "audio/x-mod"},
                    {".s3z", "audio/x-mod"},
                    {".sca", "application/x-supercard"},
                    {".scd", "application/x-msschedule"},
                    {".sdf", "application/e-score"},
                    {".sea", "application/x-stuffit"},
                    {".sgm", "text/x-sgml"},
                    {".sgml", "text/x-sgml"},
                    {".shar", "application/x-shar"},
                    {".shtml", "magnus-internal/parsed-html"},
                    {".shw", "application/presentations"},
                    {".si6", "image/si6"},
                    {".si7", "image/vnd.stiwap.sis"},
                    {".si9", "image/vnd.lgtwap.sis"},
                    {".sis", "application/vnd.symbian.install"},
                    {".sit", "application/x-stuffit"},
                    {".skd", "application/x-koan"},
                    {".skm", "application/x-koan"},
                    {".skp", "application/x-koan"},
                    {".skt", "application/x-koan"},
                    {".slc", "application/x-salsa"},
                    {".smd", "audio/x-smd"},
                    {".smi", "application/smil"},
                    {".smil", "application/smil"},
                    {".smp", "application/studiom"},
                    {".smz", "audio/x-smd"},
                    {".sh", "application/x-sh"},
                    {".snd", "audio/basic"},
                    {".spc", "text/x-speech"},
                    {".spl", "application/futuresplash"},
                    {".spr", "application/x-sprite"},
                    {".sprite", "application/x-sprite"},
                    {".sdp", "application/sdp"},
                    {".spt", "application/x-spt"},
                    {".src", "application/x-wais-source"},
                    {".stk", "application/hyperstudio"},
                    {".stm", "audio/x-mod"},
                    {".sv4cpio", "application/x-sv4cpio"},
                    {".sv4crc", "application/x-sv4crc"},
                    {".svf", "image/vnd"},
                    {".svg", "image/svg-xml"},
                    {".svh", "image/svh"},
                    {".svr", "x-world/x-svr"},
                    {".swf", "application/x-shockwave-flash"},
                    {".swfl", "application/x-shockwave-flash"},
                    {".t", "application/x-troff"},
                    {".tad", "application/octet-stream"},
                    {".talk", "text/x-speech"},
                    {".tar", "application/x-tar"},
                    {".taz", "application/x-tar"},
                    {".tbp", "application/x-timbuktu"},
                    {".tbt", "application/x-timbuktu"},
                    {".tcl", "application/x-tcl"},
                    {".tex", "application/x-tex"},
                    {".texi", "application/x-texinfo"},
                    {".texinfo", "application/x-texinfo"},
                    {".tgz", "application/x-tar"},
                    {".thm", "application/vnd.eri.thm"},
                    {".tif", "image/tiff"},
                    {".tiff", "image/tiff"},
                    {".tki", "application/x-tkined"},
                    {".tkined", "application/x-tkined"},
                    {".toc", "application/toc"},
                    {".toy", "image/toy"},
                    {".tr", "application/x-troff"},
                    {".trk", "x-lml/x-gps"},
                    {".trm", "application/x-msterminal"},
                    {".tsi", "audio/tsplayer"},
                    {".tsp", "application/dsptype"},
                    {".tsv", "text/tab-separated-values"},
                    {".ttf", "application/octet-stream"},
                    {".ttz", "application/t-time"},
                    {".txt", "text/plain"},
                    {".ult", "audio/x-mod"},
                    {".ustar", "application/x-ustar"},
                    {".uu", "application/x-uuencode"},
                    {".uue", "application/x-uuencode"},
                    {".vcd", "application/x-cdlink"},
                    {".vcf", "text/x-vcard"},
                    {".vdo", "video/vdo"},
                    {".vib", "audio/vib"},
                    {".viv", "video/vivo"},
                    {".vivo", "video/vivo"},
                    {".vmd", "application/vocaltec-media-desc"},
                    {".vmf", "application/vocaltec-media-file"},
                    {".vmi", "application/x-dreamcast-vms-info"},
                    {".vms", "application/x-dreamcast-vms"},
                    {".vox", "audio/voxware"},
                    {".vqe", "audio/x-twinvq-plugin"},
                    {".vqf", "audio/x-twinvq"},
                    {".vql", "audio/x-twinvq"},
                    {".vre", "x-world/x-vream"},
                    {".vrml", "x-world/x-vrml"},
                    {".vrt", "x-world/x-vrt"},
                    {".vrw", "x-world/x-vream"},
                    {".vts", "workbook/formulaone"},
                    {".wax", "audio/x-ms-wax"},
                    {".wbmp", "image/vnd.wap.wbmp"},
                    {".web", "application/vnd.xara"},
                    {".wav", "audio/x-wav"},
                    {".wma", "audio/x-ms-wma"},
                    {".wmv", "audio/x-ms-wmv"},
                    {".wi", "image/wavelet"},
                    {".wis", "application/x-InstallShield"},
                    {".wm", "video/x-ms-wm"},
                    {".wmd", "application/x-ms-wmd"},
                    {".wmf", "application/x-msmetafile"},
                    {".wml", "text/vnd.wap.wml"},
                    {".wmlc", "application/vnd.wap.wmlc"},
                    {".wmls", "text/vnd.wap.wmlscript"},
                    {".wmlsc", "application/vnd.wap.wmlscriptc"},
                    {".wmlscript", "text/vnd.wap.wmlscript"},
                    {".wmv", "video/x-ms-wmv"},
                    {".wmx", "video/x-ms-wmx"},
                    {".wmz", "application/x-ms-wmz"},
                    {".wpng", "image/x-up-wpng"},
                    {".wps", "application/vnd.ms-works"},
                    {".wpt", "x-lml/x-gps"},
                    {".wri", "application/x-mswrite"},
                    {".wrl", "x-world/x-vrml"},
                    {".wrz", "x-world/x-vrml"},
                    {".ws", "text/vnd.wap.wmlscript"},
                    {".wsc", "application/vnd.wap.wmlscriptc"},
                    {".wv", "video/wavelet"},
                    {".wvx", "video/x-ms-wvx"},
                    {".wxl", "application/x-wxl"},
                    {".x-gzip", "application/x-gzip"},
                    {".xar", "application/vnd.xara"},
                    {".xbm", "image/x-xbitmap"},
                    {".xdm", "application/x-xdma"},
                    {".xdma", "application/x-xdma"},
                    {".xdw", "application/vnd.fujixerox.docuworks"},
                    {".xht", "application/xhtml+xml"},
                    {".xhtm", "application/xhtml+xml"},
                    {".xhtml", "application/xhtml+xml"},
                    {".xla", "application/vnd.ms-excel"},
                    {".xlc", "application/vnd.ms-excel"},
                    {".xll", "application/x-excel"},
                    {".xlm", "application/vnd.ms-excel"},
                    {".xls", "application/vnd.ms-excel"},
                    {".xlt", "application/vnd.ms-excel"},
                    {".xlw", "application/vnd.ms-excel"},
                    {".xm", "audio/x-mod"},
                    {".xml", "text/xml"},
                    {".xmz", "audio/x-mod"},
                    {".xpi", "application/x-xpinstall"},
                    {".xpm", "image/x-xpixmap"},
                    {".xsit", "text/xml"},
                    {".xsl", "text/xml"},
                    {".xul", "text/xul"},
                    {".xwd", "image/x-xwindowdump"},
                    {".xyz", "chemical/x-pdb"},
                    {".yz1", "application/x-yz1"},
                    {".z", "application/x-compress"},
                    {".zac", "application/x-zaurus-zac"},
                    {".zip", "application/zip"},
                    {"", "*/*"}
            };

}
