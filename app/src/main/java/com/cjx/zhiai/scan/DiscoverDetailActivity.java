package com.cjx.zhiai.scan;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.CommentBean;
import com.cjx.zhiai.bean.DiscoverBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.Tools;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-30.
 * 评论帖子
 */
public class DiscoverDetailActivity extends BaseListActivity implements View.OnClickListener{
    DiscoverBean discoverBean;
    EditText commentView;
    TextView commentCountView, praiseCountView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_detail);
        setToolBar(true, null, R.string.discover_detail);
        initListView(false, false);
        discoverBean = (DiscoverBean) getIntent().getSerializableExtra("discover");
        if(discoverBean != null){
            loadData();    
        }
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<CommentBean>>() {
        }.getType()), "article/selectCommentaryList", "article_id", discoverBean.article_id, "page", "1", "limit", "100");
    }

    @Override
    protected void onLoadResult(ArrayList<?> list) {
        if(commentView == null){
            addHeaderView(getHeaderView());
            addHeaderView(View.inflate(this, R.layout.divider_view, null));
            findViewById(R.id.discover_comment_content).setVisibility(View.VISIBLE);
            commentView = (EditText) findViewById(R.id.discover_comment_edit);
        }
        super.onLoadResult(list);
    }

    // 显示列表数据
    @Override
    protected void displayData(ArrayList<?> list) {
        if (adapter == null) {
            adapter = getAdapter(list);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged(list);
        }
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new CommentAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {
        praiseComment( "article_id", discoverBean.article_id, praiseCountView, "1");// 点赞
    }

    // 提交评论
    public void submit(View v) {
        //提交评论
        String comment = commentView.getText().toString();
        if(TextUtils.isEmpty(comment)){
            showToast(getString(R.string.discover_comment_null));
            return ;
        }
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                showToast(response.errorMsg);
                countAdd(commentCountView);
                commentView.setText(null);
                onRefresh();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "article/addCommentary", "article_id", discoverBean.article_id,
                "content", comment);
    }

    // 点赞评论
    private void praiseComment(String key, String id, final TextView textView, String type){
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                showToast(response.errorMsg);
                countAdd(textView);
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "article/addCommend", key, id, "type", type);
    }

    private View getHeaderView() {
        View v = View.inflate(this, R.layout.item_discover, null);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycle_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        ImageView headView = (ImageView) v.findViewById(R.id.discover_head);
        ImageView sexView = (ImageView) v.findViewById(R.id.discover_sex);
        TextView nameView = (TextView) v.findViewById(R.id.discover_name);
        TextView contentView = (TextView) v.findViewById(R.id.discover_content);
        TextView addressView = (TextView) v.findViewById(R.id.discover_address);
        TextView timeView = (TextView) v.findViewById(R.id.discover_time);
        praiseCountView = (TextView) v.findViewById(R.id.discover_praise_count);
        commentCountView = (TextView) v.findViewById(R.id.discover_comment_count);

        if (TextUtils.isEmpty(discoverBean.image_address)) {
            recyclerView.setVisibility(View.GONE);
            View bottomView = v.findViewById(R.id.discover_bottom_content);
            ((RelativeLayout.LayoutParams) bottomView.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.discover_content);
        } else {
            ProblemAdapter adapter = new ProblemAdapter(this, discoverBean.image_address.split(","));
            recyclerView.setAdapter(adapter);
        }

        Tools.setImageInView(this, discoverBean.head_image, headView);
        nameView.setText(TextUtils.isEmpty(discoverBean.user_name) ? discoverBean.user_real_name : discoverBean.user_name);
        if (TextUtils.isEmpty(discoverBean.sex)) {
            sexView.setVisibility(View.GONE);
        } else {
            sexView.setVisibility(View.VISIBLE);
            sexView.setImageResource(discoverBean.sex.equals("f") ? R.drawable.woman : R.drawable.man);
        }
        contentView.setText(discoverBean.content);
        addressView.setText(discoverBean.my_location);
        timeView.setText(discoverBean.article_time);
        praiseCountView.setText(discoverBean.commend);
        commentCountView.setText(discoverBean.commentary);
        v.findViewById(R.id.discover_praise).setOnClickListener(this);
        return v;
    }

    // 显示数量增加1
    private void countAdd(TextView textView){
        int count = Integer.parseInt(textView.getText().toString());
        count++;
        textView.setText(String.valueOf(count));
    }

    class CommentAdapter extends MyBaseAdapter {
        public CommentAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_discover_comment, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            CommentBean cb = (CommentBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            Tools.setImageInView(context, cb.head_image, ho.headView);
            ho.nameView.setText(TextUtils.isEmpty(cb.user_name) ? cb.user_real_name : cb.user_name);
            ho.contentView.setText(cb.mycontent);
            ho.praiseCountView.setText(cb.commend);
            ho.praiseView.setTag(R.id.discover_praise, cb.commentary_id);
        }

        class ViewHolder extends MyViewHolder implements View.OnClickListener {
            ImageView headView, sexView;
            TextView nameView, contentView, praiseCountView;
            View praiseView;
            public ViewHolder(View v) {
                super(v);
                headView = (ImageView) v.findViewById(R.id.discover_head);
                sexView = (ImageView) v.findViewById(R.id.discover_sex);
                nameView = (TextView) v.findViewById(R.id.discover_name);
                contentView = (TextView) v.findViewById(R.id.discover_content);
                praiseCountView = (TextView) v.findViewById(R.id.discover_praise_count);
                praiseView = v.findViewById(R.id.discover_praise);
                praiseView.setTag(R.id.discover_praise_count, praiseCountView);
                praiseView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                praiseComment("Commentary_id", (String)v.getTag(R.id.discover_praise), (TextView)v.getTag(R.id.discover_praise_count), "2");
            }
        }
    }
}
