package com.cjx.zhiai.advisory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.MedicineBean;
import com.cjx.zhiai.sqlite.ShopCartDAO;
import com.cjx.zhiai.util.Tools;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-04.
 * 购物车
 */
public class ShopCartActivity extends BaseListActivity {
    ArrayList<MedicineBean> cartList, buyList;
    TextView priceView, payView;
    String allPrice = "0";
    int buyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_cart);
        setToolBar(true, null, R.string.shop_cart_title);
        initListView(false, false);
        setListViweDivider(new ColorDrawable(ContextCompat.getColor(this, R.color.background_divider)),
                getResources().getDimensionPixelOffset(R.dimen.auto_margin));
        priceView = (TextView) findViewById(R.id.shop_all_price);
        payView = (TextView) findViewById(R.id.shop_cart_pay);
        loadData();
        showCount();
    }

    @Override
    protected void loadData() {
        new MyAsyncTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            ShopCartDAO.getInstance(getApplicationContext()).deleteShops(buyList);
            loadData();
        }
    }

    public void selectAll(View v) {
        // 全选
        buyCount = 0;
        if (v.isSelected()) {
            allPrice = "0";
            v.setSelected(false);
            for (MedicineBean mb : cartList) {
                mb.isSelect = false;
            }
        } else {
            if (cartList == null || cartList.isEmpty()) {
                return;
            }
            BigDecimal decimal = new BigDecimal("0");
            for (MedicineBean mb : cartList) {
                mb.isSelect = true;
                decimal = decimal.add(new BigDecimal(mb.associator_price).multiply(new BigDecimal(mb.buyCount)));
                buyCount += Integer.parseInt(mb.buyCount);
            }
            allPrice = decimal.toString();
            v.setSelected(true);
        }

        showPrice();
        showCount();
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    // 显示当前总价
    private void showPrice() {
        priceView.setText(String.format(getString(R.string.price_format), allPrice));
    }

    // 显示购买数量
    private void showCount() {
        payView.setText(String.format(getString(R.string.pay_format), buyCount));
    }

    class MyAsyncTask extends AsyncTask<String, Void, ArrayList<MedicineBean>> {

        public MyAsyncTask() {
        }

        @Override
        protected ArrayList<MedicineBean> doInBackground(String... params) {
            return ShopCartDAO.getInstance(getApplicationContext()).queryShop();
        }

        @Override
        protected void onPostExecute(ArrayList<MedicineBean> list) {
            cartList = list;
            hideLoadView();
            onLoadResult(cartList);
        }
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new ShopCartAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MedicineBean mb = (MedicineBean) parent.getAdapter().getItem(position);
        View selectView = view.findViewById(R.id.shop_select_icon);
        if (mb.isSelect) {
            mb.isSelect = false;
        } else {
            mb.isSelect = true;
        }
        selectView.setSelected(mb.isSelect);
        BigDecimal decimal1 = new BigDecimal(allPrice);
        BigDecimal decimal2 = new BigDecimal(mb.associator_price).multiply(new BigDecimal(mb.buyCount));
        if (mb.isSelect) {
            allPrice = decimal1.add(decimal2).toString();
            buyCount += Integer.parseInt(mb.buyCount);
        } else {
            allPrice = decimal1.subtract(decimal2).toString();
            buyCount -= Integer.parseInt(mb.buyCount);
        }
        showPrice();
        showCount();
    }

    public void onClick(View v) {
        // 结算
        if(cartList == null){
            showToast("请先添加商品到购物车");
            return ;
        }
        if(buyList == null){
            buyList = new ArrayList<>();
        }else{
            buyList.clear();
        }
        for(MedicineBean mb : cartList){
            if(mb.isSelect){
                buyList.add(mb);
            }
        }
        if(buyList.isEmpty()){
            showToast("请选择要购买的商品");
            return ;
        }
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("medicine", buyList);
        startActivityForResult(intent, 1);
    }

    class ShopCartAdapter extends MyBaseAdapter {
        public ShopCartAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        public View createView(Context context) {
            return View.inflate(context, R.layout.item_shop, null);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            MedicineBean mb = (MedicineBean) getItem(position);
            Tools.setImageInView(context, mb.min_picture, ho.iconView);
            ho.nameView.setText(mb.medicine_name);
            ho.countView.setText(mb.buyCount);
            ho.nowPriceView.setText(String.format(getString(R.string.price_format), mb.associator_price));
            ho.oldPriceView.setText(String.format(getString(R.string.price_format), mb.market_price));
            ho.addView.setTag(mb);
            ho.minusView.setTag(mb);
            if (mb.isSelect) {
                ho.selectView.setSelected(true);
            } else {
                ho.selectView.setSelected(false);
            }
        }

        @Override
        public MyViewHolder bindViewHolder(View v) {
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        class ViewHolder extends MyViewHolder implements View.OnClickListener {
            TextView nameView, nowPriceView, oldPriceView, countView;
            View addView, minusView, selectView;
            ImageView iconView;

            public ViewHolder(View v) {
                super(v);
                iconView = (ImageView) v.findViewById(R.id.shop_detail_photo);
                nameView = (TextView) v.findViewById(R.id.medicine_name);
                nowPriceView = (TextView) v.findViewById(R.id.medicine_associator_price);
                oldPriceView = (TextView) v.findViewById(R.id.medicine_market_price);
                oldPriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
                countView = (TextView) v.findViewById(R.id.count_view);
                selectView = v.findViewById(R.id.shop_select_icon);
                addView = v.findViewById(R.id.count_add);
                minusView = v.findViewById(R.id.count_minus);
                addView.setOnClickListener(this);
                minusView.setOnClickListener(this);
                addView.setTag(R.id.count_view, countView);
                minusView.setTag(R.id.count_view, countView);
            }

            @Override
            public void onClick(View v) {
                MedicineBean mb = (MedicineBean) v.getTag();
                TextView countView = (TextView) v.getTag(R.id.count_view);
                int count = Integer.parseInt(countView.getText().toString());
                boolean isAdd = false;
                switch (v.getId()) {
                    case R.id.count_add:
                        count++;
                        isAdd = true;
                        break;
                    case R.id.count_minus:
                        if (count > 0) {
                            count--;
                        } else {
                            return;
                        }
                        break;
                }
                updateCount(mb, String.valueOf(count), countView, isAdd);
            }

            private void updateCount(MedicineBean mb, String count, TextView countView, boolean isAdd) {
                if (ShopCartDAO.getInstance(getApplicationContext()).updateShop(mb.medicine_id, count) > 0) {
                    countView.setText(count);
                    mb.buyCount = count;
                    if (mb.isSelect) {
                        BigDecimal decimal1 = new BigDecimal(allPrice);
                        BigDecimal decimal2 = new BigDecimal(mb.associator_price);
                        if (isAdd) {
                            allPrice = decimal1.add(decimal2).toString();
                            buyCount++;
                        } else {
                            allPrice = decimal1.subtract(decimal2).toString();
                            buyCount--;
                        }
                        showPrice();
                        showCount();
                    }
                }
            }
        }
    }
}
