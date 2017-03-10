package com.cjx.zhiai.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.component.TouchImageView;
import com.cjx.zhiai.util.Tools;


/**
 * Created by cjx  图片列表界面
 */
public class ImagesActivity extends BaseActivity {
	ViewPager vp;
	View[] views;
	String[] photos; // 存储图片的地址
	int screenWidth = 0, pageCount = 0;
	TextView pageTv;

	private Animation bottomEnter, bottomExit, titleEnter, titleExit;
	private boolean isbottomExitAnim = false, istitleExitAnim = false;

	View titleContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_images);
		MyApplication app = (MyApplication) getApplication();
		screenWidth = app.getScreen_width();

		setToolBar(true, null, -1);

		findViews();
		initAnimations();

		init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		photos = null;
		vp.setAdapter(null);
		vp = null;
		System.gc();
	}

	private void init() {
		vp = (ViewPager) findViewById(R.id.images_view);
		pageTv = (TextView) findViewById(R.id.images_page);
		Intent intent = getIntent();
		photos = intent.getStringArrayExtra("photo");
		int page = intent.getIntExtra("page", 0);
		loadPicture(photos, page);
	}

	@SuppressWarnings("deprecation")
	private void loadPicture(String[] photos, int page) {
		if (photos == null || photos.length == 0) {
			return;
		}
		pageCount = photos.length;
		pageTv.setText("1/" + pageCount);
		int viewLength = pageCount;
		// 最多用3个view来循环显示图片
		if (viewLength > 3) {
			viewLength = 3;
		}
		views = new View[viewLength];
		LayoutInflater inflater = LayoutInflater.from(this);
		for (int i = 0; i < viewLength; i++) {
			View v = inflater.inflate(R.layout.image_load, null);
			TouchImageView tiv = (TouchImageView) v
					.findViewById(R.id.image_content);
			tiv.setImageGestureListener(listener);
			views[i] = v;
		}
		ImagePagerAdapter adapter = new ImagePagerAdapter(ImagesActivity.this,
				views, photos);
		vp.setAdapter(adapter);
		vp.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				pageTv.setText((arg0 + 1) + "/" + pageCount);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		if (page > 0) {
			vp.setCurrentItem(page);
		}
	}

	// 初始化显示详情时的动画
	private void initAnimations() {
		bottomExit = AnimationUtils.loadAnimation(ImagesActivity.this, R.anim.bottom_to_top);
		bottomExit.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				isbottomExitAnim = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isbottomExitAnim = false;
				pageTv.setVisibility(View.GONE);
			}
		});

		bottomEnter = AnimationUtils.loadAnimation(ImagesActivity.this, R.anim.top_to_bottom);

		titleExit = AnimationUtils.loadAnimation(ImagesActivity.this, R.anim.title_exit);
		titleExit.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				istitleExitAnim = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				istitleExitAnim = false;
				titleContent.setVisibility(View.GONE);
			}
		});

		titleEnter = AnimationUtils.loadAnimation(ImagesActivity.this, R.anim.title_enter);
	}

	private void findViews() {
		titleContent = findViewById(R.id.toolbar);
	}

	// 图片单击响应事件
	TouchImageView.ImageGestureListener listener = new TouchImageView.ImageGestureListener() {
		@Override
		public void onSingleTapConfirmed(MotionEvent e) {

			if (null == titleContent || null == pageTv) {
				findViews();
			}
			if (null == titleExit || null == bottomExit || null == titleEnter ||  null == bottomEnter) {
				initAnimations();
			}

			if (pageTv.getVisibility() == View.VISIBLE) {
				if (!istitleExitAnim) {
					titleContent.startAnimation(titleExit);
				}
				if (!isbottomExitAnim) {
					pageTv.startAnimation(bottomExit);
				}
			} else {
				pageTv.setVisibility(View.VISIBLE);
				pageTv.startAnimation(bottomEnter);
				titleContent.setVisibility(View.VISIBLE);
				titleContent.startAnimation(titleEnter);
			}
		}
	};

	class ImagePagerAdapter extends PagerAdapter {
		View[] views;
		int photoCount = 0;
		String[] photos;
		Activity context;

		public ImagePagerAdapter(Activity context, View[] l, String[] photos) {
			this.context = context;
			this.photoCount = photos.length;
			this.photos = photos;
			views = l;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v = views[position%3];
			container.removeView(v);
			TouchImageView image = (TouchImageView) v
					.findViewById(R.id.image_content);
			container.addView(v);
			String url = photos[position];
			Tools.setImageInView(context, url, image);
			return v;
		}

		@Override
		public int getCount() {
			return photoCount;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}
