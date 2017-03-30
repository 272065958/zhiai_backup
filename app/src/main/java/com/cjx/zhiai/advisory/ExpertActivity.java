package com.cjx.zhiai.advisory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseFilterActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.DepartmentBean;
import com.cjx.zhiai.bean.DoctorBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.bean.ValueTextBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.JsonParser;
import com.cjx.zhiai.util.Tools;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-27.
 * 在线专家
 */
public class ExpertActivity extends BaseFilterActivity {

    String userType; // 2=在线 1=兼职
    ArrayList<DepartmentBean> departmentList;
    int leftPopupWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int padding = getResources().getDimensionPixelOffset(R.dimen.fab_margin);
        int showYOff = getResources().getDimensionPixelOffset(R.dimen.grid_spacing);
        setListViweDivider(null, 0);

        int popupWidth = ((MyApplication.getInstance()).getScreen_width() - showYOff) / 2 - 2 * padding;
        leftPopupWidth = (int) (popupWidth * 1.5f);
        setLeftFilterText(getString(R.string.online_expert_subject), padding, showYOff, leftPopupWidth);
        setRightFilterText(getString(R.string.online_expert_sort), padding, showYOff, popupWidth);

        leftFilterString = "";
        sortType = "0";
        userType = getIntent().getStringExtra("user_type");
        departmentList = MyApplication.getInstance().getDepartmentList();
        if (departmentList == null || departmentList.isEmpty()) {
            loadDepartment();
        } else {
            DepartmentBean db = departmentList.get(0);
            if(db.officeChList == null || db.officeChList.isEmpty()){
                loadDepartment();
                return ;
            }
            canFilter = true;
            setOfficesId(db.officeChList.get(0));
        }
    }

    // 加载科室
    private void loadDepartment() {
        MyCallbackInterface myCallbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                Type type = new TypeToken<ArrayList<DepartmentBean>>() {
                }.getType();
                departmentList = JsonParser.getInstance().fromJson(response.datas, type);
                MyApplication.getInstance().setDepartmentList(departmentList);
                if(departmentList != null && !departmentList.isEmpty()){
                    DepartmentBean db = departmentList.get(0);
                    if(db.officeChList == null || db.officeChList.isEmpty()){
                        showToast("未找到科室信息");
                        return ;
                    }
                    canFilter = true;
                    setOfficesId(db.officeChList.get(0));
                }else{
                    showToast("未找到科室信息");
                }
            }

            @Override
            public void error() {
                loadView.setVisibility(View.GONE);
            }
        };
        HttpUtils.getInstance().postEnqueue(this, myCallbackInterface, "base/office");
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<DoctorBean>>() {
                }.getType()), "base/getDoctorList", "page", "1", "limit", "100", "user_type", userType,
                "sequencing_type", sortType, "offices_id", leftFilterString);
    }

    @Override
    protected View createLeftPopupView() {
        return createDepartmentPopupView();
    }

    @Override
    protected ValueTextBean[] getRightPopupItem() {
        return new ValueTextBean[]{new ValueTextBean("0", "智能排序"), new ValueTextBean("1", "人气排序")};
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new ExpertAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DoctorBean db = (DoctorBean) parent.getAdapter().getItem(position);
        Intent intent = new Intent(this, ExpertDetailActivity.class);
        intent.putExtra("doctor", db);
        intent.putExtra("user_type", userType);
        intent.putExtra("offices_id", leftFilterString);
        startActivity(intent);
    }

    private void setOfficesId(DepartmentBean db){
        leftFilterString = db.office_id;
        setLeftFilterText(db.office_name);
        loadData();
    }

    DepartParentAdapter leftAdapter;
    DepartChildAdapter rightAdapter;
    private View createDepartmentPopupView(){
        View view = View.inflate(this, R.layout.popup_tree_view, null);
        ListView leftListView = (ListView) view.findViewById(R.id.list_view_left);
        leftAdapter = new DepartParentAdapter(departmentList, this);
        leftListView.setAdapter(leftAdapter);
        leftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(leftAdapter.currentSelect != position){
                    leftAdapter.currentSelect = position;
                    leftAdapter.notifyDataSetChanged();
                    DepartmentBean db = (DepartmentBean) view.getTag(R.id.department_name);
                    rightAdapter.notifyDataSetChanged(db.officeChList);
                }
            }
        });

        ListView rightListView = (ListView) view.findViewById(R.id.list_view_right);
        rightAdapter = new DepartChildAdapter(departmentList.get(0).officeChList, this);
        rightListView.setAdapter(rightAdapter);
        rightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DepartmentBean db = (DepartmentBean) view.getTag(R.id.department_name);
                if (!rightAdapter.currentSelectId.equals(db.office_id)) {
                    rightAdapter.currentSelectId = db.office_id;
                    rightAdapter.notifyDataSetChanged();
                    hideLeftPopopWindow();
                    setOfficesId(db);
                }
            }
        });
        return view;
    }

    class ExpertAdapter extends MyBaseAdapter {

        public ExpertAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_online_expert, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            DoctorBean doctor = (DoctorBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            Tools.setImageInView(context, doctor.head_image, ho.headView);
            ho.nameView.setText(doctor.user_real_name);
            ho.hospitalView.setText(doctor.hospital_name);
            ho.departmentView.setText(doctor.office_name);
            ho.postView.setText(doctor.position);
            ho.describeView.setText(doctor.honor);
            if (doctor.price.equals("0")) {
                ho.priceView.setText(R.string.expert_price_free);
            } else {
                ho.priceView.setText(String.format(getString(R.string.expert_price_format), doctor.price));
            }
        }

        class ViewHolder extends MyViewHolder {
            ImageView headView;
            TextView nameView, hospitalView, departmentView, postView, describeView, priceView;

            public ViewHolder(View v) {
                super(v);
                headView = (ImageView) v.findViewById(R.id.expert_head);
                nameView = (TextView) v.findViewById(R.id.expert_name);
                hospitalView = (TextView) v.findViewById(R.id.expert_hospital);
                departmentView = (TextView) v.findViewById(R.id.expert_department);
                postView = (TextView) v.findViewById(R.id.expert_post);
                describeView = (TextView) v.findViewById(R.id.expert_describe);
                priceView = (TextView) v.findViewById(R.id.expert_price);
            }
        }
    }

    class DepartParentAdapter extends MyBaseAdapter {
        int currentSelect = 0;
        DepartParentAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_poput_view, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            DepartmentBean db = (DepartmentBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            View v = ho.getView();
            if(position == currentSelect){
                ho.selectView.setVisibility(View.VISIBLE);
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.pink_select_color));
                ho.textView.setTextColor(ContextCompat.getColor(context, R.color.main_color));
            }else{
                ho.selectView.setVisibility(View.GONE);
                v.setBackgroundColor(Color.TRANSPARENT);
                ho.textView.setTextColor(ContextCompat.getColor(context, R.color.text_main_color));
            }
            ho.textView.setText(db.office_name);
            ho.getView().setTag(R.id.department_name, db);
        }

        class ViewHolder extends MyViewHolder {
            View selectView;
            TextView textView;
            public ViewHolder(View view) {
                super(view);
                selectView = view.findViewById(R.id.select_icon);
                textView = (TextView) view.findViewById(R.id.item_text_view);
            }
        }
    }

    class DepartChildAdapter extends MyBaseAdapter {
        String currentSelectId;
        DepartChildAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
            currentSelectId = leftFilterString;
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_poput_view, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            DepartmentBean db = (DepartmentBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            View v = ho.getView();
            if(db.office_id.equals(currentSelectId)){
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.pink_select_color));
                ho.textView.setTextColor(ContextCompat.getColor(context, R.color.main_color));
            }else{
                v.setBackgroundColor(Color.TRANSPARENT);
                ho.textView.setTextColor(ContextCompat.getColor(context, R.color.text_main_color));
            }
            ho.textView.setText(db.office_name);
            ho.getView().setTag(R.id.department_name, db);
        }

        class ViewHolder extends MyViewHolder {
            TextView textView;
            public ViewHolder(View view) {
                super(view);
                textView = (TextView) view.findViewById(R.id.item_text_view);
            }
        }
    }
}
