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
import com.cjx.zhiai.util.OrderInfoUtil2_0;
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
    public static final String APPID = "2016073100135668";
    /** 支付宝账户登录授权业务：入参pid值 */
    public static final String PID = "";
    /** 支付宝账户登录授权业务：入参target_id值 */
    public static final String TARGET_ID = "2088521540525430";
//    /**
//     * 商户私钥，pkcs8格式
//     */
    public static final String RSA2_PRIVATE = "";
    public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALn/B+MGSYeoKs9GvJ/5ugWjjcGzt/bQ5rjuRRjpJNF0VQBVmh4yGY3SmWkQ1/s9qUdwUnID+W9rPgdp0Fm3MYvX/GZMJT0Smw+e1+T9wiV9EWiV4SnI7jplaNTXtuB1UPBqmtHFJ6ZGdVdGEQnCGDYxFFU+D5pYFZ0TX+gObG1BAgMBAAECgYBuErkXKQRhDSvwqTs+LatiZO2iwfpQTkcNEK3B1VBdyMv5O6/OyPWIkicKH9bCMDa7OYUBRsranowCFSQhxCHET9D5M6MovH6YqSpJ19EKvr+q1oums6l4uCypN/NwLPULC/N9itSgre45uF5jyBJZjG5NY9R8kHc818ZhV8agAQJBANut1qDn50l4Wr7mzfidnM5i5HCvywCS1x/VXNhS/mpwYfFj/pb32MaUDOvQJ0kaQhqh5R6tW6V2cM6oLsEE2YECQQDYv4dJ8ixAR2C6CRUodnLqUi2VxjCagkMcJGtkq5wTHqdnURJidHN4xZ3sjXZpt7T7Wl3/LJfxesM4t20Qz/PBAkAZu7JhpOi9/YA7zpOgJO5iaskxvhX8mjbi/r5ihM5Sr5l5imofSyc0k9Ezqm1/rbjCn+ZUAqCysD4kpyTa7XOBAkBK1IT+sI86eeoZED2vxIUUBN8cEFqDXWmR87jn/p9ZsoGVF9ZDC3U6Qu+s1YIGKZhgFujQyjKC+iEgGaOb5E+BAkAQ6CAx9rQ4ibUBADLKEv4Aibw3kKgbE0pmiRNJM8iOcnDkH0zEPSZ8zPTc9bXp4V9IJiFzadYKGP6ehsXJkbQ3";

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
//                    pay();
                    boolean rsa2 = (RSA2_PRIVATE.length() > 0);
                    Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
                    String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);

                    String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
                    String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
                    final String authInfo = info + "&" + sign;
                    alipayPay(authInfo);
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
