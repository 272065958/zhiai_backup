package com.cjx.zhiai.advisory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.CurePlanBean;
import com.cjx.zhiai.http.HttpUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-07.
 * 治疗方案
 */
public class CurePlanActivity extends BaseListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        setToolBar(true, null, R.string.advisory_project);
        initListView(false, false);
        loadData();
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<CurePlanBean>>() {
        }.getType()), "base/selectRecipeList", "patient_id", MyApplication.getInstance().user.user_id, "page", "1", "limit", "100");
    }

    @Override
    protected void onLoadResult(ArrayList<?> list) {
        for(Object obj : list){
            ((CurePlanBean)obj).format();
        }
        super.onLoadResult(list);
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new PlanAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CurePlanBean cpb = (CurePlanBean) parent.getAdapter().getItem(position);
        Intent intent = new Intent(this, CurePlanDetailActivity.class);
        intent.putExtra("cure_plan", cpb);
        startActivity(intent);
    }

    class PlanAdapter extends MyBaseAdapter {

        PlanAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_cure_plan, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            CurePlanBean cpb = (CurePlanBean) getItem(position);
            ho.nameView.setText(cpb.office_name+"-"+cpb.user_real_name);
            ho.timeView.setText(cpb.r_time);
        }

        class ViewHolder extends MyViewHolder {
            TextView nameView, timeView;
            public ViewHolder(View v) {
                super(v);
                nameView = (TextView) v.findViewById(R.id.cure_plan_doctor);
                timeView = (TextView) v.findViewById(R.id.cure_plan_date);
            }
        }
    }
}
