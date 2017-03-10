package com.cjx.zhiai.util;

import android.text.TextUtils;
import android.util.Log;

import com.cjx.zhiai.bean.ResultBean;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by cjx on 2016/7/13.
 */
public class JsonParser {
    static JsonParser instance;
    Gson gson;
    private JsonParser() {
        gson = new Gson();
    }

    public static JsonParser getInstance() {
        if (instance == null) {
            synchronized (JsonParser.class) {
                if (instance == null) {
                    instance = new JsonParser();
                }
            }
        }
        return instance;
    }

    /**
     * 解析服务器返回数据
     */
    public ResultBean getDatumResponse(String response){
        if(TextUtils.isEmpty(response)){
            return null;
        }
        ResultBean rb = null;
        try {
            Log.e("response", ">>> "+response);
            JSONObject obj = new JSONObject(response);
            rb = new ResultBean();
            if(obj.has("errorCode")){
                rb.errorCode = obj.getString("errorCode");
            }
            if(obj.has("errorMsg")){
                rb.errorMsg = obj.getString("errorMsg");
            }
            if(obj.has("datas")){
                rb.datas = obj.getString("datas");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rb;
    }

    public <T> T fromJson(String json, Type typeOfT){
        if(TextUtils.isEmpty(json)){
            return null;
        }
        try{
            return gson.fromJson(json, typeOfT);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Object fromJson(String json, Class<?> classOfT){
        if(TextUtils.isEmpty(json)){
            return null;
        }
        try{
            return gson.fromJson(json, classOfT);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String toJson(Object obj){
        return gson.toJson(obj);
    }
}
