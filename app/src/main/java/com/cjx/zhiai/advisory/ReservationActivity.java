package com.cjx.zhiai.advisory;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.DoctorBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

/**
 * Created by cjx on 2017-01-04.
 */
public class ReservationActivity extends BaseActivity {
    EditText remarkView;
    String officeId, date, time;
    DoctorBean doctorBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        setToolBar(true, null, R.string.reservation_title);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        doctorBean = (DoctorBean) intent.getSerializableExtra("doctor");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        officeId = intent.getStringExtra("office_id");
        ((TextView)findViewById(R.id.reserve_doctor_name)).setText(doctorBean.user_real_name);
        ((TextView)findViewById(R.id.reserve_doctor_time)).setText(date+"  "+time);
        remarkView = (EditText) findViewById(R.id.advisory_content);
    }

    public void onClick(View v){
        // 提交挂号需求
        String content = remarkView.getText().toString();
        if(TextUtils.isEmpty(content)){
            showToast(getString(R.string.reservation_edit_hint));
            return;
        }
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                showToast(response.errorMsg);
                finish();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/saveConsultInfo", "patient_id",
                MyApplication.getInstance().user.user_id, "doctor_id", doctorBean.user_id, "type", "1",
                "content", content, "bespeak_time", date+"="+time, "money", String.valueOf(doctorBean.price), "office_id", officeId);
    }
}
