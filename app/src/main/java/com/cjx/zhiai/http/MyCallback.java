package com.cjx.zhiai.http;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.activity.LoginActivity;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.Code;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.util.JsonParser;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cjx on 2016/7/18.
 */
public class MyCallback implements Callback {

    BaseActivity activity;
    MyCallbackInterface callbackInterface;
    Request request;
    public MyCallback(BaseActivity activity, MyCallbackInterface callbackInterface, Request request){
        this.activity = activity;
        this.callbackInterface = callbackInterface;
        this.request = request;
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        if(activity == null || activity.isFinishing()){
            return ;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callbackInterface.error();
                if(e instanceof SocketTimeoutException){
                    Toast.makeText(activity, activity.getString(R.string.http_error), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity, activity.getString(R.string.http_steam_exception), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if(activity == null || activity.isFinishing()){
            return ;
        }
        final String body = response.body().string();
        final ResultBean r = JsonParser.getInstance().getDatumResponse(body);
        if(r == null){
            // 保存异常信息到文件
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(r == null){
                    callbackInterface.error();
                    Toast.makeText(activity, activity.getString(R.string.http_exception), Toast.LENGTH_SHORT).show();
                    return ;
                }
                switch (r.errorCode){
                    case Code.SUCCESS:
                        callbackInterface.success(r);
                        break;
                    case Code.TOKEN_INVALID:
                        ((MyApplication)activity.getApplication()).setLogin(null);
                        HttpUtils.getInstance().clearCookie();
                        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                                activity.getString(R.string.app_name), Activity.MODE_PRIVATE);
                        String oldAcc = sharedPreferences.getString("account", null);

                        if(!TextUtils.isEmpty(oldAcc)){
                            String oldPwd = sharedPreferences.getString("password", null);
                            autoLogin(oldAcc, oldPwd, request);
                        }else{
                            callbackInterface.error();
                            Toast.makeText(activity, r.errorMsg, Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(activity, LoginActivity.class);
                            activity.startActivityForResult(loginIntent, activity.RESULT_LOGIN);
                        }
                        break;
                    default:
                        callbackInterface.error();
                        Toast.makeText(activity, r.errorMsg, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void autoLogin(String acc, String pwd, Request request){
        CustomCallback myCallback = new CustomCallback(this, request);
        HttpUtils.getInstance().postEnqueue(myCallback, "user/login", "username", acc, "password", pwd, "type", MyApplication.getInstance().userType);
    }

    class CustomCallback implements Callback{
        Callback callback;
        Request request;
        public CustomCallback(Callback callback, Request request){
            this.callback = callback;
            this.request = request;
        }
        @Override
        public void onFailure(Call call, IOException e) {
            if(activity != null && !activity.isFinishing()){
                callback.onFailure(call, e);
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.e("TAG", "auto login response");
            if(activity == null || activity.isFinishing()){
                return ;
            }
            final ResultBean r = JsonParser.getInstance().getDatumResponse(response.body().string());
            if(r != null && r.errorCode.equals(Code.SUCCESS)){
                HttpUtils.getInstance().enqueue(callback, request);
                MyApplication app = (MyApplication) activity.getApplication();
                app.setLogin(r.datas);
                activity.sendBroadcast(new Intent(MyApplication.ACTION_USER_INFO_UPDATE)); //发送登录成功广播
            }else{
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callbackInterface.error();
                        Intent loginIntent = new Intent(activity, LoginActivity.class);
                        activity.startActivityForResult(loginIntent, activity.RESULT_LOGIN);
                    }
                });
            }
        }
    }
}
