package com.cjx.zhiai;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import com.cjx.zhiai.bean.DepartmentBean;
import com.cjx.zhiai.bean.UserBean;
import com.cjx.zhiai.util.HuanXinUtil;
import com.cjx.zhiai.util.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-25.
 * 程序上下文
 */
public class MyApplication extends Application {
    public final String USER_TYPE_DOCTOR = "1", USER_TYPE_PEOPLE = "0"; // 医生和普通用户的用户类型
    public static final String ACTION_USER_INFO_UPDATE = "action_user_update_zhiai";
    public static final String ACTION_REGISTER = "action_register_zhiai";
    public static final String ACTION_WITHDRAW = "action_withdraw_zhiai";
    public static final String ACTION_ORDER = "action_order_zhiai";
    public static final String ACTION_ORDER_OPERATE = "action_order_operate_zhiai";
    public static final String ACTION_WEIXIN_PAY = "action_weixin_pay_zhiai";

    public static final String WEIXIN_APPID = "wx27a093c76a578141";

    private int SCREEN_WIDTH = 0, SCREEN_HEIGHT = 0;

    private static MyApplication instance;

    public static String token;
    public UserBean user;
    public SharedPreferences sharedPreferences;
    public String userType;
    HuanXinUtil huanxin;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        login();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public void initHuanXin(){
        if(huanxin != null){
            return ;
        }
        huanxin = HuanXinUtil.getInstance();
        huanxin.init(this);
        if(user != null){
            huanxin.login(user.user_id);
        }
    }

    // 获取登录信息
    private void login() {
        userType = sharedPreferences.getString("userType", USER_TYPE_PEOPLE);
        token = sharedPreferences.getString("token", null);
        user = (UserBean) JsonParser.getInstance().fromJson(sharedPreferences.getString("user", null), UserBean.class);
    }

    /**
     * 返回是否已经登录
     *
     * @return 是否登录
     */
    public boolean isLogin() {
        return token != null && user != null;
    }

    /**
     * 设置/清除登录信息
     */
    public void setLogin(String info) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (info == null) {
            token = null;
            user = null;
            editor.remove("token");
            editor.remove("user");
            editor.remove("userType");
            huanxin.logout();
        } else {
            try {
                JSONObject json = new JSONObject(info);
                if (json.has("token")) {
                    token = json.getString("token");
                    editor.putString("token", token);
                }
                if (json.has("user")) {
                    String userStr = json.getString("user");
                    editor.putString("user", userStr);
                    user = (UserBean) JsonParser.getInstance().fromJson(userStr, UserBean.class);
                    if(user != null && huanxin != null){
                        huanxin.login(user.user_id);
                        huanxin.setOwnUser(user.user_id, user.head_image);
                    }
                }
                editor.putString("userType", userType);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.apply();
    }

    /**
     * 保存用户信息
     */
    public void saveUserCache() {
        String json = JsonParser.getInstance().toJson(user);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", json);
        editor.apply();
    }

    /**
     * 获取屏幕尺寸
     */
    private void measureScreen() {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        SCREEN_HEIGHT = displayMetrics.heightPixels;
        SCREEN_WIDTH = displayMetrics.widthPixels;
    }

    /**
     * 获取当前手机宽度
     */
    public int getScreen_width() {
        if (SCREEN_WIDTH <= 0) {
            measureScreen();
        }
        return SCREEN_WIDTH;
    }

    /**
     * 获取当前手机高度
     */
    public int getScreen_height() {
        if (SCREEN_HEIGHT <= 0) {
            measureScreen();
        }
        return SCREEN_HEIGHT;
    }

    // 缓存科室列表
    ArrayList<DepartmentBean> departmentList;
    public void setDepartmentList(ArrayList<DepartmentBean> list){
        departmentList = list;
    }

    public ArrayList<DepartmentBean> getDepartmentList(){
        return departmentList;
    }
}
