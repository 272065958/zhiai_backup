package com.cjx.zhiai.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseGetCodeActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.component.GetCodeView;
import com.cjx.zhiai.dialog.ItemSelectDialog;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

/**
 * Created by cjx on 2016-11-26.
 * 注册界面
 */
public class RegisterActivity extends BaseGetCodeActivity {
    final int RESULT_REGISTER_DOCTOR = 1;

    String[] sexItem;
    EditText nameView, passwordView;
    TextView sexView;

    ItemSelectDialog sexDialog;
    String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setToolBar(true, null, R.string.button_register);
        action = getIntent().getAction();
        findViewById();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == RESULT_REGISTER_DOCTOR){
                Intent intent = new Intent();
                intent.putExtra("phone", phoneView.getText().toString());
                intent.putExtra("password", passwordView.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register: // 注册
                register();
                break;
            case R.id.register_copyright: // 查看条款
                break;
            case R.id.button_get_code: // 获取验证码
                getCode();
                break;
            case R.id.register_sex_content: // 选择性别
                showSexSelectDialog();
                break;
        }
    }

    // 获取界面元素
    private void findViewById() {
        if (action != null) { // 当前是医生身份
            findViewById(R.id.register_name_content).setVisibility(View.GONE);
            findViewById(R.id.register_sex_content).setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) findViewById(R.id.register_phone_content).getLayoutParams();
            lp.topMargin = getResources().getDimensionPixelOffset(R.dimen.icon_size);
            ((Button) findViewById(R.id.button_register)).setText(R.string.button_next);
            registerRefreshReceiver(new IntentFilter(MyApplication.ACTION_REGISTER));
        } else {
            nameView = (EditText) findViewById(R.id.register_name);
            sexView = (TextView) findViewById(R.id.register_sex);
        }
        phoneView = (EditText) findViewById(R.id.register_phone);
        codeView = (EditText) findViewById(R.id.register_code);
        passwordView = (EditText) findViewById(R.id.register_password);
        getCodeView = (GetCodeView) findViewById(R.id.button_get_code);
    }

    // 显示选择性别的对话框
    private void showSexSelectDialog() {
        if (sexDialog == null) {
            sexItem = getResources().getStringArray(R.array.sex);
            sexDialog = new ItemSelectDialog(this);
            sexDialog.setItemsByArray(sexItem, new ItemSelectDialog.OnItemClickListener() {
                @Override
                public void click(int position) {
                    sexView.setText(sexItem[position]);
                    sexView.setTag(position == 0 ? "m" : "f");
                    sexDialog.dismiss();
                }
            });
        }
        sexDialog.show();
    }

    @Override
    protected void checkCodeSuccess() {
        final String phone = phoneView.getText().toString();
        final String password = passwordView.getText().toString();
        if (action != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent infoIntent = new Intent(RegisterActivity.this, RegisterInfoActivity.class);
                    infoIntent.putExtra("phone", phone);
                    infoIntent.putExtra("password", password);
                    startActivityForResult(infoIntent, RESULT_REGISTER_DOCTOR);
                }
            });
            return ;
        }
        String name = nameView.getText().toString();
        String sex = (String) sexView.getTag();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                Toast.makeText(RegisterActivity.this, response.errorMsg, Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra("phone", phone);
                data.putExtra("password", password);
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "user/register", "username", name,
                "user_phone", phone, "sex", sex, "user_pwd", password, "user_type", (MyApplication.getInstance()).userType);
    }

    private void register() {
        if (nameView != null) {
            String name = nameView.getText().toString();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, getString(R.string.register_name_hint), Toast.LENGTH_SHORT).show();
                return;
            }
            String sex = (String) sexView.getTag();
            if (TextUtils.isEmpty(sex)) {
                Toast.makeText(this, getString(R.string.register_sex_hint), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String password = passwordView.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.register_password_hint), Toast.LENGTH_SHORT).show();
            return;
        }
        // 判断验证码
        checkCode();
    }
}
