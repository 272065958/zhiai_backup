package com.cjx.zhiai.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.PayResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cjx on 2016/12/12.
 * 支付页面
 */
public class PayActivity extends BaseActivity {
    final String WEIXIN_PAY = "1", ALIPAY_PAY = "2";
    private static final int SDK_PAY_FLAG = 1;

    View payWeixinView, payAlipayView;
    TextView payPriceView, payTipView;

    protected String currentPayType;
    String id; // 订单id和订单号

//    /**
//     * 支付宝支付业务：入参app_id
//     */
//    public static final String APPID = "2016121904424419";

//    /**
//     * 商户私钥，pkcs8格式
//     */
//    public static final String RSA_PRIVATE = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAKgyIBx5Jk3q/Eq8eZMc+BYalvgzzjSTXyKV+ihmbxRzxat1+YTqN5lut1ayq5MIUBbhiq8VoQB3VQcylcD2t14tmB2LoSuvmjYbM5EpUJ+m+LjL4Opb8zndMdn25LWpAk6mZ9MhKUEjkqrTToxSLWVOp9dcZhlc03ahwmyIry//AgMBAAECgYEAi+f2EfksRY/7gGc6cYadTjWb8qWVFuKnNeuvBEAAkfCjMjaV8VuqF/SiiHligpFdnUrKw0yoeezJS41mR/ZxG4pXtu4y2dMjVtRjMru5Y3h3Ar3OsxtFuXv/w09yC8dOAcOtAXZjR2SbaeeWtDz8HoAahivNdHJ8lWu1uHVnxgECQQDVII0uu0ZSqJNinu9Q+Z1WqVAcpd5XFZA4XQ2M19JaH6uHYS72fHf9k+9TqvpaWuXyt0Ce2UMH9b/rt6FQMLJ/AkEAyge8XX2iY187Y/7QV0he3S4GF4qwAUFiXmAYgNRERmomiWWlb/6SsZOOlLin/nOOK15X6k4gCA2CrXY6pJ/CgQJBAJ3VS3juK7gPK4b/mM9o7AI/xRpSJARt7a4wC1bgheFETu0lJXhY2SuroLNfjaPYaS6EU5DP6Po+HnFcPlR6m9UCQQCuth/kbbhX3Uw7/mlngdNfzORByZLJkyShXtLx3h8pEbU/zqJSBsIPRP7hiArnlkDVKmI24tb6f8yJe5vdL7eBAkEAtInDe6dD+DpP65P4lfkj/Gub5kNONn3m4DqhlpJoVomf6kvU2edyI3tqaG/KW1W7ukeNoGq4DNhArRiBbgdNGA==";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        setToolBar(true, null, "支付方式");
        initView();
        registerRefreshReceiver(new IntentFilter(MyApplication.ACTION_WEIXIN_PAY));
    }

    private void initView() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        payAlipayView = findViewById(R.id.pay_alipay);
        payWeixinView = findViewById(R.id.pay_weixin);
        payPriceView = (TextView) findViewById(R.id.pay_price);
        payTipView = (TextView) findViewById(R.id.pay_tip);
        payPriceView.setText(String.format(getString(R.string.price_format), intent.getStringExtra("pay_price")));
        payTipView.setText(intent.getStringExtra("pay_tip"));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_weixin:
                if (!v.isSelected()) {
                    if (api == null) {
                        api = WXAPIFactory.createWXAPI(this, MyApplication.WEIXIN_APPID);
                    }
                    boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
                    if (!isPaySupported) {
                        showToast("当前微信版本不支持支付功能");
                        return;
                    }
                    currentPayType = WEIXIN_PAY;
                    payAlipayView.setSelected(false);
                    v.setSelected(true);
                }
                break;
            case R.id.pay_alipay:
                if (!v.isSelected()) {
                    currentPayType = ALIPAY_PAY;
                    payWeixinView.setSelected(false);
                    v.setSelected(true);
                }
                break;
            case R.id.pay_button:
                if (currentPayType != null) {
                    pay();
//                    Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID);
//                    String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//                    String sign = OrderInfoUtil2_0.getSign(params, RSA_PRIVATE);
//                    final String orderInfo = orderParam + "&" + sign;
//                    alipayPay(orderInfo);
                }else{
                    showToast("请选择支付方式");
                }
                break;
        }
    }

    private void pay() {
        // 获取订单信息回调
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                switch (currentPayType) {
                    case WEIXIN_PAY: // 微信支付
                        weixinPay(response.datas);
                        break;
                    case ALIPAY_PAY: // 支付宝支付
                        alipayPay(response.datas);
                        break;
                }
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        showLoadDislog();
        String action = null;
        switch (currentPayType){
            case WEIXIN_PAY:
                action = "pay/weixinPayRequest";
                break;
            case ALIPAY_PAY:
                action = "pay/alipayRequest";
                break;
        }
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, action, "out_trade_no", id);
    }

    @Override
    protected void onBroadcastReceive(Intent intent) {
        super.onBroadcastReceive(intent);
        checkPayResult();
    }

    // 访问后台是否成功
    private void checkPayResult() {
        finish();
//        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
//            @Override
//            public void success(ResultBean response) {
//                dismissLoadDialog();
//                showToast(response.errorMsg);
//                finish();
//            }
//
//            @Override
//            public void error() {
//                dismissLoadDialog();
//            }
//        };
//        showLoadDislog();
//        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "ec_order/getPaymentResult", "sn", sn,
//                "payWay", currentPayType);
    }

    /************************************/
    /*************  支付宝  *************/
    /************************************/
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        showToast("支付成功");
                        checkPayResult();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showToast("支付失败");
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 支付宝支付
     */
    protected void alipayPay(final String orderInfo) {

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(PayActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /************************************/
    /**************  微信  **************/
    /************************************/

    private IWXAPI api;

    private void weixinPay(String json) {
        if (TextUtils.isEmpty(json)) {
            showToast("调用微信失败");
            return;
        }
        HashMap<String, String> map = new Gson().fromJson(json, new TypeToken<HashMap<String, String>>() {
        }.getType());
        PayReq req = new PayReq();
        req.appId = MyApplication.WEIXIN_APPID;
        req.partnerId = map.get("partnerid"); // 商户id
        req.prepayId = map.get("prepayid");
        req.nonceStr = map.get("noncestr");
        req.timeStamp = map.get("timestamp"); // 时间
        req.packageValue = map.get("package");
        req.sign = map.get("sign");
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        api.sendReq(req);
    }

}
