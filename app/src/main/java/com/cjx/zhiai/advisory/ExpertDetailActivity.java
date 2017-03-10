package com.cjx.zhiai.advisory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.DoctorBean;
import com.cjx.zhiai.bean.ExpertCommentBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.component.LoadListView;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.JsonParser;
import com.cjx.zhiai.util.Tools;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.easeui.EaseConstant;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-27.
 * 医生详情
 */
public class ExpertDetailActivity extends BaseActivity implements View.OnClickListener {

    CommentAdapter adapter;
    String userType, officeId;
    DoctorBean doctorBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        setToolBar(true, null, R.string.expert_detail);
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.advisory_video: // 视频预约
                Intent reserveIntent = new Intent(this, ExpertReservationActivity.class);
                reserveIntent.putExtra("doctor", doctorBean);
                reserveIntent.putExtra("office_id", officeId);
                startActivity(reserveIntent);
                break;
            case R.id.advisory_online: // 当isSelect时, 是图文问诊
                if (v.isSelected()) {
                    Intent intent = new Intent(this, AdvisoryImageActivity.class);
                    intent.setAction(doctorBean.user_id);
                    intent.putExtra("office_id", officeId);
                    startActivity(intent);
                } else {
                    Intent chatIntent = new Intent(this, ChatActivity.class);
                    chatIntent.putExtra("title", doctorBean.office_name + "-" + doctorBean.user_real_name);
                    chatIntent.putExtra(EaseConstant.EXTRA_USER_ID, doctorBean.user_id);
                    startActivity(chatIntent);
                }
                break;
        }
    }

    private void initView() {
        Intent intent = getIntent();
        doctorBean = (DoctorBean) intent.getSerializableExtra("doctor");
        userType = intent.getStringExtra("user_type");
        officeId = intent.getStringExtra("offices_id");
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setEnabled(false);
        LoadListView listView = (LoadListView) findViewById(R.id.list_view);
        listView.addHeaderView(getHeaderView(doctorBean));
        adapter = new CommentAdapter(null, this);
        listView.setAdapter(adapter);
        loadData();
    }

    // 获取headerView
    private View getHeaderView(DoctorBean doctor) {
        View v = View.inflate(this, R.layout.expert_detail_header, null);
        ImageView headView = (ImageView) v.findViewById(R.id.expert_head);
        TextView nameView = (TextView) v.findViewById(R.id.expert_name);
        TextView hospitalView = (TextView) v.findViewById(R.id.expert_hospital);
        TextView departmentView = (TextView) v.findViewById(R.id.expert_department);
        TextView postView = (TextView) v.findViewById(R.id.expert_post);
        TextView describeView = (TextView) v.findViewById(R.id.expert_describe);
        TextView expertView = (TextView) v.findViewById(R.id.expert_be_expert);
        TextView introduceView = (TextView) v.findViewById(R.id.expert_introduce);
        Tools.setImageInView(this, doctor.head_image, headView);
        nameView.setText(doctor.user_real_name);
        hospitalView.setText(doctor.hospital_name);
        departmentView.setText(doctor.office_name);
        postView.setText(doctor.position);
        describeView.setText(doctor.honor);
        expertView.setText(String.format(getString(R.string.expert_be_expert_format), doctor.skilled));
        introduceView.setText(doctor.doctor_summary);

        TextView onlineView = (TextView) v.findViewById(R.id.advisory_online);
        onlineView.setOnClickListener(this);
        if (userType.equals("1")) {
            View videoView = v.findViewById(R.id.advisory_video);
            videoView.setVisibility(View.VISIBLE);
            videoView.setOnClickListener(this);
            onlineView.setText(R.string.main_advisory);
            onlineView.setSelected(true); // 标记为图文问诊
        }
        return v;
    }

    private void loadData() {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
                ArrayList<ExpertCommentBean> list = JsonParser.getInstance().fromJson(response.datas,
                        new TypeToken<ArrayList<ExpertCommentBean>>() {
                        }.getType());
                displayData(list);
            }

            @Override
            public void error() {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/PatientCommentary", "page", "1", "limit", "100",
                "doctor_id", doctorBean.user_id);
    }

    private void displayData(ArrayList<?> list) {
        adapter.notifyDataSetChanged(list);
    }

    class CommentAdapter extends MyBaseAdapter {
        public CommentAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_expert_comment, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            ExpertCommentBean ecb = (ExpertCommentBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            ho.nameView.setText(ecb.user_name);
            ho.commentView.setText(ecb.content);
            ho.timeView.setText(ecb.date_time);
            Tools.setImageInView(context, ecb.head_image, ho.headView);
        }

        class ViewHolder extends MyViewHolder {
            TextView nameView, commentView, timeView;
            ImageView headView;

            public ViewHolder(View view) {
                super(view);
                nameView = (TextView) view.findViewById(R.id.expert_name);
                commentView = (TextView) view.findViewById(R.id.expert_comment);
                timeView = (TextView) view.findViewById(R.id.expert_comment_time);
                headView = (ImageView) view.findViewById(R.id.expert_head);
            }
        }
    }
}
