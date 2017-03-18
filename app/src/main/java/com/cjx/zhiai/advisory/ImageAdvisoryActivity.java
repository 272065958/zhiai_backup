package com.cjx.zhiai.advisory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.AdvisoryBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.scan.ProblemAdapter;
import com.cjx.zhiai.util.Tools;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-31.
 * 图文咨询
 */
public class ImageAdvisoryActivity extends BaseListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        setToolBar(true, null, R.string.main_advisory);
        initListView(false, false);
        setListViweDivider(null, getResources().getDimensionPixelOffset(R.dimen.auto_margin));
        loadData();
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<AdvisoryBean>>() {
        }.getType()), "base/getHistory", "user_id", MyApplication.getInstance().user.user_id, "query_type", "3",
                "page", "1", "limit", "100");
    }

    // 加载数据完成后调用
    @Override
    protected void onLoadResult(ArrayList<?> list) {
        if(list == null || list.isEmpty()){
            showToast("数据异常");
            return ;
        }
        for(Object obj : list){
            ((AdvisoryBean)obj).format();
        }
        super.onLoadResult(list);
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new AdvisoryAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class AdvisoryAdapter extends MyBaseAdapter {
        public AdvisoryAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_image_advisory, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            AdvisoryBean ab = (AdvisoryBean) getItem(position);
            ho.advisoryView.setTag(R.id.advisory_content, ab);
            if(TextUtils.isEmpty(ab.picture_address)){
                ho.recyclerView.setVisibility(View.GONE);
            }else{
                ho.recyclerView.setVisibility(View.VISIBLE);
                ProblemAdapter adapter = (ProblemAdapter) ho.recyclerView.getAdapter();
                adapter.notifyDataSetChanged(ab.picture_address.split(","));
            }
            Tools.setImageInView(context, ab.head_image, ho.headView);
            ho.nameView.setText(ab.user_name);
            ho.contentView.setText(ab.content);
            ho.timeView.setText(ab.time);
        }

        class ViewHolder extends MyViewHolder implements View.OnClickListener {
            View advisoryView;
            RecyclerView recyclerView;
            ImageView headView, sexView;
            TextView nameView, contentView, timeView;

            public ViewHolder(View view) {
                super(view);
                headView = (ImageView) view.findViewById(R.id.discover_head);
                nameView = (TextView) view.findViewById(R.id.discover_name);
                contentView = (TextView) view.findViewById(R.id.advisory_content);
                timeView = (TextView) view.findViewById(R.id.advisory_time);
                advisoryView = view.findViewById(R.id.advisory_reply);
                advisoryView.setOnClickListener(this);

                recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
                LinearLayoutManager manager = new LinearLayoutManager(context);
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(new ProblemAdapter(context, null));
            }

            @Override
            public void onClick(View v) {
                AdvisoryBean ab = (AdvisoryBean) v.getTag(R.id.advisory_content);
                Intent intent = new Intent(context, AdvisoryReplyActivity.class);
                intent.setAction(ab.bespeak_id);
                startActivity(intent);
            }
        }
    }
}
