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
import com.cjx.zhiai.advisory.ChatActivity;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.ReservationBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.util.Tools;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.easeui.EaseConstant;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-05.
 */
public class ReservationHistoryActivity extends BaseListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        setToolBar(true, null, R.string.my_appointment);
        initListView(false, false);
        setListViweDivider(ContextCompat.getDrawable(this, R.color.divider_color),
                getResources().getDimensionPixelOffset(R.dimen.auto_margin));
        loadData();
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<ReservationBean>>() {
                }.getType()), "base/getHistory", "user_id", MyApplication.getInstance().user.user_id,
                "query_type", "2", "page", "1", "limit", "100");
    }

    @Override
    protected void onLoadResult(ArrayList<?> list){
        for(Object obj : list){
            ((ReservationBean)obj).format();
        }
        super.onLoadResult(list);
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
            return View.inflate(context, R.layout.item_reservation_history, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            ReservationBean rb = (ReservationBean) getItem(position);

            ho.timeView.setText(rb.bespeak_time);
            ho.contentView.setText(rb.bespeak_content);
            ho.nameView.setText(rb.office_name + "-" + rb.doctor_name);
            if (rb.state.equals("2")) {
                ho.statusView.setText("预约成功");
                ho.statusView.setTextColor(ContextCompat.getColor(context, R.color.main_color));
                ho.videoView.setVisibility(View.VISIBLE);
                ho.videoView.setTag(rb.doctor_id);
                ho.videoView.setTag(R.string.title_user_profile, ho.nameView.getText().toString());
            } else if(rb.state.equals("4")){
                ho.statusView.setText(R.string.patient_is_cancel);
                ho.statusView.setTextColor(ContextCompat.getColor(context, R.color.text_secondary_color));
                ho.videoView.setVisibility(View.VISIBLE);
                ho.videoView.setText("金额去向");
            } else {
                ho.statusView.setTextColor(ContextCompat.getColor(context, R.color.text_main_color));
                ho.videoView.setVisibility(View.GONE);
                Tools.setPatientState(ho.statusView, rb.state);
            }
        }

        class ViewHolder extends MyViewHolder implements View.OnClickListener {
            TextView statusView, timeView, nameView, contentView;
            TextView videoView;

            public ViewHolder(View v) {
                super(v);
                statusView = (TextView) v.findViewById(R.id.reservation_status);
                timeView = (TextView) v.findViewById(R.id.reservation_time);
                nameView = (TextView) v.findViewById(R.id.reservation_department);
                contentView = (TextView) v.findViewById(R.id.reservation_content);
                videoView = (TextView) v.findViewById(R.id.reservation_video);
                videoView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                String id = (String) v.getTag();
                if(id == null){
                    showToast("金额已原路退回,请注意查收");
                }else{
                    String title = (String) v.getTag(R.string.title_user_profile);
                    Intent chatIntent = new Intent(context, ChatActivity.class);
                    chatIntent.putExtra("title", title);
                    chatIntent.putExtra(EaseConstant.EXTRA_USER_ID, id);
                    startActivity(chatIntent);
                }
            }
        }
    }
}
