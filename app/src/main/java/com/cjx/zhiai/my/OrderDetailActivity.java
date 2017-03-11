package com.cjx.zhiai.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.activity.PayActivity;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.OrderBean;
import com.cjx.zhiai.bean.OrderItemBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.bean.UserBean;
import com.cjx.zhiai.dialog.TipDialog;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.Tools;

/**
 * Created by cjx on 2017/2/10.
 * 订单详情
 */
public class OrderDetailActivity extends BaseActivity {
    OrderBean orderBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        setToolBar(true, null, R.string.order_detail_title);

        initView();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.button_cancel: // 取消订单
                showCancelDialog();
                break;
            case R.id.button_pay: // 立即支付
                Intent intent = new Intent(this, PayActivity.class);
                intent.putExtra("pay_price", orderBean.order_total);
                intent.putExtra("pay_tip", "订单支付");
                intent.putExtra("id", orderBean.order_mumber);
                startActivity(intent);
                break;
        }
    }

    TipDialog tipDialog;
    // 提示是否取消订单
    private void showCancelDialog() {
        if(tipDialog == null){
            tipDialog = new TipDialog(this);
            tipDialog.setText(getString(R.string.dialog_tip),
                    "是否取消当前订单?", getString(R.string.button_sure), getString(R.string.button_cancel));
            tipDialog.setTipComfirmListener(new TipDialog.ComfirmListener() {
                @Override
                public void comfirm() {
                    tipDialog.dismiss();
                    cancelOrder();
                }

                @Override
                public void cancel() {
                    tipDialog.dismiss();
                }
            });
        }
        tipDialog.show();
    }

    // 取消订单
    private void cancelOrder() {
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                sendBroadcast(new Intent(MyApplication.ACTION_ORDER_OPERATE));
                showToast(response.errorMsg);
                finish();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "HealingDrugs/updateOrderInfo", "orderId",
                orderBean.order_id, "type", "0");
    }

    private void initView() {
        orderBean = (OrderBean) getIntent().getSerializableExtra("order");
        if(orderBean.order_status.equals("待支付")){
            findViewById(R.id.order_button).setVisibility(View.VISIBLE);
            findViewById(R.id.order_button_line).setVisibility(View.VISIBLE);
        }
        UserBean user = MyApplication.getInstance().user;

        // 收货者信息
        TextView nameView = (TextView) findViewById(R.id.order_receive_name);
        TextView phoneView = (TextView) findViewById(R.id.order_receive_phone);
        TextView addressView = (TextView) findViewById(R.id.order_receive_address);
        nameView.setText(TextUtils.isEmpty(user.user_real_name) ? user.user_name : user.user_real_name);
        phoneView.setText(user.user_phone);
        addressView.setText(user.user_address);

        // 药品列表
        LinearLayout contentView = (LinearLayout) findViewById(R.id.order_shop_content);
        int size = orderBean.orderItem.size();
        for(int i=0; i<size; i++){
            OrderItemBean oib = orderBean.orderItem.get(i);
            contentView.addView(getItemView(oib));
            if(i < size - 1){
                contentView.addView(View.inflate(this, R.layout.divider_view, null),
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                getResources().getDimensionPixelOffset(R.dimen.grid_spacing)));
            }
        }

        // 订单价格
        TextView priceView = (TextView) findViewById(R.id.order_price);
        TextView transportView = (TextView) findViewById(R.id.order_transport);
        TextView integralView = (TextView) findViewById(R.id.order_integral);
        TextView allPriceView = (TextView) findViewById(R.id.order_all_price);
        TextView timeView = (TextView) findViewById(R.id.order_time);
        TextView numberView = (TextView) findViewById(R.id.order_number);

        priceView.setText(String.format(getString(R.string.price_format), orderBean.subtotal));
        transportView.setText(String.format(getString(R.string.price_format), "8"));
        allPriceView.setText(String.format(getString(R.string.price_format), orderBean.order_total));
        integralView.setText(orderBean.deduct_integral);
        timeView.setText(orderBean.order_time);
        numberView.setText(orderBean.order_mumber);

    }

    private View getItemView(OrderItemBean mb){
        View view = View.inflate(this, R.layout.item_order_create, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.shop_image);
        TextView nameView = (TextView) view.findViewById(R.id.shop_name);
        TextView priceView = (TextView) view.findViewById(R.id.shop_price);
        TextView countView = (TextView) view.findViewById(R.id.shop_count);

        Tools.setImageInView(this, mb.min_picture, imageView);
        nameView.setText(mb.medicine_name);
        priceView.setText(String.format(getString(R.string.price_format), mb.price));
        countView.setText(String.format(getString(R.string.count_format), mb.amount));
        return view;
    }

}
