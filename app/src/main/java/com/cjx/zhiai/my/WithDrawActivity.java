package com.cjx.zhiai.my;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;

/**
 * Created by cjx on 2016-12-31.
 * 提现界面
 */
public class WithDrawActivity extends BaseActivity {
    TextView balanceView;
    EditText moneyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        setToolBar(true, null, R.string.button_withdraw);

        findViewById();
        registerRefreshReceiver(new IntentFilter(MyApplication.ACTION_WITHDRAW));
    }

    // 收到广播回调
    protected void onBroadcastReceive(Intent intent){
        finish();
    }

    private void findViewById(){
        balanceView = (TextView) findViewById(R.id.my_balance);
        balanceView.setText(String.format(getString(R.string.my_balance_format), MyApplication.getInstance().user.money));
        moneyView = (EditText) findViewById(R.id.withdraw_money);
    }

    public void onClick(View view) {
        String price = null;
        switch (view.getId()){
            case R.id.withdraw_button:
                price = moneyView.getText().toString();
                if(TextUtils.isEmpty(price)){
                    showToast("请输入提现金额");
                    return ;
                }
                if(Float.parseFloat(price) > Float.parseFloat(MyApplication.getInstance().user.money)){
                    showToast("提现金额不能大于余额");
                    return ;
                }
                break;
            case R.id.withdraw_all:
                price = MyApplication.getInstance().user.money;
                break;
        }
        Intent intent = new Intent(this, BankSelectActivity.class);
        intent.setAction(price);
        startActivity(intent);
    }

    // 添加银行卡
    public void addCard(View view){
        Intent intent = new Intent(this, BankAddActivity.class);
        startActivity(intent);
    }
}
