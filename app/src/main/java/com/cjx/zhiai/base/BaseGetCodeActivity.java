package com.cjx.zhiai.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.cjx.zhiai.R;
import com.cjx.zhiai.component.GetCodeView;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by cjx on 2016/12/12.
 * 获取验证码的基类
 */
public abstract class BaseGetCodeActivity extends BaseActivity {
    protected EditText phoneView, codeView;
    protected GetCodeView getCodeView;
    EventHandler eh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // appkey, appscret
        SMSSDK.initSDK(this, "1a637ffe50780", "9b948fe16ed22afaa17c069f3e47b653");
        eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    Log.e("TAG", "data = " + data);
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        checkCodeSuccess();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoadDialog();
                                getCodeView.startTimer();
                            }
                        });
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    dismissLoadDialog();
                    ((Throwable) data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调

        SMSSDK.getSupportedCountries();
    }

    @Override
    protected void onDestroy() {
        getCodeView.stopTimer();
        SMSSDK.unregisterEventHandler(eh);
        super.onDestroy();
    }

    // 获取验证码
    protected void getCode() {
        String phone = phoneView.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.register_phone_hint), Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadDislog();
        SMSSDK.getVerificationCode("+86", phone);
    }

    // 检查验证码是否正确
    protected void checkCode() {
        String phone = phoneView.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.register_phone_hint), Toast.LENGTH_SHORT).show();
            return ;
        }
        String code = codeView.getText().toString();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, getString(R.string.register_code_null_hint), Toast.LENGTH_SHORT).show();
            return ;
        }
        showLoadDislog();
        SMSSDK.submitVerificationCode("+86", phone, code);
    }

    protected abstract void checkCodeSuccess();
}
