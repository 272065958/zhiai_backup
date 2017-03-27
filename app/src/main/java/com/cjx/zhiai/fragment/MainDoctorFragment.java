package com.cjx.zhiai.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.adapter.AdverPagerAdapter;
import com.cjx.zhiai.advisory.ImageAdvisoryActivity;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.AdverBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.bean.SyatemMessageBean;
import com.cjx.zhiai.component.PagerPointView;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.manager.PatientActivity;
import com.cjx.zhiai.manager.ReserveManagerActivity;
import com.cjx.zhiai.util.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by cjx on 2016-12-31.
 */
public class MainDoctorFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener{
    BaseActivity activity;
    View view;

    ViewPager viewPager;
    PagerPointView pagerPointView;
    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main_doctor, null);
            activity = (BaseActivity) getActivity();
            loadData();
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_advisory: // 图文咨询
                Intent advisoryIntent = new Intent(activity, ImageAdvisoryActivity.class);
                startActivity(advisoryIntent);
                break;
            case R.id.main_manager: // 患者管理
                Intent patientIntent = new Intent(activity, PatientActivity.class);
                startActivity(patientIntent);
                break;
            case R.id.main_reserve: // 预约挂号
                Intent managerIntent =new Intent(activity, ReserveManagerActivity.class);
                startActivity(managerIntent);
                break;
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

    @Override
    public void onResume() {
        super.onResume();
        if(canLoadNew){
            loadNews();
        }
    }

    // 加载首页轮播图
    private void loadData() {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                view.findViewById(R.id.loading_view).setVisibility(View.GONE);
                ArrayList<AdverBean> list = JsonParser.getInstance().fromJson(response.datas,
                        new TypeToken<ArrayList<AdverBean>>() {
                        }.getType());
                initView(list);
            }

            @Override
            public void error() {
                view.findViewById(R.id.loading_view).setVisibility(View.GONE);
                view.findViewById(R.id.empty_view).setVisibility(View.GONE);
            }
        };
        HttpUtils.getInstance().postEnqueue(activity, callbackInterface, "base/getIndexPicture", "type", "1");
    }

    private void initView(ArrayList<AdverBean> list) {
        if(activity.isFinishing()){
            return ;
        }
        View headerView = View.inflate(activity, R.layout.header_main_doctor, null);
        viewPager = (ViewPager) headerView.findViewById(R.id.view_pager);
        int width = ((MyApplication) getActivity().getApplication()).getScreen_width();
        int height = (int) ((width - 2 * getActivity().getResources().getDimensionPixelOffset(R.dimen.half_tab_height)) * 366 / 622f);
        viewPager.getLayoutParams().height = height;
        viewPager.setOnPageChangeListener(this);
        AdverPagerAdapter adapter = new AdverPagerAdapter(activity, list);
        viewPager.setAdapter(adapter);

        pagerPointView = (PagerPointView) headerView.findViewById(R.id.pager_point_view);
        int size = getResources().getDimensionPixelSize(R.dimen.text_margin);
        pagerPointView.setPoint(size, size, -1, R.drawable.circle_gray, adapter.getCount() + 1);
        Bitmap select = BitmapFactory.decodeResource(getResources(), R.drawable.circle_blue);
        select = Bitmap.createScaledBitmap(select, size * 3, size, true);
        pagerPointView.setSelectBitmap(select);

        headerView.findViewById(R.id.main_reserve).setOnClickListener(this);
        headerView.findViewById(R.id.main_manager).setOnClickListener(this);
        headerView.findViewById(R.id.main_advisory).setOnClickListener(this);

        ((TextView) headerView.findViewById(R.id.title_tip_view)).setText(R.string.main_new_message);

        listView = (ListView) view.findViewById(R.id.list_view);
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setEnabled(false);
        listView.addHeaderView(headerView);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        loadNews();
    }

    // 加载每日精选
    private void loadNews(){
        canLoadNew = false;
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                ArrayList<SyatemMessageBean> list = JsonParser.getInstance().fromJson(response.datas,
                        new TypeToken<ArrayList<SyatemMessageBean>>() {
                        }.getType());
                displayNews(list);
            }

            @Override
            public void error() {
            }
        };
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        HttpUtils.getInstance().postEnqueue(activity, callbackInterface, "base/selectSystemMessage",
                "page", "1", "limit", "100", "day", sdf.format(new Date()));
    }

    boolean canLoadNew = false;
    // 显示每日精选
    private void displayNews(ArrayList<?> list) {
        if(activity.isFinishing()){
            return ;
        }
        canLoadNew = true;
        listView.setAdapter(new MessageAdapter(list, activity));
    }

    class MessageAdapter extends MyBaseAdapter {
        public MessageAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_system_message, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            if(position == 0){
                ho.topLineView.setVisibility(View.INVISIBLE);
            }else{
                ho.topLineView.setVisibility(View.VISIBLE);
            }
            SyatemMessageBean smb = (SyatemMessageBean) getItem(position);
            ho.timeView.setText(smb.bespeak_time);
            ho.contentView.setText(smb.state);
        }

        class ViewHolder extends MyViewHolder{
            View topLineView;
            TextView timeView, contentView;

            public ViewHolder(View itemView) {
                super(itemView);
                topLineView = itemView.findViewById(R.id.message_top_line);
                timeView = (TextView) itemView.findViewById(R.id.message_time);
                contentView = (TextView) itemView.findViewById(R.id.message_content);
            }
        }
    }
}
