package com.cjx.zhiai.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.FileBean;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by cjx  选择手机图片界面
 */
public class ImageSelectActivity extends BaseActivity implements OnClickListener {
    final int REQUEST_STORE_PERMISSION = 8;

    List<FileBean> folderList;
    GridView folderGrid, imageGrid;
    TextView uploadTv;
    SelectFolderAdapter folderAdapter;
    SelectImageAdapter imageAdapter;
    Dialog loadingDialog;

    int screenWidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        setToolBar(true, new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        }, -1);

        if(checkPermission()){
            initView();
        }

    }

    @Override
    public void onBackPressed() {
        if (folderGrid.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else {
            showFolderView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (folderAdapter != null) {
            int c = folderGrid.getChildCount();
            for (int i = 0; i < c; i++) {
                folderAdapter.recycle(folderGrid.getChildAt(i));
            }
            folderGrid.setAdapter(null);
        }
        if (imageAdapter != null) {
            int c = imageGrid.getChildCount();
            for (int i = 0; i < c; i++) {
                imageAdapter.recycle(imageGrid.getChildAt(i));
            }
            imageGrid.setAdapter(null);
            imageAdapter.destroy();
        }
        System.gc();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_select_tip:
                if (imageAdapter != null) {
                    HashSet<String> select = imageAdapter.getSelects();
                    int size = select == null ? 0 : select.size();
                    if (size == 0) {
                        Toast.makeText(this, "请选择至少一张照片", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        int index = 0;
                        Iterator<String> it = select.iterator();
                        String[] photo = new String[size];
                        while (index < size) {
                            photo[index++] = it.next();
                        }
                        Intent data = new Intent();
                        data.putExtra("photo", photo);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showToast("您将无法正常使用图片缓存功能");
            } else {
                initView();
            }
        }
    }

    private void initView(){
        // 一个显示文件夹的gridview 一个显示图片列表的gridview
        folderGrid = (GridView) findViewById(R.id.photo_select_folder);
        imageGrid = (GridView) findViewById(R.id.photo_select_image);
        imageGrid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (uploadTv != null) {
                    uploadTv.setText(String.format(getString(R.string.select_photo_upload),
                            imageAdapter.clickAt(view, position)));
                } else {
                    String path = (String) view.getTag(R.id.photo_select_image);
                    Intent data = new Intent();
                    data.putExtra("photo", new String[]{path});
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });

        String action = getIntent().getAction();
        if (action == null) { // 这里控制是否多选图片  没有action表示可以多选
            uploadTv = (TextView) findViewById(R.id.photo_select_tip);
            uploadTv.setOnClickListener(this);
            uploadTv.setVisibility(View.VISIBLE);
            uploadTv.setText(String.format(getString(R.string.select_photo_upload), 0));
        }
        screenWidth = (MyApplication.getInstance()).getScreen_width();

        loadImage();
    }

    // 检查是否有读写文件到sdcard
    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                explainDialog();
            } else {
                ActivityCompat.requestPermissions(ImageSelectActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORE_PERMISSION);
            }
            return false;
        }
        return true;
    }

    // 显示获取权限说明
    private void explainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("此功能需要获取读取手机内存,是否授权？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //请求权限
                        ActivityCompat.requestPermissions(ImageSelectActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_STORE_PERMISSION);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showToast("您不允许使用此功能");
                        finish();
                    }
                })
                .create().show();
    }

    private void showFolderView() {
        setToolbarTitle("选择手机照片");
        if (folderAdapter == null) {
            int padding = getResources().getDimensionPixelOffset(R.dimen.text_margin);
            int size = (screenWidth - padding * 3) / 2;
            folderAdapter = new SelectFolderAdapter(ImageSelectActivity.this, folderList,
                    size, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    FileBean dir = (FileBean) v.getTag(R.id.images_view);
                    showImageView(dir.getChildImg(), dir.getName(), dir.getPath());
                }
            });
            folderGrid.setAdapter(folderAdapter);
        } else {
            if (imageGrid.getAdapter() != null) {
                imageGrid.setAdapter(null);
            }
            imageGrid.setVisibility(View.GONE);
            folderGrid.setVisibility(View.VISIBLE);
            if (folderGrid.getAdapter() == null) {
                folderGrid.setAdapter(folderAdapter);
            }
        }
    }

    private void showImageView(String[] images, String dirName, String dir) {
        if (folderAdapter != null) {
            folderGrid.setAdapter(null);
        }
        folderGrid.setVisibility(View.GONE);
        imageGrid.setVisibility(View.VISIBLE);
        setToolbarTitle(dirName);
        if (imageAdapter == null) {
            int padding = getResources().getDimensionPixelOffset(R.dimen.text_margin);
            int height = (int) ((screenWidth - 4 * padding) / 3f);
            imageAdapter = new SelectImageAdapter(dir, images, ImageSelectActivity.this,
                    height, uploadTv != null);
        } else {
            imageAdapter.notifyDataSetChanged(dir, images);
        }
        imageGrid.setAdapter(imageAdapter);
    }

    private void showLoadDialog() {
        if (loadingDialog == null) {
            loadingDialog = new Dialog(this, R.style.loading_dialog);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setContentView(R.layout.loading_view);
        }
        loadingDialog.show();
    }

    private void loadImage() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadDialog();
        new Thread() {
            @Override
            public void run() {
                folderList = new ArrayList<>();
                HashSet<String> mDirPaths = new HashSet<>();
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = ImageSelectActivity.this
                        .getContentResolver();
                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaColumns.MIME_TYPE + "=? or "
                                + MediaColumns.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaColumns.DATE_MODIFIED);
                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        // 获取图片的路径
                        String path = mCursor.getString(mCursor
                                .getColumnIndex(MediaColumns.DATA));
                        // 获取该图片的父路径名
                        File parentFile = new File(path).getParentFile();
                        if (parentFile == null)
                            continue;
                        String dirPath = parentFile.getAbsolutePath();
                        FileBean folder;
                        // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                        if (mDirPaths.contains(dirPath)) {
                            continue;
                        } else {
                            mDirPaths.add(dirPath);
                            // 初始化imageFloder
                            folder = new FileBean();
                            folder.setPath(dirPath);
                            folder.setFirstimage(path);
                            String[] child = parentFile.list(new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String filename) {
                                    return filename.endsWith(".jpg")
                                            || filename.endsWith(".png")
                                            || filename.endsWith(".jpeg");
                                }
                            });
                            folder.setChildImg(child);
                            folder.setName(parentFile.getName());
                            folderList.add(folder);
                        }
                    }
                    mCursor.close();
                    // 扫描完成，辅助的HashSet也就可以释放内存了
                    mDirPaths.clear();
                    mDirPaths = null;
                }
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        showFolderView();
                    }
                });
            }
        }.start();
    }

    class SelectFolderAdapter extends BaseAdapter {

        List<FileBean> list;
        int imagesize = 0;
        int count = 0;
        OnClickListener listener;
        Activity context;
        int padding = 0;

        public SelectFolderAdapter(Activity context, List<FileBean> list,
                                   int size, OnClickListener listener) {
            this.list = list;
            count = this.list == null ? 0 : this.list.size();
            this.imagesize = size;
            this.listener = listener;
            this.context = context;
            padding = context.getResources().getDimensionPixelOffset(
                    R.dimen.text_margin);
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public FileBean getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            if (context.isFinishing()) {
                return v;
            }
            ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = getItemView(holder);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            if (position < 2) {
                v.setPadding(0, padding, 0, padding);
            } else {
                v.setPadding(0, 0, 0, padding);
            }
            FileBean fb = getItem(position);
            holder.name.setText(fb.getName());
            holder.photo.setTag(R.id.images_view, fb);
            Glide.with(context).load(fb.getFirstimage()).into(holder.photo);
            return v;
        }

        private View getItemView(ViewHolder holder) {
            RelativeLayout rl = new RelativeLayout(context);
            ImageView iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setBackgroundResource(R.color.divider_color);
            iv.setId(R.id.images_view);
            iv.setOnClickListener(listener);
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(imagesize, imagesize);
            iv.setLayoutParams(rlp);
            TextView name = new TextView(context);
            name.setTextSize(12);
            name.setTextColor(Color.WHITE);
            name.setSingleLine();
            name.setGravity(Gravity.CENTER_VERTICAL);
            name.setEllipsize(TextUtils.TruncateAt.END);
            int padding = (int) (imagesize / 17f);
            name.setPadding(padding, 0, padding, 0);
            name.setBackgroundColor(ContextCompat.getColor(context, R.color.shadow_color));
            rlp = new RelativeLayout.LayoutParams(imagesize, (int) (imagesize / 4.3f));
            rlp.addRule(RelativeLayout.ALIGN_BOTTOM, iv.getId());
            name.setLayoutParams(rlp);
            rl.addView(iv);
            rl.addView(name);
            holder.name = name;
            holder.photo = iv;
            return rl;
        }

        class ViewHolder {
            ImageView photo;
            TextView name;
        }

        void recycle(View v) {
            if (v.getTag() == null) {
                return;
            }
            ViewHolder holder = (ViewHolder) v.getTag();
            if (holder == null) {
                return;
            }
            holder.photo.setImageBitmap(null);
        }
    }

    class SelectImageAdapter extends BaseAdapter {

        HashSet<String> select;
        String[] photos;
        Activity context;
        LayoutInflater inflater;
        int count;
        boolean isSelect = false;
        int height = 0, padding;
        String dir;

        public SelectImageAdapter(String dir, String[] photos, Activity context,
                                  int height, boolean isSelect) {
            this.photos = photos;
            this.context = context;
            this.height = height;
            this.dir = dir;
            count = this.photos == null ? 0 : this.photos.length;
            inflater = LayoutInflater.from(context);
            padding = context.getResources().getDimensionPixelOffset(
                    R.dimen.text_margin);
            this.isSelect = isSelect;
            if (isSelect) {
                select = new HashSet<>();
            }
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public String getItem(int position) {
            return photos[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            if (context.isFinishing()) {
                return v;
            }
            ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = getItemView(holder);
                v.setTag(R.id.images_view, holder);
            } else {
                holder = (ViewHolder) v.getTag(R.id.images_view);
            }
            String m = getItem(position);
            String url = dir + "/" + m;
            if (isSelect) {
                if (select.contains(url)) {
                    holder.select.setSelected(true);
                } else {
                    holder.select.setSelected(false);
                }
            }
            if (position < 3) {
                v.setPadding(0, padding, 0, padding);
            } else {
                v.setPadding(0, 0, 0, padding);
            }
            if (m.length() > 0) {
                holder.photo.setTag(R.id.photo_select_image, url);
                Glide.with(context).load(url).into(holder.photo);
            } else {
                holder.photo.setTag(R.id.photo_select_image, null);
                holder.photo.setImageBitmap(null);
            }
            return v;
        }

        private View getItemView(ViewHolder holder) {
            View view;
            if (isSelect) {
                RelativeLayout rl = new RelativeLayout(context);
                ImageView photo = new ImageView(context);
                photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                photo.setBackgroundResource(R.color.divider_color);
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                        height, height);
                photo.setLayoutParams(rlp);
                View select = new View(context);
                select.setBackgroundResource(R.drawable.select_icon);
                int selectSize = (int) (height / 4f);
                rlp = new RelativeLayout.LayoutParams(selectSize, selectSize);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                rlp.rightMargin = 2;
                rlp.topMargin = 2;
                select.setLayoutParams(rlp);

                rl.addView(photo);
                rl.addView(select);
                holder.photo = photo;
                holder.select = select;
                view = rl;
            } else {
                ImageView photo = new ImageView(context);
                photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                AbsListView.LayoutParams rlp = new AbsListView.LayoutParams(height,
                        height);
                photo.setLayoutParams(rlp);
                holder.photo = photo;
                view = photo;
            }

            return view;
        }

        public void recycle(View v) {
            if (v.getTag() == null) {
                return;
            }
            ViewHolder holder = (ViewHolder) v.getTag(R.id.images_view);
            if (holder == null) {
                return;
            }
            holder.photo.setImageBitmap(null);
        }

        class ViewHolder {
            ImageView photo;
            View select;
        }

        public void destroy() {
            if (select != null) {
                select.clear();
            }
        }

        public void notifyDataSetChanged(String dir, String[] images) {
            this.dir = dir;
            this.photos = images;
            count = this.photos == null ? 0 : this.photos.length;
            notifyDataSetChanged();
        }

        public int clickAt(View view, int position) {
            String url = dir + "/" + getItem(position);
            if (select.contains(url)) {
                select.remove(url);
                ((ViewGroup) view).getChildAt(1).setSelected(false);
            } else {
                select.add(url);
                ((ViewGroup) view).getChildAt(1).setSelected(true);
            }
            return select.size();
        }

        public HashSet<String> getSelects() {
            return select;
        }
    }

}
