package com.cjx.zhiai.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjx.zhiai.R;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/2/24.
 */
public class ItemSelectDialog extends CustomDialog implements View.OnClickListener {
    OnItemClickListener listener;

    public ItemSelectDialog(Context context) {
        super(context);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            int position = (int) v.getTag();
            if(position == -1){
                dismiss();
            }else{
                listener.click(position);
            }
        }
    }

    /**
     * 设置选项和点击位置回调
     * */
    public ItemSelectDialog setItemsByArray(String[] items, OnItemClickListener listner) {
        if (items == null || items.length == 0) {
            return this;
        }
        this.listener = listner;
        LinearLayout content = (LinearLayout) View.inflate(getContext(), R.layout.select_dialog, null);
        content.setBackgroundColor(Color.WHITE);
        setContentView(content);
        addItemView(content, items);
        return this;
    }

    /**
     * 设置选项和点击位置回调
     * */
    public ItemSelectDialog setItems(ArrayList<String> items, OnItemClickListener listner) {
        if (items == null || items.size() == 0) {
            return this;
        }
        this.listener = listner;
        LinearLayout content = (LinearLayout) View.inflate(getContext(), R.layout.select_dialog, null);
        content.setBackgroundColor(Color.WHITE);
        setContentView(content);
        addItemView(content, items);
        return this;
    }

    /**
     * 初始化item选项
     *
     * @param content 放选项的父容器
     * @param items   选项集合
     */
    private void addItemView(ViewGroup content, String[] items) {
        if (items == null) {
            return;
        }
        int length = items.length;
        if (length == 0) {
            return;
        }
        Resources res = getContext().getResources();
        for (int i = 0; i < length; i++) {
            content.addView(initItemView(i, items[i], res));
            if (i < length) {
                content.addView(initDivider());
            }
        }
        content.addView(initItemView(-1, "取消", res));
    }

    /**
     * 初始化item选项
     *
     * @param content 放选项的父容器
     * @param items   选项集合
     */
    private void addItemView(ViewGroup content, ArrayList<String> items) {
        if (items == null) {
            return;
        }
        int length = items.size();
        if (length == 0) {
            return;
        }
        Resources res = getContext().getResources();
        for (int i = 0; i < length; i++) {
            content.addView(initItemView(i, items.get(i), res));
            if (i < length) {
                content.addView(initDivider());
            }
        }
        content.addView(initItemView(-1, "取消", res));
    }

    /**
     * 显示一个选项的view
     *
     * @param position 选项所在的位置
     * @param text     选项显示的内容
     * @return
     */
    private View initItemView(int position, String text, Resources res) {
        TextView view = new TextView(getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                res.getDimensionPixelOffset(R.dimen.tab_height));
        view.setBackgroundResource(R.drawable.item_pressed_bg);
        view.setLayoutParams(lp);
        view.setGravity(Gravity.CENTER);
        view.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        view.setTag(position);
        view.setTextSize(18);
        view.setText(text);
        view.setOnClickListener(this);
        return view;
    }

    private View initDivider() {
        View view = new View(getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        view.setLayoutParams(lp);
        view.setBackgroundResource(R.color.divider_color);
        return view;
    }

    public interface OnItemClickListener {
        void click(int position);
    }
}
