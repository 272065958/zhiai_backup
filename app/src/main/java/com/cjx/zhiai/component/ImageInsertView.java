package com.cjx.zhiai.component;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cjx.zhiai.R;
import com.cjx.zhiai.activity.ImagesActivity;
import com.cjx.zhiai.util.Tools;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by cjx on 2016/9/29.
 */
public class ImageInsertView extends HorizontalScrollView implements View.OnClickListener {
    LinearLayout imageContentView;
    OnClickListener addImageListener;
    boolean showDel = true;
    View hintView;
    int itemSize;
    public ImageInsertView(Context context) {
        super(context);
        init();
    }

    public ImageInsertView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void onClick(View v) {
        int position = imageContentView.indexOfChild((View) v.getParent()) - 1;
        Intent intent = new Intent(getContext(), ImagesActivity.class);
        intent.putExtra("photo", getFullImageArray());
        intent.putExtra("page", position);
        getContext().startActivity(intent);
    }

    private void init() {
        itemSize = getResources().getDimensionPixelOffset(R.dimen.image_height);
        imageContentView = new LinearLayout(getContext());
        imageContentView.setOrientation(LinearLayout.HORIZONTAL);
        imageContentView.setGravity(Gravity.CENTER_VERTICAL);
        addView(imageContentView);
    }

    public ImageInsertView showDelView(boolean show) {
        this.showDel = show;
        return this;
    }

    // 调用这个方法后,每增加一个图片,就会自动添加一个点击选择图片的view
    public void setAddImageListener(OnClickListener addImageListener, View hintView) {
        this.hintView = hintView;
        this.addImageListener = addImageListener;
        addImageSelectView();
        imageContentView.addView(hintView);
    }

    // 添加一张图片到view
    public void addImage(String path) {
        if(hintView != null && hintView.getVisibility() == VISIBLE){
            hintView.setVisibility(GONE);
        }
        View currentImageView = View.inflate(getContext(), R.layout.select_image_view, null);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(itemSize, itemSize);
        llp.leftMargin = getResources().getDimensionPixelOffset(R.dimen.auto_margin);
        currentImageView.setLayoutParams(llp);
        imageContentView.addView(currentImageView, addImageListener == null ? 0 : 1);
        ImageView imageView = (ImageView) currentImageView.findViewById(R.id.image_content);
        imageView.setOnClickListener(this);
        Tools.setImageInView(getContext(), path, imageView);
        if (showDel) {
            ImageView delView = (ImageView) currentImageView.findViewById(R.id.delete_image);
            delView.setVisibility(VISIBLE);
            delView.setImageResource(android.R.drawable.ic_delete);
            delView.setOnClickListener(delImageListener);
        }
        currentImageView.setTag(path);
    }

    // 添加一个选择图片的view
    private void addImageSelectView() {
        View currentImageView = View.inflate(getContext(), R.layout.select_image_view, null);
        currentImageView.setOnClickListener(addImageListener);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(itemSize, itemSize);
        llp.leftMargin = getResources().getDimensionPixelOffset(R.dimen.auto_margin);
        currentImageView.setLayoutParams(llp);
        imageContentView.addView(currentImageView);
    }

    OnClickListener delImageListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            View view = (View) v.getParent();
            File file = new File((String) view.getTag());
            if (file.exists()) {
                file.delete();
            }
            imageContentView.removeView(view);
            if(hintView != null && imageContentView.getChildCount() == 2){
                hintView.setVisibility(VISIBLE);
            }
        }
    };

    public ArrayList<String> getImages() {
        int childCount = imageContentView.getChildCount();
        ArrayList<String> paths = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            View childView = imageContentView.getChildAt(i);
            if (childView.getTag() != null) {
                Object tag = childView.getTag();
                if (tag instanceof String) {
                    paths.add((String) tag);
                }

            }
        }
        return paths;
    }

    public String[] getFullImageArray() {
        String[] images = null;
        ArrayList<String> paths = getImages();
        if (!paths.isEmpty()) {
            int length = paths.size();
            images = new String[length];
            for (int i = 0; i < length; i++) {
                images[i] = paths.get(i);
            }
        }
        return images;
    }

}
