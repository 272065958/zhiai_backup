package com.cjx.zhiai.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.advisory.AdvisoryReplyActivity;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.AdvisoryHistoryBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-05.
 * 咨询历史
 */
public class AdvisoryHistoryActivity extends BaseListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        setToolBar(true, null, R.string.my_history);
        initListView(false, false);
        setListViweDivider(ContextCompat.getDrawable(this, R.color.divider_color),
                getResources().getDimensionPixelOffset(R.dimen.auto_margin));
        loadData();
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<AdvisoryHistoryBean>>() {
                }.getType()), "base/getHistory", "user_id", MyApplication.getInstance().user.user_id,
                "query_type", "1", "page", "1", "limit", "100");
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new HistoryAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class HistoryAdapter extends MyBaseAdapter {

        public HistoryAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_advisory_history, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            AdvisoryHistoryBean ahb = (AdvisoryHistoryBean) getItem(position);
            ho.nameView.setText(ahb.doctor_name);
            ho.timeView.setText(ahb.bespeak_time);
            ho.officeView.setText(ahb.office_name);
            switch (ahb.b_type){ // 0=图文咨询 1=视频咨询 2=在线咨询
                case "0":
                    ho.typeView.setText(R.string.main_advisory);
                    ho.statusView.setVisibility(View.GONE);
                    ho.commentView.setVisibility(View.GONE);
                    ho.replyView.setVisibility(View.VISIBLE);
                    ho.replyView.setTag(ahb.bespeak_id);
                    ho.praiseView.setVisibility(View.VISIBLE);
                    ho.praiseView.setTag(ahb);
                    break;
                case "1":
                    ho.typeView.setText(R.string.expert_advisory_video);
                    ho.statusView.setVisibility(View.VISIBLE);
                    ho.commentView.setVisibility(View.VISIBLE);
                    ho.commentView.setTag(ahb);
                    ho.replyView.setVisibility(View.GONE);
                    ho.praiseView.setVisibility(View.GONE);
                    break;
                case "2":
                    ho.typeView.setText(R.string.expert_advisory_online);
                    ho.statusView.setVisibility(View.VISIBLE);
                    ho.commentView.setVisibility(View.VISIBLE);
                    ho.commentView.setTag(ahb);
                    ho.replyView.setVisibility(View.GONE);
                    ho.praiseView.setVisibility(View.GONE);
                    break;
            }

        }

        class ViewHolder extends MyViewHolder implements View.OnClickListener {
            TextView typeView, nameView, timeView, officeView, statusView;
            View replyView, commentView, praiseView;
            public ViewHolder(View v) {
                super(v);
                typeView = (TextView) v.findViewById(R.id.advisory_type);
                replyView = v.findViewById(R.id.advisory_reply);
                replyView.setOnClickListener(this);
                commentView = v.findViewById(R.id.advisory_comment);
                praiseView = v.findViewById(R.id.advisory_praise);
                statusView = (TextView) v.findViewById(R.id.advisory_status);
                nameView = (TextView) v.findViewById(R.id.advisory_doctor);
                timeView = (TextView) v.findViewById(R.id.advisory_time);
                officeView = (TextView) v.findViewById(R.id.advisory_department);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.advisory_reply: // 回复
                        Intent intent = new Intent(context, AdvisoryReplyActivity.class);
                        intent.setAction((String)v.getTag());
                        startActivity(intent);
                        break;
                    case R.id.advisory_comment: // 评论
                        AdvisoryHistoryBean ahb = (AdvisoryHistoryBean)v.getTag();
                        intent = new Intent(context, AdvisoryCommentActivity.class);
                        intent.putExtra("bespeak_id", ahb.bespeak_id);
                        intent.putExtra("doctor_id", ahb.doctor_id);
                        startActivity(intent);
                        break;
                    case R.id.advisory_praise: // 赞
                        praise((AdvisoryHistoryBean)v.getTag());
                        break;
                }
            }
        }
    }

    // 为咨询记录点赞
    private void praise(AdvisoryHistoryBean ahb){
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                showToast(response.errorMsg);
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/saveCommend", "bespeak_id", ahb.bespeak_id,
                "patient_id", MyApplication.getInstance().user.user_id, "doctor_id", ahb.doctor_id);
    }
}
