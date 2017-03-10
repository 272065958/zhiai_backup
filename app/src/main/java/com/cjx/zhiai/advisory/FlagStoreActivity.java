package com.cjx.zhiai.advisory;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cjx on 2017/1/23.
 */
public class FlagStoreActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag_store);
        setToolBar(true, null, R.string.flagstore_title);

        loadData();
    }

    private void loadData() {
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                try {
                    JSONArray array = new JSONArray(response.datas);
                    JSONObject obj = array.getJSONObject(0);
                    if(obj.has("drugstore_name")){
                        TextView nameView = (TextView) findViewById(R.id.store_name);
                        nameView.setText(obj.getString("drugstore_name"));
                    }
                    if(obj.has("phone")){
                        TextView phoneView = (TextView) findViewById(R.id.store_phone);
                        phoneView.setText(obj.getString("phone"));
                    }
                    if(obj.has("image_address")){
                        ImageView imageView = (ImageView) findViewById(R.id.store_image);
                        Tools.setImageInView(FlagStoreActivity.this, obj.getString("image_address"), imageView);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "HealingDrugs/getFlagship");
    }


}
