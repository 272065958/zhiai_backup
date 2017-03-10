
package com.cjx.zhiai.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

/**
 * Created by cjx on 2016-12-19.
 * 填写医生信息界面
 */
public class RegisterInfoActivity extends BaseActivity {
    final int REQUEST_HOSPITAL = 1, REQUEST_DEPARTMENT = 2, REQUEST_POST = 3;
    EditText nameView, headerView, priceView, professionView;
    TextView hospitalView, departmentView, postView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);
        setToolBar(true, null, R.string.register_doctor_info);

        findViewById();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_HOSPITAL:
                    hospitalView.setText(data.getStringExtra("name"));
                    hospitalView.setTag(data.getStringExtra("id"));
                    break;
                case REQUEST_DEPARTMENT:
                    departmentView.setText(data.getStringExtra("name"));
                    departmentView.setTag(data.getStringExtra("id"));
                    break;
                case REQUEST_POST:
                    postView.setText(data.getStringExtra("name"));
                    postView.setTag(data.getStringExtra("id"));
                    break;
            }
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.register_hospital_content: // 选择医院
                Intent hospitalIntent = new Intent(this, HospitalSelectActivity.class);
                startActivityForResult(hospitalIntent, REQUEST_HOSPITAL);
                break;
            case R.id.register_department_content: // 选择科室
                Intent departmentIntent = new Intent(this, DepartmentSelectActivity.class);
                startActivityForResult(departmentIntent, REQUEST_DEPARTMENT);
                break;
            case R.id.register_post_content: // 选择职称
                Intent postIntent = new Intent(this, PostSelectActivity.class);
                startActivityForResult(postIntent, REQUEST_POST);
                break;
            case R.id.button_register:
//                if(v.isSelected()){ // 审核
                    register();
//                }else{ // 体验
//
//                }
                break;
        }
    }

    private void register() {
        String name = nameView.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.setting_info_true_name_hint), Toast.LENGTH_SHORT).show();
            return;
        }
        String hospital = (String) hospitalView.getTag();
        if (TextUtils.isEmpty(hospital)) {
            Toast.makeText(this, getString(R.string.register_doctor_hospital_hint), Toast.LENGTH_SHORT).show();
            return;
        }
        String department = (String) departmentView.getTag();
        if (TextUtils.isEmpty(department)) {
            Toast.makeText(this, getString(R.string.register_doctor_department_hint), Toast.LENGTH_SHORT).show();
            return;
        }
        String post = (String) postView.getTag();
        if (TextUtils.isEmpty(post)) {
            Toast.makeText(this, getString(R.string.register_doctor_post_hint), Toast.LENGTH_SHORT).show();
            return;
        }
        String header = headerView.getText().toString();
        if (TextUtils.isEmpty(header)) {
            Toast.makeText(this, getString(R.string.register_doctor_header_hint2), Toast.LENGTH_SHORT).show();
            return;
        }
        String price = priceView.getText().toString();
        if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, getString(R.string.register_doctor_price_hint2), Toast.LENGTH_SHORT).show();
            return;
        }
        String profession = professionView.getText().toString();
        if (TextUtils.isEmpty(profession)) {
            Toast.makeText(this, getString(R.string.register_doctor_profession_hint2), Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                Toast.makeText(RegisterInfoActivity.this, response.errorMsg, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        Intent intent = getIntent();
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "user/register", "user_real_name", name, "user_phone", intent.getStringExtra("phone"),
                "user_pwd", intent.getStringExtra("password"), "user_type", ((MyApplication)getApplication()).USER_TYPE_DOCTOR, "hospital_id", hospital,
                "office_id", department, "position_id", post, "price", price, "honor", header);
    }

    private void findViewById(){
        nameView = (EditText) findViewById(R.id.register_name);
        headerView = (EditText) findViewById(R.id.register_header);
        priceView = (EditText) findViewById(R.id.register_price);
        professionView = (EditText) findViewById(R.id.register_profession);
        hospitalView = (TextView) findViewById(R.id.register_hospital);
        departmentView = (TextView) findViewById(R.id.register_department);
        postView = (TextView) findViewById(R.id.register_post);
    }


}
