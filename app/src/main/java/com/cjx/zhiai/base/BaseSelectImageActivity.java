package com.cjx.zhiai.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.cjx.zhiai.activity.CropImageActivity;
import com.cjx.zhiai.component.ImageInsertView;
import com.cjx.zhiai.dialog.ImageGetDialog;
import com.cjx.zhiai.util.Tools;
import com.cjx.zhiai.util.UploadImageTool;

/**
 * Created by cjx on 2016-11-29.
 * 选择拍照或者相册照片的基类
 */
public abstract class BaseSelectImageActivity extends BaseActivity implements View.OnClickListener {
    final int RESULT_IMAGE_SELECT = 102, REQUEST_IMAGE_CAPTURE = 101, REQUEST_IMAGE_CROP = 103, REQUEST_CAMERA_PERMISSION = 104;

    final int IMAGE_COUNT = 9;
    String cropPath;
    protected String selectType = UploadImageTool.IMAGE_TYPE_OTHER;
    ImageGetDialog selectDialog;
    UploadImageTool uploadTools;
    protected ImageInsertView imageInsertView;
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
                        uploadTools.upload(new String[]{selectDialog.getCurrentPhotoPath()});
                        break;
                    case UploadImageTool.IMAGE_TYPE_USER:
                        startCropIntent(selectDialog.getCurrentPhotoPath());
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
        int count = imageInsertView.getCount();
        Log.e("TAG", "current count = "+count);
        if(count >= IMAGE_COUNT){
            showToast("选择图片不能超过"+IMAGE_COUNT+"张");
            return ;
        }
        showSelectDialog(IMAGE_COUNT - count);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showToast("您不允许使用相机功能");
            } else {
                selectDialog.startCamera();
            }
        }
    }

    private void showSelectDialog(int count) {
        if (selectDialog == null) {
            selectDialog = new ImageGetDialog(this);
            selectDialog.setRequestParams(REQUEST_IMAGE_CAPTURE, RESULT_IMAGE_SELECT, selectType, REQUEST_CAMERA_PERMISSION);
            uploadTools = new UploadImageTool(this, selectType, new UploadImageTool.UploadResult() {
                @Override
                public void onResult(String string) {
                    uploadResult(string);
                }
            });
        }
        selectDialog.setMaxCount(count);
        selectDialog.show();
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
