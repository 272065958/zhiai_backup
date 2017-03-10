package com.cjx.zhiai.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;


/**
 * Created by cjx on 2016-12-19.
 * 选择身份
 */
public class IdentitySelectActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_select);
    }

    public void identityDoctor(View v){
        // 医生身份
        Intent intent = new Intent("doctor");
        setResult(RESULT_OK, intent);
        finish();
    }

    public void identityUser(View v){
        // 用户身份
        Intent intent = new Intent("user");
        setResult(RESULT_OK, intent);
        finish();
    }
}
