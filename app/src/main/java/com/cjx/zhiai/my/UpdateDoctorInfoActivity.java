package com.cjx.zhiai.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.bean.UserBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

/**
 * Created by cjx on 2016-12-25.
 * 更新医生信息
 */
public class UpdateDoctorInfoActivity extends BaseActivity {

    TextView nameView, hospitalView, departmentView, postView;
    EditText headerView, priceView, professionView, infoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_doctor_info);
        setToolBar(true, null, R.string.my_setting_info);

        findViewById();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        save();
        return super.onOptionsItemSelected(item);
    }

    private void findViewById() {
        hospitalView = (TextView) findViewById(R.id.update_hospital);
        departmentView = (TextView) findViewById(R.id.update_department);
        postView = (TextView) findViewById(R.id.update_post);
        nameView = (TextView) findViewById(R.id.update_name);
        headerView = (EditText) findViewById(R.id.update_header);
        headerView.setSelectAllOnFocus(true);
        priceView = (EditText) findViewById(R.id.register_price);
        priceView.setSelectAllOnFocus(true);
        professionView = (EditText) findViewById(R.id.register_profession);
        professionView.setSelectAllOnFocus(true);
        infoView = (EditText) findViewById(R.id.register_doctor_information);
        infoView.setSelectAllOnFocus(true);
    }

    private void initView(){
        UserBean user = MyApplication.getInstance().user;
        nameView.setText(user.user_real_name);
        hospitalView.setText(user.hospital_name);
        departmentView.setText(user.office_name);
        postView.setText(user.position);
        headerView.setText(user.honor);
        priceView.setText(user.price);
        professionView.setText(user.skilled);
        infoView.setText(user.doctor_summary);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.button_save:
                save();
                break;
            case R.id.button_cancel:
                finish();
                break;
        }
    }

    private void save(){
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
        String info = infoView.getText().toString();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                Toast.makeText(UpdateDoctorInfoActivity.this, response.errorMsg, Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(MyApplication.ACTION_USER_INFO_UPDATE));
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        showLoadDislog();
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "user/modifyUserInfo", "honor", header,
                "price", price, "summary", info, "skilled", profession, "type", "1");
    }
}
