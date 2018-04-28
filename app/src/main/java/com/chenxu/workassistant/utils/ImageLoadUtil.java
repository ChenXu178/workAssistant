package com.chenxu.workassistant.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Android on 2018/3/22.
 */

public class ImageLoadUtil {
    public static Bitmap btp;
    public static void getBitmapForImgResourse(Context mContext, int imgId, ImageView mImageView) throws IOException {
        InputStream is = mContext.getResources().openRawResource(imgId);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inSampleSize = 1;
        btp = BitmapFactory.decodeStream(is, null, options);
        mImageView.setImageBitmap(btp);
        is.close();
    }

    public static void close(){
        if (btp != null){
            btp.recycle();
        }
    }

    public static Bitmap parseFile(File file){
        btp = BitmapFactory.decodeFile(file.getAbsolutePath(),getBitmapOption(2));
        return btp;
    }

    private static BitmapFactory.Options getBitmapOption(int inSampleSize){
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }
}
