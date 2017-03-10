package com.cjx.zhiai.manager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.PatientBean;
import com.cjx.zhiai.util.Tools;

import java.util.ArrayList;

/**
 * Created by cjx on 2017/1/23.
 */
public class ReserveAdapter extends MyBaseAdapter {
    public ReserveAdapter(ArrayList<?> list, BaseActivity context) {
        super(list, context);
    }

    @Override
    protected View createView(Context context) {
        return View.inflate(context, R.layout.item_reserve_manager, null);
    }

    @Override
    protected MyViewHolder bindViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    protected void bindData(int position, MyViewHolder holder) {
        PatientBean pb = (PatientBean) getItem(position);
        ViewHolder ho = (ViewHolder) holder;
        ho.getView().setTag(R.id.patient_content, pb);
        Tools.setImageInView(context, pb.head_image, ho.headView);
        ho.nameView.setText(pb.user_name);
        ho.sexView.setImageResource(pb.sex.equals("f") ? R.drawable.woman : R.drawable.man);
        ho.timeView.setText(String.format(context.getString(R.string.advisort_time_format), pb.bespeak_time.replace("=", " ")));
        ho.reseanView.setText(pb.bespeak_content);
        Tools.setPatientState(ho.stateView, pb.state);
    }

    class ViewHolder extends MyViewHolder {
        ImageView headView, sexView;
        TextView nameView, timeView, stateView, reseanView;
        public ViewHolder(View view) {
            super(view);
            headView = (ImageView) view.findViewById(R.id.reserve_head);
            sexView = (ImageView) view.findViewById(R.id.reserve_sex);
            nameView = (TextView) view.findViewById(R.id.reserve_name);
            timeView = (TextView) view.findViewById(R.id.patient_content);
            stateView = (TextView) view.findViewById(R.id.patient_state);
            reseanView = (TextView) view.findViewById(R.id.patient_resean);
        }
    }
}
