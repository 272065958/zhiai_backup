package com.cjx.zhiai.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.activity.WebActivity;
import com.cjx.zhiai.bean.AdverBean;
import com.cjx.zhiai.util.Tools;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by cjx on 2016-11-26.
 * 广告轮播图的设配器
 */
public class AdverPagerAdapter extends PagerAdapter implements View.OnClickListener {

    private ArrayList<AdverBean> list;
    private LinkedList<ImageView> views;
    private Context context;
    public AdverPagerAdapter(Context context, ArrayList<AdverBean> list){
        this.list = list;
        this.context = context;
        views = new LinkedList<>();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
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
        AdverBean ab = list.get(position);
        Tools.setImageInView(context, ab.address, imageView);
        imageView.setTag(R.id.tag_view, ab);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ImageView imageView = (ImageView) object;
        container.removeView(imageView);
        views.push(imageView);
    }

    @Override
    public void onClick(View v) {
        AdverBean ab = (AdverBean) v.getTag(R.id.tag_view);
        if(!TextUtils.isEmpty(ab.url)){
            Intent intent = new Intent(context, WebActivity.class);
            intent.putExtra("url", ab.url);
            Log.e("TAG", "url = "+ab.url);
            context.startActivity(intent);
        }
    }

    private ImageView createView(){
        ImageView iv = new ImageView(context);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setOnClickListener(this);
        return iv;
    }
}