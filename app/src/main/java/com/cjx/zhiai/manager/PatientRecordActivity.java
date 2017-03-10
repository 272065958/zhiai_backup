package com.cjx.zhiai.manager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.PatientBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.Tools;

/**
 * Created by cjx on 2016-12-31.
 * 患者管理修改记录
 */
public class PatientRecordActivity extends BaseActivity implements TextWatcher {
    PatientBean pb;
    TextView countView, button;
    EditText contentView, reseanView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_record);
        setToolBar(true, null, getIntent().getIntExtra("title", R.string.main_manager));
        pb = (PatientBean) getIntent().getSerializableExtra("patient");
        initView();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int count = s.length();
        countView.setText(count + "/300");
        if (count == 0) {
            button.setClickable(false);
            button.setBackgroundResource(R.drawable.black_fragment_bg);
        } else {
            button.setClickable(true);
            button.setBackgroundResource(R.drawable.red_rectangle_bg);
        }
    }

    private void initView() {
        reseanView = (EditText) findViewById(R.id.patient_resean);
        countView = (TextView) findViewById(R.id.advisory_count);
        button = (TextView) findViewById(R.id.button_submit);
        button.setClickable(false);
        button.setBackgroundResource(R.drawable.black_fragment_bg);
        contentView = (EditText) findViewById(R.id.advisory_content);
        contentView.addTextChangedListener(this);
        TextView recordView = (TextView) findViewById(R.id.patient_update);
        if(TextUtils.isEmpty(pb.notes)){
            recordView.setText(R.string.button_record);
        }else{
            reseanView.setText(pb.notes);
        }

        ImageView headView = (ImageView) findViewById(R.id.discover_head);
        ImageView sexView = (ImageView) findViewById(R.id.discover_sex);
        TextView nameView = (TextView) findViewById(R.id.discover_name);
        TextView stateView = (TextView) findViewById(R.id.patient_state);
        TextView timeView = (TextView) findViewById(R.id.patient_content);
        Tools.setImageInView(this, pb.head_image, headView);
        nameView.setText(pb.user_name);
        sexView.setImageResource(pb.sex.equals("f") ? R.drawable.woman : R.drawable.man);
        timeView.setText(String.format(getString(R.string.advisort_time_format), pb.bespeak_time));
        Tools.setPatientState(stateView, pb.state);
    }

    // 记录备注
    public void update(View v){
        final String content = reseanView.getText().toString();
        if(TextUtils.isEmpty(content)){
            showToast("请填写记录内容");
            return;
        }
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                button.setText(R.string.button_update);
                showToast(response.errorMsg);
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/updateNotes", "bespeak_id", pb.bespeak_id, "notes", content);
//        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/addRecipe", "bespeak_id", pb.bespeak_id, "recipe_content", content);
    }

    // 提交诊断方案
    public void onClick(View v){
        final String content = contentView.getText().toString();
        if(TextUtils.isEmpty(content)){
            showToast(getString(R.string.patient_record_hint));
            return;
        }
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
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/addRecipe", "bespeak_id", pb.bespeak_id, "recipe_content", content);
    }
}
