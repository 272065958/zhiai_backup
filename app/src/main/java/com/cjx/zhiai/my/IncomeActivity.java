package com.cjx.zhiai.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;

/**
 * Created by cjx on 2016-12-31.
 */
public class IncomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        setToolBar(true, null, R.string.my_income);

        TextView priceView = (TextView) findViewById(R.id.price_view);
        priceView.setText(String.format(getString(R.string.price_format), MyApplication.getInstance().user.money));
    }

    public void onClick(View v){
        if(Float.parseFloat(MyApplication.getInstance().user.money) > 0){
            Intent intent = new Intent(this, WithDrawActivity.class);
            startActivity(intent);
        }else{
            showToast("没有可提现余额");
        }
    }
}
