package com.cjx.zhiai.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.PostBean;
import com.cjx.zhiai.http.HttpUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-20.
 * 选择职称
 */
public class PostSelectActivity extends BaseListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        setToolBar(true, null, R.string.register_post_select);
        initListView(false, false);
        loadData();
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<PostBean>>(){}.getType()),
                "base/getPosition", "page", "1", "limit", "300");
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new PostAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PostBean hb = (PostBean) parent.getAdapter().getItem(position);
        Intent data = new Intent();
        data.putExtra("name", hb.position);
        data.putExtra("id", hb.p_id);
        setResult(RESULT_OK, data);
        finish();
    }

    class PostAdapter extends MyBaseAdapter {

        public PostAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_post_select, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            PostBean pb = (PostBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            ho.nameView.setText(pb.position);
        }

        class ViewHolder extends MyViewHolder {
            TextView nameView;
            public ViewHolder(View view) {
                super(view);
                nameView = (TextView) view.findViewById(R.id.post_name);
            }
        }
    }
}
