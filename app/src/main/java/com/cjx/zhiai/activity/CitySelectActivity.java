package com.cjx.zhiai.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.cjx.zhiai.adapter.TreeAdapter;
import com.cjx.zhiai.base.BaseTreeActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.base.TreeBean;
import com.cjx.zhiai.bean.CityBean;
import com.cjx.zhiai.component.tablayout.TabLayout;
import com.cjx.zhiai.sqlite.CityDAO;

import java.util.ArrayList;

/**
 * Created by cjx on 2017/2/14.
 *
 * Intent intent = new Intent(this, CitySelectActivity.class);
 * intent.putExtra("title", "");
 * intent.putExtra("view", R.layout.activity_tree_select);
 * intent.putExtra("tab_name", "中国");
 * intent.putExtra("level", 3);
 * startActivityForResult(intent, 1);
 *
 */
public class CitySelectActivity extends BaseTreeActivity {

    int maxLevel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        maxLevel = currentIntent.getIntExtra("level", Integer.MAX_VALUE);
        loadList(null);
    }

    @Override
    protected void loadList(final String id) {
        int level = tabLayout.getTabCount();

        AsyncTask<String, Void, ArrayList<CityBean>> task = new AsyncTask<String, Void, ArrayList<CityBean>>() {
            @Override
            protected ArrayList<CityBean> doInBackground(String... params) {
                return CityDAO.getInstance(getApplicationContext()).getCity(params[0], params[1]);
            }

            @Override
            protected void onPostExecute(ArrayList<CityBean> cityBeans) {
                onLoadResult(cityBeans, id);
            }
        };
        task.execute(id, String.valueOf(level));
    }

    @Override
    protected MyBaseAdapter getMyBaseAdapter(ArrayList<?> list) {
        return new TreeAdapter(list, this);
    }

    @Override
    protected void returnPosition(TreeBean tb) {
        setResult(RESULT_OK, new Intent(tb.getName()));
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TreeBean tb = (TreeBean) adapter.getItem(position);
        if (tb.isChild() || tabLayout.getTabCount() == maxLevel) {
            returnPosition(tb);
        } else {
            TabLayout.Tab tab = tabLayout.newTab().setText(tb.getName());
            tabLayout.addTab(tab, true);
            loadChildTree(tb.getId());
        }
    }
}
