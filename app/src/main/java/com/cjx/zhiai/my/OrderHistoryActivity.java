package com.cjx.zhiai.my;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseClassAdapter;
import com.cjx.zhiai.base.BaseTabActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.OrderBean;
import com.cjx.zhiai.bean.OrderItemBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.util.Tools;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-06.
 * 我的订单
 */
public class OrderHistoryActivity extends BaseTabActivity {

    String[] stat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolBar(true, null, R.string.my_order);
        page = 1;
        limit = 10;
        stat = new String[]{"3", "1", "2"};
        initPagerView(new String[]{"全部", "待付款", "已完成"}, false, false);
        setListViweDivider(null, 0);

        registerRefreshReceiver(new IntentFilter(MyApplication.ACTION_ORDER_OPERATE));
    }

    // 收到广播回调
    protected void onBroadcastReceive(Intent intent){
        refresh();
    }

    @Override
    protected void loadData(int position) {
        HttpUtils.getInstance().postEnqueue(this, getMyCallbackInterface(position, new TypeToken<ArrayList<OrderBean>>() {
        }.getType()), "HealingDrugs/selectOrderList", "page", String.valueOf(page), "limit", String.valueOf(limit), "type", stat[position]);
    }

    @Override
    protected void onLoadResult(int position, ArrayList<?> list) {
        for (Object obj : list) {
            ((OrderBean) obj).format();
        }
        super.onLoadResult(position, list);
    }

    @Override
    protected MyBaseAdapter getMyBaseAdapter(int position, ArrayList<?> list) {
        return new MyOrderAdapter(list, this, 1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OrderBean ob = (OrderBean) (parent.getAdapter()).getItem(position);
        if(ob == null || ob.orderItem == null || ob.orderItem.isEmpty()){
            showToast("没有商品信息");
            return ;
        }
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra("order", ob);
        startActivity(intent);
    }

    class MyOrderAdapter extends BaseClassAdapter {

        public MyOrderAdapter(ArrayList<?> list, BaseActivity context, int columNum) {
            super(list, context, columNum);
        }

        @Override
        protected ParentViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, ParentViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            OrderBean ob = (OrderBean) getItem(position);
            ho.titleView.setText(ob.FlagshipName);
            ho.timeView.setText(ob.order_time);
            ho.priceView.setText(String.format(getString(R.string.price_format), ob.order_total));
            ho.stateView.setText(ob.order_status);
        }

        @Override
        protected ArrayList<?> getItemList(int position) {
            return ((OrderBean) getItem(position)).orderItem;
        }

        @Override
        protected View createItemView(Context context) {
            return View.inflate(context, R.layout.item_order, null);
        }

        @Override
        protected void bindItemData(int position, Object obj, ItemViewHolder holder) {
            ItemHolder ho = (ItemHolder) holder;
            OrderItemBean oib = (OrderItemBean) obj;
            Tools.setImageInView(context, oib.min_picture, ho.iconView);
            ho.countView.setText(String.format(getString(R.string.count_total_format), oib.amount));
            ho.nameView.setText(oib.medicine_name);
        }

        @Override
        protected ItemViewHolder bindItemViewHolder(View v) {
            return new ItemHolder(v);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_order_content, null);
        }

        class ViewHolder extends ParentViewHolder {
            TextView titleView, stateView, timeView, priceView;

            public ViewHolder(View v) {
                super(v);
                titleView = (TextView) v.findViewById(R.id.order_title);
                stateView = (TextView) v.findViewById(R.id.order_status);
                timeView = (TextView) v.findViewById(R.id.order_time);
                priceView = (TextView) v.findViewById(R.id.order_all_price);
            }

            protected LinearLayout getContentView(View view) {
                return (LinearLayout) view.findViewById(R.id.order_item_content);
            }
        }

        class ItemHolder extends ItemViewHolder {
            ImageView iconView;
            TextView nameView, countView;

            public ItemHolder(View view) {
                super(view);
                iconView = (ImageView) view.findViewById(R.id.shop_detail_photo);
                nameView = (TextView) view.findViewById(R.id.medicine_brand);
                countView = (TextView) view.findViewById(R.id.medicine_count);
            }
        }
    }
}
