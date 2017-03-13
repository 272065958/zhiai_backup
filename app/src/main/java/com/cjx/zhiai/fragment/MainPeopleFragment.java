package com.cjx.zhiai.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.activity.FeaturedActivity;
import com.cjx.zhiai.adapter.AdverPagerAdapter;
import com.cjx.zhiai.advisory.AdvisoryActivity;
import com.cjx.zhiai.advisory.HospitalActivity;
import com.cjx.zhiai.advisory.MedicineActivity;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.AdverBean;
import com.cjx.zhiai.bean.NewBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.component.PagerPointView;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.JsonParser;
import com.cjx.zhiai.util.Tools;
import com.cjx.zhiai.util.ZoomOutPageTransformer;
import com.cjx.zhiai.activity.AboutActivity;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-26.
 * 首页碎片
 */
public class MainPeopleFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {
    BaseActivity activity;
    View view;

    ViewPager viewPager;
    PagerPointView pagerPointView;
    View viewPagerContent;

    FeaturedAdapter featuredAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main_people, null);
            activity = (BaseActivity) getActivity();
            loadData();
        }
        return view;
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
        HttpUtils.getInstance().postEnqueue(activity, callbackInterface, "base/getIndexPicture", "type", "0");
    }

    private void initView(ArrayList<AdverBean> list) {
        if(activity.isFinishing()){
            return ;
        }
        view.findViewById(R.id.main_content).setVisibility(View.VISIBLE);
        viewPagerContent = view.findViewById(R.id.view_pager_content);
        viewPagerContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return viewPager.dispatchTouchEvent(event);
            }
        });
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setOnPageChangeListener(this);
        AdverPagerAdapter adapter = new AdverPagerAdapter(activity, list);
        viewPager.setAdapter(adapter);

        pagerPointView = (PagerPointView) view.findViewById(R.id.pager_point_view);
        int size = getResources().getDimensionPixelSize(R.dimen.text_margin);
        pagerPointView.setPoint(size, size, -1, R.drawable.circle_gray, adapter.getCount() + 1);
        Bitmap select = BitmapFactory.decodeResource(getResources(), R.drawable.circle_blue);
        select = Bitmap.createScaledBitmap(select, size * 3, size, true);
        pagerPointView.setSelectBitmap(select);

        int width = ((MyApplication) getActivity().getApplication()).getScreen_width();
        int height = (int) ((width - 2 * getActivity().getResources().getDimensionPixelOffset(R.dimen.half_tab_height)) * 366 / 622f);
        viewPager.getLayoutParams().height = height;

        view.findViewById(R.id.main_app_about).setOnClickListener(this);
        view.findViewById(R.id.main_find_expert).setOnClickListener(this);
        view.findViewById(R.id.main_find_hospital).setOnClickListener(this);
        view.findViewById(R.id.main_medicine).setOnClickListener(this);

        ((TextView) view.findViewById(R.id.title_tip_view)).setText(R.string.main_day_featured);
        loadNews();
    }

    // 加载每日精选
    private void loadNews(){
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                ArrayList<NewBean> list = JsonParser.getInstance().fromJson(response.datas,
                        new TypeToken<ArrayList<NewBean>>() {
                        }.getType());
                displayNews(list);
            }

            @Override
            public void error() {
            }
        };
        HttpUtils.getInstance().postEnqueue(activity, callbackInterface, "article/newsList", "news_type",
                "1", "page", "1", "limit", "10");
    }

    // 显示每日精选
    private void displayNews(ArrayList<NewBean> list) {
        if(activity.isFinishing()){
            return ;
        }
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        featuredAdapter = new FeaturedAdapter(list);
        recyclerView.setAdapter(featuredAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            viewPagerContent.invalidate();
        }
    }

    @Override
    public void onPageSelected(int position) {
        pagerPointView.setPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_app_about: // 关于app
                Intent aboutIntent = new Intent(getActivity(), AboutActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.main_find_expert: // 找专家
                Intent expertIntent = new Intent(getActivity(), AdvisoryActivity.class);
                startActivity(expertIntent);
                break;
            case R.id.main_find_hospital: // 找医院
                Intent hospitalIntent = new Intent(getActivity(), HospitalActivity.class);
                hospitalIntent.putExtra("title", R.string.main_find_hospital);
                startActivity(hospitalIntent);
                break;
            case R.id.main_medicine: // 找药
                Intent medicineIntent = new Intent(getActivity(), MedicineActivity.class);
                startActivity(medicineIntent);
                break;
        }
    }

    class FeaturedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
        ArrayList<NewBean> list;
        FeaturedAdapter(ArrayList<NewBean> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(getActivity(), R.layout.item_day_featured, null);
            view.setOnClickListener(this);
            RecyclerView.LayoutParams rlp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            rlp.leftMargin = getResources().getDimensionPixelOffset(R.dimen.auto_margin);
            if (viewType == getItemCount() - 1) {
                rlp.rightMargin = getResources().getDimensionPixelOffset(R.dimen.auto_margin);
            }
            view.setLayoutParams(rlp);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            NewBean nb = list.get(position);
            ViewHolder ho = (ViewHolder) holder;
            Tools.setImageInView(activity, nb.min_image, ho.imageView);
            ho.titleView.setText(nb.title);
            ho.browseView.setText(String.format(getString(R.string.main_browser_count_format), nb.page_view));
            ho.itemView.setTag(R.id.featured_image, nb);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public void onClick(View v) {
            Intent featuredIntent = new Intent(activity, FeaturedActivity.class);
            NewBean nb = (NewBean) v.getTag(R.id.featured_image);
            int count = Integer.parseInt(nb.page_view);
            count ++;
            nb.page_view = String.valueOf(count);
            ((TextView)v.findViewById(R.id.featured_browse)).setText(String.format(getString(R.string.main_browser_count_format),
                    nb.page_view));
            featuredIntent.setAction(nb.news_id);
            startActivity(featuredIntent);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView titleView, browseView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.featured_image);
                titleView = (TextView) itemView.findViewById(R.id.featured_title);
                browseView = (TextView) itemView.findViewById(R.id.featured_browse);
            }
        }
    }
}
