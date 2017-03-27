package com.cjx.zhiai.util;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.http.ProgressRequestHandler;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * Created by cjx on 2016-12-25.
 */
public class UploadImageTool implements DialogInterface.OnCancelListener {
    public static final String IMAGE_TYPE_USER = "1", IMAGE_TYPE_DOCTOR = "2", IMAGE_TYPE_OTHER = "3";
    Call call;
    AsyncTask<String, Void, ArrayList<String>> task;
    BaseActivity activity;
    String selectType;
    UploadResult result;
    public UploadImageTool(BaseActivity activity, String selectType, UploadResult result) {
        this.activity = activity;
        this.selectType = selectType;
        this.result = result;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (task != null) {
            task.cancel(true);
        } else {
            if (call != null && call.isExecuted()) {
                call.cancel();
            }
        }
        Toast.makeText(activity, activity.getString(R.string.upload_cancel), Toast.LENGTH_SHORT).show();
    }

    /**
     * 上传图片
     *
     * @param photos 待上传的图片路径
     */
    public void upload(String... photos) {
        activity.showLoadDislog(this);
        task = new AsyncTask<String, Void, ArrayList<String>>() {
            @Override
            protected ArrayList<String> doInBackground(String... params) {
                ArrayList<String> images = new ArrayList<>();
                for (String p : params) {
                    String path = compressionImage(p);
                    if (path != null) {
                        images.add(path);
                    }
                }
                return images;
            }

            @Override
            protected void onPostExecute(ArrayList<String> images) {
                task = null;
                activity.dismissLoadDialog();
                MyCallbackInterface callbackInterface = new MyCallbackInterface() {

                    @Override
                    public void success(ResultBean response) {
                        activity.dismissLoadDialog();
                        Toast.makeText(activity, response.errorMsg, Toast.LENGTH_SHORT).show();
                        result.onResult(response.datas);
                    }

                    @Override
                    public void error() {
                        activity.dismissLoadDialog();
                    }
                };
                if(selectType.equals(IMAGE_TYPE_DOCTOR)){
                    call = HttpUtils.getInstance().upload(activity,
                            new ProgressRequestHandler(activity, activity.loadDialog.getTipView()),
                            callbackInterface, images, "user/uploadHeadImages");
                }else{
                    call = HttpUtils.getInstance().upload(activity,
                            new ProgressRequestHandler(activity, activity.loadDialog.getTipView()),
                            callbackInterface, images, "picture/uploadHeadImages", "imageType", selectType);
                }

            }
        };
        task.execute(photos);
    }

    final int MAX_SIZE = 1440;

    /**
     * 压缩图片
     *
     * @param path 压缩后图片的路径  当返回null是表示压缩失败
     * @return
     */
    private String compressionImage(String path) {
        String savePath = null;
        try {
            int degree = 0;
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opt);
            int bitmapWidth = opt.outWidth;
            int bitmapHeight = opt.outHeight;
            if (opt.outWidth > MAX_SIZE || opt.outHeight > MAX_SIZE) {
                float widthRatio = bitmapWidth / (float) MAX_SIZE;
                float heightRatio = bitmapHeight / (float) MAX_SIZE;
                if (widthRatio > 1 || heightRatio > 1) {
                    if (widthRatio > heightRatio) {
                        bitmapWidth = MAX_SIZE;
                        bitmapHeight = (int) (bitmapHeight / widthRatio);
                        opt.inSampleSize = widthRatio > 2 ? (int) (widthRatio - 1) : 1;
                    } else {
                        bitmapHeight = MAX_SIZE;
                        bitmapWidth = (int) (bitmapWidth / heightRatio);
                        opt.inSampleSize = heightRatio > 2 ? (int) (heightRatio - 1) : 1;
                    }
                }
            }
            opt.inJustDecodeBounds = false;
            Bitmap srcBitmap = BitmapFactory.decodeFile(path, opt);
            if (srcBitmap == null) {
                return null;
            }
            if (bitmapWidth != opt.outWidth) { // 如果原图大小不等于屏幕大小, 则重新创建于屏幕大小的图
                srcBitmap = Bitmap.createScaledBitmap(srcBitmap, bitmapWidth, bitmapHeight, true);
            }
            if (degree != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(degree);
                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
            }
            savePath = Tools.getTempPath(activity) + "IMG_" + System.currentTimeMillis() + ".jpg";
            int quality = 30 * (opt.inSampleSize + 1);
            Log.e("TAG", "quality = " + quality);
            if (Tools.compressImage(srcBitmap, savePath, quality > 100 ? 100 : quality)) {
                return savePath;
            } else {
                return null;
            }
        } catch (Error e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savePath;
    }

    public interface UploadResult{
        void onResult(String string);
    }

}
