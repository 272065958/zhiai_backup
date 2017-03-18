package com.cjx.zhiai.advisory;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.AdvisoryCommentBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.scan.ProblemAdapter;
import com.cjx.zhiai.util.Tools;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-31.
 * 评论图文咨询
 */
public class AdvisoryReplyActivity extends BaseListActivity {
    String bespeakId;
    EditText commentView;
    View commentContentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advisory_reply);
        setToolBar(true, null, R.string.main_advisory);

        bespeakId = getIntent().getAction();
        commentView = (EditText) findViewById(R.id.discover_comment_edit);
        initListView(false, false);
        setListViweDivider(null, getResources().getDimensionPixelOffset(R.dimen.auto_margin));
        loadData();
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<AdvisoryCommentBean>>() {
        }.getType()), "base/selectContentList", "bespeak_id", bespeakId, "page", "1", "limit", "100");
    }

    // 加载数据完成后调用
    @Override
    protected void onLoadResult(ArrayList<?> list) {
        if(list == null || list.isEmpty()){
            showToast("数据异常");
            return ;
        }
        for(Object obj : list){
            ((AdvisoryCommentBean)obj).format();
        }
        if(commentContentView == null){
            AdvisoryCommentBean acb = (AdvisoryCommentBean) list.get(0);
            addHeaderView(getHeader(acb));
            commentContentView = findViewById(R.id.advisory_comment_content);
            commentContentView.setVisibility(View.VISIBLE);
        }
        list.remove(0);
        super.onLoadResult(list);
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new CommentAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void submit(View view) {
        // 回复评论
        String commment = commentView.getText().toString();
        if (TextUtils.isEmpty(commment)) {
            showToast("请输入评论内容");
            return;
        }
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                showToast(response.errorMsg);
                commentView.setText(null);
                onRefresh();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/replyQuestion", "bespeak_id", bespeakId,
                "belong_to", MyApplication.getInstance().userType, "content", commment);
    }

    private View getHeader(AdvisoryCommentBean acb) {
        View view = View.inflate(this, R.layout.header_advisory_reply, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        if (!TextUtils.isEmpty(acb.picture_address)) {
            recyclerView.setAdapter(new ProblemAdapter(this, acb.picture_address.split(",")));
            recyclerView.setVisibility(View.VISIBLE);
        }
        ImageView headView = (ImageView) view.findViewById(R.id.discover_head);
        TextView nameView = (TextView) view.findViewById(R.id.discover_name);
        TextView contentView = (TextView) view.findViewById(R.id.advisory_content);
        TextView timeView = (TextView) view.findViewById(R.id.advisory_time);
        Tools.setImageInView(this, acb.head_image, headView);
        nameView.setText(acb.user_name);
        contentView.setText(acb.content);
        timeView.setText(acb.time);

        return view;
    }

    class CommentAdapter extends MyBaseAdapter {
        public CommentAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_advisory_comment, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            AdvisoryCommentBean acb = (AdvisoryCommentBean) getItem(position);
            Tools.setImageInView(context, acb.head_image, ho.headView);
            ho.nameView.setText(acb.user_name);
            ho.timeView.setText(acb.time);
            ho.contentView.setText(acb.content);
        }

        class ViewHolder extends MyViewHolder {
            ImageView headView;
            TextView nameView, timeView, contentView;

            public ViewHolder(View view) {
                super(view);
                headView = (ImageView) view.findViewById(R.id.discover_head);
                nameView = (TextView) view.findViewById(R.id.discover_name);
                timeView = (TextView) view.findViewById(R.id.discover_time);
                contentView = (TextView) view.findViewById(R.id.discover_content);
            }
        }
    }
}
