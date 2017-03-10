package com.cjx.zhiai.base;

import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.component.LoadListView;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-30.
 * 加载列表数据的基类
 */
public abstract class BaseListActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    protected LoadListView listView;
    protected SwipeRefreshLayout refreshLayout;
    protected View loadView, emptyView;
    protected MyBaseAdapter adapter;

    protected void initListView(boolean refresh, boolean loadMore) {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        if (refresh) {
            refreshLayout.setOnRefreshListener(this);
        } else {
            refreshLayout.setEnabled(false);
        }
        listView = (LoadListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        loadView = findViewById(R.id.loading_view);
        emptyView = findViewById(R.id.empty_view);
    }

    // 设置listView的分割线
    protected void setListViweDivider(Drawable divider, int dividerHeight) {
        if (listView != null) {
            listView.setDivider(divider);
            listView.setDividerHeight(dividerHeight);
        }
    }

    // 忘listview添加一个headerview
    protected void addHeaderView(View header) {
        if (listView != null) {
            listView.setHeaderDividersEnabled(true);
            listView.addHeaderView(header);
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    /**
     * 加载数据
     */
    protected abstract void loadData();

    protected void hideLoadView() {
        if (loadView.getVisibility() == View.VISIBLE) {
            loadView.setVisibility(View.GONE);
        }
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    ListCallbackInterface callbackInterface;
    protected MyCallbackInterface getMycallback(Type type){
        if(callbackInterface == null){
            callbackInterface = new ListCallbackInterface(type);
        }
        return callbackInterface;
    }

    class ListCallbackInterface implements MyCallbackInterface{
        Type type;
        ListCallbackInterface(Type type){
            this.type = type;
        }

        @Override
        public void success(ResultBean response) {
            ArrayList<?> list = JsonParser.getInstance().fromJson(response.datas, type);
            onLoadResult(list);
        }

        @Override
        public void error() {
            hideLoadView();
        }
    }

    // 加载数据完成后调用
    protected void onLoadResult(ArrayList<?> list) {
        hideLoadView();
        displayData(list);
    }

    // 显示列表数据
    protected void displayData(ArrayList<?> list) {
        if (adapter == null) {
            adapter = getAdapter(list);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged(list);
        }
        if (list == null || list.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 返回显示内容的adapter
     *
     * @return
     */
    protected abstract MyBaseAdapter getAdapter(ArrayList<?> list);
}
