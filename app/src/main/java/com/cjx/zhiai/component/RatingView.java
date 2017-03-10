package com.cjx.zhiai.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;

public class RatingView extends View {

	private int starSpace = 0;
	private int starSize = 0;
	private float part = 0;
	private Bitmap bitmap;
	private Canvas canvas;
	private Bitmap select = null;
	private Bitmap normal = null;
	private Matrix matrix;
	private int currentScore = -1, prevScore = -1;
	private int tag;
	private int mTouchSlop;
	private float mLastMotionX;
	private float mLastMotionY;
	private OnRatingChangeListener listener;
	private boolean isClick = false;

	public RatingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RatingView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	public void setRatingSize(int size, LayoutParams lp, Bitmap select,
			Bitmap normal) {
		matrix = new Matrix();
		this.starSize = size;
		this.starSpace = size / 4;
		this.select = select;
		this.normal = normal;
		this.part = size + starSpace;
		int width = (int) (5 * this.part);
		if (lp == null) {
			lp = getLayoutParams();
		}
		if (lp != null) {
			lp.height = size;
			lp.width = width;
		}
		bitmap = Bitmap.createBitmap(width, starSize, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
	}

	private void touchDraw(int count) {
		if (count < 1) {
			count = 1;
		} else if (count > 5) {
			count = 5;
		}
		if (currentScore != count) {
			drawStar(count);
		}
	}

	public void drawStar(int count) {
		currentScore = count;
		int start = (int) (starSpace / 2f);
		canvas.drawColor(0, Mode.CLEAR);
		for (int i = 0; i < 5; i++) {
			if (i >= count) {
				canvas.drawBitmap(normal, start, 0, null);
			} else {
				canvas.drawBitmap(select, start, 0, null);
			}
			start += starSpace + starSize;
		}
		invalidate();
	}

	public void setOnRatingChangeListener(OnRatingChangeListener listener) {
		this.listener = listener;
	}

	public void recycle() {
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		matrix = null;
		canvas = null;
	}

	/**
	 * 处理拦截后的touch事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			isClick = true;
			break;
		case MotionEvent.ACTION_MOVE:
			final float dx = x - mLastMotionX;
			final float xDiff = Math.abs(dx);
			final float yDiff = Math.abs(y - mLastMotionY);
			if (xDiff > mTouchSlop && xDiff > yDiff) {
				getParent().requestDisallowInterceptTouchEvent(true);
				touchDraw((int) Math.ceil(x / part));
				isClick = false;
			} else if (yDiff > mTouchSlop) {
				isClick = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (isClick) {
				touchDraw((int) Math.ceil(mLastMotionX / part));
			}
			callback();
			break;
		case MotionEvent.ACTION_CANCEL:
			callback();
			break;
		}
		return true;
	}

	private void callback() {
		if (prevScore != currentScore) {
			prevScore = currentScore;
			if (listener != null) {
				listener.change(currentScore);
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (bitmap == null || bitmap.isRecycled()) {
			return;
		}
		canvas.drawBitmap(bitmap, matrix, null);
	}

	public interface OnRatingChangeListener {
		void change(int count);
	}
}
