package com.cjx.zhiai.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.PatientBean;
import com.cjx.zhiai.http.HttpUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2017/1/23.
 */
public class ReserveDayManagerActivity extends BaseListActivity {
    String action;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        action = getIntent().getAction();
        setToolBar(true, null, action);

        initListView(false, false);
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            loadData();
            setResult(RESULT_OK);
        }
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<PatientBean>>() {
        }.getType()), "base/oneDayBespeak", "day", action, "page", "1", "limit", "100");
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new ReserveAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PatientBean pb = (PatientBean) parent.getAdapter().getItem(position);
        Intent intent = new Intent(this, PatientComfirmActivity.class);
        intent.putExtra("patient", pb);
        startActivityForResult(intent, 1);
    }
}
