package com.cjx.zhiai.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.component.LoadListView;
import com.cjx.zhiai.component.tablayout.TabLayout;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by cjx on 2016/8/17.
 */
public abstract class BaseTreeActivity extends BaseActivity  implements TabLayout.OnTabSelectedListener,
        AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener{
    protected SparseArray<ArrayList<?>> treeList;
    protected SparseArray<String> idList;

    protected SwipeRefreshLayout refreshLayout;
    protected ListView listView;
    protected View loadView;
    protected View emptyView;
    protected TabLayout tabLayout;
    protected Intent currentIntent;

    protected MyBaseAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentIntent = getIntent();
        setContentView(currentIntent.getIntExtra("view", -1));
        setToolBar(true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, currentIntent.getStringExtra("title"));
        initView();
    }

    @Override
    public void onBackPressed() {
        if (tabLayout != null && tabLayout.getTabCount() > 1) {
            int position = tabLayout.getTabCount() - 1;
            tabLayout.removeTabAt(position);
            navigationChange(position - 1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getTag() != null || adapter == null) {
            return;
        }
        int position = tab.getPosition();
        int count = tabLayout.getTabCount();
        for (int i = count - 1; i > position; i--) {
            tabLayout.removeTabAt(i);
        }
        navigationChange(position);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onRefresh() {
        loadChildTree((String) listView.getTag());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TreeBean tb = (TreeBean) adapter.getItem(position);
        if (tb.isChild()) {
            returnPosition(tb);
        } else {
            TabLayout.Tab tab = tabLayout.newTab().setText(tb.getName());
            tabLayout.addTab(tab, true);
            loadChildTree(tb.getId());
        }
    }

    // 返回选中的item
    protected abstract void returnPosition(TreeBean tb);

    /**
     * 初始化界面控件
     */
    private void initView() {
        Intent intent = getIntent();
        treeList = new SparseArray<>();
        idList = new SparseArray<>();
        loadView = findViewById(R.id.loading_view);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        emptyView = findViewById(R.id.empty_view);
        listView.setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.divider_height));
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if(tabLayout != null){
            tabLayout.setOnTabSelectedListener(this);
            tabLayout.addTab(tabLayout.newTab().setText(intent.getStringExtra("tab_name")));
        }
    }

    // 设置是否可以下拉刷新
    protected void setRefreshEnable(boolean enable){
        refreshLayout.setEnabled(enable);
    }

    // 获取树列表
    protected void loadChildTree(String id) {
        listView.setTag(id); // 设置当前listview要显示的列表标识
        if(!refreshLayout.isRefreshing()){
            loadView.setVisibility(View.VISIBLE);
        }
        listView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);

        loadList(id);
    }

    // 点击导航栏后更新页面数据
    protected void navigationChange(int position) {
        loadView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        listView.setTag(idList.get(position));
        adapter.notifyDataSetChanged(treeList.get(position));
    }

    // 显示未提交退出提示对话框
//    private void showExitDialog() {
//        if (exitDialog == null) {
//            exitDialog = new TipDialog(this);
//            exitDialog.setText(R.string.tip_title, R.string.meter_exit, R.string.button_sure, R.string.button_cancel).setTipComfirmListener(new TipDialog.ComfirmListener() {
//                @Override
//                public void comfirm() {
//                    exitDialog.dismiss();
//                    finish();
//                }
//
//                @Override
//                public void cancel() {
//                    exitDialog.dismiss();
//                }
//            });
//        }
//        exitDialog.show();
//    }

    // 获取缓存的列表数据
    protected abstract void loadList(String id);

    protected MyCallbackInterface getMyCallbackInterface(String id, Type type){
        return new MyLoadCallback(id, type);
    }

    // 获取显示列表的适配器
    protected abstract MyBaseAdapter getMyBaseAdapter(ArrayList<?> list);

    protected void hideLoadView(){
        if(loadView.getVisibility() == View.VISIBLE){
            loadView.setVisibility(View.GONE);
        }
        if(refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
    }

    // 加载数据完成后调用
    protected void onLoadResult(ArrayList<?> list, String id){
        hideLoadView();
        displayData(list, id);
    }

    class MyLoadCallback implements MyCallbackInterface{
        String id;
        Type type;
        public MyLoadCallback(String id, Type type){
            this.id = id;
            this.type = type;
        }

        @Override
        public void success(ResultBean response) {
            ArrayList<?> list = JsonParser.getInstance().fromJson(response.datas, type);
            onLoadResult(list, id);
        }

        @Override
        public void error() {
            hideLoadView();
        }
    }

    // 显示数据
    protected void displayData(ArrayList<?> list, String id) {
        int position = tabLayout.getTabCount() - 1;
        treeList.append(position, list);
        idList.append(position, id);
        if (adapter == null) {
            adapter = getMyBaseAdapter(list);
            LoadListView listView = (LoadListView) findViewById(R.id.list_view);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        } else {
            adapter.notifyDataSetChanged(list);
        }
        if (adapter.getCount() == 0) {
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.empty_view).setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
}
