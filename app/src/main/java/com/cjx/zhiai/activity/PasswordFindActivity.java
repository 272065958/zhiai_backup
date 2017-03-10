package com.cjx.zhiai.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseGetCodeActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.component.GetCodeView;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

/**
 * Created by cjx on 2016-11-26.
 * 找回密码
 */
public class PasswordFindActivity extends BaseGetCodeActivity {
    EditText passwordView, checkPwdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_find);
        setToolBar(true, null, R.string.find_password_title);
        findViewById();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.button_submit: // 提交按钮
                find();
                break;
            case R.id.button_get_code: // 获取验证码
                getCode();
                break;
        }
    }

    private void findViewById(){
        phoneView = (EditText)findViewById(R.id.update_phone);
        codeView = (EditText)findViewById(R.id.update_code);
        passwordView = (EditText)findViewById(R.id.update_password);
        checkPwdView = (EditText)findViewById(R.id.update_password_comfirm);
        getCodeView = (GetCodeView) findViewById(R.id.button_get_code);
    }

    // 找回密码
    private void find(){
        final String pwd = passwordView.getText().toString();
        if(TextUtils.isEmpty(pwd)){
            Toast.makeText(this, getString(R.string.register_password_hint), Toast.LENGTH_SHORT).show();
            return ;
        }
        if(!pwd.equals(checkPwdView.getText().toString())){
            Toast.makeText(this, getString(R.string.password_different_error), Toast.LENGTH_SHORT).show();
            return ;
        }
        // 判断验证码是否正确
        checkCode();
    }

    @Override
    protected void checkCodeSuccess() {
        final String phone = phoneView.getText().toString();
        final String pwd = passwordView.getText().toString();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                Toast.makeText(PasswordFindActivity.this, response.errorMsg, Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra("phone", phone);
                data.putExtra("password", pwd);
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "userPassword/resetPassword",
                "phone", phone, "newpassword", pwd);
    }
}
