package com.cjx.zhiai.advisory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.activity.CitySelectActivity;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseFilterActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.HospitalBean;
import com.cjx.zhiai.bean.ValueTextBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.util.Tools;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-29.
 */
public class HospitalActivity extends BaseFilterActivity {

    int padding, showYOff;
    String latitude, longitude; // 上次定位用户的经纬度
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        padding = getResources().getDimensionPixelOffset(R.dimen.fab_margin);
        showYOff = getResources().getDimensionPixelOffset(R.dimen.grid_spacing);
        setListViweDivider(null, 0);

        sortType = "1";
        canFilter = true;
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        latitude = sharedPreferences.getString("latitude", "");
        longitude = sharedPreferences.getString("longitude", "");
        leftFilterString = sharedPreferences.getString("province", "广州市");

        // 初始化界面
        int popupWidth = ((MyApplication.getInstance()).getScreen_width() - showYOff) / 2 - 2 * padding;
        setLeftFilterText(leftFilterString, padding, showYOff, popupWidth);
        setRightFilterText(getString(R.string.online_expert_sort), padding, showYOff, popupWidth);
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            leftFilterString = data.getAction();
            setLeftFilterText(leftFilterString);
            onRefresh();
        }
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<HospitalBean>>() {
        }.getType()), "base/hospital", "area_name", leftFilterString, "type", sortType, "page", "1", "limit", "100",
                "user_latitude", latitude, "user_longitude", longitude);
    }

    /**
     * 显示左边的筛选view
     */
    @Override
    protected void showLeftPopupWindow(View v) {
        Intent intent = new Intent(this, CitySelectActivity.class);
        intent.putExtra("title", getString(R.string.hospital_filter_address));
        intent.putExtra("view", R.layout.activity_tree_select);
        intent.putExtra("tab_name", "中国");
        intent.putExtra("level", 2);
        startActivityForResult(intent, 1);
    }

    @Override
    protected ViewGroup createLeftPopupView() {
//        ValueTextBean[] items = new ValueTextBean[]{new ValueTextBean(null, "全国"), new ValueTextBean(null, "北京市"),
//                new ValueTextBean(null, "上海市"), new ValueTextBean(null, "广东省")};
//        return createPopupView(items, leftPopupClickListener);
        return null;
    }

    @Override
    protected ValueTextBean[] getRightPopupItem() {
        return new ValueTextBean[]{new ValueTextBean("1", "智能排序"), new ValueTextBean("2", "距离排序")};
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new HospitalAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

//    View.OnClickListener leftPopupClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (v.isSelected()) {
//                return;
//            }
//            TextView textView = (TextView) v.findViewById(R.id.item_text_view);
//            itemSelect((ViewGroup)leftPopupView, v);
//            hideLeftPopopWindow();
//            setLeftFilterText(textView.getText());
//        }
//    };


    class HospitalAdapter extends MyBaseAdapter {

        public HospitalAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_hospital, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            HospitalBean hb = (HospitalBean) getItem(position);
            Tools.setImageInView(context, hb.head_image, ho.iconView);
            ho.nameView.setText(hb.hospital_name);
            ho.nameView.setText(hb.grade_name);
            ho.phoneView.setText(hb.hospital_phone);
            ho.addressView.setText(hb.hospital_address);
        }

        class ViewHolder extends MyViewHolder {
            ImageView iconView;
            TextView nameView, gradeView, addressView, phoneView;
            public ViewHolder(View v) {
                super(v);
                iconView = (ImageView) v.findViewById(R.id.hospital_icon);
                nameView = (TextView) v.findViewById(R.id.hospital_name);
                gradeView = (TextView) v.findViewById(R.id.hospital_describe);
                addressView = (TextView) v.findViewById(R.id.hospital_address);
                phoneView = (TextView) v.findViewById(R.id.hospital_tel);
            }
        }
    }

}
