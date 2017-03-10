package com.cjx.zhiai.my;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

/**
 * Created by cjx on 2017-01-01.
 * 添加银行卡
 */
public class BankAddActivity extends BaseActivity {

    EditText nameView, numberView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_add);
        setToolBar(true, null, R.string.bank_add);

        nameView = (EditText) findViewById(R.id.card_name);
        numberView = (EditText) findViewById(R.id.card_number);

    }

    public void onClick(View v){
        String name = nameView.getText().toString();
        if(TextUtils.isEmpty(name)){
            showToast("请输入持卡人姓名");
            return ;
        }
        String number = numberView.getText().toString();
        if(TextUtils.isEmpty(name)){
            showToast("请输入卡号");
            return ;
        }
        // 保存卡
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                showToast(response.errorMsg);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/addCardNumber",
                "name", name, "card_number", number);

    }

}
