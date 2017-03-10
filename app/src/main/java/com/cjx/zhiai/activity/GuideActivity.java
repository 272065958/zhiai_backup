package com.cjx.zhiai.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;

import java.util.Stack;

/**
 * Created by cjx on 2017/2/15.
 * 程序引导页
 */
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    final int[] guides = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
    final int guideCount = 2;
    View enterButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ViewPager vp = (ViewPager) findViewById(R.id.view_pager);
        vp.setOnPageChangeListener(this);
        MyPagerAdapter adapter = new MyPagerAdapter(guides.length);
        vp.setAdapter(adapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(enterButton == null && position == guideCount){
            enterButton = findViewById(R.id.button_enter);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.hide_to_show);
            animation.setStartOffset(1000);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    enterButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            enterButton.startAnimation(animation);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {

    }

    public void onClick(View view){
        SharedPreferences.Editor editor = MyApplication.getInstance().sharedPreferences.edit();
        editor.putBoolean("first", true);
        editor.apply();
        finish();
    }

    public class MyPagerAdapter extends PagerAdapter {
        int pageCount = 0;

        Stack<ImageView> pools;

        MyPagerAdapter(int pageCount) {
            this.pageCount = pageCount;
            pools = new Stack<>();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView v = (ImageView) object;
            container.removeView(v);
            pools.push(v);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView v = getImageView();
            container.addView(v);
            int res = guides[position];
            if(v.getTag() == null || (int)v.getTag() != res){
                v.setImageResource(guides[position]);
                v.setTag(res);
            }
            return v;
        }

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        private ImageView getImageView(){
            if(pools.isEmpty()){
                ImageView imageView = new ImageView(GuideActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                return imageView;
            }else{
                return pools.pop();
            }
        }
    }

}
