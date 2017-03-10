package com.cjx.zhiai.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.activity.LoginActivity;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.dialog.TipDialog;

/**
 * Created by cjx on 2016-12-05.
 */
public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setToolBar(true, null, R.string.my_setting);

        MyApplication app = (MyApplication) getApplication();
        if (app.userType.equals(app.USER_TYPE_DOCTOR)) {
            findViewById(R.id.my_setting_info_line).setVisibility(View.GONE);
            findViewById(R.id.my_setting_info).setVisibility(View.GONE);
            LinearLayout.LayoutParams rlp = (LinearLayout.LayoutParams) findViewById(R.id.my_setting_logout).getLayoutParams();
            rlp.topMargin = 0;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_setting_info: // 个人信息
                Intent infoIntent = new Intent(this, UpdatePeopleInfoActivity.class);
                startActivity(infoIntent);
                break;
            case R.id.my_setting_login: // 登录信息
                Intent loginIntent = new Intent(this, LoginSettingActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.my_setting_logout: // 退出登录
                showLogoutDialog();
                break;
        }
    }

    TipDialog logoutDialog;

    private void showLogoutDialog() {
        if (logoutDialog == null) {
            logoutDialog = new TipDialog(this);
            logoutDialog.setText(R.string.dialog_tip, R.string.dialog_login_exit, R.string.button_sure,
                    R.string.button_cancel).setTipComfirmListener(new TipDialog.ComfirmListener() {
                @Override
                public void comfirm() {
                    logoutDialog.dismiss();
                    ((MyApplication) getApplication()).setLogin(null);
                    setResult(RESULT_OK);
                    finish();
                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                @Override
                public void cancel() {
                    logoutDialog.dismiss();
                }
            });
        }
        logoutDialog.show();
    }
}
