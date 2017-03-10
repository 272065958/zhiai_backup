package com.cjx.zhiai.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cjx.zhiai.BuildConfig;
import com.cjx.zhiai.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by cjx on 2016-11-25.
 * 工具类
 */
public class Tools {
    private static String cachPath;
    /**
     * 获取缓存文件保存的路径
     *
     * @param c
     * @return
     */
    public synchronized static String getCachPath(Context c) {
        if (cachPath == null) {
            cachPath = getDiskCacheDir(c) + "/";
        }
        File f = new File(cachPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        return cachPath;
    }

    private static String tempPath;
    public synchronized static String getTempPath(Context c){
        if(tempPath == null){
            tempPath = getCachPath(c)+"temp/";
        }
        File f = new File(tempPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        return tempPath;
    }

    private static String getDiskCacheDir(Context context) {
        if (context == null) {
            return null;
        }
        boolean hasExternal = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        File cacheDir;
        if (hasExternal) {
            cacheDir = context.getExternalCacheDir();
        } else {
            cacheDir = context.getCacheDir();
        }
        if (cacheDir == null) {
            return null;
        }
        return cacheDir.getAbsolutePath();
    }

    // 检查网络状态
    public static boolean netConnect(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) { // 打开了网络
            if (info.getState() == NetworkInfo.State.CONNECTED) { // 可以上网
                return true;
            }
        }
        return false;
    }

    /**
     * 获取压缩的属性
     *
     * @param opt
     * @return
     */
    public static BitmapFactory.Options getCompressOpt(BitmapFactory.Options opt, int orientation,
                                                       int width, int height) {
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90
                || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            if (opt.outWidth > height || opt.outHeight > width) {
                int widthRatio = opt.outWidth / height;
                int heightRatio = opt.outHeight / width;
                if (widthRatio > 1 || heightRatio > 1) {
                    if (widthRatio > heightRatio) {
                        opt.inSampleSize = widthRatio;
                    } else {
                        opt.inSampleSize = heightRatio;
                    }
                }
            }
        } else {
            if (opt.outWidth > width || opt.outHeight > height) {
                int widthRatio = opt.outWidth / width;
                int heightRatio = opt.outHeight / height;
                if (widthRatio > 1 || heightRatio > 1) {
                    if (widthRatio > heightRatio) {
                        opt.inSampleSize = widthRatio;
                    } else {
                        opt.inSampleSize = heightRatio;
                    }
                }
            }
        }
        return opt;
    }

    public static boolean compressImage(Bitmap src, String savePath, int quality) {
        boolean result = false;
        if (src != null) {
            FileOutputStream fos = null;
            try {
                File f = new File(savePath);
                fos = new FileOutputStream(f);
                result = src.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                src.recycle();
            }
        }
        return result;
    }

    public static void setImageInView(Context context, String url, ImageView iv) {
        if (TextUtils.isEmpty(url)) {
            iv.setImageResource(R.color.shadow_color);
        }else{
            Glide.with(context).load(BuildConfig.IMAGE_URI+url).error(R.color.shadow_color).into(iv);
        }
    }

    public static void setPatientState(TextView textView, String state){
        switch (state){
            case "1":
                textView.setText(R.string.patient_to_comfirm);
                break;
            case "2":
                textView.setText(R.string.patient_is_comfirm);
                break;
            case "3":
                textView.setText(R.string.patient_is_timeout);
                break;
            case "4":
                textView.setText(R.string.patient_is_cancel);
                break;
        }
    }
}
