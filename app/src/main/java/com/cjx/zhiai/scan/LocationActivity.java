package com.cjx.zhiai.scan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;


import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.util.LocationUtil;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/11/30.
 * 选择位置
 */
public class LocationActivity extends BaseListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        setToolBar(true, null, R.string.register_hospital_position);
        initListView(false, false);


        loadData();
    }

    @Override
    protected void loadData() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationUtil.getInstance().setLocationCallback(new LocationUtil.LocationCallback() {
                @Override
                public void callback(String lat, String lon) {
                    HttpUtils.getInstance().postEnqueue(LocationActivity.this, getMycallback(new TypeToken<ArrayList<String>>() {
                            }.getType()), "article/getAddress", "longitude", lat,
                            "latitude", lon);
                }
            }).startLocation(this);
        }else{
            ArrayList<String> list = new ArrayList<>();
            onLoadResult(list);
        }
    }

    @Override
    protected void onLoadResult(ArrayList<?> list) {
        ArrayList<String> org = (ArrayList<String>) list;
        org.add(0, "不显示位置");
        super.onLoadResult(org);
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new LocationAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_OK, new Intent((String) parent.getAdapter().getItem(position)));
        }
        finish();
    }

    class LocationAdapter extends MyBaseAdapter {
        public LocationAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_location, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            String text = (String) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            ho.positionView.setText(text);
        }

        class ViewHolder extends MyViewHolder {
            TextView positionView;

            public ViewHolder(View view) {
                super(view);
                positionView = (TextView) view.findViewById(R.id.position_view);
            }
        }
    }
}
