package com.cjx.zhiai.base;

import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Toast;

import com.cjx.zhiai.activity.CropImageActivity;
import com.cjx.zhiai.activity.ImageSelectActivity;
import com.cjx.zhiai.dialog.ItemSelectDialog;
import com.cjx.zhiai.util.Tools;
import com.cjx.zhiai.util.UploadImageTool;

import java.io.File;

/**
 * Created by cjx on 2016-11-29.
 * 选择拍照或者相册照片的基类
 */
public abstract class BaseSelectImageActivity extends BaseActivity implements View.OnClickListener, ItemSelectDialog.OnItemClickListener {
    final int RESULT_IMAGE_SELECT = 102, REQUEST_IMAGE_CAPTURE = 101, REQUEST_IMAGE_CROP = 103;

    String mCurrentPhotoPath, cropPath;
    protected String selectType = UploadImageTool.IMAGE_TYPE_OTHER;
    ItemSelectDialog selectDialog;
    UploadImageTool uploadTools;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case RESULT_IMAGE_SELECT:
                if (data != null) {
                    String[] photos = data.getStringArrayExtra("photo");
                    if (photos != null) {
                        switch (selectType) {
                            case UploadImageTool.IMAGE_TYPE_OTHER:
                                uploadTools.upload(photos);
                                break;
                            case UploadImageTool.IMAGE_TYPE_USER:
                                startCropIntent(photos[0]);
                                break;
                        }
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                switch (selectType) {
                    case UploadImageTool.IMAGE_TYPE_OTHER:
                        uploadTools.upload(new String[]{mCurrentPhotoPath});
                        break;
                    case UploadImageTool.IMAGE_TYPE_USER:
                        startCropIntent(mCurrentPhotoPath);
                        break;
                }
                break;
            case REQUEST_IMAGE_CROP:
                uploadTools.upload(new String[]{cropPath});
                break;
        }
    }

    @Override
    public void onClick(View v) {
        showSelectDialog();
    }

    private void showSelectDialog() {
        if (selectDialog == null) {
            selectDialog = new ItemSelectDialog(this);
            selectDialog.setItemsByArray(new String[]{"拍照", "选择照片"}, this);
            uploadTools = new UploadImageTool(this, selectType, new UploadImageTool.UploadResult() {
                @Override
                public void onResult(String string) {
                    uploadResult(string);
                }
            });
        }
        selectDialog.show();
    }

    @Override
    public void click(int position) {
        if (position == 0) {
            // 调用自定义相机
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                if (mCurrentPhotoPath == null) {
                    mCurrentPhotoPath = Tools.getTempPath(BaseSelectImageActivity.this) +
                            "IMG_" + System.currentTimeMillis() + ".jpg";
                }
                File file = new File(mCurrentPhotoPath);
                Uri uri;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider",
                            file);
                }else{
                    uri = Uri.fromFile(file);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(BaseSelectImageActivity.this, "no system camera find", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent intent = new Intent(this, ImageSelectActivity.class);
            switch (selectType) {
                case UploadImageTool.IMAGE_TYPE_USER:
                    intent.setAction("");
                    break;
            }
            startActivityForResult(intent, RESULT_IMAGE_SELECT);
        }
        selectDialog.dismiss();
    }

    private void startCropIntent(String filePath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        cropPath =  Tools.getTempPath(this) + "IMG_" + System.currentTimeMillis() + ".jpg";
        Intent i = new Intent(this, CropImageActivity.class);
        i.setAction(filePath);
        i.putExtra("degree", degree);
        i.putExtra("mAspectX", 1);
        i.putExtra("mAspectY", 1);
//        i.putExtra("outputX", outputX);
//        i.putExtra("outputY", outputY);
        i.putExtra("savePath", cropPath);
        startActivityForResult(i, REQUEST_IMAGE_CROP);
    }

    /**
     * 上传图片成功后回调
     *
     * @param data 图片在网络上的路径
     */
    protected abstract void uploadResult(String data);
}
