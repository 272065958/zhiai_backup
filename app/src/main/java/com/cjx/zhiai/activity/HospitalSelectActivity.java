package com.cjx.zhiai.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.HospitalBean;
import com.cjx.zhiai.http.HttpUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-19.
 */
public class HospitalSelectActivity extends BaseListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_select);
        setToolBar(true, null, R.string.register_hospital_select);

        initListView(false, false);
        setListViweDivider(ContextCompat.getDrawable(this, R.color.background_divider),
                getResources().getDimensionPixelOffset(R.dimen.grid_spacing));
        loadData();
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<HospitalBean>>(){}.getType()), "base/hospital",
                "area_name", "广东省", "page", "1", "limit", "300", "type", "1");
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new HospitalAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HospitalBean hb = (HospitalBean) parent.getAdapter().getItem(position);
        Intent data = new Intent();
        data.putExtra("name", hb.hospital_name);
        data.putExtra("id", hb.hospital_id);
        setResult(RESULT_OK, data);
        finish();
    }

    class HospitalAdapter extends MyBaseAdapter {

        public HospitalAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_register_hospital, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            HospitalBean hb = (HospitalBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            ho.nameView.setText(hb.hospital_name);
        }

        class ViewHolder extends MyViewHolder {
            TextView nameView;
            public ViewHolder(View view) {
                super(view);
                nameView = (TextView) view.findViewById(R.id.hospital_name);
            }
        }
    }
}
