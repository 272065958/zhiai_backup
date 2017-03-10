package com.cjx.zhiai.advisory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.activity.CitySelectActivity;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseFilterActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.MedicineRoomBean;
import com.cjx.zhiai.bean.ValueTextBean;
import com.cjx.zhiai.http.HttpUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-30.
 */
public class MedicineRoomActivity extends BaseFilterActivity {

    int padding, showYOff;
    String latitude, longitude; // 上次定位用户的经纬度
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        padding = getResources().getDimensionPixelOffset(R.dimen.fab_margin);
        showYOff = getResources().getDimensionPixelOffset(R.dimen.grid_spacing);
        setListViweDivider(null, 0);
        canFilter = true;
        sortType = "1";
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        latitude = sharedPreferences.getString("latitude", "");
        longitude = sharedPreferences.getString("longitude", "");

        leftFilterString = sharedPreferences.getString("province", "广东省");
        int popupWidth = (((MyApplication)getApplication()).getScreen_width() - showYOff) / 2 - 2*padding;
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

    /**
     * 显示左边的筛选view
     */
    @Override
    protected void showLeftPopupWindow(View v) {
        Intent intent = new Intent(this, CitySelectActivity.class);
        intent.putExtra("title", getString(R.string.hospital_filter_address));
        intent.putExtra("view", R.layout.activity_tree_select);
        intent.putExtra("tab_name", "中国");
        startActivityForResult(intent, 1);
    }

    @Override
    protected ViewGroup createLeftPopupView() {
//        ValueTextBean[] items = new ValueTextBean[]{new ValueTextBean(null, "全国"), new ValueTextBean(null, "北京市"),
//                new ValueTextBean(null, "上海市"), new ValueTextBean(null, "广东省")};
//        return createPopupView(items, leftPopupClickListener);
        return null;
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

    @Override
    protected ValueTextBean[] getRightPopupItem() {
        return new ValueTextBean[]{new ValueTextBean("1", "智能排序"), new ValueTextBean("2", "距离排序")};
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<MedicineRoomBean>>(){}.getType()),
                "HealingDrugs/getDrugstoreList", "page", "1", "limit", "100", "area_name", leftFilterString, "type", sortType,
                "user_latitude", latitude, "user_longitude", longitude);
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new RoomAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, MedicineRoomDetailActivity.class);
        MedicineRoomBean mrb = (MedicineRoomBean) parent.getAdapter().getItem(position);
        intent.putExtra("room", mrb);
        startActivity(intent);
    }

    class RoomAdapter extends MyBaseAdapter {

        public RoomAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_medicine_room, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            MedicineRoomBean mrb = (MedicineRoomBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            ho.nameView.setText(mrb.drugstore_name);
            ho.addressView.setText(mrb.address);
        }

        class ViewHolder extends MyViewHolder {
            TextView nameView, addressView;
            public ViewHolder(View v) {
                super(v);
                nameView = (TextView) v.findViewById(R.id.medicine_room_name);
                addressView = (TextView) v.findViewById(R.id.medicine_room_address);
            }
        }
    }

}
