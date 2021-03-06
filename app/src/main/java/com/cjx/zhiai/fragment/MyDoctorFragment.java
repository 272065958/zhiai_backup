package com.cjx.zhiai.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.activity.CropImageActivity;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.UserBean;
import com.cjx.zhiai.dialog.ImageGetDialog;
import com.cjx.zhiai.my.HelpActivity;
import com.cjx.zhiai.my.IncomeActivity;
import com.cjx.zhiai.my.SettingActivity;
import com.cjx.zhiai.my.UpdateDoctorInfoActivity;
import com.cjx.zhiai.util.Tools;
import com.cjx.zhiai.util.UploadImageTool;

/**
 * Created by cjx on 2016-11-26.
 * 个人中心
 */
public class MyDoctorFragment extends Fragment implements View.OnClickListener {
    final int RESULT_IMAGE_SELECT = 102, REQUEST_IMAGE_CAPTURE = 101, REQUEST_IMAGE_CROP = 103, REQUEST_CAMERA_PERMISSION = 104;

    final int RESULT_SETTING = 1;

    String cropPath;
    View view;
    ImageView headView;
    UploadImageTool uploadTools;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_my_doctor, null);
            view.findViewById(R.id.my_income).setOnClickListener(this);
            view.findViewById(R.id.my_info).setOnClickListener(this);
            view.findViewById(R.id.my_setting).setOnClickListener(this);
            view.findViewById(R.id.my_help).setOnClickListener(this);
            headView = (ImageView) view.findViewById(R.id.my_head);
            headView.setOnClickListener(this);
            updateInfo();
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case RESULT_IMAGE_SELECT:
                if (data != null) {
                    String[] photos = data.getStringArrayExtra("photo");
                    if (photos != null) {
                        startCropIntent(photos[0]);
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                startCropIntent(selectDialog.getCurrentPhotoPath());
                break;
            case REQUEST_IMAGE_CROP:
                uploadTools.upload(new String[]{cropPath});
                break;
            case RESULT_SETTING:
                Activity activity = getActivity();
                if(activity != null){
                    activity.finish();
                }

                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_income: // 我的收入
                Intent incomeIntent = new Intent(getActivity(), IncomeActivity.class);
                startActivity(incomeIntent);
                break;
            case R.id.my_info: // 个人资料
                Intent infoIntent = new Intent(getActivity(), UpdateDoctorInfoActivity.class);
                startActivity(infoIntent);
                break;
            case R.id.my_help: // 帮助与反馈
                Intent helpIntent = new Intent(getActivity(), HelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.my_setting: // 设置
                Intent settingIntent = new Intent(getActivity(), SettingActivity.class);
                startActivityForResult(settingIntent, RESULT_SETTING);
                break;
            case R.id.my_head:
                showSelectDialog();
                break;
        }
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
        cropPath = getSavePath();
        Intent i = new Intent(getActivity(), CropImageActivity.class);
        i.setAction(filePath);
        i.putExtra("degree", degree);
        i.putExtra("mAspectX", 1);
        i.putExtra("mAspectY", 1);
//        i.putExtra("outputX", outputX);
//        i.putExtra("outputY", outputY);
        i.putExtra("savePath", cropPath);
        startActivityForResult(i, REQUEST_IMAGE_CROP);
    }

    private String getSavePath() {
        return Tools.getTempPath(getActivity()) + "IMG_" + System.currentTimeMillis() + ".jpg";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                ((BaseActivity)getActivity()).showToast("您不允许使用相机功能");
            } else {
                selectDialog.startCamera();
            }
        }
    }

    ImageGetDialog selectDialog;

    private void showSelectDialog() {
        if (selectDialog == null) {
            selectDialog = new ImageGetDialog(getContext());
            selectDialog.setRequestParams(REQUEST_IMAGE_CAPTURE, RESULT_IMAGE_SELECT, UploadImageTool.IMAGE_TYPE_USER, REQUEST_CAMERA_PERMISSION);
            uploadTools = new UploadImageTool((BaseActivity) getActivity(), UploadImageTool.IMAGE_TYPE_DOCTOR, new UploadImageTool.UploadResult() {
                @Override
                public void onResult(String string) {
                    Tools.setImageInView(getActivity(), string, headView);
                    headView.setTag(R.id.update_head, headView);
                }
            });
        }
        selectDialog.show();
    }

    public void updateInfo() {
        UserBean user = MyApplication.getInstance().user;
        if (user != null) {
            Tools.setImageInView(getActivity(), user.head_image, headView);
        }
    }
}
