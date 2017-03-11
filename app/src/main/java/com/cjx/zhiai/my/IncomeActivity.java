package com.cjx.zhiai.my;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.bean.UserBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.JsonParser;

/**
 * Created by cjx on 2016-12-31.
 */
public class IncomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        setToolBar(true, null, R.string.my_income);

        loadData();
        registerRefreshReceiver(new IntentFilter(MyApplication.ACTION_WITHDRAW));
    }

    // 收到广播回调
    protected void onBroadcastReceive(Intent intent){
        findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
        loadData();
    }

    private void loadData() {
        MyCallbackInterface myCallbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
                SharedPreferences sp = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
                sp.edit().putString("user", response.datas).apply();
                MyApplication.getInstance().user = (UserBean) JsonParser.getInstance().fromJson(response.datas, UserBean.class);
                findViewById(R.id.income_content).setVisibility(View.VISIBLE);
                TextView priceView = (TextView) findViewById(R.id.price_view);
                priceView.setText(String.format(getString(R.string.price_format), MyApplication.getInstance().user.money));
            }

            @Override
            public void error() {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
            }
        };
        HttpUtils.getInstance().postEnqueue(this, myCallbackInterface, "user/userInfo");
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
