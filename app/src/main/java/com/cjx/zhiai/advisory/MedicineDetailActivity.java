package com.cjx.zhiai.advisory;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.adapter.ImagePagerAdapter;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.MedicineBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.component.PagerPointView;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.JsonParser;

/**
 * Created by cjx on 2016-12-03.
 */
public class MedicineDetailActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    PagerPointView pagerPointView;
    MedicineBean medicineBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);
        setToolBar(true, null, R.string.medicine_title);
        registerRefreshReceiver(new IntentFilter(MyApplication.ACTION_ORDER));
        loadData();
    }

    @Override
    protected void onBroadcastReceive(Intent intent){
        finish();
    }

    private void loadData(){
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
                findViewById(R.id.medicine_content).setVisibility(View.VISIBLE);
                medicineBean = (MedicineBean) JsonParser.getInstance().fromJson(response.datas, MedicineBean.class);
                initView();
            }

            @Override
            public void error() {
                findViewById(R.id.loading_view).setVisibility(View.GONE);
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "HealingDrugs/medicineParticulars", "medicine_id", getIntent().getAction());
    }

    private void initView(){
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        int width = ((MyApplication)getApplication()).getScreen_width();
        int height = (int) (width * 298 / 593f);
        viewPager.getLayoutParams().height = height;
        viewPager.addOnPageChangeListener(this);
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, medicineBean.max_picture.split(","));
        viewPager.setAdapter(adapter);
        pagerPointView = (PagerPointView)findViewById(R.id.pager_point_view);
        int size = getResources().getDimensionPixelSize(R.dimen.text_margin);
        pagerPointView.setPoint(size, size, -1, R.drawable.circle_gray, adapter.getCount() + 1);
        Bitmap select = BitmapFactory.decodeResource(getResources(), R.drawable.circle_blue);
        select = Bitmap.createScaledBitmap(select, size*3, size, true);
        pagerPointView.setSelectBitmap(select);

        TextView ilnessView = (TextView) findViewById(R.id.medicine_ilness);
        TextView symptomView = (TextView) findViewById(R.id.medicine_symptom);
        TextView doseView = (TextView) findViewById(R.id.medicine_dose);
        TextView typeView = (TextView) findViewById(R.id.medicine_type);
        ilnessView.setText(medicineBean.ilness);
        symptomView.setText(medicineBean.symptom);
        doseView.setText(medicineBean.dose);
        typeView.setText(medicineBean.medicine_type);
    }

    public void onClick(View v){
        buy();
    }

    private void buy(){
        Intent intent = new Intent(this, MedicineBuyActivity.class);
        intent.putExtra("medicine", medicineBean);
        startActivity(intent);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        pagerPointView.setPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
