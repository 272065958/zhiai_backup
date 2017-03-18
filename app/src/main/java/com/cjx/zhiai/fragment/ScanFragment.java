package com.cjx.zhiai.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.DiscoverBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.component.LoadListView;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.scan.DiscoverDetailActivity;
import com.cjx.zhiai.scan.ProblemAdapter;
import com.cjx.zhiai.scan.ScanCreateActivity;
import com.cjx.zhiai.scan.ScanSearchActivity;
import com.cjx.zhiai.util.JsonParser;
import com.cjx.zhiai.util.Tools;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-26.
 * 发现模块
 */
public class ScanFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    final int RESULT_CREATE = 1, RESULT_COMMENT = 2;
    View view;

    protected LoadListView listView;
    protected SwipeRefreshLayout refreshLayout;
    View loadView, emptyView;
    MyBaseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_scan, null);
            view.findViewById(R.id.scan_search).setOnClickListener(this);
            view.findViewById(R.id.scan_write).setOnClickListener(this);
            initListView();
            loadData();
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case RESULT_CREATE:
                    onRefresh();
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_search:
                Intent searchIntent = new Intent(getActivity(), ScanSearchActivity.class);
                startActivity(searchIntent);
                break;
            case R.id.scan_write:
                Intent createIntent = new Intent(getActivity(), ScanCreateActivity.class);
                startActivityForResult(createIntent, RESULT_CREATE);
                break;
        }
    }

    protected void initListView() {
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        listView = (LoadListView) view.findViewById(R.id.list_view);
        loadView = view.findViewById(R.id.loading_view);
        emptyView = view.findViewById(R.id.empty_view);
        setListViweDivider(new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.background_divider)),
                getResources().getDimensionPixelOffset(R.dimen.auto_margin));
    }

    // 设置listView的分割线
    protected void setListViweDivider(Drawable divider, int dividerHeight) {
        listView.setDivider(divider);
        listView.setDividerHeight(dividerHeight);
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    /**
     * 加载数据
     */
    protected void loadData() {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                hideLoadView();
                Type type = new TypeToken<ArrayList<DiscoverBean>>() {
                }.getType();
                ArrayList<DiscoverBean> list = JsonParser.getInstance().fromJson(response.datas, type);
                if(list != null && !list.isEmpty()){
                    for(DiscoverBean db : list){
                        db.format();
                    }
                }
                onLoadResult(list);
            }

            @Override
            public void error() {
                hideLoadView();
            }
        };
        HttpUtils.getInstance().postEnqueue((BaseActivity) getActivity(), callbackInterface,
                "article/getArcitelList", "page", "1", "limit", "100");
    }

    protected void hideLoadView() {
        if (loadView.getVisibility() == View.VISIBLE) {
            loadView.setVisibility(View.GONE);
        }
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
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
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new ScanAdapter(list, (BaseActivity) getActivity());
    }

    class ScanAdapter extends MyBaseAdapter implements View.OnClickListener {
        public ScanAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            View view = View.inflate(context, R.layout.item_discover, null);
            view.setOnClickListener(this);
            return view;
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            DiscoverBean db = (DiscoverBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            Tools.setImageInView(context, db.head_image, ho.headView);
            ho.nameView.setText(TextUtils.isEmpty(db.user_name) ? db.user_real_name : db.user_name);
            if (TextUtils.isEmpty(db.sex)) {
                ho.sexView.setVisibility(View.GONE);
            } else {
                ho.sexView.setVisibility(View.VISIBLE);
                ho.sexView.setImageResource(db.sex.equals("f") ? R.drawable.woman : R.drawable.man);
            }
            ho.contentView.setText(db.content);
            ho.addressView.setText(db.my_location);
            ho.timeView.setText(db.article_time);
            ho.praiseView.setText(db.commend);
            ho.commentView.setText(db.commentary);
            if (TextUtils.isEmpty(db.image_address)) {
                ho.recyclerView.setVisibility(View.GONE);
                ((RelativeLayout.LayoutParams) ho.bottomView.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.discover_content);
            } else {
                ho.recyclerView.setVisibility(View.VISIBLE);
                ((RelativeLayout.LayoutParams) ho.bottomView.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.recycle_view);
                ProblemAdapter adapter = (ProblemAdapter) ho.recyclerView.getAdapter();
                adapter.notifyDataSetChanged(db.image_address.split(","));
            }
            ho.getView().setTag(R.id.scan_search, db);
        }

        @Override
        public void onClick(View v) {
            Intent discoverIntent = new Intent(context, DiscoverDetailActivity.class);
            DiscoverBean db = (DiscoverBean) v.getTag(R.id.scan_search);
            discoverIntent.putExtra("discover", db);
            startActivity(discoverIntent);
        }

        class ViewHolder extends MyViewHolder {
            View bottomView;
            RecyclerView recyclerView;
            ImageView headView, sexView;
            TextView nameView, contentView, addressView, timeView, praiseView, commentView;

            public ViewHolder(View v) {
                super(v);
                recyclerView = (RecyclerView) v.findViewById(R.id.recycle_view);
                LinearLayoutManager manager = new LinearLayoutManager(context);
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(new ProblemAdapter(getActivity(), null));

                headView = (ImageView) v.findViewById(R.id.discover_head);
                sexView = (ImageView) v.findViewById(R.id.discover_sex);
                nameView = (TextView) v.findViewById(R.id.discover_name);
                contentView = (TextView) v.findViewById(R.id.discover_content);
                addressView = (TextView) v.findViewById(R.id.discover_address);
                timeView = (TextView) v.findViewById(R.id.discover_time);
                praiseView = (TextView) v.findViewById(R.id.discover_praise_count);
                commentView = (TextView) v.findViewById(R.id.discover_comment_count);
                bottomView = v.findViewById(R.id.discover_bottom_content);
            }
        }
    }

}
