package com.cjx.zhiai.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cjx.zhiai.R;

/**
 * Created by cjx on 2017-01-03.
 * 定位工具
 */
public class LocationUtil {
    private static LocationUtil instance;
    LocationClient mLocationClient = null;
    BDLocationListener myListener = new MyLocationListener();
    SharedPreferences sharedPreferences;
    private LocationUtil() {

    }

    public static LocationUtil getInstance() {
        if (instance == null) {
            synchronized (JsonParser.class) {
                if (instance == null) {
                    instance = new LocationUtil();
                }
            }
        }
        return instance;
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span=1000;
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public void startLocation(Context context) {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(context);     //声明LocationClient类
            mLocationClient.registerLocationListener(myListener);
            sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
            initLocation();
        }
        mLocationClient.start();
    }

    class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            //Receive Location
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String lat = String.valueOf(location.getLatitude());
            String lon = String.valueOf(location.getLongitude());
            editor.putString("latitude", lat);
            editor.putString("longitude", lon);
            editor.putString("province", location.getProvince());
            editor.apply();
            if(callback != null){
                callback.callback(lat, lon);
            }
        }
    }

    LocationCallback callback;
    public LocationUtil setLocationCallback(LocationCallback callback){
        this.callback = callback;
        return instance;
    }

    public interface LocationCallback{
        void callback(String lat, String lon);
    }
}
