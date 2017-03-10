package com.cjx.zhiai.scan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.activity.FeaturedActivity;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseSearchActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.HotBean;
import com.cjx.zhiai.bean.NewBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.JsonParser;
import com.cjx.zhiai.util.Tools;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-30.
 * 发现-搜索
 */
public class ScanSearchActivity extends BaseSearchActivity implements TextWatcher, View.OnClickListener {

    View hotView, countViewContent;
    TextView countView;
    String quertText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSearch(R.drawable.background_find, getString(R.string.discover_search_hint), true, this);
        loadHotData();
    }

    @Override
    protected void searchValue(String quertText) {
        this.quertText = quertText;
        findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
        loadData();
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<NewBean>>() {
        }.getType()), "article/newsList", "content", quertText, "news_type", "2", "page", "1", "limit", "100");
    }

    @Override
    protected void onLoadResult(ArrayList<?> list) {
        hotView.setVisibility(View.GONE);
        if (countViewContent == null) {
            addCountView();
        } else if (countViewContent.getVisibility() == View.GONE) {
            countViewContent.setVisibility(View.VISIBLE);
        }
        countView.setText(String.format(getString(R.string.descover_search_tip), list == null ? 0 : list.size()));
        super.onLoadResult(list);
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new SearchAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewBean nb = (NewBean) parent.getAdapter().getItem(position);
        Intent featuredIntent = new Intent(this, FeaturedActivity.class);
        featuredIntent.putExtra("title", R.string.descover_imformation);
        featuredIntent.setAction(nb.news_id);
        startActivity(featuredIntent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) { // 没有搜索文字
            hotView.setVisibility(View.VISIBLE);
            hideListView();
            countViewContent.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        String tip = ((TextView)v).getText().toString();
        searchView.setText(tip);
        searchView.setSelection(tip.length());
        search(searchView);
    }

    // 加载热门
    private void loadHotData() {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                Type type = new TypeToken<ArrayList<HotBean>>() {
                }.getType();
                ArrayList<HotBean> list = JsonParser.getInstance().fromJson(response.datas, type);
                initHotView(list);
            }

            @Override
            public void error() {

            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "article/getHotSearch");
    }

    // 初始化热门搜索
    private void initHotView(ArrayList<HotBean> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        ViewStub stub = (ViewStub) findViewById(R.id.hot_search);
        hotView = stub.inflate();
        LinearLayout content = (LinearLayout) hotView.findViewById(R.id.hot_tip_content);
        content.addView(getHotItemView(list.get(0)));
        int size = list.size();
        if (size > 1) {
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            llp.leftMargin = getResources().getDimensionPixelOffset(R.dimen.bigger_margin);
            for (int i = 1; i < size; i++) {
                content.addView(getHotItemView(list.get(i)), llp);
            }
        }
    }

    // 创建一个热门搜索的item
    private View getHotItemView(HotBean hb) {
        TextView tipView = (TextView) View.inflate(this, R.layout.item_hot_tip, null);
        tipView.setText(hb.h_name);
        tipView.setTag(hb.h_id);
        tipView.setOnClickListener(this);
        return tipView;
    }

    // 添加相关资讯的view
    private void addCountView() {
        countViewContent = View.inflate(this, R.layout.title_view, null);
        countView = (TextView) countViewContent.findViewById(R.id.title_tip_view);
        countViewContent.setId(R.id.title_tip_view);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) listViewContent.getLayoutParams();
        rlp.addRule(RelativeLayout.BELOW, R.id.title_tip_view);
        rlp.topMargin = 0;
        rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.BELOW, R.id.search_bg);
        rlp.topMargin = getResources().getDimensionPixelOffset(R.dimen.button_height);
        ((ViewGroup) listViewContent.getParent()).addView(countViewContent, rlp);
    }

    class SearchAdapter extends MyBaseAdapter {
        public SearchAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_imformation, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            NewBean nb = (NewBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            ho.titleView.setText(nb.title);
            ho.describeView.setText(nb.summary);
            Tools.setImageInView(context, nb.min_image, ho.iconView);
        }

        class ViewHolder extends MyViewHolder {
            ImageView iconView;
            TextView titleView, describeView;

            public ViewHolder(View view) {
                super(view);
                iconView = (ImageView) view.findViewById(R.id.imformation_icon);
                titleView = (TextView) view.findViewById(R.id.imformation_name);
                describeView = (TextView) view.findViewById(R.id.imformation_describe);
            }
        }
    }
}
