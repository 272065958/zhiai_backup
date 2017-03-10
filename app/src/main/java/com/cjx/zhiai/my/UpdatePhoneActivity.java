package com.cjx.zhiai.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseGetCodeActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.component.GetCodeView;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

/**
 * Created by cjx on 2016/12/13.
 * 更换手机号
 */
public class UpdatePhoneActivity extends BaseGetCodeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_phone);
        setToolBar(true, null, R.string.setting_update_phone);
        findViewById();
    }

    private void findViewById(){
        phoneView = (EditText) findViewById(R.id.update_phone);
        codeView = (EditText) findViewById(R.id.update_code);
        getCodeView = (GetCodeView) findViewById(R.id.button_get_code);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.button_update: // 更换操作
                // 判断验证码
                checkCode();
                break;
            case R.id.button_get_code: // 获取验证码
                getCode();
                break;
        }
    }

    @Override
    protected void checkCodeSuccess() {
        final String phone = phoneView.getText().toString();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                Toast.makeText(UpdatePhoneActivity.this, response.errorMsg, Toast.LENGTH_SHORT).show();
                MyApplication app = (MyApplication) getApplication();
                app.user.user_phone = phone;
                sendBroadcast(new Intent(MyApplication.ACTION_USER_INFO_UPDATE));
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "user/modifyPhone", "phone", phone);
    }
}
