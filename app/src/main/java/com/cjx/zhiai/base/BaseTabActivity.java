package com.cjx.zhiai.base;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.adapter.TabPagerAdapter;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.component.LoadListView;
import com.cjx.zhiai.component.tablayout.TabLayout;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-25.
 */
public abstract class BaseTabActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        TabLayout.OnTabSelectedListener {
    protected int page, limit;
    protected LoadListView[] listViews;
    protected SwipeRefreshLayout[] refreshLayouts;
    protected View[] loadViews;
    protected View[] emptyView;
    protected MyBaseAdapter[] adapters;
    protected TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_list);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_LOGIN:
                    executeLoad(tabLayout.getSelectedTabPosition());
                    break;
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        if (listViews[position].getTag() != null) {
            listViews[position].setTag(null);
            executeLoad(position);
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    // 初始化界面
    protected void initPagerView(String[] titles, boolean refresh, boolean loadMore) {
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        int pageCount = titles.length;
        listViews = new LoadListView[pageCount];
        loadViews = new View[pageCount];
        refreshLayouts = new SwipeRefreshLayout[pageCount];
        emptyView = new View[pageCount];
        adapters = new MyBaseAdapter[pageCount];
        View views[] = new View[pageCount];
        for (int i = 0; i < pageCount; i++) {
            View v = View.inflate(this, R.layout.item_list_view, null);
            SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh_layout);
            if (refresh) {
                refreshLayout.setColorSchemeResources(new int[]{R.color.colorPrimary});
                refreshLayout.setOnRefreshListener(new MyRefreshListener(i));
            } else {
                refreshLayout.setEnabled(false);
            }

            LoadListView listView = (LoadListView) v.findViewById(R.id.list_view);
//            listView.setDivider();
//            listView.setDividerHeight();
            listView.setOnItemClickListener(this);
            views[i] = v;
            listView.setTag(true);
            refreshLayouts[i] = refreshLayout;
            listViews[i] = listView;
            loadViews[i] = v.findViewById(R.id.loading_view);
            emptyView[i] = v.findViewById(R.id.empty_view);
        }
        TabPagerAdapter adapter = new TabPagerAdapter(views, titles);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setOnTabSelectedListener(this);
        tabLayout.setupWithViewPager(viewPager);
    }

    // 设置listView的分割线
    protected void setListViweDivider(Drawable divider, int dividerHeight) {
        if (listViews != null) {
            for(LoadListView llv : listViews){
                llv.setDivider(divider);
                llv.setDividerHeight(dividerHeight);
            }
        }
    }

    // 收到刷新广播
    @Override
    protected void onBroadcastReceive(Intent intent){
        super.onBroadcastReceive(intent);
        refresh();
    }

    // 整体刷新
    protected void refresh() {
        int position = tabLayout.getSelectedTabPosition();
        int count = tabLayout.getTabCount();
        for (int i = 0; i < count; i++) {
            if (i != position) {
                listViews[i].setTag(true); // 标记当前所有界面都要刷新
            }
        }
        executeLoad(position);
    }

    protected void executeLoad(int position){
        if(!refreshLayouts[position].isRefreshing()){
            loadViews[position].setVisibility(View.VISIBLE);
        }
        loadData(position);
    }

    protected void hideLoadView(int position){
        SwipeRefreshLayout refreshLayout = refreshLayouts[position];
        View loadView = loadViews[position];
        if(loadView.getVisibility() == View.VISIBLE){
            loadView.setVisibility(View.GONE);
        }
        if(refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
    }

    // 加载数据列表
    protected abstract void loadData(int position);

    protected abstract MyBaseAdapter getMyBaseAdapter(int position, ArrayList<?> list);

    // 加载数据完成后调用
    protected void onLoadResult(int position, ArrayList<?> list){
        hideLoadView(position);
        displayData(position, list);
    }

    protected MyCallbackInterface getMyCallbackInterface(int position, Type type){
        return new TabCallInterface(position, type);
    }

    // 显示数据列表
    protected void displayData(int position, ArrayList<?> list) {
        MyBaseAdapter adapter = adapters[position];
        if (adapter == null) {
            adapter = getMyBaseAdapter(position, list);
            ListView listView = listViews[position];
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);
            adapters[position] = adapter;
        } else {
            adapter.notifyDataSetChanged(list);
        }
        if (adapter.getCount() == 0) {
            emptyView[position].setVisibility(View.VISIBLE);
        } else {
            emptyView[position].setVisibility(View.GONE);
        }
    }

    class MyRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        int currentPosition;

        public MyRefreshListener(int position) {
            currentPosition = position;
        }

        @Override
        public void onRefresh() {
            executeLoad(currentPosition);
        }
    }

    class TabCallInterface implements MyCallbackInterface{
        int position;
        Type type;
        public TabCallInterface(int position, Type type){
            this.position = position;
            this.type = type;
        }

        @Override
        public void success(ResultBean response) {
            ArrayList<?> list = new Gson().fromJson(response.datas, type);
            onLoadResult(position, list);
        }

        @Override
        public void error() {
            onLoadResult(position, null);
        }
    }
}