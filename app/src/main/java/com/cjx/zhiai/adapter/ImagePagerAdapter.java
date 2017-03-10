package com.cjx.zhiai.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cjx.zhiai.util.Tools;

import java.util.LinkedList;

/**
 * Created by cjx on 2016-11-26.
 * 显示viewpager的设配器
 */
public class ImagePagerAdapter extends PagerAdapter {

    String res[];
    LinkedList<ImageView> views;
    Context context;
    public ImagePagerAdapter(Context context, String res[]){
        this.res = res;
        this.context = context;
        views = new LinkedList<>();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return res.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView;
        if(views.isEmpty()){
            imageView = createView();
        }else{
            imageView = views.pop();
        }
        container.addView(imageView);
        Tools.setImageInView(context, res[position], imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ImageView imageView = (ImageView) object;
        container.removeView(imageView);
        views.push(imageView);
    }

    private ImageView createView(){
        return new ImageView(context);
    }
}