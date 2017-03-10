package com.cjx.zhiai.advisory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.adapter.ImagePagerAdapter;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.MedicineRoomBean;
import com.cjx.zhiai.component.PagerPointView;

/**
 * Created by cjx on 2016-11-26.
 */
public class MedicineRoomDetailActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    PagerPointView pagerPointView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_room_detail);

        setToolBar(true, null, R.string.medicine_room_title);


        MedicineRoomBean mrb = (MedicineRoomBean) getIntent().getSerializableExtra("room");
        TextView nameView = (TextView) findViewById(R.id.hospital_name);
        TextView addressView = (TextView) findViewById(R.id.hospital_address);
        TextView phoneView = (TextView) findViewById(R.id.hospital_tel);
        nameView.setText(mrb.drugstore_name);
        addressView.setText(mrb.address);
        phoneView.setText(mrb.phone);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        int width = ((MyApplication)getApplication()).getScreen_width();
        int height = (int) (width * 299 / 627f);
        viewPager.getLayoutParams().height = height;

        if(!TextUtils.isEmpty(mrb.image_address)){
            viewPager.addOnPageChangeListener(this);
            ImagePagerAdapter adapter = new ImagePagerAdapter(this, mrb.image_address.split(","));
            viewPager.setAdapter(adapter);
            pagerPointView = (PagerPointView)findViewById(R.id.pager_point_view);
            int size = getResources().getDimensionPixelSize(R.dimen.text_margin);
            pagerPointView.setPoint(size, size, -1, R.drawable.circle_gray, adapter.getCount() + 1);
            Bitmap select = BitmapFactory.decodeResource(getResources(), R.drawable.circle_blue);
            select = Bitmap.createScaledBitmap(select, size*3, size, true);
            pagerPointView.setSelectBitmap(select);
        }
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
