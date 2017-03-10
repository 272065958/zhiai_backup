package com.cjx.zhiai.advisory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.advisory.adapter.MedicineAdapter;
import com.cjx.zhiai.base.BaseSearchActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.MedicineBean;
import com.cjx.zhiai.http.HttpUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-30.
 * 发现-搜索
 */
public class MedicineSearchActivity extends BaseSearchActivity {

    String quertText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSearch(R.drawable.background_find, getString(R.string.discover_search_hint), false, null);
    }

    @Override
    protected void searchValue(String quertText) {
        this.quertText = quertText;
        findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
        loadData();
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<MedicineBean>>(){}.getType()),
                "HealingDrugs/selectMedicine", "page", "1", "limit", "100", "content", quertText, "type", "2");
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
