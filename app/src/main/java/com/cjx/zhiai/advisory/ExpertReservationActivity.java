package com.cjx.zhiai.advisory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.DoctorBean;
import com.cjx.zhiai.bean.ReserveTimeBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.bean.TimeBean;
import com.cjx.zhiai.component.LoadListView;
import com.cjx.zhiai.dialog.ItemSelectDialog;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.JsonParser;
import com.cjx.zhiai.util.Tools;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-29.
 * 预约界面
 */
public class ExpertReservationActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    TimeAdapter adapter;
    String officeId;
    DoctorBean doctorBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        setToolBar(true, null, R.string.expert_advisory_video);
        initView();
    }

    private void initView(){
        Intent intent = getIntent();
        doctorBean = (DoctorBean) intent.getSerializableExtra("doctor");
        officeId = intent.getStringExtra("office_id");
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setEnabled(false);
        LoadListView listView = (LoadListView) findViewById(R.id.list_view);
        listView.addHeaderView(getHeaderView(doctorBean));
        adapter = new TimeAdapter(null, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        loadData();
    }

    // 获取headerView
    private View getHeaderView(DoctorBean doctor){
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        View v = View.inflate(this, R.layout.expert_header, linearLayout);
        ImageView headView = (ImageView) v.findViewById(R.id.expert_head);
        TextView nameView = (TextView) v.findViewById(R.id.expert_name);
        TextView hospitalView = (TextView) v.findViewById(R.id.expert_hospital);
        TextView departmentView = (TextView) v.findViewById(R.id.expert_department);
        TextView postView = (TextView) v.findViewById(R.id.expert_post);
        TextView priceView = (TextView) v.findViewById(R.id.expert_price);
        TextView expertView = (TextView) v.findViewById(R.id.expert_be_expert);
        Tools.setImageInView(this, doctor.head_image, headView);
        nameView.setText(doctor.user_real_name);
        hospitalView.setText(doctor.hospital_name);
        departmentView.setText(doctor.office_name);
        postView.setText(doctor.position);
        priceView.setText(String.format(getString(R.string.expert_reservation_format),
                (new BigDecimal(doctor.price).divide(new BigDecimal("100"))).toString()));
        expertView.setText(String.format(getString(R.string.expert_be_expert_format), doctor.skilled));
        return linearLayout;
    }

    private void loadData(){
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
                ArrayList<ReserveTimeBean> list = JsonParser.getInstance().fromJson(response.datas,
                        new TypeToken<ArrayList<ReserveTimeBean>>(){}.getType());
                if(list != null && list.size() > 0){
                    for(ReserveTimeBean rtb : list){
                        rtb.format();
                    }
                }
                displayData(list);
            }

            @Override
            public void error() {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/queryDoctorTime", "doctor_id", doctorBean.user_id);
    }

    private void displayData(ArrayList<?> list){
        adapter.notifyDataSetChanged(list);
        if(list == null || list.size() == 0){
            TextView emptyView = (TextView) findViewById(R.id.empty_view);
            emptyView.setText("没有可预约时间");
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    public void select(View v){

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ReserveTimeBean rtb = (ReserveTimeBean) adapter.getItem(position - 1);
        selectTime(rtb.date, rtb.times.get(0));
    }

    class TimeAdapter extends MyBaseAdapter {
        TimeAdapter(ArrayList<?> list, BaseActivity context){
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_expert_reservation, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            ReserveTimeBean rtb = (ReserveTimeBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            ho.dateView.setText(rtb.date);
            if(rtb.times != null && !rtb.times.isEmpty()){
                ho.timeView.setText(String.format(getString(R.string.expert_reservation_time_format), rtb.times.get(0).time));
                ho.moreView.setTag(rtb);
            }else{
                ho.timeView.setText(null);
                ho.moreView.setTag(null);
            }
        }

        class ViewHolder extends MyViewHolder implements View.OnClickListener{
            TextView dateView, timeView;
            View moreView;
            public ViewHolder(View view){
                super(view);
                dateView = (TextView) view.findViewById(R.id.reserve_date);
                timeView = (TextView) view.findViewById(R.id.reserve_time);
                moreView = view.findViewById(R.id.reserve_time_more);
                moreView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                ReserveTimeBean rtb = (ReserveTimeBean) v.getTag();
                if(rtb != null){
                    showSelectDialog(rtb);
                }
            }
        }
    }

    ItemSelectDialog selectDialog;
    ItemSelectDialog.OnItemClickListener listener;
    private void showSelectDialog(ReserveTimeBean rtb){
        if(selectDialog == null){
            selectDialog = new ItemSelectDialog(this);
            listener = new ItemSelectDialog.OnItemClickListener() {
                @Override
                public void click(int position) {
                    ReserveTimeBean objs = (ReserveTimeBean) selectDialog.getTag();
                    TimeBean item = objs.times.get(position);
                    selectTime(objs.date, item);
                    selectDialog.dismiss();
                }
            };
        }
        ArrayList<String> times = new ArrayList<>();
        for(TimeBean tb : rtb.times){
            times.add(tb.time);
        }
        selectDialog.setItems(times, listener);
        selectDialog.setTag(rtb);
        selectDialog.show();
    }

    // 选好日期时间, 进入预约界面
    private void selectTime(String date, TimeBean time){
        Intent intent = new Intent(this, ReservationActivity.class);
        intent.putExtra("date", date);
        intent.putExtra("time", time.time);
        intent.putExtra("id", time.id);
        intent.putExtra("doctor", doctorBean);
        intent.putExtra("office_id", officeId);
        startActivity(intent);
    }
}
