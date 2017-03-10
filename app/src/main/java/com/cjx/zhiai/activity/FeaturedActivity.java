package com.cjx.zhiai.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.NewBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.JsonParser;
import com.cjx.zhiai.util.Tools;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-26.
 * 每日精选
 */
public class FeaturedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured);
        setToolBar(true, null, getIntent().getIntExtra("title", R.string.main_day_featured));

        loadData(getIntent().getAction());
    }

    private void loadData(String action) {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response.datas);
                    if (object.has("otherInfo")) {
                        Type type = new TypeToken<ArrayList<NewBean>>(){}.getType();
                        ArrayList<NewBean> list =  JsonParser.getInstance().fromJson(object.getString("otherInfo"), type);
                        if (list != null && !list.isEmpty()) {
                            NewBean newBean = list.get(0);
                            newBean.content = object.getString("content");
                            initView(newBean);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error() {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "article/selectNews", "news_id", action);
    }

    private void initView(NewBean newBean) {
        findViewById(R.id.featured_content).setVisibility(View.VISIBLE);
        int width = ((MyApplication) getApplication()).getScreen_width();
        int height = (int) (width * 299 / 627f);
        ImageView imageView = (ImageView) findViewById(R.id.featured_image);
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        lp.height = height;
        Tools.setImageInView(this, newBean.max_image, imageView);
        ((TextView)findViewById(R.id.featured_title)).setText(newBean.title);
        ((TextView)findViewById(R.id.featured_time)).setText(newBean.create_time);
        ((TextView)findViewById(R.id.featured_detail)).setText(Html.fromHtml(newBean.content));
    }
}
