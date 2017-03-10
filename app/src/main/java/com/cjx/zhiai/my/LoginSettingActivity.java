package com.cjx.zhiai.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;

/**
 * Created by cjx on 2016-12-06.
 */
public class LoginSettingActivity extends BaseActivity {
    TextView phoneView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_setting);
        setToolBar(true, null, R.string.my_setting_login);

        phoneView = (TextView) findViewById(R.id.login_phone);
        initPhone();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            initPhone();
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.setting_password_update: // 修改密码
                Intent pwdIntent = new Intent(this, PasswordUpdateActivity.class);
                startActivity(pwdIntent);
                break;
            case R.id.setting_phone_update: // 修改手机号
                Intent phoneIntent = new Intent(this, UpdatePhoneActivity.class);
                startActivity(phoneIntent);
                break;
        }
    }

    private void initPhone(){
        phoneView.setText(((MyApplication)getApplication()).user.user_phone);
    }
}
