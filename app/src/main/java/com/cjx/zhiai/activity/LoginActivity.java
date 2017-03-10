package com.cjx.zhiai.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

/**
 * Created by cjx on 2016-11-25.
 * 登录界面
 */
public class LoginActivity extends BaseActivity {

    final int RESULT_REGISTER = 2, RESULT_IDENTITY = 1;

    EditText accView, pwdView;
    ImageView iconView;
    TextView labelView;
    MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyApplication) getApplication();
        if (app.isLogin()) {
            startMain();
            return;
        }

        setContentView(R.layout.activity_login);
        findViewById();
        Intent identitySelect = new Intent(this, IdentitySelectActivity.class);
        startActivityForResult(identitySelect, RESULT_IDENTITY);

        if (!app.sharedPreferences.getBoolean("first", false)) {
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_REGISTER:
                    String acc = data.getStringExtra("phone");
                    String pwd = data.getStringExtra("password");
                    accView.setText(acc);
                    pwdView.setText(pwd);
                    login(acc, pwd);
                    break;
                case RESULT_IDENTITY:
                    String action = data.getAction();
                    if (action.equals("doctor")) { // 医生
                        iconView.setImageResource(R.drawable.doctor);
                        iconView.setSelected(true); // 当身份是医生时iconView标记为真
                        labelView.setText(R.string.identity_doctor);
                        app.userType = app.USER_TYPE_DOCTOR;
                    } else {
                        iconView.setImageResource(R.drawable.patient);
                        labelView.setText(R.string.identity_user);
                        app.userType = app.USER_TYPE_PEOPLE;
                    }
                    break;
            }
        } else if (requestCode == RESULT_IDENTITY) {
            finish();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login: // 登录
                String acc = accView.getText().toString();
                if (TextUtils.isEmpty(acc)) {
                    Toast.makeText(this, getString(R.string.register_phone_hint), Toast.LENGTH_SHORT).show();
                    return;
                }
                String pwd = pwdView.getText().toString();
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(this, getString(R.string.register_password_hint), Toast.LENGTH_SHORT).show();
                    return;
                }
                login(acc, pwd);
                break;
            case R.id.button_forget: // 忘记密码
                Intent pwdIntent = new Intent(this, PasswordFindActivity.class);
                startActivityForResult(pwdIntent, RESULT_REGISTER);
                break;
            case R.id.button_register: // 注册
                Intent regIntent = new Intent(this, RegisterActivity.class);
                if (iconView.isSelected()) {
                    regIntent.setAction("doctor");
                }
                startActivityForResult(regIntent, RESULT_REGISTER);
                break;
        }
    }

    private void findViewById() {
        iconView = (ImageView) findViewById(R.id.identity_icon);
        labelView = (TextView) findViewById(R.id.identity_tip);
        accView = (EditText) findViewById(R.id.login_phone);
        pwdView = (EditText) findViewById(R.id.login_password);
        SharedPreferences preferences = app.sharedPreferences;
        if (preferences.contains("account")) {
            accView.setText(preferences.getString("account", null));
            pwdView.setText(preferences.getString("password", null));
        }
    }

    // 进入主界面
    private void startMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void login(final String acc, final String pwd) {
        showLoadDislog();
        MyCallbackInterface myCallbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                SharedPreferences.Editor editor = app.sharedPreferences.edit();
                editor.putString("account", acc);
                editor.putString("password", pwd);
                editor.apply();
                MyApplication app = (MyApplication) getApplication();
                app.setLogin(response.datas);
                if (app.isLogin()) {
                    startMain();
                }
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, myCallbackInterface, "user/login", "username", acc,
                "password", pwd, "type", MyApplication.getInstance().userType);
    }
}
