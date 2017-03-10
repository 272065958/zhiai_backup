package com.cjx.zhiai.my;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

/**
 * Created by cjx on 2016-11-26.
 * 修改密码
 */
public class PasswordUpdateActivity extends BaseActivity {

    EditText oldPwdView, pwdView, checkPwdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_update);
        setToolBar(true, null, R.string.update_password_title);
        findViewById();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_submit: // 提交按钮
                update();
                break;
        }
    }

    private void findViewById(){
        oldPwdView = (EditText) findViewById(R.id.old_password);
        pwdView = (EditText) findViewById(R.id.update_password);
        checkPwdView = (EditText) findViewById(R.id.update_password_comfirm);
    }

    private void update(){
        String oldPwd = oldPwdView.getText().toString();
        if(TextUtils.isEmpty(oldPwd)){
            Toast.makeText(this, getString(R.string.register_password_old_hint), Toast.LENGTH_SHORT).show();
            return ;
        }
        String pwd = pwdView.getText().toString();
        if(TextUtils.isEmpty(pwd)){
            Toast.makeText(this, getString(R.string.register_password_hint), Toast.LENGTH_SHORT).show();
            return ;
        }
        if(!pwd.equals(checkPwdView.getText().toString())){
            Toast.makeText(this, getString(R.string.password_different_error), Toast.LENGTH_SHORT).show();
            return ;
        }
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                Toast.makeText(PasswordUpdateActivity.this, response.errorMsg, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        showLoadDislog();
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "userPassword/modefyPassword",
                "oldPassword", oldPwd, "newPassword", pwd);
    }
}