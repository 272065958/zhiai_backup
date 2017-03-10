package com.cjx.zhiai.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class TouchImageView extends ImageView {

	Matrix matrix;
	boolean scaleTouch = true;

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF last = new PointF();
	PointF start = new PointF();
	final float minScale = 1f;
	float maxScale = 2f;
	float[] m;

	int viewWidth, viewHeight;
	static final int CLICK = 3;
	float saveScale = 1f;
	protected float origWidth, origHeight;
	int oldMeasuredWidth, oldMeasuredHeight;

	ScaleGestureDetector mScaleDetector;
	GestureDetector gustureDecetor;
	Context context;
	ImageGestureListener listener;
	public TouchImageView(Context context) {
		super(context);
		sharedConstructing(context);
	}

	public TouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sharedConstructing(context);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		saveScale = 1;
		matrix.reset();
		super.setImageBitmap(bm);
	}

	public void setScaleTouch(boolean scaleTouch){
		this.scaleTouch = scaleTouch;
	}

	private void sharedConstructing(Context context) {
		super.setClickable(true);
		this.context = context;
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		gustureDecetor = new GestureDetector(getContext(),
				new GestureListener());
		matrix = new Matrix();
		m = new float[9];
		setImageMatrix(matrix);
		setScaleType(ScaleType.MATRIX);
		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gustureDecetor.onTouchEvent(event);
				if(scaleTouch){
					mScaleDetector.onTouchEvent(event);
					PointF curr = new PointF(event.getX(), event.getY());
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						last.set(curr);
						start.set(last);
						mode = DRAG;
						break;

					case MotionEvent.ACTION_MOVE:
						if (mode == DRAG) {
							float deltaX = curr.x - last.x;
							float deltaY = curr.y - last.y;
							float fixTransX = getFixDragTrans(deltaX, viewWidth,
									origWidth * saveScale);
							float fixTransY = getFixDragTrans(deltaY, viewHeight,
									origHeight * saveScale);
							matrix.postTranslate(fixTransX, fixTransY);
							fixTrans();
							last.set(curr.x, curr.y);
							if (isIntercept(deltaX)) {
								// 通知父类不要干涉我当前的触摸事件
								getParent()
										.requestDisallowInterceptTouchEvent(true);
							}
						}
						break;

					case MotionEvent.ACTION_UP:
						mode = NONE;
						int xDiff = (int) Math.abs(curr.x - start.x);
						int yDiff = (int) Math.abs(curr.y - start.y);
						if (xDiff < CLICK && yDiff < CLICK)
							performClick();
						break;

					case MotionEvent.ACTION_POINTER_UP:
						mode = NONE;
						break;
					}
					setImageMatrix(matrix);
					invalidate();
				}
				return true;// indicate event was handled
			}

		});
	}

	public boolean isIntercept(float deltaX) {
		boolean intercept = false;
		if (deltaX == 0 || saveScale == 1.0f) {
			return intercept;
		}
		if (deltaX > 0) {
			if (m[Matrix.MTRANS_X] < 0) {
				intercept = true;
			}
		} else {
			if (m[Matrix.MTRANS_X] > viewWidth - origWidth * saveScale) {
				intercept = true;
			}
		}
		return intercept;
	}

	public void setImageGestureListener(ImageGestureListener listener){
		this.listener = listener;
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mode = ZOOM;
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float mScaleFactor = detector.getScaleFactor();
			float origScale = saveScale;
			saveScale *= mScaleFactor;
			if (saveScale > maxScale) {
				saveScale = maxScale;
				mScaleFactor = maxScale / origScale;
			} else if (saveScale < minScale) {
				saveScale = minScale;
				mScaleFactor = minScale / origScale;
			}

			if (origWidth * saveScale <= viewWidth
					|| origHeight * saveScale <= viewHeight) {
				matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2,
						viewHeight / 2);
				matrix.getValues(m);
				matrix.postTranslate((viewWidth - origWidth * saveScale) / 2 - m[Matrix.MTRANS_X],
						(viewHeight - origHeight * saveScale) / 2 - m[Matrix.MTRANS_Y]);
			} else {
				matrix.postScale(mScaleFactor, mScaleFactor,
						detector.getFocusX(), detector.getFocusY());
				fixTrans();
			}

			return true;
		}
	}

	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if(scaleTouch){
				float origScale = saveScale;
				float mScaleFactor;
				if(origScale == minScale){
					saveScale = maxScale;
					mScaleFactor = maxScale / origScale;
				}else{
					saveScale = minScale;
					mScaleFactor = minScale / origScale;
				}
				matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2,
						viewHeight / 2);
				matrix.getValues(m);
				matrix.postTranslate((viewWidth - origWidth * saveScale) / 2  - m[Matrix.MTRANS_X],
						(viewHeight - origHeight * saveScale) / 2  - m[Matrix.MTRANS_Y]);
			}
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if(listener != null){
				listener.onSingleTapConfirmed(e);
			}
			return super.onSingleTapConfirmed(e);
		}
	}

	void fixTrans() {
		matrix.getValues(m);
		float transX = m[Matrix.MTRANS_X];
		float transY = m[Matrix.MTRANS_Y];
		float fixTransX = getFixTrans(transX, viewWidth, origWidth * saveScale);
		float fixTransY = getFixTrans(transY, viewHeight, origHeight * saveScale);
		matrix.postTranslate(fixTransX, fixTransY);
	}

	float getFixTrans(float trans, float viewSize, float contentSize) {
		float minTrans, maxTrans;

		if (contentSize <= viewSize) {
			minTrans = 0;
			maxTrans = viewSize - contentSize;
		} else {
			minTrans = viewSize - contentSize;
			maxTrans = 0;
		}

		if (trans < minTrans)
			return -trans + minTrans;
		if (trans > maxTrans)
			return -trans + maxTrans;
		return 0;
	}

	float getFixDragTrans(float delta, float viewSize, float contentSize) {
		if (contentSize <= viewSize) {
			return 0;
		}
		return delta;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		viewHeight = MeasureSpec.getSize(heightMeasureSpec);
		if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight
				|| viewWidth == 0 || viewHeight == 0)
			return;
		oldMeasuredHeight = viewHeight;
		oldMeasuredWidth = viewWidth;

		if (saveScale == 1) {
			float scale;

			Drawable drawable = getDrawable();
			if (drawable == null || drawable.getIntrinsicWidth() == 0
					|| drawable.getIntrinsicHeight() == 0)
				return;
			int bmWidth = drawable.getIntrinsicWidth();
			int bmHeight = drawable.getIntrinsicHeight();

			float scaleX = (float) viewWidth / (float) bmWidth;
			float scaleY = (float) viewHeight / (float) bmHeight;
			scale = Math.min(scaleX, scaleY);
			matrix.setScale(scale, scale);

			float redundantYSpace = viewHeight - (scale * bmHeight);
			float redundantXSpace = viewWidth - (scale * bmWidth);
			redundantYSpace /= 2;
			redundantXSpace /= 2;

			matrix.postTranslate(redundantXSpace, redundantYSpace);

			origWidth = viewWidth - 2 * redundantXSpace;
			origHeight = viewHeight - 2 * redundantYSpace;
			setImageMatrix(matrix);
		}
		fixTrans();
	}

	public interface ImageGestureListener{
		void onSingleTapConfirmed(MotionEvent e);
	}
}
