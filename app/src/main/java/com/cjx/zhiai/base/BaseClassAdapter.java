package com.cjx.zhiai.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by cjx on 2016/12/29.
 */
public abstract class BaseClassAdapter extends MyBaseAdapter {
    LinearLayout.LayoutParams itemParam, lineParams, rightParams;
    int columNum;

    public BaseClassAdapter(ArrayList<?> list, BaseActivity context, int columNum) {
        super(list, context);
        this.columNum = columNum;
        MyApplication app = (MyApplication) context.getApplication();
        int viewSpace = context.getResources().getDimensionPixelOffset(R.dimen.grid_spacing);
        int width = (app.getScreen_width() - (columNum - 1) * viewSpace) / columNum;
        itemParam = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewSpace);
        if(columNum > 1){
            contentStack = new Stack<>();
            rightParams = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            rightParams.leftMargin = viewSpace;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ParentViewHolder holder;
        if (convertView == null) {
            convertView = createView(context);
            holder = bindViewHolder(convertView);
            convertView.setTag(R.id.tag_view, holder);
        } else {
            holder = (ParentViewHolder) convertView.getTag(R.id.tag_view);
        }
        initContentView(position, holder.contentView, getItemList(position));
        bindData(position, holder);
        return convertView;
    }

    @Override
    protected void bindData(int position, MyViewHolder holder) {

    }

    abstract protected ParentViewHolder bindViewHolder(View view);

    abstract protected void bindData(int position, ParentViewHolder holder);

    abstract protected ArrayList<?> getItemList(int position);

    // 创建childItem View
    protected abstract View createItemView(Context context);

    // 填充ChildItem View的内容
    protected abstract void bindItemData(int position, Object obj, ItemViewHolder holder);

    Stack<View> itemStack = new Stack<>();
    Stack<View> lineStack = new Stack<>();
    Stack<LinearLayout> contentStack;

    // 清空容器
    private void clearContentView(LinearLayout contentView){
        int childCount = contentView.getChildCount();
        for (int i = childCount - 1; i > -1; i--) { // 遍历移除所有子view
            View view = contentView.getChildAt(i);
            contentView.removeView(view);
            if (view.getTag(R.id.tag_type) != null) {
                if (view.getTag(R.id.tag_type).equals("content")) { // 当前是一个"行"的容器, 继续遍历里面的item
                    LinearLayout linearLayout = (LinearLayout) view;
                    int lineChildCount = linearLayout.getChildCount();
                    for (int j = lineChildCount - 1; j > -1; j--) {
                        View itemView = linearLayout.getChildAt(j);
                        itemStack.add(itemView);
                        linearLayout.removeView(itemView);
                    }
                    contentStack.add(linearLayout);
                } else { // 往item的栈里添加一个缓存
                    itemStack.add(view);
                }
            } else { // 往分隔线的栈里添加一个缓存
                lineStack.add(view);
            }
        }
    }

    // 初始化显示childItem的容器
    protected synchronized void initContentView(int position, LinearLayout contentView, ArrayList<?> list) {
        clearContentView(contentView);
        if (list == null || list.isEmpty()) {
            return;
        }
        int size = list.size();
        if(columNum == 1){ // 每个item一行
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    contentView.addView(getLineView(), lineParams);
                }
                View v1 = getItemView(position, list.get(i));
                contentView.addView(v1, itemParam);
            }
        }else{
            int line = (int) Math.ceil(size / (float) columNum);
            for (int i = 0; i < line; i++) {
                if (i > 0) {
                    contentView.addView(getLineView(), lineParams);
                }
                int p = i * columNum;
                View v1 = getItemView(position, list.get(p));
                if (p == size - 1) { // 如果换行的第一个item刚好是最后一个, 就不需要创建一个"行"的容器
                    contentView.addView(v1, itemParam);
                } else { // 创建一个"行"的容器,  并遍历添加属于这一行的item
                    LinearLayout linearLayout = getContentView();
                    linearLayout.addView(v1, itemParam);
                    for (int j = 1; j < columNum; j++) {
                        int index = p + j;
                        if (index < size) { // 还没有超过数组的总数, 继续添加
                            View vj = getItemView(position, list.get(index));
                            linearLayout.addView(vj, rightParams);
                        } else {
                            break;
                        }
                    }
                    contentView.addView(linearLayout);
                }
            }
        }
    }

    private LinearLayout getContentView() {
        if (contentStack.isEmpty()) {
            LinearLayout linearLayout = new LinearLayout(context);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(lp);
            linearLayout.setBackgroundResource(R.color.divider_color);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setTag(R.id.tag_type, "content");
            return linearLayout;
        } else {
            return contentStack.pop();
        }
    }

    private synchronized View getItemView(int position, Object obj) {
        View v;
        ItemViewHolder holder;
        if (itemStack.isEmpty()) {
            v = createItemView(context);
            holder = bindItemViewHolder(v);
            v.setTag(R.id.tag_view, holder);
            v.setTag(R.id.tag_type, "item");
        } else {
            v = itemStack.pop();
            holder = (ItemViewHolder) v.getTag(R.id.tag_view);
        }
        bindItemData(position, obj, holder);
        return v;
    }

    protected abstract ItemViewHolder bindItemViewHolder( View v);

    private View getLineView() {
        if (lineStack.isEmpty()) {
            return View.inflate(context, R.layout.divider_view, null);
        } else {
            return lineStack.pop();
        }
    }

    public class ItemViewHolder {
        View itemView;

        public ItemViewHolder(View v) {
            itemView = v;
        }

        public View getItemView() {
            return itemView;
        }
    }

    public abstract class ParentViewHolder extends MyBaseAdapter.MyViewHolder {
        public LinearLayout contentView;

        public ParentViewHolder(View view) {
            super(view);
            contentView = getContentView(view);
        }

        // 填充child item的容器
        protected abstract LinearLayout getContentView(View view);
    }
}
