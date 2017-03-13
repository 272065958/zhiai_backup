package com.cjx.zhiai.advisory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.advisory.adapter.MedicineAdapter;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.MedicineBean;
import com.cjx.zhiai.http.HttpUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-29.
 */
public class MedicineActivity extends BaseListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);
        setToolBar(true, null, R.string.find_medicine);

        initListView(false, false);
        loadData();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find_medicine: // 找药
                Intent searchIntent = new Intent(this, MedicineSearchActivity.class);
                startActivity(searchIntent);
                break;
            case R.id.find_medicine_room: // 找药房
                Intent roomIntent = new Intent(this, MedicineRoomActivity.class);
                roomIntent.putExtra("title", R.string.find_medicine_room);
                startActivity(roomIntent);
                break;
        }
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<MedicineBean>>(){}.getType()),
                "HealingDrugs/selectMedicine", "type", "1", "limit", "100", "page", "1");
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new MedicineAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MedicineBean mb = (MedicineBean) parent.getAdapter().getItem(position);
        Intent intent = new Intent(this, MedicineDetailActivity.class);
        intent.setAction(mb.medicine_id);
        startActivity(intent);
    }
}
