package com.cjx.zhiai.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

/**
 * Created by cjx on 17-3-20.
 */

public class IntegralActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral);
        setToolBar(true, null, R.string.my_integration);

        loadData();
    }

    private void loadData() {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
                try {
                    ((TextView) findViewById(R.id.integral_view)).setText(response.datas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error() {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "HealingDrugs/getIntegral");
    }


}
