package com.cjx.zhiai.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.cjx.zhiai.activity.ImageSelectActivity;
import com.cjx.zhiai.util.Tools;
import com.cjx.zhiai.util.UploadImageTool;

import java.io.File;

/**
 * Created by cjx on 17-3-30.
 */

public class ImageGetDialog extends ItemSelectDialog implements ItemSelectDialog.OnItemClickListener{
    private String mCurrentPhotoPath;
    private Context context;
    private int REQUEST_IMAGE_CAPTURE, RESULT_IMAGE_SELECT, REQUEST_CAMERA_PERMISSION;
    private String selectType;
    private int maxCount;
    public ImageGetDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void click(int position) {
        if (position == 0) {
            if(!checkPermission()){
                return ;
            }
            startCamera();
        } else {
            Intent intent = new Intent(context, ImageSelectActivity.class);
            if(selectType != null){
                switch (selectType) {
                    case UploadImageTool.IMAGE_TYPE_USER:
                        intent.setAction("");
                        break;
                    default:
                        intent.putExtra("maxCount", maxCount);
                        break;
                }
            }
            ((Activity)context).startActivityForResult(intent, RESULT_IMAGE_SELECT);
        }
        dismiss();
    }

    // 设置回调参数
    public void setRequestParams(int REQUEST_IMAGE_CAPTURE, int RESULT_IMAGE_SELECT, String selectType, int REQUEST_CAMERA_PERMISSION){
        this.REQUEST_IMAGE_CAPTURE = REQUEST_IMAGE_CAPTURE;
        this.RESULT_IMAGE_SELECT = RESULT_IMAGE_SELECT;
        this.selectType = selectType;
        this.REQUEST_CAMERA_PERMISSION = REQUEST_CAMERA_PERMISSION;
        setItemsByArray(new String[]{"拍照", "选择照片"}, this);
    }

    public void setMaxCount(int maxCount){
        this.maxCount = maxCount;
    }

    // 获取拍照的图片地址
    public String getCurrentPhotoPath(){
        return mCurrentPhotoPath;
    }

    // 检查是否有读写文件到sdcard
    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                explainDialog();
            } else {
                ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CAMERA_PERMISSION);
            }
            return false;
        }
        return true;
    }

    // 显示获取权限说明
    private void explainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("程序多处需要用到位置权限,是否授权？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //请求权限
                        ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CAMERA_PERMISSION);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "您不允许使用相机功能", Toast.LENGTH_SHORT).show();
                    }
                })
                .create().show();
    }

    // 调用系统相机
    public void startCamera() {
        // 调用自定义相机
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            if (mCurrentPhotoPath == null) {
                mCurrentPhotoPath = Tools.getTempPath(context) +
                        "IMG_" + System.currentTimeMillis() + ".jpg";
            }
            File file = new File(mCurrentPhotoPath);
            Uri uri;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",
                        file);
            }else{
                uri = Uri.fromFile(file);
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            ((Activity)context).startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(context, "no system camera find", Toast.LENGTH_SHORT).show();
        }
    }
}
