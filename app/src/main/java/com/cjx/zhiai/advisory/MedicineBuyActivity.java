package com.cjx.zhiai.advisory;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.MedicineBean;
import com.cjx.zhiai.sqlite.ShopCartDAO;
import com.cjx.zhiai.util.Tools;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-04.
 */
public class MedicineBuyActivity extends BaseActivity {
    MedicineBean medicineBean;
    TextView cartCountView;
    int cartCount;
    BigDecimal decimal = new BigDecimal("100");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_buy);
        setToolBar(true, null, R.string.shop_detail_title);
        registerRefreshReceiver(new IntentFilter(MyApplication.ACTION_ORDER));
        initView();

    }

    // 收到广播回调
    @Override
    protected void onBroadcastReceive(Intent intent){
        finish();
    }

    private void initView() {
        medicineBean = (MedicineBean) getIntent().getSerializableExtra("medicine");
        if(medicineBean == null){
            showToast("没有药品信息");
            finish();
            return ;
        }
        medicineBean.buyCount = "1";
        ImageView imageView = (ImageView) findViewById(R.id.medicine_image);
        TextView nameView = (TextView) findViewById(R.id.medicine_name);
        TextView associatorPriceView = (TextView) findViewById(R.id.medicine_associator_price);
        TextView marketPriceView = (TextView) findViewById(R.id.medicine_market_price);
        marketPriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
        TextView saleView = (TextView) findViewById(R.id.medicine_sale_count);
        TextView periodView = (TextView) findViewById(R.id.medicine_guarantee_period);
        TextView numberView = (TextView) findViewById(R.id.medicine_ratify_number);
        TextView producerView = (TextView) findViewById(R.id.medicine_producer);

        Tools.setImageInView(this, medicineBean.max_picture, imageView);
        nameView.setText(medicineBean.medicine_name);
        associatorPriceView.setText(String.format(getString(R.string.price_format), new BigDecimal(medicineBean.associator_price).divide(decimal)));

        marketPriceView.setText(String.format(getString(R.string.price_format), new BigDecimal(medicineBean.market_price).divide(decimal)));
        periodView.setText(String.format(getString(R.string.price_format), medicineBean.guarantee_period));

        saleView.setText(String.format(getString(R.string.medicine_sale_format), medicineBean.sales_volume));
        numberView.setText(medicineBean.ratify_number);
        producerView.setText(medicineBean.producer);

        cartCountView = (TextView) findViewById(R.id.shop_count);
        cartCount = (int) ShopCartDAO.getInstance(getApplicationContext()).getShopCount();
        showCountView();
    }

    private void showCountView(){
        if(cartCount > 0){
            cartCountView.setVisibility(View.VISIBLE);
            cartCountView.setText(String.valueOf(cartCount));
        }else{
            cartCountView.setVisibility(View.GONE);
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.store_detail:
                Intent intent = new Intent(this, FlagStoreActivity.class);
                startActivity(intent);
                break;
            case R.id.shop_cart_add: // 添加到购物车
                medicineBean.buyCount = "1";
                int count = (int) ShopCartDAO.getInstance(getApplicationContext()).insertShop(medicineBean);
                if(count > 0){
                    cartCount ++;
                    showCountView();
                    showToast("添加成功!");
                }else{
                    showToast("购物车已有此商品");
                }
                break;
            case R.id.shop_cart_pay: // 立即购买
                Intent orderIntent = new Intent(this, OrderActivity.class);
                ArrayList<MedicineBean> list = new ArrayList<>();
                list.add(medicineBean);
                orderIntent.putExtra("medicine", list);
                startActivity(orderIntent);
                break;
            case R.id.shop_cart_icon:  // 购物车
                Intent cartIntent = new Intent(this, ShopCartActivity.class);
                startActivity(cartIntent);
                break;
        }
    }
}
