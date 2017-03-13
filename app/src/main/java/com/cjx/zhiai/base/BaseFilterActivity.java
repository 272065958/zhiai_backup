package com.cjx.zhiai.base;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.bean.ValueTextBean;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-27.
 * 包含筛选的listview界面
 */
public abstract class BaseFilterActivity extends BaseListActivity {
    protected boolean canFilter = false;
    protected String sortType;
    protected String leftFilterString;
    PopupWindow leftPopupWindow, rightPopupWindow;
    protected View leftPopupView, rightPopupView;

    int leftXOff, leftYOff, rightXOff, rightYOff, leftPopupWidth, rightPopupWidth;


    TextView leftTextView, rightTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_base);
        Intent intent = getIntent();
        setToolBar(true, null, intent.getIntExtra("title", -1));
        initView();
    }

    /**
     * 设置窗口的透明度
     *
     * @param alpha 1.0f ~ 0.0f
     */
    private void setWindowAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }

    private void initView() {
        initListView(false, false);
        leftTextView = (TextView) findViewById(R.id.filter_left_text);
        rightTextView = (TextView) findViewById(R.id.filter_right_text);
    }

    /**
     * 设置左侧筛选文本
     */
    protected void setLeftFilterText(CharSequence text, int xOff, int yOff, int popupWidth) {
        leftTextView.setText(text);
        leftXOff = xOff;
        leftYOff = yOff;
        leftPopupWidth = popupWidth;
    }

    protected void setLeftFilterText(CharSequence text) {
        leftTextView.setText(text);
    }

    /**
     * 设置右侧筛选文本
     */
    protected void setRightFilterText(CharSequence text, int xOff, int yOff, int popupWidth) {
        rightTextView.setText(text);
        rightXOff = xOff;
        rightYOff = yOff;
        rightPopupWidth = popupWidth;
    }

    protected void setRightFilterText(CharSequence text) {
        rightTextView.setText(text);
    }

    public void onClick(View v) {
        if (!canFilter) {
            return;
        }
        switch (v.getId()) {
            case R.id.filter_left:
                showLeftPopupWindow(v);
                break;
            case R.id.filter_right:
                showRightPopupWindow(v);
                break;
        }
    }

    /**
     * 显示左边的筛选view
     */
    protected void showLeftPopupWindow(View v) {
        if (leftPopupWindow == null) {
            leftPopupView = createLeftPopupView();
            leftPopupView.setTag(v);
            leftPopupWindow = createPopupWindow(leftPopupView, leftPopupWidth);
            leftPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            leftPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ((View) leftPopupView.getTag()).setSelected(false);
                    // popupwindow 消失时回调
                    setWindowAlpha(1.0f);
                }
            });
        }
        leftPopupWindow.showAsDropDown(v, leftXOff, leftYOff);
        v.setSelected(true);
        setWindowAlpha(0.8f);
    }

    /**
     * 显示右边的筛选view
     */
    protected void showRightPopupWindow(View v) {
        if (rightPopupWindow == null) {
            rightPopupView = createRightPopupView();
            rightPopupView.setTag(v);
            rightPopupWindow = createPopupWindow(rightPopupView, rightPopupWidth);
            rightPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            rightPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ((View) rightPopupView.getTag()).setSelected(false);
                    // popupwindow 消失时回调
                    setWindowAlpha(1.0f);
                }
            });
        }
        rightPopupWindow.showAsDropDown(v, rightXOff, rightYOff);
        v.setSelected(true);
        setWindowAlpha(0.8f);
    }

    protected void hideLeftPopopWindow() {
        leftPopupWindow.dismiss();
    }

    protected void hideRightPopupWindow() {
        rightPopupWindow.dismiss();
    }

    // 创建左侧popupView
    protected abstract View createLeftPopupView();

    protected abstract ValueTextBean[] getRightPopupItem();

    // 创建右侧popupView
    private View createRightPopupView() {
        ValueTextBean[] items = getRightPopupItem();
        return createPopupView(items, rightPopupClickListener);
    }

    private View.OnClickListener rightPopupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.isSelected()) {
                return;
            }
            TextView textView = (TextView) v.findViewById(R.id.item_text_view);
            itemSelect((ViewGroup) rightPopupView, v);
            hideRightPopupWindow();
            setRightFilterText(textView.getText());
            sortType = (String) textView.getTag();
            onRefresh();
        }
    };

    // 创建一个popupwindow的view
    protected ViewGroup createPopupView(ValueTextBean[] items, View.OnClickListener listener) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundResource(R.drawable.gray_rectangle_bg);
        boolean isAuto = true;
        for (ValueTextBean item : items) {
            View view = createPopouItemView(item, listener);
            if (isAuto) {
                setItemSelect(true, view);
                isAuto = false;
            }
            linearLayout.addView(view);
        }
        return linearLayout;
    }

    // 创建选项view
    private View createPopouItemView(ValueTextBean vtb, View.OnClickListener listener) {
        View view = View.inflate(this, R.layout.item_poput_view, null);
        TextView textView = (TextView) view.findViewById(R.id.item_text_view);
        textView.setText(vtb.text);
        textView.setTag(vtb.value);
        view.setOnClickListener(listener);
        return view;
    }

    // 取消上次点击的view,显示当前点击的view
    protected void itemSelect(ViewGroup viewGroup, View clickView) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            if (view != clickView) {
                if (view.isSelected()) {
                    setItemSelect(false, view);
                }
            } else {
                setItemSelect(true, view);
            }
        }
    }

    // 设置view的选中状态
    private void setItemSelect(boolean select, View view) {
        View iconView = view.findViewById(R.id.select_icon);
        TextView textView = (TextView) view.findViewById(R.id.item_text_view);
        if (select) {
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.pink_select_color));
            iconView.setVisibility(View.VISIBLE);
            textView.setTextColor(ContextCompat.getColor(this, R.color.main_color));
        } else {
            view.setBackgroundColor(Color.WHITE);
            iconView.setVisibility(View.GONE);
            textView.setTextColor(ContextCompat.getColor(this, R.color.text_main_color));
        }
        view.setSelected(select);
    }

    /**
     * 创建一个popupWindow
     *
     * @param viewGroup popupWindow 的view
     * @param width     popupWindow 的宽
     * @return
     */
    private PopupWindow createPopupWindow(View viewGroup, int width) {
        PopupWindow popupWindow = new PopupWindow(viewGroup, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        return popupWindow;
    }

    protected MyCallbackInterface getMycallback(Type type) {
        return new FilterCallbackInterface(type, leftFilterString, sortType);
    }

    class FilterCallbackInterface implements MyCallbackInterface {
        Type type;
        String leftPar, rightPar;

        FilterCallbackInterface(Type type, String leftPar, String rightPar) {
            this.type = type;
            this.leftPar = leftPar;
            this.rightPar = rightPar;
        }

        @Override
        public void success(ResultBean response) {
            if (leftPar.equals(leftFilterString) && rightPar.equals(sortType)) {
                ArrayList<?> list = JsonParser.getInstance().fromJson(response.datas, type);
                onLoadResult(list);
            }
        }

        @Override
        public void error() {
            hideLoadView();
        }
    }
}
