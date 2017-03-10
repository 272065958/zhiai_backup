package com.cjx.zhiai.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by cjx on 2016-12-14.
 * 截图显示图片的view
 */
public class CropImageView extends ImageView {

	HighlightView mHighlightView;

	private Runnable mOnLayoutRunnable = null;
	Bitmap mBitmap = null;

	private final Matrix mDisplayMatrix = new Matrix();

	float mLastX, mLastY;
	int mMotionEdge;

	public CropImageView(Context context) {
		super(context);
		init();
	}

	public CropImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		Runnable r = mOnLayoutRunnable;
		if (r != null) {
			mOnLayoutRunnable = null;
			r.run();
		}
		if (mBitmap != null) {
			getProperBaseMatrix(mBitmap, mDisplayMatrix);
			setImageMatrix(mDisplayMatrix);
			if (mHighlightView != null) {
				mHighlightView.mMatrix.set(getImageMatrix());
				mHighlightView.invalidate();
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mHighlightView != null) {
			mHighlightView.draw(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mHighlightView == null) {
			return false;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int edge = mHighlightView.getEdge(event.getX(), event.getY());
			if (edge != HighlightView.GROW_NONE) {
				mMotionEdge = edge;
				mLastX = event.getX();
				mLastY = event.getY();
				mHighlightView.setMode((edge == HighlightView.MOVE) ? HighlightView.ModifyMode.Move
                                : HighlightView.ModifyMode.Grow);
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			mMotionEdge = HighlightView.GROW_NONE;
			mHighlightView.setMode(HighlightView.ModifyMode.None);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			if (mMotionEdge != HighlightView.GROW_NONE) {
				mHighlightView.handleMotion(mMotionEdge, event.getX() - mLastX, event.getY() - mLastY);
				mLastX = event.getX();
                mLastY = event.getY();
			}
			break;
		}
		return true;
	}

	private void init() {
		setScaleType(ScaleType.MATRIX);
	}

	public HighlightView getHighlightView() {
		return mHighlightView;
	}

	public void setHighlightView(HighlightView hv) {
		mHighlightView = hv;
		invalidate();
	}

	// Setup the base matrix so that the image is centered and scaled properly.
	private void getProperBaseMatrix(Bitmap bitmap, Matrix matrix) {
		float viewWidth = getWidth();
		float viewHeight = getHeight();

		float w = bitmap.getWidth();
		float h = bitmap.getHeight();
		matrix.reset();

		// We limit up-scaling to 2x otherwise the result may look bad if it's
		// a small icon.
		float widthScale = Math.min(viewWidth / w, 2.0f);
		float heightScale = Math.min(viewHeight / h, 2.0f);
		float scale = Math.min(widthScale, heightScale);

		// matrix.postConcat(bitmap.getRotateMatrix());
		matrix.postScale(scale, scale);

		matrix.postTranslate((viewWidth - w * scale) / 2F, (viewHeight - h
				* scale) / 2F);
	}

	public void setImageBitmapResetBase(final Bitmap bitmap,
			final boolean resetSupp) {
		final int viewWidth = getWidth();
		mBitmap = bitmap;
		if (viewWidth <= 0) {
			mOnLayoutRunnable = new Runnable() {
				@Override
				public void run() {
					setImageBitmapResetBase(bitmap, resetSupp);
				}
			};
			return;
		}

		if (bitmap != null) {
			getProperBaseMatrix(bitmap, mDisplayMatrix);
			setImageBitmap(bitmap);
		} else {
			mDisplayMatrix.reset();
			setImageBitmap(null);
		}
		setImageMatrix(mDisplayMatrix);
	}

	@Override
	public void setImageBitmap(Bitmap bitmap) {
		super.setImageBitmap(bitmap);
		Drawable d = getDrawable();
		if (d != null) {
			d.setDither(true);
		}
		if (mBitmap != null && mBitmap != bitmap) {
			mBitmap.recycle();
		}
		mBitmap = bitmap;
	}
}
