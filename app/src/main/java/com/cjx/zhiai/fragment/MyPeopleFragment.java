package com.cjx.zhiai.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.bean.UserBean;
import com.cjx.zhiai.my.AdvisoryHistoryActivity;
import com.cjx.zhiai.my.OrderHistoryActivity;
import com.cjx.zhiai.my.ReservationHistoryActivity;
import com.cjx.zhiai.my.SettingActivity;
import com.cjx.zhiai.util.Tools;

/**
 * Created by cjx on 2016-11-26.
 * 个人中心
 */
public class MyPeopleFragment extends Fragment implements View.OnClickListener {
    final int RESULT_SETTING = 1;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_my_people, null);
            view.findViewById(R.id.my_about).setOnClickListener(this);
            view.findViewById(R.id.my_service).setOnClickListener(this);
            view.findViewById(R.id.my_setting).setOnClickListener(this);
            view.findViewById(R.id.my_appointment).setOnClickListener(this);
            view.findViewById(R.id.my_integration).setOnClickListener(this);
            view.findViewById(R.id.my_order).setOnClickListener(this);
            view.findViewById(R.id.my_history).setOnClickListener(this);
            updateInfo();
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_history: // 咨询历史
                Intent advisoryIntent = new Intent(getActivity(), AdvisoryHistoryActivity.class);
                startActivity(advisoryIntent);
                break;
            case R.id.my_appointment: // 预约
                Intent reservationIntent = new Intent(getActivity(), ReservationHistoryActivity.class);
                startActivity(reservationIntent);
                break;
            case R.id.my_order: // 订单
                Intent orderIntent = new Intent(getActivity(), OrderHistoryActivity.class);
                startActivity(orderIntent);
                break;
            case R.id.my_integration: // 积分
                break;
            case R.id.my_service: // 协议
                break;
            case R.id.my_about: // 关于我们
                break;
            case R.id.my_setting: // 设置
                Intent settingIntent = new Intent(getActivity(), SettingActivity.class);
                startActivityForResult(settingIntent, RESULT_SETTING);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_SETTING) {
                getActivity().finish();
            }
        }
    }

    public void updateInfo() {
        UserBean user = MyApplication.getInstance().user;
        if (user == null || view == null) {
            return;
        }
        ((TextView) view.findViewById(R.id.my_name)).setText(user.user_name);
        ((TextView) view.findViewById(R.id.my_phone)).setText(user.user_phone);
        ((ImageView) view.findViewById(R.id.my_sex)).setImageResource(user.sex.equals("f") ? R.drawable.woman : R.drawable.man);
        ImageView head = (ImageView) view.findViewById(R.id.my_head);
        Tools.setImageInView(getActivity(), user.head_image, head);
    }
}
