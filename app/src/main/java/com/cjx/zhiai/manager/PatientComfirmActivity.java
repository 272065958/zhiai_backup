package com.cjx.zhiai.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.advisory.ChatActivity;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.PatientBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.hyphenate.easeui.EaseConstant;

/**
 * Created by cjx on 2017-01-03.
 * 患者管理确认页面
 */
public class PatientComfirmActivity extends BaseActivity {
    PatientBean patientBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_comfirm);

        patientBean = (PatientBean) getIntent().getSerializableExtra("patient");
        switch (patientBean.state){
            case "1": // 待确认
                setToolBar(true, null, R.string.patient_to_comfirm);
                break;
            case "2": // 已确认
                setToolBar(true, null, R.string.patient_is_comfirm);
                findViewById(R.id.patient_cancel).setVisibility(View.INVISIBLE);
                TextView updateView = (TextView) findViewById(R.id.patient_update);
                updateView.setText(R.string.button_video_comfirm);
                break;
            case "3": // 过期
            case "4": // 取消
                findViewById(R.id.patient_cancel).setVisibility(View.GONE);
                findViewById(R.id.patient_update).setVisibility(View.GONE);
                break;
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.patient_update:
                if(patientBean.state.equals("2")){ // 视频诊断
                    Intent chatIntent = new Intent(this, ChatActivity.class);
                    chatIntent.putExtra("title", patientBean.user_name);
                    chatIntent.putExtra(EaseConstant.EXTRA_USER_ID, patientBean.patient_id);
                    startActivity(chatIntent);
                    return ;
                }
                // 确认预约
                setState("2");
                break;
            case R.id.patient_cancel:
                // 取消预约
                setState("4");
                break;
        }
    }

    // 确认或取消预约
    private void setState(String state){
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
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/updateState", "bespeak_id", patientBean.bespeak_id,
                "state", state);
    }
}
