package com.cjx.zhiai.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.base.TreeBean;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/9/30.
 */
public class TreeAdapter extends MyBaseAdapter {

    public TreeAdapter(ArrayList<?> list, BaseActivity context) {
        super(list, context);
    }

    @Override
    protected View createView(Context context) {
        return View.inflate(context, R.layout.item_tree, null);
    }

    @Override
    protected MyViewHolder bindViewHolder(View view) {
        ViewHolder holder = new ViewHolder(view);
        holder.nameView = (TextView) view.findViewById(R.id.tree_name);
        return holder;
    }

    @Override
    protected void bindData(int position, MyViewHolder holder) {
        TreeBean tb = (TreeBean) getItem(position);
        ((ViewHolder) holder).nameView.setText(tb.getName());
    }

    class ViewHolder extends MyViewHolder {
        TextView nameView;

        public ViewHolder(View v) {
            super(v);
        }
    }
}