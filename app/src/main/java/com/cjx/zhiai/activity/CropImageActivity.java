package com.cjx.zhiai.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.component.CropImageView;
import com.cjx.zhiai.component.HighlightView;
import com.cjx.zhiai.util.Tools;

/**
 * Created by cjx on 2016/12/14.
 */
public class CropImageActivity extends BaseActivity {

    MyApplication app;
    Runnable mRunFaceDetection;
    CropImageView cropImageView;
    private int mAspectX, mAspectY;
    private int mOutputX, mOutputY;
    private boolean mCircleCrop = false;
    private boolean mScale;
    private Bitmap mBitmap;
    String savePath = null;
    boolean mSaving = false;
    HighlightView mCrop;
    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String filePath = intent.getAction();
        int degree = extras.getInt("degree", 0);
        int screenOrientation = getRequestedOrientation();
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opt);
        final float bWidth, bHeight, sWidth, sHeight;
        if (degree == 90 || degree == 270) {
            bWidth = opt.outHeight * 1.0f;
            bHeight = opt.outWidth * 1.0f;
        } else {
            bWidth = opt.outWidth * 1.0f;
            bHeight = opt.outHeight * 1.0f;
        }
        if(screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){ // 判断是否需要横屏
            if(bWidth > bHeight){
                // 设置为横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                return ;
            }
        }
        mAspectX = extras.getInt("mAspectX");
        mAspectY = extras.getInt("mAspectY");
        mOutputX = extras.getInt("outputX");
        mOutputY = extras.getInt("outputY");
        mScale = extras.getBoolean("scale", true);
        savePath = extras.getString("savePath");
        app = MyApplication.getInstance();
        if(screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            sWidth = app.getScreen_height()*1.0f;
            sHeight = app.getScreen_width()*1.0f;
        }else{
            sWidth = app.getScreen_width()*1.0f;
            sHeight = app.getScreen_height()*1.0f;
        }
        setContentView(R.layout.activity_cropimage);
        AsyncTask<Object, Void, Bitmap> dealTask = new AsyncTask<Object, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Object... params) {
                return dealBitmap((String)params[0], (int)params[1], (BitmapFactory.Options) params[2],
                        (float)params[3], (float)params[4], (float)params[5], (float)params[6]);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                showDealView(bitmap);
            }
        };
        dealTask.execute(filePath, degree, opt, bWidth, bHeight, sWidth, sHeight);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.save:
                save();
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }

    // 处理图片尺寸
    private Bitmap dealBitmap(String bitmapPath, int degree, BitmapFactory.Options opt, float bWidth,
                               float bHeight, float width, float height) {
        Bitmap bitmap = null;
        try {
            // 图片最后显示的宽高
            int bitmapWidth, bitmapHeight;
            float inSampleSize; // 比例最大的值

            // 图片与屏幕的比例
            float bitmapScale = bWidth / bHeight;
            float screenScale = width / height;

            if (bitmapScale == screenScale) {
                bitmapWidth = (int) width;
                bitmapHeight = (int) height;
                inSampleSize = bWidth / width;
            } else if (bitmapScale > screenScale) {
                bitmapWidth = (int) width;
                bitmapHeight = (int) (width / bitmapScale);
                inSampleSize = bWidth / width;
            } else {
                bitmapHeight = (int) height;
                bitmapWidth = (int) (height * bitmapScale);
                inSampleSize = bHeight / height;
            }
            // 图片大于屏幕尺寸两倍, 设置inSampleSize
            if (inSampleSize > 2) {
                opt.inSampleSize = (int) (inSampleSize - 1);
            }
            opt.inJustDecodeBounds = false;
            Bitmap srcBitmap = BitmapFactory.decodeFile(bitmapPath, opt);
            if (srcBitmap == null) {
                return null;
            }
            if (bWidth != width || bHeight != height) { // 如果原图大小不等于屏幕大小, 则重新创建于屏幕大小的图
                if (degree == 90 || degree == 270) {
                    bitmap = Bitmap.createScaledBitmap(srcBitmap, bitmapHeight, bitmapWidth, true);
                } else {
                    bitmap = Bitmap.createScaledBitmap(srcBitmap, bitmapWidth, bitmapHeight, true);
                }
            } else {
                bitmap = srcBitmap;
            }
            if (degree != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(degree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Toast.makeText(this, "Out of memory error", Toast.LENGTH_SHORT).show();
        }
        return bitmap;
    }

    // 显示界面
    private void showDealView(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(this, "photo not found", Toast.LENGTH_SHORT).show();
            return;
        }
        mBitmap = bitmap;
        cropImageView = (CropImageView) findViewById(R.id.crop_image);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) cropImageView.getLayoutParams();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        lp.width = width;
        lp.height = height;
        cropImageView.setVisibility(View.VISIBLE);
        findViewById(R.id.loading_view).setVisibility(View.GONE);
        findViewById(R.id.save).setVisibility(View.VISIBLE);
        cropImageView.setImageBitmapResetBase(bitmap, true);
        runFaceDetection();
    }

    private void runFaceDetection(){
        mRunFaceDetection = new Runnable() {
            Matrix mImageMatrix;

            // Create a default HightlightView if we found no face in the picture.
            private void makeDefault() {
                if (mBitmap == null) {
                    return;
                }
                HighlightView hv = new HighlightView(cropImageView);

                int width = mBitmap.getWidth();
                int height = mBitmap.getHeight();

                Rect imageRect = new Rect(0, 0, width, height);

                // CR: sentences!
                // make the default size about 4/5 of the width or height
                int cropWidth = Math.min(width, height) * 4 / 5;
                int cropHeight = cropWidth;

                if (mAspectX != 0 && mAspectY != 0) {
                    if (mAspectX > mAspectY) {
                        cropHeight = cropWidth * mAspectY / mAspectX;
                    } else {
                        cropWidth = cropHeight * mAspectX / mAspectY;
                    }
                }

                int x = (width - cropWidth) / 2;
                int y = (height - cropHeight) / 2;

                RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
                hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop, mAspectX != 0 && mAspectY != 0);
                cropImageView.setHighlightView(hv);
            }

            @Override
            public void run() {
                mImageMatrix = cropImageView.getImageMatrix();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        makeDefault();
                        mCrop = cropImageView.getHighlightView();
                        if (mCrop != null) {
                            mCrop.setFocus(true);
                        }
                    }
                });
            }
        };
        mRunFaceDetection.run();
    }

    // 保存截图
    private void save() {
        if (mSaving) {
            return;
        }
        mSaving = true;
        Rect r = mCrop.getCropRect();
        int width = r.width();
        int height = r.height();

        Bitmap crop = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(crop);
        Rect cropRect = new Rect(0, 0, width, height);
        canvas.drawBitmap(mBitmap, r, cropRect, null);

        if (mCircleCrop) {
            Canvas c = new Canvas(crop);
            Path p = new Path();
            float radius;
            if (width > height) {
                radius = height / 2F;
            } else {
                radius = width / 2F;
            }
            p.addCircle(width / 2F, height / 2F, radius, Path.Direction.CW);
            c.clipPath(p, android.graphics.Region.Op.DIFFERENCE);
            c.drawColor(0x00000000, android.graphics.PorterDuff.Mode.CLEAR);
        }
        if (mOutputX != 0 && mOutputY != 0) {
            if (mScale) {
                // Scale the image to the required dimensions.
                crop = Bitmap.createScaledBitmap(crop, mOutputX, mOutputY, true);
                // Bitmap old = crop;
                // crop = transform(new Matrix(), crop, mOutputX, mOutputY,
                // true);
                // if (old != crop) {
                // old.recycle();
                // }
            } else {

				/*
                 * Don't scale the image crop it to the size requested. Create
				 * an new image with the cropped image in the center and the
				 * extra space filled.
				 */

                // Don't scale the image but instead fill it so it's the
                // required dimension
                Bitmap b = Bitmap.createBitmap(mOutputX, mOutputY, Bitmap.Config.ARGB_8888);
                canvas = new Canvas(b);

                Rect srcRect = mCrop.getCropRect();
                Rect dstRect = new Rect(0, 0, mOutputX, mOutputY);

                int dx = (srcRect.width() - dstRect.width()) / 2;
                int dy = (srcRect.height() - dstRect.height()) / 2;

                // If the srcRect is too big, use the center part of it.
                srcRect.inset(Math.max(0, dx), Math.max(0, dy));

                // If the dstRect is too big, use the center part of it.
                dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

                // Draw the cropped bitmap in the center.
                canvas.drawBitmap(mBitmap, srcRect, dstRect, null);

                // Set the cropped bitmap as the new bitmap.
                crop.recycle();
                crop = b;
            }
        }
        if (Tools.compressImage(crop, savePath, 100)) {
            setResult(RESULT_OK);
        } else {
            crop.recycle();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        super.onDestroy();
        System.gc();
    }
}
