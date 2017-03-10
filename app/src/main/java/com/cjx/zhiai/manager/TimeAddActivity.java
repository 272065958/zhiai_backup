package com.cjx.zhiai.manager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.ValueTextBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.component.wheel.WheelLayout;
import com.cjx.zhiai.dialog.DateSelectDialog;
import com.cjx.zhiai.dialog.ListItemSelectDialog;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

import java.util.ArrayList;

/**
 * Created by cjx on 2017-01-03.
 */
public class TimeAddActivity extends BaseActivity {
    TextView dateView, startTimeView, endTimeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_add);
        setToolBar(true, null, R.string.time_add_title);

        dateView = (TextView) findViewById(R.id.time_date);
        startTimeView = (TextView) findViewById(R.id.time_hour_from);
        endTimeView = (TextView) findViewById(R.id.time_hour_to);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.time_hour_from_select:
            case R.id.time_hour_from:
                timeSelect(startTimeView);
                break;
            case R.id.time_hour_to_select:
            case R.id.time_hour_to:
                timeSelect(endTimeView);
                break;
            case R.id.time_date_select:
            case R.id.time_date:
                daySelect(dateView);
                break;
        }
    }

    // 选择时间
    public void timeSelect(TextView v) {
        showTimeDialog(v);
    }

    // 选择日期
    public void daySelect(TextView v) {
        showDateDialog(v);
    }

    DateSelectDialog dayDialog;

    private void showDateDialog(TextView v) {
        if (dayDialog == null) {
            dayDialog = new DateSelectDialog(this, true, WheelLayout.TimeStyle.FUTURE);
        }
        dayDialog.bind(v);
        dayDialog.show();
    }

    ArrayList<ValueTextBean> times;
    ListItemSelectDialog timeDialog;

    private void showTimeDialog(TextView v) {
        if (timeDialog == null) {
            times = getTimes();
            timeDialog = new ListItemSelectDialog(this);
            timeDialog.setItems(times, new ListItemSelectDialog.OnListItemClickListener() {
                @Override
                public void click(int position) {
                    TextView textView = (TextView) timeDialog.getTag();
                    ValueTextBean cb = times.get(position);
                    if(textView == endTimeView){
                        if(startTimeView.getTag() == null){
                            showToast("请选择开始时段");
                            return;
                        }else {
                            int startValue = Integer.parseInt((String) startTimeView.getTag());
                            if(Integer.parseInt(cb.value) <= startValue){
                                showToast("结束时段不能前于开始时段");
                                return ;
                            }
                            if(Integer.parseInt(cb.value) - startValue > 2){
                                showToast("时段最高不能超过1小时");
                                return ;
                            }
                        }
                    }else if(endTimeView.getTag() != null){
                        int endValue = Integer.parseInt((String) endTimeView.getTag());
                        if(endValue - Integer.parseInt(cb.value) > 2){
                            showToast("时段最高不能超过1小时");
                            return ;
                        }
                        if (Integer.parseInt(cb.value) >= endValue) {
                            showToast("开始时段不能晚于结束时段");
                            return ;
                        }
                    }
                    textView.setTag(cb.value);
                    textView.setText(cb.text);
                    timeDialog.dismiss();
                }
            });
        }
        timeDialog.setTag(v);
        timeDialog.show();
    }

    private ArrayList<ValueTextBean> getTimes() {
        ArrayList<ValueTextBean> times = new ArrayList<>();
        times.add(new ValueTextBean("0", "09:00"));
        times.add(new ValueTextBean("1", "09:30"));
        int value = 2;
        for (int i = 10; i < 23; i++) {
            times.add(new ValueTextBean(String.valueOf(value), i + ":00"));
            value++;
            times.add(new ValueTextBean(String.valueOf(value), i + ":30"));
            value++;
        }
        return times;
    }

    public void submit(View view) {
        String day = dateView.getText().toString();
        if (TextUtils.isEmpty(day)) {
            showToast("请选择日期");
            return;
        }
        day = day.replace("-", "");
        String from = startTimeView.getText().toString();
        if (TextUtils.isEmpty(from)) {
            showToast("请选择开始时段");
            return;
        }
        String to = endTimeView.getText().toString();
        if (TextUtils.isEmpty(to)) {
            showToast("请选择结束时段");
            return;
        }
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                showToast(response.errorMsg);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/addBespeakTime",
                "doctor_id", MyApplication.getInstance().user.user_id, "date", day, "time", from + "-" + to);
    }
}
