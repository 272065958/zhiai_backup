package com.cjx.zhiai.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.PatientBean;
import com.cjx.zhiai.http.HttpUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by cjx on 2017-01-01.
 */
public class ReserveManagerActivity extends BaseListActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_manager);
        setToolBar(true, null, R.string.main_reserve);

        initListView(false, false);
        setListViweDivider(null, getResources().getDimensionPixelOffset(R.dimen.auto_margin));
        initView();
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.time_manager, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, TimeManagerActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        int itemWidth = getResources().getDimensionPixelOffset(R.dimen.button_height);
        int width = (MyApplication.getInstance()).getScreen_width();
        int margin = (int) ((width - itemWidth * 7) / 7f);
        LinearLayout dayContent = (LinearLayout) findViewById(R.id.manager_day_content);
        int paddint = margin / 2;
        dayContent.setPadding(paddint, 0, paddint, 0);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int dayCount = getDayCount(year, month);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int weekStartDay = dayOfMonth - dayOfWeek + 1; // 这周的第一天是几号
        for (int i = 0; i < 6; i++) {
            TextView tv = (TextView) dayContent.getChildAt(i);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv.getLayoutParams();
            lp.rightMargin = margin;
            int day = weekStartDay + i;
            if (day == dayOfMonth) {
                tv.setBackgroundResource(R.drawable.red_cricle_bg);
            }
            getDayStr(year, month, dayCount, day, tv);
        }
        TextView tv = (TextView) dayContent.getChildAt(6);
        int day = weekStartDay + 6;
        if (day == dayOfMonth) {
            tv.setBackgroundResource(R.drawable.red_cricle_bg);
        }
        getDayStr(year, month, dayCount, day, tv);
//        int pointWidth = getResources().getDimensionPixelOffset(R.dimen.auto_margin);
//        margin = (int) ((width - pointWidth * 7) / 7f);
//        LinearLayout pointContent = (LinearLayout) findViewById(R.id.manager_point_content);
//        paddint = margin / 2;
//        pointContent.setPadding(paddint, 0, paddint, 0);
//        for (int i = 0; i < 6; i++) {
//            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pointContent.getChildAt(i).getLayoutParams();
//            lp.rightMargin = margin;
//        }
    }

    @Override
    public void onClick(View v) {
        String day = (String) v.getTag();
        Intent intent = new Intent(this, ReserveDayManagerActivity.class);
        intent.setAction(day);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<PatientBean>>() {
        }.getType()), "base/getHistory", "user_id", MyApplication.getInstance().user.user_id, "query_type", "5", "page", "1", "limit", "100");
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new ReserveAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PatientBean pb = (PatientBean) parent.getAdapter().getItem(position);
        Intent intent = new Intent(this, PatientComfirmActivity.class);
        intent.putExtra("patient", pb);
        startActivityForResult(intent, 1);
    }

    // 获取本月的总天数
    private int getDayCount(int year, int month) {
        int dayCount = 0;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                dayCount = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                dayCount = 30;
                break;
            case 2:
                if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                    dayCount = 29;
                } else {
                    dayCount = 28;
                }
                break;
        }
        return dayCount;
    }

    // 设置日期和本月的
    private void getDayStr(int year, int month, int maxCount, int day, TextView tv) {
        if (day < 1) { // 属于上个月
            month = month - 1;
            if (month == 0) { // 属于上年的12月份
                month = 12;
                year = year - 1;
                maxCount = 31;
            } else {
                maxCount = getDayCount(year, month);
            }
            day = maxCount + day;
        } else if (day > maxCount) { // 属于下个月
            month = month + 1;
            if (month > 12) { // 属于明年
                year = year + 1;
                month = 1;
            }
            day = day - maxCount;
        }
        tv.setText(String.valueOf(day));
        tv.setTag(year + "-" + month + "-" + day);
        tv.setOnClickListener(this);
    }
}
