package com.cjx.zhiai.advisory;

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
import com.cjx.zhiai.bean.MedicineBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.bean.UserBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.my.UpdatePeopleInfoActivity;
import com.cjx.zhiai.util.JsonParser;
import com.cjx.zhiai.util.Tools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cjx on 2017/1/23.
 * 订单填写
 */
public class OrderActivity extends BaseActivity {
    ArrayList<MedicineBean> medicineList;
    //    final String transportPrice = "8";
    View integralView;
    TextView totalPriceView, allPriceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        setToolBar(true, null, R.string.order_title);
        findViewById();
        loadInteger();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setReceiveInfo();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadInteger() {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                try {
                    ((TextView) findViewById(R.id.integral)).setText(response.datas);
                    int integral = Integer.parseInt(response.datas);
                    if (integral > 0) {
                        integralView.setClickable(true);
                        integralView.setTag(response.datas);
                        integralView.setTag(R.id.tag_view, response.datas);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error() {

            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "HealingDrugs/getIntegral");
    }

    private void findViewById() {
        medicineList = (ArrayList<MedicineBean>) getIntent().getSerializableExtra("medicine");
        if (medicineList == null || medicineList.isEmpty()) {
            showToast("没有商品信息");
            return;
        }
        integralView = findViewById(R.id.integral_select);
        integralView.setClickable(false);

        TextView transportPriceView = (TextView) findViewById(R.id.order_transport_price);
        totalPriceView = (TextView) findViewById(R.id.order_total_price);
        allPriceView = (TextView) findViewById(R.id.order_all_price);

        LinearLayout contentView = (LinearLayout) findViewById(R.id.order_shop_content);
        int size = medicineList.size();
        BigDecimal price = new BigDecimal("0");
        for (int i = 0; i < size; i++) {
            MedicineBean mb = medicineList.get(i);
            contentView.addView(getItemView(mb));
            if (i < size - 1) {
                contentView.addView(View.inflate(this, R.layout.divider_view, null),
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                getResources().getDimensionPixelOffset(R.dimen.grid_spacing)));
            }
            price = price.add(new BigDecimal(mb.associator_price).multiply(new BigDecimal(mb.buyCount)));
        }
        String priceStr = price.toString();
        totalPriceView.setText(String.format(getString(R.string.price_format), priceStr));
        totalPriceView.setTag(priceStr);
        allPriceView.setText(String.format(getString(R.string.price_format), priceStr));
        allPriceView.setTag(priceStr);
        transportPriceView.setText(String.format(getString(R.string.price_format), "8"));

        setReceiveInfo();
    }

    // 设置收货地址
    private void setReceiveInfo() {
        UserBean user = MyApplication.getInstance().user;
        TextView nameView = (TextView) findViewById(R.id.order_receive_name);
        TextView phoneView = (TextView) findViewById(R.id.order_receive_phone);
        TextView addressView = (TextView) findViewById(R.id.order_receive_address);
        nameView.setText(TextUtils.isEmpty(user.user_real_name) ? user.user_name : user.user_real_name);
        phoneView.setText(user.user_phone);
        addressView.setText(user.user_address);
    }

    private View getItemView(MedicineBean mb) {
        View view = View.inflate(this, R.layout.item_order_create, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.shop_image);
        TextView nameView = (TextView) view.findViewById(R.id.shop_name);
        TextView priceView = (TextView) view.findViewById(R.id.shop_price);
        TextView countView = (TextView) view.findViewById(R.id.shop_count);

        Tools.setImageInView(this, mb.min_picture, imageView);
        nameView.setText(mb.medicine_name);
        priceView.setText(String.format(getString(R.string.price_format), mb.associator_price));
        countView.setText(String.format(getString(R.string.count_format), mb.buyCount));
        return view;
    }

    // 设置个人信息
    public void setInfo(View v) {
        Intent intent = new Intent(this, UpdatePeopleInfoActivity.class);
        startActivityForResult(intent, 1);
    }

    // 用积分抵扣
    public void selectIntegral(View view) {
        boolean isSelect = !view.isSelected();
        view.setSelected(isSelect);

        float allPrice = Float.parseFloat((String) allPriceView.getTag());
        float allIntegral = Float.parseFloat((String) integralView.getTag(R.id.tag_view));
        BigDecimal price = new BigDecimal((String) allPriceView.getTag());
        BigDecimal integral = new BigDecimal((String) integralView.getTag(R.id.tag_view));
        BigDecimal multiple = new BigDecimal("10");
        String priceStr;
        if (isSelect) {
            if (allPrice > allIntegral / 10) { // 总价大于积分价
                priceStr = price.subtract(integral.divide(multiple)).toString();
                integralView.setTag(integralView.getTag(R.id.tag_view));
            } else {
                priceStr = "0";
                integralView.setTag(price.multiply(multiple).toString());
            }
        } else {
            priceStr = price.add(new BigDecimal((String)integralView.getTag()).divide(multiple)).toString();
            integralView.setTag("0");
        }
        allPriceView.setText(String.format(getString(R.string.price_format), priceStr));
        allPriceView.setTag(priceStr);

    }

    // 提交订单
    public void submit(View view) {
        HashMap<String, String> map = new HashMap<>();
        for (MedicineBean mb : medicineList) {
            map.put(mb.medicine_id, mb.buyCount);
        }
        String orderItemList = JsonParser.getInstance().toJson(map);
        final String order_total = (String) allPriceView.getTag(); // 合计
        String subtotal = (String) totalPriceView.getTag(); // 小计
        String integral = integralView.isSelected() ? (String) integralView.getTag() : "0"; // 积分
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                showToast(response.errorMsg);
                try {
                    JSONObject obj = new JSONObject(response.datas);
                    if(obj.has("orderInfo")){
                        JSONArray array = obj.getJSONArray("orderInfo");
                        JSONObject obj1 = array.getJSONObject(0);
                        if(obj1.has("order_mumber")){
                            sendBroadcast(new Intent(MyApplication.ACTION_ORDER));
                            finish();
                            Intent payIntent = new Intent(OrderActivity.this, PayActivity.class);
                            payIntent.putExtra("pay_price", order_total);
                            payIntent.putExtra("pay_tip", "购买药品");
                            payIntent.putExtra("id", obj1.getString("order_mumber"));
                            startActivity(payIntent);
                        }else{
                            showToast(getString(R.string.http_error));
                        }
                    }else{
                        showToast(getString(R.string.http_error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(getString(R.string.http_error));
                }

            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "HealingDrugs/saveOrder", "orderItemList", orderItemList,
                "order_total", order_total, "subtotal", subtotal, "integral", integral);
    }
}
