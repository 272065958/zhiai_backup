package com.cjx.zhiai.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.cjx.zhiai.R;

public class SideBar extends View {
    private SectionIndexer selectionIndexer = null;
    private ListView list;
    private String[] sideItem;
    private int itemLength;
    private int choose = -1;// 选中
    private Paint paint = new Paint();
    private int contentHeight = 0;
    private TextView mTextDialog;

    private final float NORMAL_ALPHA = 0.7f;

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setAlpha(NORMAL_ALPHA);
        }
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setAlpha(NORMAL_ALPHA);
        }
    }

    public SideBar(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setAlpha(NORMAL_ALPHA);
        }
    }

    /**
     * 重写这个方法
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (sideItem == null) {
            return;
        }
        // 获取焦点改变背景颜色.
        int width = getWidth(); // 获取对应宽度
        int height = getContentHeight();
        int singleHeight = height / itemLength;// 获取每一个字母的高度
        paint.setAntiAlias(true);
        // 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
        paint.setTextSize(width / 2);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        float textC = -(fontMetrics.ascent + fontMetrics.descent) / 2;
        paint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary_color));
        for (int i = 0; i < itemLength; i++) {
            String s = sideItem[i];
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - paint.measureText(s) / 2;
            float yPos = singleHeight * i + singleHeight / 2 + textC;
            canvas.drawText(s, xPos, yPos, paint);
        }

    }

    public SideBar setSideItem(String[] _sideItem) {
        sideItem = _sideItem;
        itemLength = sideItem.length;
        invalidate();
        return this;
    }

    // 设置绑定的ListView和SectionIndexer
    public void bind(ListView _list, SectionIndexer _selectionIndexer) {
        list = _list;
        selectionIndexer = _selectionIndexer;
    }

    // 设置切换字母时的提示view
    public SideBar setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
        return this;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (sideItem == null) {
            return super.onTouchEvent(event);
        }
        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_UP:
                choose = -1;//
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.GONE);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    setAlpha(NORMAL_ALPHA);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    setAlpha(1f);
                }
            default:
                final float y = event.getY();// 点击y坐标
                final int oldChoose = choose;
                final int c = (int) (y / getContentHeight() * itemLength);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
                if (oldChoose != c) {
                    if (c >= 0 && c < itemLength) {
                        if (selectionIndexer == null) {
                            return super.onTouchEvent(event);
                        }
                        int position = selectionIndexer.getPositionForSection(c);
                        if (position > -2) {
                            list.setSelection(position);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(sideItem[c]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }
                        choose = c;
                    }
                }
                break;
        }

        return true;
    }

    private int getContentHeight() {
        if (contentHeight == 0) {
            contentHeight = getHeight();
        }
        return contentHeight;
    }
}