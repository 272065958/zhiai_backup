package com.cjx.zhiai.my;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.activity.PayActivity;
import com.cjx.zhiai.advisory.ChatActivity;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.ReservationBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.dialog.TipDialog;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.Tools;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.easeui.EaseConstant;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-05.
 * 我的预约
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            onRefresh();
        }
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<ReservationBean>>() {
                }.getType()), "base/getHistory", "user_id", MyApplication.getInstance().user.user_id,
                "query_type", "2", "page", "1", "limit", "100");
    }

    @Override
    protected void onLoadResult(ArrayList<?> list) {
        for (Object obj : list) {
            ((ReservationBean) obj).format();
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
            ho.videoView.setTag(rb);
            switch (rb.state) {
                case "2":
                    switch (rb.operateTime) {
                        case 0:
                            ho.videoView.setText("视频诊断");
                            ho.videoView.setBackgroundResource(R.drawable.gray_frament_bg);
                            ho.videoView.setTextColor(Color.GRAY);
                            ho.statusView.setText("预约成功");
                            break;
                        case 1:
                            ho.videoView.setText("视频诊断");
                            ho.videoView.setBackgroundResource(R.drawable.red_frament_bg);
                            ho.videoView.setTextColor(Color.WHITE);
                            ho.statusView.setText("预约成功");
                            break;
                        case -1:
                            ho.videoView.setBackgroundResource(R.drawable.red_frament_bg);
                            ho.videoView.setTextColor(Color.WHITE);
                            ho.videoView.setText("确认");
                            ho.statusView.setText("预约完成");
                            break;
                    }
                    ho.statusView.setTextColor(ContextCompat.getColor(context, R.color.main_color));
                    ho.videoView.setVisibility(View.VISIBLE);
                    ho.videoView.setTag(R.string.title_user_profile, ho.nameView.getText().toString());
                    break;
                case "4":
                    ho.statusView.setText(R.string.patient_is_cancel);
                    ho.statusView.setTextColor(ContextCompat.getColor(context, R.color.text_secondary_color));
                    ho.videoView.setVisibility(View.VISIBLE);
                    ho.videoView.setTextColor(Color.WHITE);
                    ho.videoView.setBackgroundResource(R.drawable.red_frament_bg);
                    ho.videoView.setText("金额去向");
                    break;
                case "5":
                    ho.videoView.setVisibility(View.VISIBLE);
                    ho.videoView.setTextColor(Color.WHITE);
                    ho.videoView.setBackgroundResource(R.drawable.red_frament_bg);
                    ho.videoView.setText("立即付款");
                    ho.statusView.setText(R.string.patient_pre_pay);
                    ho.statusView.setTextColor(ContextCompat.getColor(context, R.color.main_color));
                    break;
                default:
                    ho.statusView.setTextColor(ContextCompat.getColor(context, R.color.text_main_color));
                    ho.videoView.setVisibility(View.GONE);
                    Tools.setPatientState(ho.statusView, rb.state);
                    break;
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
                ReservationBean rb = (ReservationBean) v.getTag();
                switch (((TextView) v).getText().toString()) {
                    case "视频诊断":
                        switch (rb.operateTime) {
                            case 0:
                                showToast("没到预约时间");
                                break;
                            case 1:
                                String title = (String) v.getTag(R.string.title_user_profile);
                                Intent chatIntent = new Intent(context, ChatActivity.class);
                                chatIntent.putExtra("title", title);
                                chatIntent.putExtra(EaseConstant.EXTRA_USER_ID, rb.doctor_id);
                                startActivity(chatIntent);
                                break;
                        }
                        break;
                    case "金额去向":
                        showToast("金额已原路退回,请注意查收");
                        break;
                    case "确认":
                        showComfirmDialog(rb.bespeak_id);
                        break;
                    case "立即付款":
                        Intent intent = new Intent(context, PayActivity.class);
                        intent.putExtra("pay_price", rb.money);
                        intent.putExtra("pay_tip", "预约支付");
                        intent.putExtra("id", rb.bespeak_number);
                        startActivityForResult(intent, 1);
                        break;
                }
            }
        }
    }

    TipDialog tipDialog;

    private void showComfirmDialog(String id) {
        if (tipDialog == null) {
            tipDialog = new TipDialog(this);
            tipDialog.setText("提示", "确认完成后,将会把钱转给医生,是否确定?", "确定", "取消");
            tipDialog.setTipComfirmListener(new TipDialog.ComfirmListener() {
                @Override
                public void comfirm() {
                    tipDialog.dismiss();
                    String id = (String) tipDialog.getTag();
                    comfirmReservation(id);
                }

                @Override
                public void cancel() {
                    tipDialog.dismiss();
                }
            });
        }
        tipDialog.setTag(id);
        tipDialog.show();
    }

    // 完成视频预约
    private void comfirmReservation(String id) {
        MyCallbackInterface myCallbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                showToast(response.errorMsg);
                loadData();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        showLoadDislog();
        HttpUtils.getInstance().postEnqueue(this, myCallbackInterface, "base/updateState", "bespeak_id", id, "state", "3");
    }


}
