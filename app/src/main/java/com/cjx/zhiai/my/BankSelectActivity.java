package com.cjx.zhiai.my;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

import java.util.ArrayList;

/**
 * Created by cjx on 2017-01-01.
 */
public class BankSelectActivity extends BaseListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_select);
        setToolBar(true, null, R.string.bank_select);

        initListView(false, false);
        loadData();
        registerRefreshReceiver(new IntentFilter(MyApplication.ACTION_WITHDRAW));
    }

    // 收到广播回调
    protected void onBroadcastReceive(Intent intent){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            loadData();
        }
    }

    @Override
    protected void loadData() {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                hideLoadView();
                if(!TextUtils.isEmpty(response.datas)){
                    String[] items = response.datas.split(",");
                    ArrayList<String> list = new ArrayList<>();
                    for (String s : items){
                        list.add(s);
                    }
                    onLoadResult(list);
                }
            }

            @Override
            public void error() {
                hideLoadView();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/getCardNumber");
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new CardAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String number = (String) parent.getAdapter().getItem(position);
        withdraw(number);
    }

    public void addCard(View v){
        // 添加提现银行卡
        Intent intent = new Intent(this, BankAddActivity.class);
        startActivityForResult(intent, 1);
    }

    private void withdraw(final String number){
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                Intent intent = new Intent(BankSelectActivity.this, WithDrawDetailActivity.class);
                intent.putExtra("money", getIntent().getAction());
                intent.putExtra("number", number);
                startActivity(intent);
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/withdrawCash", "user_id", MyApplication.getInstance().user.user_id,
                "money", getIntent().getAction(), "card_number", number);
    }

    class CardAdapter extends MyBaseAdapter {
        public CardAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_card, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            String number = (String) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            ho.cardView.setText(number);
        }

        class ViewHolder extends MyViewHolder {
            TextView cardView;
            public ViewHolder(View view) {
                super(view);
                cardView = (TextView) view.findViewById(R.id.card_number);
            }
        }
    }
}
