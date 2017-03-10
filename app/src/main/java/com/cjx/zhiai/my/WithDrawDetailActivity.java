package com.cjx.zhiai.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;

/**
 * Created by cjx on 2017-01-01.
 */
public class WithDrawDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_detail);
        setToolBar(true, null, R.string.withdraw_detail);

        Intent intent = getIntent();

        TextView numberView = (TextView) findViewById(R.id.card_number);
        TextView moneyView = (TextView) findViewById(R.id.withdraw_money);

        moneyView.setText(String.format(getString(R.string.price_format), intent.getStringExtra("money")));
        numberView.setText(intent.getStringExtra("number"));

        sendBroadcast(new Intent(MyApplication.ACTION_WITHDRAW));
    }

    public void onClick(View v){
        finish();
    }
}
