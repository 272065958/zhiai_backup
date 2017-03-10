package com.cjx.zhiai.http;

import android.util.Log;

import com.cjx.zhiai.BuildConfig;
import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.base.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by cjx on 2016/6/21.
 * 访问网络的类
 */
public class HttpUtils {
    static HttpUtils instance;
    OkHttpClient client;
    private String serverApiUri = BuildConfig.SERVER_API_URI;
//    private MyCookieJar cookieJar;

    final String API_FILE = "api_connect";

//    class MyCookieJar implements CookieJar {
//        private final ArrayList<Cookie> cookieStore = new ArrayList<>();
//
//        @Override
//        public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
//            //当获得一个Response时，会调用这个方法来存储Cookie
//            cookieStore.addAll(list);
//        }
//
//        @Override
//        public List<Cookie> loadForRequest(HttpUrl httpUrl) {
//            //当要call一个Request时，会调用这个方法来为请求的head添加cookie
//            List<Cookie> cookies = cookieStore;
//            return cookies != null ? cookies : new ArrayList<Cookie>();
//        }
//
//        public void clearCookie(){
//            if(cookieStore != null){
//                cookieStore.clear();
//            }
//        }
//    }

    private HttpUtils() {
//        cookieJar = new MyCookieJar();
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
//                .cookieJar(cookieJar)
                .build();
    }

    public static HttpUtils getInstance() {
        if (instance == null) {
            synchronized (HttpUtils.class) {
                if (instance == null) {
                    instance = new HttpUtils();
                }
            }
        }
        return instance;
    }

    // 清除登录cookid
    public void clearCookie() {
//        cookieJar.clearCookie();
    }

    public void setServerApiUri(String url) {
        serverApiUri = url;
    }

    public String getServerApiUri() {
        return serverApiUri;
    }

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    /**
     * 使用okhttp的post方法访问服务器
     *
     * @param activity          当前界面
     * @param callbackInterface 自定义回调函数
     * @param action            服务器action
     * @param params            访问的参数
     */
    public void postEnqueue(BaseActivity activity, MyCallbackInterface callbackInterface, String action, String... params) {
        Request request = getRequest(serverApiUri + action, getFormBody(params));
        enqueue(generateMyCallback(activity, callbackInterface, request), request);
    }

    // 发起post请求
    public void postEnqueue(Callback callback, String action, String... params) {
        Request request = getRequest(serverApiUri + action, getFormBody(params));
        enqueue(callback, request);
    }

    // 发起请求
    public void enqueue(Callback callback, Request request) {
        client.newCall(request).enqueue(callback);
    }

    // 获取一个request
    private Request getRequest(String url, RequestBody body) {
        Log.e("TAG", "url = " + url);
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).post(body).build();
        return request;
    }

    /**
     * 获取一个请求的参数body
     *
     * @param params
     * @return
     */
    private RequestBody getFormBody(String... params) {
        RequestBody body = null;
        if (params != null) {
            int length = params.length;
            if (length % 2 == 0) {
                FormBody.Builder builder = new FormBody.Builder();
                int count = length / 2;
                for (int i = 0; i < count; i++) {
                    String value = params[i * 2 + 1];
                    if (value == null) {
                        value = "";
                    }
                    builder.add(params[i * 2], value);
                    Log.e("TAG", params[i * 2] + "=" + value);
                }
                if (MyApplication.token != null) {
                    builder.add("token", MyApplication.token);
                    Log.e("TAG", "token=" + MyApplication.token);
                }
                body = builder.build();
            } else {
                Log.e("TAG", "======== > params error");
            }
        }
        return body;
    }

    // 创建一个文件上传请求
    private RequestBody getMultipartBody(ArrayList<String> path, String... params) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (params != null) {
            int length = params.length;
            if (length % 2 == 0) {
                int count = length / 2;
                for (int i = 0; i < count; i++) {
                    String value = params[i * 2 + 1];
                    if (value == null) {
                        value = "";
                    }
                    builder.addFormDataPart(params[i * 2], value);
                    Log.e("TAG", params[i * 2] + "=" + value);
                }
                if (MyApplication.token != null) {
                    builder.addFormDataPart("token", MyApplication.token);
                }
            } else {
                Log.e("TAG", "======== > params error");
            }
        }
        if (!path.isEmpty()) {
            int i = 0;
            for (String str : path) {
                File f = new File(str);
                if (f.exists()) {
                    // 设置上传文件为  图片
                    builder.addFormDataPart("files", f.getName(), RequestBody.create(MEDIA_TYPE_PNG, f));
                    Log.e("TAG", "files " + i + " path = " + str);
                } else {
                    Log.e("TAG", "file_" + i + " no exist,  path = " + str);
                }
            }
        }
        return builder.build();
    }

    // 创建一个下载
    public Call download(String url, final ProgressResponseListener listener, Callback callback) {
        //构造请求
        final Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient downloadClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        ProgressResponseBody body = new ProgressResponseBody(originalResponse.body(), listener);
                        return originalResponse.newBuilder().body(body).build();
                    }
                })
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                        //其他配置
                .build();
        Call call = downloadClient.newCall(request);
        call.enqueue(callback);
        return call;
    }

    // 创建一个上传
    public Call upload(BaseActivity activity, ProgressRequestListener listener,
                       MyCallbackInterface callbackInterface, ArrayList<String> path, String action, String... params) {
        //构造上传请求，类似web表单
        RequestBody requestBody = getMultipartBody(path, params);
        final Request request = getRequest(serverApiUri + action, new ProgressRequestBody(requestBody, listener));
        return upload(generateMyCallback(activity, callbackInterface, request), request);
    }

    private Call upload(Callback callback, Request request) {
        Call call = null;
        try {
            call = client.newCall(request);
            call.enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return call;
    }

    private Callback generateMyCallback(BaseActivity activity, MyCallbackInterface callbackInterface, Request request) {
        return new MyCallback(activity, callbackInterface, request);
    }

}
