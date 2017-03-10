package com.cjx.zhiai.advisory.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.MedicineBean;
import com.cjx.zhiai.util.Tools;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-29.
 */
public class MedicineAdapter extends MyBaseAdapter {

    public MedicineAdapter(ArrayList<?> list, BaseActivity context) {
        super(list, context);
    }

    @Override
    protected View createView(Context context) {
        return View.inflate(context, R.layout.item_medicine, null);
    }

    @Override
    protected MyViewHolder bindViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    protected void bindData(int position, MyViewHolder holder) {
        ViewHolder ho = (ViewHolder) holder;
        MedicineBean mb = (MedicineBean) getItem(position);
        Tools.setImageInView(context, mb.min_picture, ho.iconView);
        ho.nameView.setText(mb.medicine_name);
        ho.effectView.setText(mb.effect);
    }

    class ViewHolder extends MyViewHolder {
        ImageView iconView;
        TextView nameView, effectView;
        public ViewHolder(View view) {
            super(view);
            iconView = (ImageView) view.findViewById(R.id.medicine_icon);
            nameView = (TextView) view.findViewById(R.id.medicine_name);
            effectView = (TextView) view.findViewById(R.id.medicine_effect);
        }
    }
}